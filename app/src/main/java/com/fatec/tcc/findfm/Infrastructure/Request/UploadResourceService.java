package com.fatec.tcc.findfm.Infrastructure.Request;

import android.app.Activity;

import com.fatec.tcc.findfm.Model.Http.Request.UploadResourceRequest;
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
        HttpTypedRequest<UploadResourceRequest, ResponseBody, ErrorResponse> uploadResource = new HttpTypedRequest<>(
                activity,
                HttpMethod.PUT.getCodigo(),
                UploadResourceRequest.class,
                ResponseBody.class,
                ErrorResponse.class,
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
                (Exception error) -> {
                    String retorno = isFoto ? "foto" : "video";
                    setChanged();
                    notifyObservers(retorno);
                }
        );

        UploadResourceRequest param = new UploadResourceRequest();
        param.setDados(dados);

        uploadResource.setFullUrl(HttpUtils.buildUrl(activity.getResources(),"upload"));
        uploadResource.setRequestObject(param);
        uploadResource.setResourceUpload(true);
        uploadResource.setContentType(contentType);
        uploadResource.execute(activity);
    }

}
