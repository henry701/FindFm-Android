package com.fatec.tcc.findfm.Request;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.fatec.tcc.findfm.Infrastructure.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Utils.HttpMethod;

import org.json.JSONObject;

/**
 * Classe para facilitar execução de requisições
 * **/
public class HttpRequest {

    private Context context;
    private JsonObjectRequest jsonObjectRequest;

    public HttpRequest(Context context) {
        this.context = context;
    }

    /**
     * Cria e executa chamada com método selecionado e parâmetros, a resposta será no ServerCallBack
     * @param url URL completa para a requisição
     * @param httpMethod Método HTTP da requisição, usar enumerada HttpMethod
     * @param JSONObjectParam JSONObject que será passa com a requisição
     * @param callBack ServerCallBack que será executado quando a requisição terminar
     * **/
    public void executeNewRequest(String url, HttpMethod httpMethod, JSONObject JSONObjectParam, ServerCallBack callBack){

        this.jsonObjectRequest = new JsonObjectRequest(httpMethod.getCodigo(), url, JSONObjectParam,
                response -> {
                    Log.d("[LOG CHAMADAS]", "Resposta recebida -> " + response.toString());
                    callBack.onSucess(response);
                },
                error -> {
                    Log.d("[LOG CHAMADAS]",  "Erro recebido -> " + error.getMessage());
                    callBack.onError(error);
                }
        );

        this.execute();
    }

    /**
     * Adiciona a requisição na RequestQueue para ser executada
     * **/
    private HttpRequest execute() {
        Log.d("[LOG CHAMADAS]", "Criando chamada " + HttpMethod.from(this.jsonObjectRequest.getMethod()) + " para :" + this.jsonObjectRequest.getUrl());
        SharedRequestQueue.getInstance(this.context).addToRequestQueue(this.jsonObjectRequest);
        return this;
    }

}
