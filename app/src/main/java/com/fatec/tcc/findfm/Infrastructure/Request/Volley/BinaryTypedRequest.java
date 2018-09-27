package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.app.Activity;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Utils.JsonUtils;

import java.nio.charset.Charset;

public class BinaryTypedRequest<TResponse, TErrorResponse> extends AbstractTypedRequest<TResponse, TErrorResponse> {

    public BinaryTypedRequest(Activity view,
                              int method,
                              Class<TResponse> receiveClass,
                              Class<TErrorResponse> errorResponseClass,
                              String fullUrl,
                              byte[] requestBody,
                              Response.Listener<TResponse> responseListener,
                              Response.Listener<TErrorResponse> errorResponseListener,
                              Response.ErrorListener errorListener,
                              String contentType)
    {
        super(view,
              method,
              receiveClass,
              errorResponseClass,
              fullUrl,
              requestBody,
              responseListener,
              errorResponseListener,
              errorListener,
              contentType);
    }

    @Override
    protected TResponse parseResponse(NetworkResponse networkResponse)
    {
        if(super.getReceiveClass().getSimpleName().equals("BinaryResponse")){
            Log.i("CHAMADAS TYPED BINARY", "Binário recebido: " + networkResponse.data);
            TResponse receivedObject = (TResponse) new BinaryResponse().setData(networkResponse.data);
            return receivedObject;
        }else {
            String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers, "UTF-8");
            String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
            Log.i("CHAMADAS TYPED BINARY", "Dados recebidos: " + responseString);
            TResponse receivedObject = JsonUtils.GSON.fromJson(responseString, super.getReceiveClass());
            return receivedObject;
        }
    }

    @Override
    protected TErrorResponse parseErrorResponse(NetworkResponse networkResponse)
    {
        String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers,"UTF-8");
        String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
        Log.e("CHAMADAS TYPED JSON", "Dados recebidos (ERROR): " + responseString);
        TErrorResponse receivedObject = JsonUtils.GSON.fromJson(responseString, super.getErrorResponseClass());
        return receivedObject;
    }

    public void setRequest(byte[] requestObject)
    {
        super.setRequestBody(requestObject);
    }
}
