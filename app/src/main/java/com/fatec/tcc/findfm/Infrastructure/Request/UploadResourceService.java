package com.fatec.tcc.findfm.Infrastructure.Request;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.BinaryTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;

import java.util.Observable;

public class UploadResourceService extends Observable{

    private Activity activity;

    public UploadResourceService(Activity activity){
        this.activity = activity;
    }

    public void uploadFiles(byte[] dados, String contentType, boolean isFoto) {
        BinaryTypedRequest<ResponseBody, ErrorResponse> uploadResource = new BinaryTypedRequest<>(
                activity,
                HttpMethod.PUT.getCodigo(),
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(activity.getResources(),"upload"),
                dados,
                (ResponseBody response) -> {
                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                        String retorno = isFoto ? "foto," : "video,";
                        retorno += (String) response.getData();
                        setChanged();
                        notifyObservers(retorno);
                    }
                },
                (ErrorResponse error) -> {
                    String retorno = isFoto ? "foto" : "video";
                    setChanged();
                    notifyObservers(retorno);
                },
                (VolleyError error) -> {
                    String retorno = isFoto ? "foto" : "video";
                    setChanged();
                    notifyObservers(retorno);
                },
                contentType
        );
        SharedRequestQueue.addToRequestQueue(activity.getApplicationContext(), uploadResource);
    }

}
