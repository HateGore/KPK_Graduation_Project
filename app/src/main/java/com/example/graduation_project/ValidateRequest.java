package com.example.graduation_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    final static private String URL ="http://cafe5879.cafe24.com/emailValidateRequest.php";

    private Map<String, String> parameters;

    public ValidateRequest(String email2, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("email",email2);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
