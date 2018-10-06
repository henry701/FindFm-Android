package com.fatec.tcc.findfm.Infrastructure.Request.Volley;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.Utils.FindFM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequestTypedRequest<TResponse, TErrorResponse> extends Request<NetworkResponse> implements Response.ErrorListener
{
    private final AbstractTypedRequest<TResponse, TErrorResponse> absReq;

    public VolleyRequestTypedRequest(int method, String fullUrl, AbstractTypedRequest absReq)
    {
        super(method, fullUrl, error -> absReq.encappedRequest.onErrorResponse(error));
        this.absReq = absReq;
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        Response<NetworkResponse> res = parseNetworkResponse(error.networkResponse);
        customErrorDelivery(res.result);
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse networkResponse)
    {
        if(networkResponse == null)
        {
            Log.e("[VOLEY REQUEST]", "NetworkResponse is null!!");
            throw new RuntimeException("NetworkResponse is null!");
        }
        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(networkResponse);
        return Response.success(networkResponse, cacheEntry);
    }

    @Override
    protected void deliverResponse(NetworkResponse response)
    {
        TResponse successResponse;
        try
        {
            successResponse = absReq.parseResponse(response);
        }
        catch(Exception e)
        {
            customErrorDelivery(response);
            return;
        }
        absReq.responseListener.onResponse(successResponse);
    }

    private void customErrorDelivery(NetworkResponse response)
    {
        TErrorResponse errorResponse;
        try
        {
            errorResponse = absReq.parseErrorResponse(response);
        }
        catch(Exception e2)
        {
            absReq.errorListener.onErrorResponse(new VolleyError(e2));
            return;
        }
        absReq.errorResponseListener.onResponse(errorResponse);
    }

    @Override
    public Map<String, String> getHeaders()
    {
        TokenData tokenData = null;

        try {
            tokenData = FindFM.getTokenData(absReq.getView());
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
        return absReq.getContentType();
    }

    @Override
    public byte[] getBody()
    {
        return absReq.requestBody;
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return absReq.errorListener;
    }
}
