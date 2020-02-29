package com.example.graduation_project;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapCompareActivity extends AppCompatActivity {
    Bitmap bitmap;
    String url;
    String contentid;
    String key = "IkguwPNvNewFWJqbZkLx%2F96u2iRVD59mPrSq8xm0YfbWa7qcWFR155mFpJsoWt9Ql2zR5SwwF1FVLbdSOWWw1A%3D%3D";
    // OpenAPI의 인증 키
    String app_name;
    Locale locale;

    private LinkedList<String> Title = new LinkedList<String>();
    private LinkedList<String> Overview = new LinkedList<String>();
    private LinkedList<String> Mapx = new LinkedList<String>();
    private LinkedList<String> Mapy = new LinkedList<String>();
    private LinkedList<String> Addr = new LinkedList<String>();
    private LinkedList<String> Img = new LinkedList<String>();

    TextView exp;
    TextView title;
    TextView addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_compare);

        title = findViewById(R.id.map_compare_title);
        addr = findViewById(R.id.map_compare_addr);
        exp = findViewById(R.id.map_compare_exp);
        title.setSelected(true);
        addr.setSelected(true);


        Resources res = getResources();
        app_name = res.getString(R.string.app_name);

        ImageView img = findViewById(R.id.map_compare_imageView);

        Button return_btn = findViewById(R.id.map_compare_return);
        Button map_btn = findViewById(R.id.map_compare_location);
        Button ar_btn = findViewById(R.id.map_compare_ar);


        Intent intent = getIntent();

        locale = this.getResources().getConfiguration().locale;
        contentid = intent.getStringExtra("id");
        System.out.println("ccc : " + contentid);

        Main_Method_Thread2 t2 = new Main_Method_Thread2();
        t2.setDaemon(true);
        t2.start();



        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                t2.interrupt();
                compare_Img_Thread img_thread = new compare_Img_Thread();
                img_thread.start();
                try {
                    img_thread.join();
                    img.setImageBitmap(bitmap);
                    // 인터넷에서 가져온 image를 bitmap으로 이미지뷰에다 채워넣음.
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    img_thread.interrupt();

                }
            }
        };



        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);


        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapCompareActivity.this, RecommendedLocationActivity.class);
                // 4월 15일 추가본

                intent.putExtra("mapx", Mapx.peek());
                intent.putExtra("mapy", Mapy.peek());
                intent.putExtra("title", title.getText().toString());
                System.out.println("클릭됨" + Mapx.peek() + "그리고 "+ Mapy.peek());
                // 4월 15일 추가본
                MapCompareActivity.this.startActivity(intent);
                finish();
            }
        });

        ar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentid.equals("125983") || contentid.equals("126207")
                        || contentid.equals("126510")  || contentid.equals("127425")
                        || contentid.equals("126512")  || contentid.equals("127421")
                        || contentid.equals("126216")

                ) {
                    Intent arintent = new Intent(MapCompareActivity.this, ARActivity.class);
                    arintent.putExtra("id", contentid);
                    arintent.putExtra("overview", exp.getText().toString());
                    MapCompareActivity.this.startActivity(arintent);
                }

                else {
                    Toast.makeText(getApplicationContext(), "아직 준비가 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    class compare_Img_Thread extends Thread {

        @Override
        public void run() {
            // 지정한 URL로부터 Bitmap 값을 가져오는 메소드입니다.
            try {
                String str_url = url;
                URL img_URL = new URL(str_url);//http://tong.visitkorea.or.kr/cms/resource/15/2371515_image3_1.jpg
                HttpURLConnection conn = (HttpURLConnection) img_URL.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

            }

            catch(Exception e){
                    e.printStackTrace();
                }

        }

    }

    // MM1
    private void Main_Method(String contentid) {
        try {
            Locale locale = this.getResources().getConfiguration().locale;
            // 어느 나라인지 확인.
            String queryURL = "";
            // 사용할 쿼리URL

            if (locale.getLanguage() == "ko") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="
                                +key
                                +"&contentId="
                                + contentid
                                +"&defaultYN=Y&overviewYN=Y&mapinfoYN=Y&addrinfoYN=Y&firstImageYN=Y&MobileOS=ETC&MobileApp="
                                +"종합과제테스트";

            }
            else if (locale.getLanguage() == "en") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/EngService/detailCommon?ServiceKey="
                                +key
                                +"&contentId="
                                + contentid
                                +"&defaultYN=Y&overviewYN=Y&mapinfoYN=Y&addrinfoYN=Y&firstImageYN=Y&MobileOS=ETC&MobileApp="
                                +"종합과제테스트";
            }
            else if (locale.getLanguage() == "ja") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/JpnService/detailCommon?ServiceKey="
                                +key
                                +"&contentId="
                                + contentid
                                +"&defaultYN=Y&overviewYN=Y&mapinfoYN=Y&addrinfoYN=Y&firstImageYN=Y&MobileOS=ETC&MobileApp="
                                +"종합과제테스트";
            }
            else if (locale.getLanguage() == "zh") {
                queryURL =
                        "http://api.visitkorea.or.kr/openapi/service/rest/ChsService/detailCommon?ServiceKey="
                                +key
                                +"&contentId="
                                + contentid
                                +"&defaultYN=Y&overviewYN=Y&mapinfoYN=Y&addrinfoYN=Y&firstImageYN=Y&MobileOS=ETC&MobileApp="
                                +"종합과제테스트";
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
            StringBuffer titleBuf = new StringBuffer();
            StringBuffer ovBuf = new StringBuffer();
            StringBuffer mapxBuf = new StringBuffer();
            StringBuffer mapyBuf = new StringBuffer();
            StringBuffer addrBuf = new StringBuffer();
            StringBuffer imgBuf = new StringBuffer();

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
                            System.out.println("title : "+titleBuf.toString());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Title.add(0, titleBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        }
                        else if (tag.equals("mapx")) {
                            xpp.next();
                            mapxBuf.setLength(0);
                            mapxBuf.append(xpp.getText());
                            System.out.println("mapx : "+mapxBuf.toString());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Mapx.add(0, mapxBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        }
                        else if (tag.equals("mapy")) {
                            xpp.next();
                            mapyBuf.setLength(0);
                            mapyBuf.append(xpp.getText());
                            System.out.println("mapy : "+mapyBuf.toString());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Mapy.add(0, mapyBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        }
                        else if (tag.equals("addr1")) {
                            xpp.next();
                            addrBuf.setLength(0);
                            addrBuf.append(xpp.getText());
                            System.out.println("addr1 : "+addrBuf.toString());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Addr.add(0, addrBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        }
                        else if (tag.equals("firstimage")) {
                            xpp.next();
                            imgBuf.setLength(0);
                            imgBuf.append(xpp.getText());
                            System.out.println("img : "+imgBuf.toString());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            Img.add(0, imgBuf.toString());
                            // 스트링버퍼값 링크드리스트에 올리기
                        }
                        else if (tag.equals("overview")) {
                            xpp.next();
                            ovBuf.setLength(0);
                            ovBuf.append(xpp.getText());
                            // xml에서 가져온 title 값을 스트링버퍼에 저장
                            System.out.println("overview : "+ovBuf.toString());
                            String ov_modify = ovBuf.toString();
                            ov_modify = ov_modify.replaceAll("<strong>", "").replaceAll("<br />", "\n").replaceAll("</strong>", "").replaceAll("<br>", "");
                            System.out.println("overview_modify : "+ ov_modify);
                            Overview.add(0, ov_modify);
                            // 스트링버퍼값 링크드리스트에 올리기
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
        }
         catch(Exception e){
                // 4월 15일 추가본
                System.out.println("193번 줄 익셉션 발생");
                // 4월 15일 추가본
                e.printStackTrace();
            }

    }

    // 메인메소드 실행용 쓰레드
    class Main_Method_Thread2 extends Thread {
        @Override
        public void run() {
            Main_Method(contentid);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // 4월 15일 추가본
                    interrupt(); // 쓰레드 종료
                    title.setText(Title.peek());
                    addr.setText(Addr.peek());
                    exp.setText(Overview.peek());
                    url = Img.peek();

                }
            });

        }
    }

}
