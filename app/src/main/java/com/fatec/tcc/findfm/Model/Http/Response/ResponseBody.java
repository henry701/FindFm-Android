package com.fatec.tcc.findfm.Model.Http.Response;

/**
 * Representa o response body da requisição
 * **/
public class ResponseBody {

    /**
     * Se a requisição foi executa com sucesso ou não
     * **/
    private boolean success;

    /**
     * Representa responseCode da enum ResponseCode
     * **/
    private int responseCode;

    /**
     * Mensagem ao usuário
     * **/
    private String message;

    /**
     * Qualquer outros dados retornados pelo server
     * **/
    private Object data;


    public boolean isSuccess() {
        return success;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
