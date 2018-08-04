package com.fatec.tcc.findfm.Request;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Volley.ErrorResponseException;
import com.fatec.tcc.findfm.Infrastructure.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Volley.SharedRequestQueue;

import java9.util.function.Consumer;

public class HttpTypedRequest<TRequest, TResponse, TErrorResponse> {

    private final int method;

    private final Class<TRequest> requestClass;
    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;

    private final Consumer<TResponse> onSuccess;
    private final Consumer<TErrorResponse> onBusinessError;
    private final Consumer<Exception> onCriticalError;

    private String fullUrl;
    private TRequest requestObject;

    private JsonTypedRequest<TRequest, TResponse, TErrorResponse> request;

    public HttpTypedRequest(int method,
                            Class<TRequest> requestClass,
                            Class<TResponse> receiveClass,
                            Class<TErrorResponse> errorResponseClass,
                            Consumer<TResponse> onSuccess,
                            Consumer<TErrorResponse> onBusinessError,
                            Consumer<Exception> onCriticalError)
    {
        this.method = method;
        this.requestClass = requestClass;
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
        this.onSuccess = onSuccess;
        this.onBusinessError = onBusinessError;
        this.onCriticalError = onCriticalError;

        initRequest();
    }

    private void initRequest() {
        request = new JsonTypedRequest<>
        (
            method,
            requestClass,
            receiveClass,
            errorResponseClass,
            fullUrl,
            requestObject,
            (TResponse response) ->
            {
                Log.i("[LOG CHAMADAS TYPED]", "Success!");
                onSuccess.accept(response);
            },
            (VolleyError error) ->
            {
                Throwable innerException = error.getCause();
                if(innerException instanceof ErrorResponseException)
                {
                    ErrorResponseException errorResponseException = (ErrorResponseException) innerException;
                    TErrorResponse errorResponse = (TErrorResponse) errorResponseException.getErrorResponse();
                    Log.w("[LOG CHAMADAS TYPED]", "Error Business");
                    onBusinessError.accept(errorResponse);
                }
                else
                {
                    Log.e("[LOG CHAMADAS TYPED]", "Error Critical");
                    onCriticalError.accept(error);
                }
            }
        );
    }

    public void execute(Context context) {
        RequestQueue queue = SharedRequestQueue.getRequestQueue(context);
        queue.add(request);
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public TRequest getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(TRequest requestObject) {
        this.requestObject = requestObject;
    }
}
