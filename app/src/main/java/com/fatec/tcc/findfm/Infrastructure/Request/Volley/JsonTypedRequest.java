package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.JsonUtils;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonTypedRequest<TRequest, TResponse, TErrorResponse> extends com.android.volley.toolbox.JsonRequest<TResponse> {

    private final Class<TRequest> sendClass;
    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;
    private String jsonRequestBody;
    private final Activity view;
    private boolean resourceUpload = false;
    private String contentType;

    public JsonTypedRequest(Activity view,
                            int method,
                            Class<TRequest> sendClass,
                            Class<TResponse> receiveClass,
                            Class<TErrorResponse> errorResponseClass,
                            String fullUrl,
                            TRequest requestBody,
                            Response.Listener<TResponse> listener,
                            Response.ErrorListener errorListener)
    {
        super(method, fullUrl, JsonUtils.GSON.toJson(requestBody), listener, errorListener);
        this.sendClass = sendClass;
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
        this.jsonRequestBody = JsonUtils.GSON.toJson(requestBody);
        this.view = view;
        this.setRetryPolicy(new DefaultRetryPolicy(600000, 0, 1));
    }

    @Override
    protected Response<TResponse> parseNetworkResponse(NetworkResponse networkResponse) {
        String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers,"UTF-8");
        String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
        Log.i("[LOG CHAMADAS TYPED]", "Dados recebidos: " + responseString);
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(networkResponse);

        if(networkResponse.statusCode >= 200 && networkResponse.statusCode <= 299) {
            TResponse receivedObject = JsonUtils.GSON.fromJson(responseString, receiveClass);
            return Response.success(receivedObject, cacheEntry);
        }
        else {
            TErrorResponse receivedErrorObject = JsonUtils.GSON.fromJson(responseString, errorResponseClass);
            ErrorResponseException errorResponseException = new ErrorResponseException(receivedErrorObject);
            VolleyError volleyError = new VolleyError("Response returned error statusCode!", errorResponseException);
            return Response.error(volleyError);
        }
    }

    @Override
    public Map<String, String> getHeaders()
    {
        TokenData tokenData = null;

        try {
            tokenData = FindFM.getTokenData(view);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        if(tokenData == null)
        {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>(1, 1.0f);
        headers.put("Authorization", "Bearer " + tokenData.getAccessToken());

        return headers;
    }

    @Override
    public String getBodyContentType() {
        if(isResourceUpload())
            return getContentType();
        return String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    }

    public String getBodyAsJson(){
        return this.jsonRequestBody;
    }

    public boolean isResourceUpload() {
        return resourceUpload;
    }

    public void setResourceUpload(boolean resourceUpload) {
        this.resourceUpload = resourceUpload;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
