package com.example.graduation_project;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


// 1. 버튼을 < > 를 만들어두고 < 를 누르면 이전 페이지(int number = 1; 가 -- 되게, 이때 if문을 써서 number == 1 그러면 Toast.makeText를 띄워서 이전 페이지가 없습니다.).
// > 를 누르면 number++, 끝에 페이지를 넘으면 익셉션이 뜨거나 Null 이 나올 텐데 try catch 로 잡아서 다음 페이지가 없습니다라는 메시지를 듸우면 된다.
// 2. 만들어진 number는 api에 주석달린것처럼 집어넣으면 됨.
// 3. 리스트를 클릭하면 좌표값이 넘어가게 ---가장 중요---
// 4. 좌표버튼을 누르면 mapActivity로 넘어가게.


public class RecommendActivity extends AppCompatActivity {
    /*
    본 엑티비티는 메뉴 엑티비티에서 [어디로 갈까요?] 버튼을 누르면 실행되는 엑티비티이다.
    구현하는 기능은
    1.  에딧텍스트에 한국어로 지역명을 입력하면 OpenAPI로 데이터를 끌어와 호출하는 기능 (= Main_Method_01, 이하 MM1)
    2.  위치정보를 이용해 x,y값을 가져오고, 이를 토대로 주변의 관광 데이터를 끌어와 호출하는 기능 (= Main_Method_03, 이하 MM2)
    이 있다.
     */
    private ListView listview;
    // 목록을 보일 리스트뷰
    private ListViewAdapter adapter;
    // 커스텀 어댑터를 상속할 어뎁터 객체
    private LinkedList<String> Title = new LinkedList<String>();
    // OpenAPI의 xml로부터 제목(=title)값을 받아올 링크드리스트
    // 링크드리스트에 대한 설명은 이 URL(https://m.blog.naver.com/PostView.nhn?blogId=highkrs&logNo=220443469613&proxyReferer=https%3A%2F%2Fwww.google.com%2F)을 참고할 것.
    private LinkedList<String> Addr = new LinkedList<String>();
    // OpenAPI의 xml로부터 주소(=addr1)값을 받아올 링크드리스트
    private LinkedList<String> img = new LinkedList<String>();
    // OpenAPI의 xml로부터 제목(=title)값을 받아올 링크드리스트
    private LinkedList<String> id = new LinkedList<String>();

    public static AppCompatActivity recommendActivity;


    //0508 추가
    private Spinner area_spinner;
    private Spinner sigungu_spinner;
    ArrayList<String> areaList;
    ArrayAdapter<String> areaAdapter;
    ArrayList<String> sigunguList;
    ArrayAdapter<String> sigunguAdapter;
    int areacode;
    int pageNo = 1;
    int totalCount = 0;
    //0508 추가

    //0509 추가
    Double latitude;
    Double longitude;
    //0509 추가

    EditText search_edit;
    // 지역 이름 등을 입력하는 에딧 텍스트.
    // 레이아웃 최상단에 위치해있다.
    String key = "IkguwPNvNewFWJqbZkLx%2F96u2iRVD59mPrSq8xm0YfbWa7qcWFR155mFpJsoWt9Ql2zR5SwwF1FVLbdSOWWw1A%3D%3D";
    // OpenAPI의 인증 키
    String edit_getText;
    // 설명이 필요한가?
    FloatingActionButton search_btn;
    // MM1,MM2 발동시키는 버튼
    FloatingActionButton location_btn;
    // MM3 발동시키는 버튼

    FloatingActionButton prev_btn;
    // 정수 5월 15일 추가. < 버튼
    FloatingActionButton next_btn;
    // > 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        // 변수 설정

        recommendActivity = RecommendActivity.this;
        adapter = new ListViewAdapter();
        listview = (ListView) findViewById(R.id.recom_Listview);
        search_btn = (FloatingActionButton) findViewById(R.id.recom_search_btn);
        location_btn = (FloatingActionButton) findViewById(R.id.recom_location_btn);


        //정수 5월 15일 추가. <> 버튼
        prev_btn = (FloatingActionButton) findViewById(R.id.prev_btn);
        next_btn = (FloatingActionButton) findViewById(R.id.next_btn);


        //0508 spinner 설정
        area_spinner = (Spinner) findViewById(R.id.area_code_spinner);
        Resources res = getResources();
        final String[] area_string_array =
                {"",
                        res.getString(R.string.SEOUL), res.getString(R.string.INCHEON),
                        res.getString(R.string.DAEJEON), res.getString(R.string.DAEGU),
                        res.getString(R.string.GWANGJU), res.getString(R.string.BUSAN),
                        res.getString(R.string.ULSAN), res.getString(R.string.SEJONG),
                        res.getString(R.string.GYEONGGI), res.getString(R.string.GANGWON),
                        res.getString(R.string.CHUNGBUK), res.getString(R.string.CHUNGNAM),
                        res.getString(R.string.KYONGBUK), res.getString(R.string.KYONGNAM),
                        res.getString(R.string.JEONBUK), res.getString(R.string.JEONNAM),
                        res.getString(R.string.JEJU)
                };

        areaList = new ArrayList<>();
        for (String s : area_string_array) {
            areaList.add(s);
        }


        areaAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.simple_spinner, areaList);


        area_spinner.setAdapter(areaAdapter);


        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageNo = 1;
                areacode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 어뎁터 할당
        listview.setAdapter(adapter);

        // MM1 실행 버튼
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areacode > 8) {
                    areacode += 22;
                }
                Toast.makeText(getApplicationContext(), "" + areacode + "!!!", Toast.LENGTH_LONG).show();
                final Main_Method_Thread thread = new Main_Method_Thread();
                thread.setDaemon(true);
                thread.start();
            }
        });

        // MM2 실행 버튼
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecommendActivity.this, MapActivity.class);
                RecommendActivity.this.startActivity(intent);


            }
        });

        // 정수 5월 15일 추가. 버튼 추가.  <
        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo--;
                if (pageNo == 0) {
                    Toast.makeText(getApplicationContext(), "이전 페이지가 없습니다.", Toast.LENGTH_LONG).show();
                    pageNo++;
                }
                else {
                    final Main_Method_Thread thread = new Main_Method_Thread();
                    thread.setDaemon(true);
                    thread.start();
                }


            }

        });


        // 정수 5월 15일 추가. 버튼 추가.  <
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo++;
                System.out.println("List size = " + totalCount/7);
                if (pageNo > totalCount/7) {
                    Toast.makeText(RecommendActivity.this, "마지막 페이지 입니다.",Toast.LENGTH_SHORT).show();
                    pageNo--;
                }
                else {
                    final Main_Method_Thread thread = new Main_Method_Thread();
                    thread.setDaemon(true);
                    thread.start();
                }
            }});

    }

    // MM1
    private void Main_Method(int areacode) {
        try {
            Locale locale = this.getResources().getConfiguration().locale;
            // 어느 나라인지 확인.
            String queryURL = "";
            // 사용할 쿼리URL

            if (locale.getLanguage() == "ko") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="
                                + key
                                + "&contentTypeId=&areaCode="
                                + areacode
                                + "&sigunguCode=&cat1=A02&cat2=A0201&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=P&numOfRows=7"
                                + "&pageNo="
                                + pageNo;
            }
            else if (locale.getLanguage() == "en") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/EngService/areaBasedList?ServiceKey="
                                + key
                                + "&contentTypeId=&areaCode="
                                + areacode
                                + "&sigunguCode=&cat1=A02&cat2=A0201&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=P&numOfRows=7"
                                + "&pageNo="
                                + pageNo;
            }
            else if (locale.getLanguage() == "ja") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/JpnService/areaBasedList?ServiceKey="
                                + key
                                + "&contentTypeId=&areaCode="
                                + areacode
                                + "&sigunguCode=&cat1=A02&cat2=A0201&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=P&numOfRows=7"
                                + "&pageNo="
                                + pageNo;
            }
            else if (locale.getLanguage() == "zh") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/ChsService/areaBasedList?ServiceKey="
                                + key
                                + "&contentTypeId=&areaCode="
                                + areacode
                                + "&sigunguCode=&cat1=A02&cat2=A0201&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=P&numOfRows=7"
                                + "&pageNo="
                                + pageNo;
            }
            URL url = new URL(queryURL);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // XmlPullParserfactory.newInstance()를 호출해 XmlPullParserFactory의 인스턴스를 취득한 후
            XmlPullParser xpp = factory.newPullParser();
            // 같은 인스턴스의 newPullParser()를 호출함.
            xpp.setInput(new InputStreamReader(is, "UTF-8"));
            //inputstream 으로부터 xml 입력받기

            String tag;
            StringBuffer titleBuf = new StringBuffer(); // title 값 저장할 스트링버퍼
            StringBuffer addrBuf = new StringBuffer(); // addr1 값 저장할 스트링버퍼
            StringBuffer imgBuf = new StringBuffer(); // img 값 저장할 스트링버퍼
            StringBuffer totalCountBuf = new StringBuffer();
            StringBuffer idBuf = new StringBuffer();



            // 4월 15일 추가본
            Title.clear();
            Addr.clear();
            img.clear();
            // 4월 15일 추가본

            id.clear();

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        if (tag.equals("item")) ;// 첫번째 검색결과
                        else if (tag.equals("title")) {
                            xpp.next();
                            titleBuf.setLength(0);
                            titleBuf.append(xpp.getText());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Title.add(0, titleBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        } else if (tag.equals("addr1")) {
                            xpp.next();
                            addrBuf.setLength(0);
                            addrBuf.append(xpp.getText());
                            Addr.add(0, addrBuf.toString());
                        } else if (tag.equals("firstimage")) {
                            xpp.next();
                            imgBuf.setLength(0);
                            imgBuf.append(xpp.getText());
                            img.add(0, imgBuf.toString());
                        } else if (tag.equals("contentid")) {
                            xpp.next();
                            idBuf.setLength(0);
                            idBuf.append(xpp.getText());
                            id.add(0, idBuf.toString());
                        }
                        else {
                        }
                        if (tag.equals("totalCount")) {
                            if (totalCount == 0) {
                                xpp.next();
                                totalCountBuf.setLength(0);
                                totalCountBuf.append(xpp.getText());
                                totalCount = Integer.parseInt(totalCountBuf.toString());
                            }
                        }


                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        break;
                }

                eventType = xpp.next();
            }
        } catch (Exception e) {
            // 4월 15일 추가본
            System.out.println("193번 줄 익셉션 발생");
            // 4월 15일 추가본
            e.printStackTrace();
        }
    }

    // 4월 8일 추가본 GPSListener 정의
    private class GPSListener implements LocationListener {

        // 위치 정보 확인시 자동 호출되는 메소드
        @Override
        public void onLocationChanged(Location location) {
            try {

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            // 위도, 경도값 받기
            String msg = "Latitude : " + latitude + "\nLongitude:" + longitude + "여긴가";
            Geocoder geocoder = new Geocoder(RecommendActivity.this);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            List<Address> list = null;
            list = geocoder.getFromLocation(latitude, longitude, 10);
            if (list != null) {
                if (list.size() == 0) {
                    search_edit.setText("해당 주소 없음");
                } else {
                    System.out.println("리스트 사이즈 = " + list.size());
                    int i_size = list.size();
                    for (int i = 0; i < i_size; i++) {
                        System.out.println("리스트 겟" + i + " = " + list.get(i));
                    }
                    System.out.println("리스트 사이즈 = " + list.size());
                    Address address = list.get(0);
//                    Toast.makeText(getApplicationContext(), "latitude = " + address.getLatitude() + "\nlongitude = " + address.getLongitude() + "Tlqkdkdkdk", Toast.LENGTH_LONG).show();

//                    setGPS(address.getLatitude(), address.getLongitude());

                }

            }
        }
        catch(IOException io)
        {
            io.printStackTrace();
            Log.e("IOException", "퍼미션 오류 - 퍼미션 여부를 체크하세요.");
            System.out.println("퍼미션 오류 - 퍼미션 여부를 체크하세요.");
        }
    }



        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    // 메인메소드 실행용 쓰레드
    class Main_Method_Thread extends Thread {
        @Override
        public void run() {
            Main_Method(areacode);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // 4월 15일 추가본
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    // 4월 15일 추가본

                    int img_size = img.size();
                    for (int i = 0; i < img_size; i++) {

                        adapter.addVO(img.pollLast(), Title.pollLast(), Addr.pollLast(), id.pollLast());
                        // 속도 개선 최적화
                        if (i == img.size() - 1) {
                            // i 가 끝에 도달하면 쓰레드를 종료
                            System.out.println("쓰레드 사망 + img.size() = " + img.size());
                        }
                        // 4월 15일 추가본
                        listview.setAdapter(adapter);
                        // 4월 15일 추가본
                    }
                    interrupt(); // 쓰레드 종료
                }
            });

        }
    }


}
