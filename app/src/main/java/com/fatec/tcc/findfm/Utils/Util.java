package com.fatec.tcc.findfm.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

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
}