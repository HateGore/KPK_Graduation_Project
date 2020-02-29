package com.example.graduation_project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter1, adapter2;
    private Spinner nationSpinner, agesSpinner;
    private AlertDialog dialog;
    private boolean validate = false;
    String userStyle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //리스트spinner설정
        nationSpinner = (Spinner) findViewById(R.id.nationSpinner);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.nationList, android.R.layout.simple_dropdown_item_1line);
        nationSpinner.setAdapter(adapter1);

        agesSpinner = (Spinner) findViewById(R.id.agesSpinner);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.agesList, android.R.layout.simple_dropdown_item_1line);
        agesSpinner.setAdapter(adapter2);

        //단순text
        EditText email2 = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.pw);

        //라디오 설정(남,녀)
        RadioGroup userGroup = (RadioGroup) findViewById(R.id.sexGroup);
        int GroupID = userGroup.getCheckedRadioButtonId();
        userStyle = ((RadioButton) findViewById(GroupID)).getText().toString();//초기화 값을 지정해줌

        //라디오버튼이 눌리면 값을 바꿔주는 부분
        userGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton genderButton = (RadioButton) findViewById(i);
                userStyle = genderButton.getText().toString();
                Toast.makeText(RegisterActivity.this, userStyle, Toast.LENGTH_SHORT).show();
            }
        });

        //회원가입시 이메일이 사용가능한지 검증하는 부분
        Button validateButton = (Button) findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email2.getText().toString();

                if (validate) {
                    return;//검증 완료
                }
                //ID 값을 입력하지 않았다면
                if (email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일을 입력해주세요")
                            .setPositiveButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }


                //검증시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {//사용할 수 있는 이메일라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이 이메일을 사용할 수 있습니다")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();
                                //email.setEnabled(false);//아이디값을 바꿀 수 없도록 함
                                validate = true;//검증완료


                            } else {//사용할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이미 존재한 이메일입니다")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };//Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                ValidateRequest validateRequest = new ValidateRequest(email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);


            }
        });

        //회원가입버튼 눌렀을때
        Button registerButton3 = (Button) findViewById(R.id.registerButton);
        registerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email3 = email2.getText().toString();
                String password3 = password.getText().toString();
                String agesSpinner3 = agesSpinner.getSelectedItem().toString();
                String nationSpinner3 = nationSpinner.getSelectedItem().toString();


                //회원가입 시작


                Toast.makeText(RegisterActivity.this, "회원가입완료", Toast.LENGTH_SHORT).show();
                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                        RegisterRequest registerRequest = new RegisterRequest(email3, password3, nationSpinner3,userStyle, agesSpinner3);
                        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                        queue.add(registerRequest);


                finish();//액티비티를 종료시킴(회원등록 창을 닫음)

            }
        });
    }
}
