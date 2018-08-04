package com.fatec.tcc.findfm.Controller;

import android.content.Context;
import android.content.res.Resources;

import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Request.HttpRequest;
import com.fatec.tcc.findfm.Request.ServerCallBack;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;

public class LoginController {

    private Context context;
    private Resources resources;

    public LoginController(Context context, Resources resources){
        this.resources = resources;
        this.context = context;
    }

    public void logar(Usuario usuario, ServerCallBack callBack){
        //final String url = "http://httpbin.org/get?param1=hello";
        final String url = HttpUtils.buildUrl(this.resources,"metro_api/login/logar");
        //final String url = HttpUtils.buildUrl(this.resources,"login");
        HttpRequest request = new HttpRequest(this.context);
        request.executeNewRequest(url, HttpMethod.POST, JsonUtils.toJsonObject(usuario), callBack);
    }


}
