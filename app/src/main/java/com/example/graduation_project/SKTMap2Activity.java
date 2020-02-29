package com.example.graduation_project;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapData;

import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SKTMap2Activity extends AppCompatActivity {
    TMapView tMapView;
    private String tMapViewApiKey = "3d81e8cd-bb23-4486-8396-274b0e6a5886";
    SKTListViewAdapter sktListViewAdapter;
    ListView listView;
    Double current_latitude;
    Double current_longtitude;
    Double destination_latitude;
    Double destination_longtitude;
    private String OdsayApiKey = "oafdV7F8+lhrwK9MPzKFh5sm2BQAfDI8yAiI1jNlP9g";
    OnResultCallbackListener currentOnResultCallbackListener, destinationOnResultCallbackListener;
    private LinkedList<String> distance = new LinkedList<String>();
    private LinkedList<String> bus_startName = new LinkedList<String>();
    private LinkedList<String> bus_endName = new LinkedList<String>();
    private LinkedList<String> bus_remain = new LinkedList<String>();
    private LinkedList<String> bus_number = new LinkedList<String>();
    private LinkedList<String> train_startName = new LinkedList<String>();
    private LinkedList<String> train_endName = new LinkedList<String>();
    private LinkedList<String> train_remain = new LinkedList<String>();
    private LinkedList<String> train_line = new LinkedList<String>();
    private LinkedList<String> sequence = new LinkedList<String>();
    private LinkedList<String> startX = new LinkedList<String>();
    private LinkedList<String> startY = new LinkedList<String>();
    private LinkedList<String> endX = new LinkedList<String>();
    private LinkedList<String> endY = new LinkedList<String>();
    // 추가
    int work=0;
    int bus=0;
    int train=0;

    Location info_location;
    Location current_bus_loc = new Location("current_bus_loc");
    Location destination_bus_loc = new Location("destination_bus_loc");;
    Location current_station_loc = new Location("current_station_loc");;
    Location destination_station_loc = new Location("destination_station_loc");;

    // current_location_Location(현위치), destination_location_Location(목적지), currnet_bus_info.TMapPoint(현위치 근처 버스), destination_bus_info.TMapPoint(목적지 근처 버스),
    // current_station_info.getTMapPoint()(현위치 근처 지하철)
    InfoDAO currnet_bus_info_test = new InfoDAO();
    InfoDAO current_station_info_test = new InfoDAO();
    InfoDAO destination_station_info_test = new InfoDAO();
    TMapPoint destination_location_SKT_test = new TMapPoint(0.0,0.0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sktmap2);

        // 기초적 맵 세팅 ( SKT 맵을 띄우기 위한 리니어 레이아웃 작성 )
        LinearLayout skt_linear = (LinearLayout) findViewById(R.id.sktMap2);

        // tMapView 객체 생성
        tMapView = new TMapView(this);

        // API 키 등록
        tMapView.setSKTMapApiKey(tMapViewApiKey);

        // 하단에 대중교통 정보를 띄울 리스트뷰 생성
        listView = (ListView) findViewById(R.id.skt_Listview2);


        // 리스트에 값을 적용할 어뎁터 객체 생성
        sktListViewAdapter = new SKTListViewAdapter();
        listView.setAdapter(sktListViewAdapter);

        // intent 로 현재 위치와 목적지 위치 정보 가져오기.
        Intent intent = getIntent();
        current_latitude = intent.getDoubleExtra("currentlatitude", 126.97);
        current_longtitude = intent.getDoubleExtra("currentlongtitude", 37.56);
        destination_latitude = intent.getDoubleExtra("markerlatitude", 126.68);
        destination_longtitude = intent.getDoubleExtra("markerlongtitude", 37.36);

        info_location = new Location("location");
        // 디폴트
        info_location.setLongitude(0.0);
        info_location.setLatitude(0.0);

        // ODSayService Library 사용을 위한 키등록 및 기능 설정
        ODsayService oDsayService = ODsayService.init(this, OdsayApiKey);
        oDsayService.setReadTimeout(3000);
        oDsayService.setConnectionTimeout(3000);

        // 추가
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try
        {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            Toast.makeText(getApplicationContext(), "위치정보 : " + provider + "\n" +
                            "위도 : " + longitude + "\n" +
                            "경도 : " + latitude + "\n" +
                            "고도  : " + altitude, Toast.LENGTH_LONG).show();

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);


        }
        catch (SecurityException se) {
            se.printStackTrace();
        }




        // 현위치 근처 버스 찾기 - 이 부분
        InfoDAO currnet_bus_info = new InfoDAO();
        currentOnResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try
                {
                    if (api == API.POINT_SEARCH)
                    {
                        JSONObject result = oDsayData.getJson().optJSONObject("result");
                        JSONArray station = result.getJSONArray("station");
                        JSONObject station_info = station.getJSONObject(0);
                        String stationName = station_info.getString("stationName");
                        String stationLat = station_info.getString("y");
                        String stationLog = station_info.getString("x");
                        TMapPoint tMapPoint = new TMapPoint(Double.parseDouble(stationLat), Double.parseDouble(stationLog));
                        currnet_bus_info.setName(stationName);
                        currnet_bus_info.setLatitude(Double.parseDouble(stationLat));
                        currnet_bus_info.setLongtitude(Double.parseDouble(stationLog));
                        currnet_bus_info.settMapPoint(tMapPoint);
                        currnet_bus_info_test = currnet_bus_info;
                        System.out.println("result : " + stationName + " " + stationLat + " " + stationLog);
                        System.out.println("result_dao : " + currnet_bus_info.getName() + " " + currnet_bus_info.gettMapPoint());
                    }

                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        JSONArray array = oDsayData.getJson().optJSONObject("result").getJSONArray("path");
                        if (array.get(0).toString().length() > 3192) {
                            System.out.println("String 분리 1 : "+array.get(0).toString().substring(0,3192));
                            System.out.println("String 분리 2 : "+array.get(0).toString().substring(3193));
                        }
                        JSONArray obj = array.getJSONObject(0).getJSONArray("subPath");
                        int obj_size = obj.length();
                        for (int i=0; i<obj_size; i++) {
                            JSONObject odsay = obj.getJSONObject(i);
                            String trafficType = odsay.getString("trafficType");

                            if (trafficType.equals("3"))
                            {
                                String get_distance = odsay.getString("distance");
                                if (!get_distance.equals("0")) {
                                    distance.add(get_distance);
                                    System.out.println("trafficType : 3 and distance is " + get_distance);
                                    System.out.println("----------편의를 위한 분단------------");
                                    sequence.add(trafficType);
                                    startX.add("0");
                                    startY.add("0");
                                    endX.add("0");
                                    endY.add("0");

                                }
                            }
                            else if (trafficType.equals("2"))
                            {
                                System.out.println("trafficType : 2");
                                String get_startName =  odsay.getString("startName");
                                String get_endName =  odsay.getString("endName");
                                String get_stationCount =  odsay.getString("stationCount");
                                String get_startX = odsay.getString("startX");
                                String get_startY = odsay.getString("startY");
                                String get_endX = odsay.getString("endX");
                                String get_endY = odsay.getString("endY");
                                JSONArray lane = odsay.getJSONArray("lane");
                                JSONObject lane_obj = lane.getJSONObject(0);
                                System.out.println("lane : " + lane_obj.getString("busNo"));
                                System.out.println("startName : " + get_startName);
                                System.out.println("endName : " + get_endName);
                                System.out.println("stationCount : " + get_stationCount);
                                System.out.println("----------편의를 위한 분단------------");
                                bus_number.add(lane_obj.getString("busNo"));
                                bus_startName.add(get_startName);
                                bus_endName.add(get_endName);
                                bus_remain.add(get_stationCount);
                                sequence.add(trafficType);
                                startX.add(get_startX);
                                startY.add(get_startY);
                                endX.add(get_endX);
                                endY.add(get_endY);
                            }
                            else if (trafficType.equals("1"))
                            {
                                System.out.println("trafficType : 1");
                                String get_startName =  odsay.getString("startName");
                                String get_endName =  odsay.getString("endName");
                                String get_stationCount =  odsay.getString("stationCount");
                                String get_startX = odsay.getString("startX");
                                String get_startY = odsay.getString("startY");
                                String get_endX = odsay.getString("endX");
                                String get_endY = odsay.getString("endY");
                                JSONArray lane = odsay.getJSONArray("lane");
                                JSONObject lane_obj = lane.getJSONObject(0);
                                System.out.println("lane : " + lane_obj.getString("name"));
                                System.out.println("startName : " + get_startName);
                                System.out.println("endName : " + get_endName);
                                System.out.println("stationCount : " + get_stationCount);
                                System.out.println("----------편의를 위한 분단------------");
                                train_line.add(lane_obj.getString("name"));
                                train_startName.add(get_startName);
                                train_endName.add(get_endName);
                                train_remain.add(get_stationCount);
                                sequence.add(trafficType);
                                startX.add(get_startX);
                                startY.add(get_startY);
                                endX.add(get_endX);
                                endY.add(get_endY);
                            }
                        }
                        System.out.println(sequence);

                        sktListViewAdapter.clear();
                        sktListViewAdapter.notifyDataSetChanged();

                        int sequence_size = sequence.size();
                        for (int i=0; i<sequence_size; i++)
                        {
                            if (sequence.get(i).equals("3")) {
                                sktListViewAdapter.addVO(
                                        "현재 위치",
                                        "다음 위치",
                                        "",
                                        "",
                                        sequence.get(i)
                                );
                                System.out.println("equal 3 ");
                                System.out.println("work = " + work);
                                System.out.println(distance);
                                System.out.println("----------편의를 위한 분단------------");
                                work++;
                            }
                            else if (sequence.get(i).equals("2")) {
                                System.out.println("equal 2 ");
                                sktListViewAdapter.addVO(
                                        bus_startName.get(bus),
                                        bus_endName.get(bus),
                                        bus_number.get(bus)+"번 버스",
                                        bus_remain.get(bus) + "개 정거장",
                                        sequence.get(i)
                                );

                                System.out.println("bus = " + bus);
                                System.out.println(bus_startName);
                                System.out.println(bus_endName);
                                System.out.println(bus_number);
                                System.out.println(bus_remain);
                                System.out.println("----------편의를 위한 분단------------");
                                bus++;
                            }
                            else if (sequence.get(i).equals("1")) {
                                System.out.println("equal 1 ");
                                sktListViewAdapter.addVO(
                                        train_startName.get(train),
                                        train_endName.get(train),
                                        train_line.get(train),
                                        train_remain.get(train) + "개 역",
                                        sequence.get(i)
                                );
                                System.out.println("train = " + train);
                                System.out.println(train_startName);
                                System.out.println(train_endName);
                                System.out.println(train_line);
                                System.out.println(train_remain);
                                System.out.println("----------편의를 위한 분단------------");
                                train++;
                            }
                        }
                        // 마지막 정류장 추가
                        int work_size = 0;
                        int work_has = 0;
                        if (sequence.contains("3")) {
                            work_has = 1;
                            for (String work : sequence) {
                                if (work.equals("3")) {
                                    work_size++;
                                }
                            }
                        }
                        System.out.println("work_size" + work_size);
                        startX.addLast(endX.get(train +work_size));
                        startY.addLast(endY.get(train +work_size));
                        endX.addLast(Double.toString(destination_longtitude));
                        endY.addLast(Double.toString(destination_latitude));

                        sktListViewAdapter.addVO(
                                train_endName.getLast(),
                                "목적지",
                                "",
                                "",
                                "3"
                        );

                        listView.setAdapter(sktListViewAdapter);
                    }
                }
                catch (JSONException je)
                {
                    Toast.makeText(getApplicationContext(), "오류가 발생하였으므로 다시 실행해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    je.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                System.out.println("ODSayService onError Method");
            }
        };

        // 목적지 근처 버스 찾기
        InfoDAO destination_bus_info = new InfoDAO();
        destinationOnResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try
                {
                    if (api == API.POINT_SEARCH)
                    {
                        JSONObject result = oDsayData.getJson().optJSONObject("result");
                        JSONArray station = result.getJSONArray("station");
                        JSONObject station_info = station.getJSONObject(0);
                        String stationName = station_info.getString("stationName");
                        String stationLat = station_info.getString("y");
                        String stationLog = station_info.getString("x");
                        System.out.println("result : " + stationName + " " + stationLat + " " + stationLog);
                        TMapPoint tMapPoint = new TMapPoint(Double.parseDouble(stationLat), Double.parseDouble(stationLog));
                        destination_bus_info.setName(stationName);
                        destination_bus_info.setLatitude(Double.parseDouble(stationLat));
                        destination_bus_info.setLongtitude(Double.parseDouble(stationLog));
                        destination_bus_info.settMapPoint(tMapPoint);
                        System.out.println("result : " + stationName + " " + stationLat + " " + stationLog);
                        System.out.println("result_dao : " + destination_bus_info.getName() + " " + destination_bus_info.gettMapPoint());

                    }
                }
                catch (JSONException je)
                {
                    Toast.makeText(getApplicationContext(), "오류가 발생하였으므로 다시 실행해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    je.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s, API api) {

            }
        };


        // Location과 TMapPoint를 통해 현위치 및 목적지 위치 정보 저장하기.
        Location current_location_Location = new Location("current_loc");
        current_location_Location.setLatitude(current_latitude);
        current_location_Location.setLongitude(current_longtitude);
        Location destination_location_Location = new Location("destination_loc");
        destination_location_Location.setLatitude(destination_latitude);
        destination_location_Location.setLongitude(destination_longtitude);
        TMapPoint current_location_SKT = new TMapPoint(current_latitude, current_longtitude);
        TMapPoint destination_location_SKT = new TMapPoint(destination_latitude, destination_longtitude);
        destination_location_SKT.setLatitude(destination_latitude);
        destination_location_SKT.setLongitude(destination_longtitude);

        System.out.println("현 위치 : " + current_location_Location.getLatitude() + "  " + current_location_Location.getLongitude());
        System.out.println("목적지 : " + destination_location_Location.getLatitude() + "  " + destination_location_Location.getLongitude());
        // 출발지와 목적지 간 거리 구하기.

        float c_d_distance = current_location_Location.distanceTo(destination_location_Location);
        System.out.println("현 위치와 목적지 사이 거리 : " + c_d_distance);
        // LinkedList와 SKTPOIItem을 활용한 현위치 및 목적지 위치 정보 저장하기.
        TMapData tMapData = new TMapData();

        // 출발지 근처 지하철 찾기
        InfoDAO current_station_info = new InfoDAO();
        InfoDAO destination_station_info = new InfoDAO();
        if (c_d_distance < 500) {tMapData.findPathData(current_location_SKT, destination_location_SKT, tMapPolyLine ->
            {   tMapPolyLine.setID("");
                tMapView.addTMapPath(tMapPolyLine);
            });
        }
        else
        {
            LinkedList<TMapPOIItem> current_POIName = new LinkedList<TMapPOIItem>();
            if (tMapView.isValidTMapPoint(current_location_SKT)) {
                tMapData.findAroundNamePOI(current_location_SKT,
                        "지하철",
                        10000,
                        36,
                        arrayList -> {
                            int size = arrayList.size();
                            if (size == 0) { System.out.println("출발지 근처 지하철을 찾는 과정에서 문제가 발생하였습니다.");}
                            else
                            {
                                for (int i = 0; i< size; i++)
                                {
                                    TMapPOIItem item = arrayList.get(i);
                                    if (item.getPOIName().contains("[")) {
                                        current_POIName.add(item);
                                    }
                                    System.out.println("아이템은 " + item.getPOIName() + "입니다.");
                                }
                                current_station_info.setName(current_POIName.get(0).getPOIName());
                                current_station_info.settMapPoint(current_POIName.get(0).getPOIPoint());
                                current_station_info.setLatitude(current_POIName.get(0).getPOIPoint().getLatitude());
                                current_station_info.setLongtitude(current_POIName.get(0).getPOIPoint().getLongitude());
                                current_station_info_test = current_station_info;
                            }
                        });
            }
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("currentDAO : "
                            +"\n이름 : " + current_station_info.getName()
                            +"\n포인트 : " + current_station_info.gettMapPoint()
                            +"\nLatitude : " + current_station_info.getLatitude()
                            +"\nLongtitude : " + current_station_info.getLongtitude()

                    );
                    current_station_loc.setLatitude(current_station_info.getLatitude());
                    current_station_loc.setLongitude(current_station_info.getLongtitude());
                }
            };

            Timer timer = new Timer();
            timer.schedule(timerTask, 1000);



            // 목적지 근처 지하철 찾기
            LinkedList<TMapPOIItem> destination_POIName = new LinkedList<TMapPOIItem>();
            if (tMapView.isValidTMapPoint(destination_location_SKT)) {
                tMapData.findAroundNamePOI(destination_location_SKT,
                        "지하철",
                        10000,
                        36,
                        arrayList -> {
                            int size = arrayList.size();
                            if (size == 0) { System.out.println("도착지 근처 지하철을 찾는 과정에서 문제가 발생하였습니다.");}
                            else
                            {
                                for (int i = 0; i< size; i++)
                                {
                                    TMapPOIItem item = arrayList.get(i);
                                    if (item.getPOIName().contains("[")) {
                                        destination_POIName.add(item);
                                    }
                                    System.out.println("아이템은 " + item.getPOIName() + "입니다.");
                                }
                                destination_station_info.setName(destination_POIName.get(0).getPOIName());
                                destination_station_info.settMapPoint(destination_POIName.get(0).getPOIPoint());
                                destination_station_info.setLatitude(destination_POIName.get(0).getPOIPoint().getLatitude());
                                destination_station_info.setLongtitude(destination_POIName.get(0).getPOIPoint().getLongitude());
                                destination_station_info_test = destination_station_info;
                            }
                        });
            }

            TimerTask timerTask2 = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("destinationDAO : "
                            +"\n이름 : " + destination_station_info.getName()
                            +"\n포인트 : " + destination_station_info.gettMapPoint()
                            +"\nLatitude : " + destination_station_info.getLatitude()
                            +"\nLongtitude : " + destination_station_info.getLongtitude()

                    );
                    destination_station_loc.setLatitude(destination_station_info.getLatitude());
                    destination_station_loc.setLongitude(destination_station_info.getLongtitude());
                }
            };

            Timer timer2 = new Timer();
            timer2.schedule(timerTask2, 1500);


            // ODSayService 를 활용해 버스정류장 위치 찾기.
            TimerTask timerTask3 = new TimerTask() {
                @Override
                public void run() {
                    oDsayService.requestPointSearch(
//                            String.valueOf(current_station_info.getLongtitude()),
                            String.valueOf(current_location_Location.getLongitude()),
//                            String.valueOf(current_station_info.getLatitude()),
                            String.valueOf(current_location_Location.getLatitude()),
                            "4000",
                            "1",
                            currentOnResultCallbackListener);


                    oDsayService.requestPointSearch(
                            String.valueOf(current_station_info.getLongtitude()),
                            String.valueOf(current_station_info.getLatitude()),
                            "4000",
                            "1",
                            destinationOnResultCallbackListener);


                }
            };
            Timer timer3 = new Timer();
            timer3.schedule(timerTask3, 2000);

            TimerTask timertask4 = new TimerTask() {
                @Override
                public void run() {
                    try {
                        System.out.println("current_bus_info : " + currnet_bus_info.getName() + " " + currnet_bus_info.gettMapPoint());
                        System.out.println("destination_bus_info : " + destination_bus_info.getName() + " " + destination_bus_info.gettMapPoint());
                        current_bus_loc.setLatitude(currnet_bus_info.getLatitude());
                        current_bus_loc.setLongitude(currnet_bus_info.getLongtitude());
                        destination_bus_loc.setLatitude(destination_bus_info.getLatitude());
                        destination_bus_loc.setLongitude(destination_bus_info.getLongtitude());
                    }
                    catch (NullPointerException ne ) {
                        ne.printStackTrace();
                    }
                }
            };
            Timer timer4 = new Timer();
            timer4.schedule(timertask4, 3000);


            // 본격적인 안내 시작
            TimerTask timertask5 = new TimerTask() {
                @Override
                public void run() {
                    oDsayService.requestSearchPubTransPath(
                            String.valueOf(currnet_bus_info.getLongtitude()),
                            String.valueOf(currnet_bus_info.getLatitude()),
//                            "126.5536979",
//                            "37.2816875",
                            String.valueOf(destination_station_info.getLongtitude()),
                            String.valueOf(destination_station_info.getLatitude()),
//                            "127.015076",
//                            "37.26555",
                            "0",
                            "0",
                            "0",
                            currentOnResultCallbackListener
                    );


                    tMapData.findPathData(current_location_SKT, currnet_bus_info.gettMapPoint(), tMapPolyLine -> {
                        tMapPolyLine.setID("현위치 -> 현위치 근처 버스 정류장");
                        tMapView.addTMapPath(tMapPolyLine);
                    });
                }
            };
            Timer timer5 = new Timer();
            timer5.schedule(timertask5, 5000);


            // 선긋기
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
                    tMapView.removeTMapPath();
//                    tMapView.setCenterPoint(Double.parseDouble(startY.get(position)), Double.parseDouble(startX.get(position)), true);
                    TMapPoint starts = new TMapPoint(Double.parseDouble(startY.get(position)), Double.parseDouble(startX.get(position)));
                    TMapPoint ends = new TMapPoint(Double.parseDouble(endY.get(position)), Double.parseDouble(endX.get(position)));
                    tMapData.findPathData(starts, ends, tMapPolyLine -> {
                        tMapPolyLine.setID("긋는다.");
                        tMapView.addTMapPath(tMapPolyLine);
                    });



                }
            });
            // test



            tMapView.setCenterPoint(current_longtitude, current_latitude);
            skt_linear.addView(tMapView);
        }


//        tMapData.findPathData(current_location_SKT, destination_location_SKT, tMapPolyLine -> {
//            tMapPolyLine.setID("전체 긋기");
//            tMapView.addTMapPath(tMapPolyLine);
//        });

        // 나침반 모드 On
//        tMapView.setCompassMode(true);



        // 과제 : 현재 위치 구하기, 이프문을 통한 선긋기, 추천 마커 커스터마이징
        // current_location_Location(현위치), destination_location_Location(목적지), currnet_bus_info.TMapPoint(현위치 근처 버스), destination_bus_info.TMapPoint(목적지 근처 버스),
        // current_station_info.getTMapPoint()(현위치 근처 지하철)



    }

    // 추가
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            info_location.setLatitude(latitude);
            info_location.setLongitude(longitude);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


}
