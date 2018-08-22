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
     * Representa code da enum ResponseCode
     * **/
    private int code;

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

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
