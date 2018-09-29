package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.app.Activity;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.Utils.FindFM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTypedRequest<TResponse, TErrorResponse>
{
    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;
    private final Activity view;
    private final String contentType;

    final Response.Listener<TResponse> responseListener;
    final Response.Listener<TErrorResponse> errorResponseListener;
    final Response.ErrorListener errorListener;

    byte[] requestBody;

    final VolleyRequestTypedRequest<TResponse, TErrorResponse> encappedRequest;

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
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
        this.requestBody = requestBody;
        this.responseListener = responseListener;
        this.errorListener = errorListener;
        this.errorResponseListener = errorResponseListener;
        this.view = view;
        this.contentType = contentType;

        encappedRequest = new VolleyRequestTypedRequest<TResponse, TErrorResponse>(method, fullUrl, this);
        encappedRequest.setRetryPolicy(new DefaultRetryPolicy(600000, 0, 1));
    }

    protected abstract TResponse parseResponse(NetworkResponse response);

    protected abstract TErrorResponse parseErrorResponse(NetworkResponse response);

    public void execute()
    {
        SharedRequestQueue.addToRequestQueue(this.view, encappedRequest);
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

    public byte[] getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }
}
