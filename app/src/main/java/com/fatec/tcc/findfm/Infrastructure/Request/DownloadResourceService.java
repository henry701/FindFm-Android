package com.fatec.tcc.findfm.Infrastructure.Request;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.BinaryTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;

import java.util.Observable;

public class DownloadResourceService extends Observable {

    private Activity activity;

    public DownloadResourceService(Activity activity){
        this.activity = activity;
    }

    public void getResource(String id){
        BinaryTypedRequest<BinaryResponse, ErrorResponse> uploadResource = new BinaryTypedRequest<>(
                activity,
                HttpMethod.GET.getCodigo(),
                BinaryResponse.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(activity.getResources(),"resource/" + id),
                null,
                (BinaryResponse response) -> {

                    if(response.getData() != null) {
                        setChanged();
                        notifyObservers(response);
                    }
                },
                (ErrorResponse error) -> {
                    Log.e("[ERRO]Download Service", error.getMessage());
                    setChanged();
                    notifyObservers(null);
                },
                (VolleyError error) -> {
                    Log.e("[ERRO]Download Service", error.getMessage());
                    setChanged();
                    notifyObservers(null);
                },
                null
        );
        uploadResource.execute();
    }

}
