package com.example.graduation_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    final static private String URL ="http://cafe5879.cafe24.com/UserRegister.php";

    private Map<String, String> parameters;

    public RegisterRequest(String email, String pw, String nation, String sex, String ages) {
        super(Method.POST, URL,null, null);
        parameters = new HashMap<>();
        parameters.put("email",email);
        parameters.put("pw",pw);
        parameters.put("nation",nation);
        parameters.put("sex",sex);
        parameters.put("ages",ages);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }


}
