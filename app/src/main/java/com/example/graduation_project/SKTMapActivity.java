package com.example.graduation_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.internal.ResourceUtils;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPathLayer;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/*
    작성자 : 김평기
    작성일 : 2019-09-03
    제공 기능 : 길찾기
    관련 액티비티 : MapActivity(Intent로 넘어옴)
    외부 라이브러리 및 플러그인 :
            SKTMapLibrary(=com.skt.Tmap_1.60.jar)
            ODsayAndroidSDK

    기능 설명 : SKT Tmap을 통해 Google Map보다 정확한 지리 정보 및 Path 메소드를 통해 맵에 선을 그어 길찾기 기능을 제공하고,
               또한 ODsay 라이브러리를 활용하여 지하철 및 버스 정보를 가져와 위의 길찾기 기능을 강화한다.

    길찾기 단계
        1. 현재 위치에서 가장 가까운 지하철까지 안내
        2. 가까운 지하철에 도달했을 경우, 목적지와 가장 가까운 지하철까지의 길 안내
        3. 목적지와 가까운 지하철까지 도달하면 목적지와 가까운 지하철에서 목적지까지 길안내

        길찾기 알고리즘
        1. 출발지 -> 목적지 까지의 거리가 가까울 경우 :
            버스 및 지하철 정보를 전달하지 않고 곧바로 출발지와 목적지까지 tMapView.addTMapPath(tMapPolyLine, 이하 선긋기) 메소드를 이용해 연결

        2. 출발지 -> 목적지 까지의 거리가 멀 경우 :
            1) 출발지에서 출발역까지 거리가 가까울 경우 출발지에서 출발역까지 바로 선긋기.
            2) 출발지에서 출발역까지 거리가 멀 경우, 출발지에서 가까운 버스정류장까지, 그리고 출발역에서 가장 가까운 버스정류장까지 선긋기.
            3) 도착지에서 도착역까지 거리가 가까울 경우 도착지에서 도착역까지 바로 선긋기.
            4) 도착지에서 도착역까지 거리가 멀 경우, 도착지에서 가까운 버스정류장까지, 그리고 도착역에서 가장 가까운 버스정류장까지 선긋기.

        예외 : 현 위치와 목적지가 지하철을 경유하지 않아도 될 정도로 가까울 경우, 현 위치에서 목적지까지 바로 안내.
 */
/*
    순서 :
        현 위치 받고, 목적지 위치 받고.
        현 위치 주변 지하철 찾기
        현 위치 주변 버스 정류장 찾기
        목적지 주변 지하철 찾기
        목적지 주변 버스정류장 찾기
        현위치 -> 목적지 거리 측정
        현위치 -> 주변 역 거리 측정
        현위치 -> 주변 버스정류장 거리 측정
        목적지 -> 주변 역 거리 측정
        목적지 -> 주변 버스정류장 거리 측정
        루트 짜기
        if ( 현 위치 vs 목적지 위치 비교해서 가까우면) {
            현 위치 -선 긋기-> 목적지
        }
        else if (현 위치 vs 목적지 위치 비교해서 멀면) {
            if ( 현 위치와 현재 역까지 가까울 경우 )
            {
                현재역 좌표 저장
                현 위치 -선 긋기-> 현재역
            }
            else ( 현 위치와 현재 역까지 멀 경우 ) {
                현재역 좌표 저장
                현 위치에서 가까운 버스 정류장 찾기
                현재 역에서 가까운 버스 정류장 찾기
                현위 버스정류장 좌표 저장
                현재 버스정류장 좌표 저장
                현위 버스 정류장 -> 현역 버스 정류장 루트 찾기
                리스트 반영
                현위 버스 정류장 -> 현역 버스 정류장 선긋기
                버스 정류장 위치 도착시 버스 정류장 -> 현재 역까지 선긋기
            }
        }

        if (현재 위치가 현재역 좌표와 근접시) {
            현재역 -> 도착역 선긋기
        }

        if (현재 위치가 도착역 좌표와 근접시) {
            if (도착지 -> 도착역 가까움) {
                바로 선긋기
            }
            else // 도착지 -> 도착역 멈 {
                도착역 좌표 저장
                현 위치에서 가까운 버스 정류장 찾기
                도착역에서 가까운 버스 정류장 찾기
                현위 버스정류장 좌표 저장
                도착 버스정류장 좌표 저장
                현위 버스 정류장 -> 도착 버스 정류장 루트 찾기
                리스트 반영
                현위 버스 정류장 -> 도착 버스 정류장 선긋기
                버스 정류장 위치 도착시 버스 정류장 -> 도착 역까지 선긋기
            }
        }

 */

public class SKTMapActivity extends AppCompatActivity {

    TMapView tMapView;
    Double current_longtitude;
    Double current_latitude;
    Double marker_longtitude;
    Double marker_latitude;

    TMapPoint start_station_loc;
    TMapPoint end_station_loc;

    String near_start_station_ID = null;
    String near_destination_station_ID = null;

    Double near_current_station_longtitue;
    Double near_current_station_latitude;
    Double near_destination_station_longtitue;
    Double near_destination_station_latitude;


    private SKTListViewAdapter sktListViewAdapter;
    private ListView listView;
    private LinkedList<String> start_station_name_LL = new LinkedList<String>();
    private LinkedList<String> destination_station_name_LL = new LinkedList<String>();
    private LinkedList<String> start_station_line_LL = new LinkedList<String>();
    private LinkedList<String> station_remainning = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sktmap);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.sktMap);
        // SKT 맵을 띄우기 위한 리니어 레이아웃 작성

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("3d81e8cd-bb23-4486-8396-274b0e6a5886");
        // T-Map API 키 등록

        sktListViewAdapter = new SKTListViewAdapter();
        listView = (ListView) findViewById(R.id.skt_Listview) ;
        listView.setAdapter(sktListViewAdapter);


        Intent intent = getIntent();
        current_latitude = intent.getDoubleExtra("currentlatitude", 0);
        current_longtitude = intent.getDoubleExtra("currentlongtitude", 0 );
        marker_latitude = intent.getDoubleExtra("markerlatitude", 0 );
        marker_longtitude = intent.getDoubleExtra("markerlongtitude", 0 );
        // 맵 엑티비티로부터 값 가져오기


        String ODsay_API_Key = "oafdV7F8+lhrwK9MPzKFh5sm2BQAfDI8yAiI1jNlP9g";
        ODsayService oDsayService = ODsayService.init(this, ODsay_API_Key);
        oDsayService.setReadTimeout(5000);
        oDsayService.setConnectionTimeout(5000);
        OnResultCallbackListener onResultCallbackListener
                = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try
                {

                    if (api == API.POINT_SEARCH) {
                        JSONObject step_1 = oDsayData.getJson().optJSONObject("result");
                        System.out.println(step_1 + "step_1");
                        JSONArray step_2 = step_1.getJSONArray("station");
                        JSONObject step_3 = step_2.getJSONObject(0);
                        String str = step_3.getString("stationID");
                        String str2 = step_3.getString("stationName");
                        if (near_start_station_ID == null) {
                            near_start_station_ID = str;
                            System.out.println("Hello");
                            System.out.println("str :: " + str);
                            System.out.println("str2 :: " + str2);
                        }
                        else if (near_destination_station_ID == null) {
                            near_destination_station_ID = str;
                            System.out.println("World");
                            System.out.println("end_str :: " + str);
                            System.out.println("end_str2 :: " + str2);
                        }
                    }

                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        JSONObject jsonObject = oDsayData.getJson();
                        System.out.println("jsonObject : " + jsonObject.toString());

                    }

                    if (api == API.SUBWAY_PATH) {
                        JSONObject jsonObject = oDsayData.getJson();
                        System.out.println("jsonObject : " + jsonObject.toString());
                        JSONObject step_1 = oDsayData.getJson().optJSONObject("result");

                        JSONObject t1 = step_1.optJSONObject("driveInfoSet");
                        JSONArray t2 = t1.getJSONArray("driveInfo");


                        JSONObject e1 = step_1.optJSONObject("exChangeInfoSet");
                        JSONArray e2 = e1.getJSONArray("exChangeInfo");

                        String end = step_1.getString("globalEndName");


                        System.out.println("end ::::: " + end);

                        for (int i=0; i < t2.length(); i++) {

                            JSONObject t3 = t2.optJSONObject(i);


                            String startName = t3.getString("startName");
                            String lane = t3.getString("laneName");
                            String wayName = t3.getString("wayName");
                            String stationCount = t3.getString("stationCount");


                            System.out.println(i + "번째 루프 :::::");
                            System.out.println("시작역 : " + startName);
                            System.out.println(stationCount + "개 역");
                            System.out.println("라인명 : " + lane + " " + wayName + " 방면");


                            start_station_name_LL.add(0, startName);
                            start_station_line_LL.add(0,  lane + " " + wayName + " 방면");
                            station_remainning.add(0, stationCount + "개 역");
                        }
                        for (int j=0; j<e2.length(); j++) {
                            JSONObject e3 = e2.getJSONObject(j);
                            String exName = e3.getString("exName");
                            destination_station_name_LL.add(0, exName);
                            if (j == e2.length() -1) {
                                destination_station_name_LL.add(0, end);
                            }
                        }

                        sktListViewAdapter.clear();
                        sktListViewAdapter.notifyDataSetChanged();



                        int size = start_station_name_LL.size();
                        for (int i=0; i<size; i++) {
                            sktListViewAdapter.addVO(
                                    start_station_name_LL.pollLast(),
                                    destination_station_name_LL.pollLast(),
                                    start_station_line_LL.pollLast(),
                                    station_remainning.pollLast(),
                                    "1");
                            listView.setAdapter(sktListViewAdapter);
                        }
                    }

                    if (api == API.BUS_STATION_INFO)
                    {
                        JSONObject jsonObject = oDsayData.getJson();
                        System.out.println("jsonObject : " + jsonObject.toString());
                        JSONObject step_1 = oDsayData.getJson().optJSONObject("result");

                    }



                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    System.out.println("제이슨 에러");
                }
                catch (ArrayIndexOutOfBoundsException io) {
                    io.printStackTrace();
                    System.out.println("IndexOutOfBoundsException");
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SUBWAY_STATION_INFO) {}
            }
        };



        Toast.makeText(getApplicationContext(), "인텐트 확인 : "
                + "cur_long : " + current_longtitude
                + "\ncur_lati : " + current_latitude
                + "\nmar_long : " + marker_longtitude
                + "\nmar_lati : " + marker_latitude
        , Toast.LENGTH_LONG).show();
        // 추후 삭제

        System.out.println("로그 확인 : "
                + "cur_long : " + current_longtitude
                + "\ncur_lati : " + current_latitude
                + "\nmar_long : " + marker_longtitude
                + "\nmar_lati : " + marker_latitude);
        // 로그용



        TMapData tMapData = new TMapData();
        TMapPoint startPoint = new TMapPoint(current_latitude, current_longtitude);
        // 현위치
        TMapPoint endPoint = new TMapPoint(marker_latitude, marker_longtitude);
        // 목적지 위치

        TMapMarkerItem destination = new TMapMarkerItem();
        Bitmap destination_marker = BitmapFactory.decodeResource(getResources(), R.drawable.destination);
        destination.setName("목적지");
        destination.setTMapPoint(endPoint);
        destination.setPosition(0.5f, 1.0f);
        destination.setIcon(destination_marker);
        tMapView.addMarkerItem("destination", destination);
        // 목적지 마커 설정




        LinkedList<TMapPOIItem> start_POIName= new LinkedList<TMapPOIItem>();
        if (tMapView.isValidTMapPoint(startPoint)) {
            tMapData.findAroundNamePOI(startPoint,"지하철", 10000, 36, arrayList -> {
                for (int i = 0; i< arrayList.size(); i++) {
                    TMapPOIItem item = arrayList.get(i);
                    start_POIName.add(item);
                }
            });
            // 출발지 주변 지하철 정보 가져오기
        }

        // 목적지 주변 지하철들 가져오기
        LinkedList<TMapPOIItem> end_POIName = new LinkedList<TMapPOIItem>();
        if (tMapView.isValidTMapPoint(endPoint)) {
            tMapData.findAroundNamePOI(endPoint,"지하철", 10000, 6, arrayList -> {
                for (int i = 0; i< arrayList.size(); i++) {
                    TMapPOIItem item = arrayList.get(i);
                    end_POIName.add(item);
                    System.out.println("POI Name: " + item.getPOIName() + "," + "Address: "
                            + item.getPOIAddress().replace("null", "") + "  POI Distance" + item.getDistance(startPoint));
                }
            });
        }






        // 단계 1. 출발지에서 가장 가까운 역까지 안내
        TimerTask method_1 = new TimerTask() {
            @Override
            public void run() {

                final List<TMapPOIItem> start_targetName =
                        start_POIName.stream()
                                .filter(name -> name.getPOIName().contains("["))
                                .limit(1)
                                .collect(Collectors.toList());


                TMapMarkerItem near_current_station_marker = new TMapMarkerItem();

                Bitmap near_currnet_station_marker_icon = BitmapFactory.decodeResource(getResources(), R.drawable.train);
                near_current_station_marker.setTMapPoint(start_targetName.get(0).getPOIPoint());
                near_current_station_marker.setName(start_targetName.get(0).getPOIName());
                near_current_station_marker.setVisible(near_current_station_marker.VISIBLE);
                near_current_station_marker.setIcon(near_currnet_station_marker_icon);
                tMapView.addMarkerItem("near_current_station_marker", near_current_station_marker);

                near_current_station_longtitue = start_targetName.get(0).getPOIPoint().getLongitude();
                near_current_station_latitude = start_targetName.get(0).getPOIPoint().getLatitude();

                tMapData.findPathData(startPoint, endPoint, tMapPolyLine ->  {
                    tMapPolyLine.setID("1");
                    tMapView.addTMapPath(tMapPolyLine);
                });

                start_station_loc = start_targetName.get(0).getPOIPoint();

                System.out.println("current_longtitude :: "+near_current_station_longtitue);
                System.out.println("current_latitude :: "+near_current_station_latitude);
                oDsayService.requestPointSearch(String.valueOf(near_current_station_longtitue), String.valueOf(near_current_station_latitude), "250", "2", onResultCallbackListener);

            }
        };

        Timer timer = new Timer();
        timer.schedule(method_1, 2000);



        // 단계 2 지하철 정보 가져오기
        TimerTask method_2 = new TimerTask() {
            @Override
            public void run() {
                oDsayService.requestSubwayPath(
                        "1000",
                        near_start_station_ID,
                        near_destination_station_ID,
                        "1",
                        onResultCallbackListener
                );
            }
        };

        Timer timer2 = new Timer();
        timer2.schedule(method_2, 3000);


        // 단계 3 목적지 주변 지하철에서 목적지까지의 길 안내
        TimerTask method_3 = new TimerTask() {
            @Override
            public void run() {

                final List<TMapPOIItem> end_targetName =
                        end_POIName.stream()
//                                .filter(name -> name.getPOIName().contains("["))
                                .limit(4)
                                .collect(Collectors.toList());

//                if (end_targetName.isEmpty()) {
//                    TMapPOIItem tMapPoint = new TMapPOIItem();
//
//                    end_targetName.add(tMapPoint);
//                }

                TMapMarkerItem near_destination_station_marker = new TMapMarkerItem();
                Bitmap near_destination_station_marker_icon = BitmapFactory.decodeResource(getResources(), R.drawable.train);
                near_destination_station_marker.setIcon(near_destination_station_marker_icon);
                near_destination_station_marker.setTMapPoint(end_targetName.get(0).getPOIPoint());
                near_destination_station_marker.setName(end_targetName.get(0).getPOIName());
                tMapView.addMarkerItem("near_destination_station_marker", near_destination_station_marker);

                end_station_loc = end_targetName.get(0).getPOIPoint();

                near_destination_station_longtitue = end_targetName.get(0).getPOIPoint().getLongitude();
                near_destination_station_latitude = end_targetName.get(0).getPOIPoint().getLatitude();

//                tMapData.findPathData(startPoint, end_targetName.get(0).getPOIPoint(), tMapPolyLine ->  {
//                    tMapPolyLine.setID("1");
//                    tMapView.addTMapPath(tMapPolyLine);
//                });


                System.out.println("destination_longtitude :: "+near_destination_station_longtitue);
                System.out.println("destination_latitude :: "+near_destination_station_latitude);
                oDsayService.requestPointSearch(String.valueOf(near_destination_station_longtitue), String.valueOf(near_destination_station_latitude), "250", "2", onResultCallbackListener);
            }
        };

        Timer timer3 = new Timer();
        timer3.schedule(method_3, 2200);


        tMapView.setCenterPoint(current_longtitude, current_latitude);

        linearLayout.addView(tMapView);
    }
}
