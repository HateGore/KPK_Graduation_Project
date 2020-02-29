// userStyle 제거함


package com.example.graduation_project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView registerButton = (TextView) findViewById(R.id.registerButton);

        //회원가입버튼
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AgreeActivity.class);
                LoginActivity.this.startActivity(intent);


            }
        });

        //로그인버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    //내부클래스로서 인터넷에 접속한 뒤에 response가 건너오면 response를 저장할수있도록한다
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {

                                String userID = jsonResponse.getString("email");

                                Toast.makeText(LoginActivity.this, userID + "님 접속하셨습니다", Toast.LENGTH_SHORT).show();

                               Intent re = new Intent(LoginActivity.this, MapActivity.class);
                               re.putExtra("email", userID);


                               LoginActivity.this.startActivity(re);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인에 실패하였습니다").setNegativeButton("다시 시도", null).create().show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                //각 3개 매개변수로 삼고 response받아오기
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                //volley인터넷 접속해서 request전송하고 response를 받아온다
                queue.add(loginRequest);
            }

        });
    }
}


