package com.example.graduation_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();//intent로 넘어온값을 저장할수 있도록한다
        userID = intent.getStringExtra("userID");
        Locale locale = this.getResources().getConfiguration().locale;
        String strLang = locale.getLanguage();
//        String message = userID+"님 어서 오세요! ====사용언어===="+strLang;
        String message = userID+"님 어서 오세요!";
        final TextView textView_message = (TextView) findViewById(R.id.welcome);
        textView_message.setText(message);

        final Button btn_select_create = (Button) findViewById(R.id.btn_select_create);
        btn_select_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RecommendActivity.class);
                MenuActivity.this.startActivity(intent);
            }
        });
        // 추천 기능 실행시키기

        final Button btn_select_read = (Button) findViewById(R.id.btn_select_read);
        btn_select_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                MenuActivity.this.startActivity(intent);

            }
        });
        // 조회 기능 실행시키기

    }
}
