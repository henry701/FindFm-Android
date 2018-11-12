package com.fatec.tcc.findfm.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Classe para facilitar operaç
 * **/
public class Util {

    /**
     * Abre nova activity
     * @param origem Context da aplicação
     * @param destino .Class da activity destino
     * **/
    public static void open_form(Context origem, Class destino) {
        Intent intent = new Intent(origem, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(origem, intent, null );
    }
    /**
     * Abre nova activity com parâmetros através de bundle
     * @param origem Context da aplicação
     * @param destino .Class da activity destino
     * @param paramBundle Bundle com chave valor dos parâmetros a serem passados para próxima tela
     * @param paramBundlePath path único na aplicação para acessar o bundle passado
     * **/
    public static void open_form_withParam(Context origem, Class destino, String paramBundlePath, Bundle paramBundle) {
        Intent intent = new Intent(origem, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(paramBundlePath, paramBundle);
        startActivity(origem, intent, null );
    }
    /**
     * Abre nova activity e não permite que seja possivel voltar à tela anterior pelo botão return
     * @param origem Context da aplicação
     * @param destino .Class da activity destino
     * **/
    public static void open_form__no_return(Context origem, Class destino) {
        Intent intent = new Intent(origem, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(origem, intent, null );
    }
    /**
     * Abre nova activity com parâmetros através de bundle e não permite que seja possivel voltar à tela anterior pelo botão return
     * @param origem Context da aplicação
     * @param destino .Class da activity destino
     * @param paramBundle Bundle com chave valor dos parâmetros a serem passados para próxima tela
     * @param paramBundlePath path único na aplicação para acessar o bundle passado
     * **/
    public static void open_form_withParam__no_return(Context origem, Class destino, String paramBundlePath, Bundle paramBundle) {
        Intent intent = new Intent(origem, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(paramBundlePath, paramBundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(origem, intent, null );
    }
    /**
     * Esconde o teclado
     * @param activity Activity atuaç
     * **/
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static Address getLocalizacao(Context context, double latitude, double longitude){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> locais;
        try {
            locais = geocoder.getFromLocation(latitude, longitude, 10);
            if (locais.size() > 0 ){
                for (Address local : locais){
                    if(local.getLocality() != null && local.getLocality().length() > 0){
                        return  local;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}