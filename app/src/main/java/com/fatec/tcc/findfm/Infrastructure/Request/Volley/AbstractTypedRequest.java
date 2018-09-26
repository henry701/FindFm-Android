package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.app.Activity;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.Utils.FindFM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTypedRequest<TResponse, TErrorResponse> extends com.android.volley.Request<NetworkResponse> {

    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;
    private final Activity view;
    private final String contentType;

    private final Response.Listener<TResponse> responseListener;
    private final Response.Listener<TErrorResponse> errorResponseListener;
    private final Response.ErrorListener errorListener;

    private byte[] requestBody;

    public AbstractTypedRequest(Activity view,
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
        super(method, fullUrl, errorListener);
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
        this.requestBody = requestBody;
        this.responseListener = responseListener;
        this.errorListener = errorListener;
        this.errorResponseListener = errorResponseListener;
        this.view = view;
        this.contentType = contentType;
        this.setRetryPolicy(new DefaultRetryPolicy(600000, 0, 1));
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse networkResponse)
    {
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(networkResponse);
        return Response.success(networkResponse, cacheEntry);
    }

    @Override
    protected void deliverResponse(NetworkResponse response)
    {
        TResponse successResponse;
        try
        {
            successResponse = parseResponse(response);
        }
        catch(Exception e)
        {
            TErrorResponse errorResponse;
            try
            {
                errorResponse = parseErrorResponse(response);
            }
            catch(Exception e2)
            {
                this.errorListener.onErrorResponse(new VolleyError(e2));
                return;
            }
            this.errorResponseListener.onResponse(errorResponse);
            return;
        }
        this.responseListener.onResponse(successResponse);
    }

    protected abstract TResponse parseResponse(NetworkResponse response);

    protected abstract TErrorResponse parseErrorResponse(NetworkResponse response);

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
    public String getBodyContentType()
    {
        return getContentType();
    }

    @Override
    public byte[] getBody()
    {
        return this.requestBody;
    }

    ////// Get/set below ////////

    public Class<TResponse> getReceiveClass() {
        return receiveClass;
    }

    public Class<TErrorResponse> getErrorResponseClass() {
        return errorResponseClass;
    }

    public Activity getView() {
        return view;
    }

    public String getContentType() {
        return contentType;
    }

    public Response.Listener<TResponse> getResponseListener() {
        return responseListener;
    }

    public Response.Listener<TErrorResponse> getErrorResponseListener() {
        return errorResponseListener;
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }
}
