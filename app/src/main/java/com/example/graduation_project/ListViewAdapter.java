package com.example.graduation_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/*
    작성자 : 김평기
    작성일 : 2019-09-03
    제공 기능 : 모름
    관련 액티비티 : RecommendActivity
    외부 라이브러리 및 플러그인 :


    기능 설명 : RecommendActivity의 리스트뷰에 이미지, 문화재명, 주소가 담긴 리스트들을 생성하기 위함.

    상세정보 단계 :
        1.
 */

public class ListViewAdapter extends BaseAdapter {
    Bitmap bitmap;
    static int get_pos;
    private ArrayList<ListVO> listVO = new ArrayList<ListVO>() ;
    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listVO.size() ;
    }

    // ** 이 부분에서 리스트뷰에 데이터를 넣어줌 **
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //postion = ListView의 위치      /   첫번째면 position = 0
        final int pos = position;
        get_pos = pos;
        final Context context = parent.getContext();


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, parent, false);
        }

        final ImageView image = (ImageView) convertView.findViewById(R.id.list_img) ;
        final TextView title = (TextView) convertView.findViewById(R.id.list_title) ;
        final TextView Context = (TextView) convertView.findViewById(R.id.list_context) ;

        final TextView contentID = (TextView) convertView.findViewById(R.id.list_id);

        final ListVO listViewItem = listVO.get(position);



        // 아이템 내 각 위젯에 데이터 반영
        title.setText(listViewItem.getTitle());
        title.setSelected(true);
        Context.setSelected(true);
        Context.setText(listViewItem.getContext());
        final String str_url = listViewItem.getImg();

        contentID.setText(listViewItem.getContentId());

        Img_Thread img_thread = new Img_Thread();
        img_thread.start();

        try {
            img_thread.join();
            image.setImageBitmap(bitmap);
            // 인터넷에서 가져온 image를 bitmap으로 이미지뷰에다 채워넣음.


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            img_thread = null;
        }



        //리스트뷰 클릭 이벤트
        //여기에 3번째 리스트뷰 안의 리스트 클릭시, 리스트의 Text값이 다음 엑티비티로 넘어가게하는 메소드를 구현하면 됨.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, (pos+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, CompareActivity.class);
                // 4월 15일 추가본
                intent.putExtra("img", str_url);
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("addr", Context.getText().toString());
                intent.putExtra("id", contentID.getText().toString());
                // 4월 15일 추가본

//                RecommendActivity recommendActivity = (RecommendActivity) RecommendActivity.recommendActivity;
//
                context.startActivity(intent);
//
//                recommendActivity.finish();

            }
        });


        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return listVO.get(position) ;
    }

    // 데이터값 넣어줌
    public void addVO(String url, String title, String desc, String id) {
        ListVO item = new ListVO();

        item.setImg(url);
        item.setTitle(title);
        item.setContext(desc);
        item.setContentId(id);

        listVO.add(item);
    }

    public void clear() {
        listVO.clear();
    }

    class Img_Thread extends Thread {

        @Override
        public void run() {
            // 지정한 URL로부터 Bitmap 값을 가져오는 메소드입니다.
            try {
                final ListVO listViewItem = listVO.get(get_pos);
                String str_url = listViewItem.getImg();
                URL img_URL = new URL(str_url);//http://tong.visitkorea.or.kr/cms/resource/15/2371515_image3_1.jpg
                HttpURLConnection conn = (HttpURLConnection) img_URL.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            interrupt();

        }

    }


}
