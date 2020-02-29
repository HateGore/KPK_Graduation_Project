package com.example.graduation_project;

import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MakeIdRequest extends StringRequest {

    final static private String URL ="http://cafe5879.cafe24.com/MakeEmail.php";

    private Map<String, String> parameters;

    public MakeIdRequest(String email) {
        super(Method.POST, URL, null, null);
        parameters = new HashMap<>();
        parameters.put("email",email);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }

}
