package com.example.graduation_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    final static private String URL ="http://cafe5879.cafe24.com/UserLogin.php";
    private Map<String, String> parameters;

    public LoginRequest(String email, String pw, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("email",email);
        parameters.put("pw",pw);

        //LoginRequest.class는 Login.php파일의 id,password를 매개변수로 보낸뒤에 결과값을 가져오는 역할을 수행함
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}



