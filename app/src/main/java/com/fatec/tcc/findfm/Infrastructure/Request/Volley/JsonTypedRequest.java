package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fatec.tcc.findfm.Utils.JsonUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonTypedRequest<TRequest, TResponse, TErrorResponse> extends AbstractTypedRequest<TResponse, TErrorResponse> {

    private final Class<TRequest> requestClass;

    public JsonTypedRequest(Activity view,
                            int method,
                            Class<TRequest> requestClass,
                            Class<TResponse> receiveClass,
                            Class<TErrorResponse> errorResponseClass,
                            String fullUrl,
                            TRequest requestBody,
                            Response.Listener<TResponse> responseListener,
                            Response.Listener<TErrorResponse> errorResponseListener,
                            Response.ErrorListener errorListener)
    {
        super(view,
              method,
              receiveClass,
              errorResponseClass,
              fullUrl,
              JsonUtils.GSON.toJson(requestBody).getBytes(StandardCharsets.UTF_8),
              responseListener,
              errorResponseListener,
              errorListener,
              "application/json; charset=utf-8");
        this.requestClass = requestClass;
    }

    @Override
    protected TResponse parseResponse(NetworkResponse networkResponse)
    {
        String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers,"UTF-8");
        String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
        Log.i("CHAMADAS TYPED JSON", "Dados recebidos: " + responseString);
        TResponse receivedObject = JsonUtils.GSON.fromJson(responseString, super.getReceiveClass());
        return receivedObject;
    }

    @Override
    protected TErrorResponse parseErrorResponse(NetworkResponse networkResponse)
    {
        String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers,"UTF-8");
        String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
        // Log.i("CHAMADAS TYPED JSON", "Dados recebidos (ERROR): " + responseString);
        TErrorResponse receivedObject = JsonUtils.GSON.fromJson(responseString, super.getErrorResponseClass());
        return receivedObject;
    }

    public void setRequest(TRequest requestObject)
    {
        super.setRequestBody(JsonUtils.GSON.toJson(super.getRequestBody()).getBytes(StandardCharsets.UTF_8));
    }

    public Class<TRequest> getRequestClass() {
        return requestClass;
    }
}
