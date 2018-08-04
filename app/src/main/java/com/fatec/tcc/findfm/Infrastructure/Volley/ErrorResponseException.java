package com.fatec.tcc.findfm.Infrastructure.Volley;

public class ErrorResponseException extends Exception {

    private final Object errorResponse;

    public ErrorResponseException(Object errorResponse) {
        this.errorResponse = errorResponse;
    }

    public Object getErrorResponse() {
        return errorResponse;
    }
}
