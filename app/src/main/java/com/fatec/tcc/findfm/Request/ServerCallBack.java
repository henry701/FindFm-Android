package com.fatec.tcc.findfm.Request;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Interface para respostas das requisições reescrever os dois métodos
 * **/
public interface ServerCallBack {

    void onSucess(JSONObject result);
    void onError(VolleyError error);

}
