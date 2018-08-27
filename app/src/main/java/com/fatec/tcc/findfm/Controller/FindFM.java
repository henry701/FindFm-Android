package com.fatec.tcc.findfm.Controller;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;

public class FindFM extends Application {

    private Bundle params;
    private static FindFM singleInstance = null;

    private static TokenData tokenData;

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
    }

    public Bundle getParams() {
        if (params == null) {
            params = new Bundle();
        }
        return params;
    }
    public static FindFM getInstance()
    {
        return singleInstance;
    }

    public void setParams(Bundle bundle){
        this.params = bundle;
    }

    public static TokenData getTokenData() {
        return tokenData;
    }

    public static void setTokenData(TokenData tokenData) {
        FindFM.tokenData = tokenData;
    }

    public static boolean isLogado(Activity view){
        return view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getBoolean("isLogado", false);
    }

    public static String getUserName(Activity view){
        return view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getString("username","Visitante");
    }

    public static void logarUsuario(Activity view, TiposUsuario tipoUsuario, String nomeUsuario){
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putBoolean("isLogado", true);
        editor.putString("tipoUsuario", tipoUsuario.getTexto());
        editor.putString("nomeUsuario", nomeUsuario);
        editor.apply();
    }

    public static void logoutUsuario(Activity view){
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putBoolean("isLogado", false);
        editor.putString("tipoUsuario", TiposUsuario.INDEFINIDO.getTexto());
        editor.putString("nomeUsuario", null);
        editor.apply();
    }
}
