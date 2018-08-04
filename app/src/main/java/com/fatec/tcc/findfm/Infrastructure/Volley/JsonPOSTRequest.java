package com.fatec.tcc.findfm.Infrastructure.Volley;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fatec.tcc.findfm.Utils.JsonUtils;

import java.nio.charset.Charset;

public class JsonPOSTRequest<TRequest, TResponse, TErrorResponse> extends JsonRequest<TResponse> {

    private final Class<TRequest> sendClass;
    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;

    public JsonPOSTRequest(Class<TRequest> sendClass,
                           Class<TResponse> receiveClass,
                           Class<TErrorResponse> errorResponseClass,
                           String fullUrl,
                           TRequest requestBody,
                           Response.Listener<TResponse> listener,
                           Response.ErrorListener errorListener)
    {
        super(Method.GET, fullUrl, JsonUtils.GSON.toJson(requestBody), listener, errorListener);
        this.sendClass = sendClass;
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
    }

    @Override
    protected Response<TResponse> parseNetworkResponse(NetworkResponse networkResponse) {
        String responseCharset = HttpHeaderParser.parseCharset(networkResponse.headers,"UTF-8");
        String responseString = new String(networkResponse.data, Charset.forName(responseCharset));
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
}
