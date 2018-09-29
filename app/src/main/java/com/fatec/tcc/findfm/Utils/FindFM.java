package com.fatec.tcc.findfm.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;

import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Date;
import java.util.HashMap;

public class FindFM extends Application {

    private Bundle params;
    private HashMap<String, Object> map;
    private static FindFM singleInstance = null;
    private static Usuario usuario;
    private static Musico musico;
    private static Contratante contratante;
    private static TokenData tokenData;

    public static void setUsuario(Usuario usuario) {
        FindFM.usuario = usuario;
    }

    public static void setMusico(Musico musico) {
        FindFM.musico = musico;
    }

    public static void setContratante(Contratante contratante) {
        FindFM.contratante = contratante;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        singleInstance = this;
    }

    public static Usuario getUsuario(){
        return FindFM.getInstance().usuario;
    }

    public static Musico getMusico(){
        return FindFM.getInstance().musico;
    }

    public static Contratante getContratante(){
        return FindFM.getInstance().contratante;
    }

    private Bundle getParams() {
        if (params == null) {
            params = new Bundle();
        }
        return params;
    }

    private HashMap<String, Object> getMapp(){
        if(map == null){
            map = new HashMap<>();
        }
        return map;
    }

    private static FindFM getInstance()
    {
        return singleInstance;
    }

//--------------------------------------------------------------------------------------------------
    /**
     * Token Data
     * */

    public static TokenData getTokenData(Activity view) throws ClassCastException {
        SharedPreferences pref = view.getSharedPreferences("FindFM_param", MODE_PRIVATE);
        TokenData tokenData = new TokenData();
        tokenData.setAccessToken(pref.getString("tokenData", ""));
        tokenData.setCreated( new Date( pref.getLong("tokenDateCre", 0) ) );
        tokenData.setExpiration( new Date( pref.getLong("tokenDateExp", 0) ) );
        return tokenData;
    }

    public static void setTokenData(Activity view, TokenData tokenData) {
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putString("tokenData", tokenData.getAccessToken());
        editor.putLong("tokenDateExp", tokenData.getExpiration().getTime());
        editor.putLong("tokenDateCre", tokenData.getCreated().getTime());
        editor.apply();
        FindFM.tokenData = tokenData;
    }

//--------------------------------------------------------------------------------------------------
    /**
     * Login / Logout
     * */

    public static boolean isLogado(Activity view){
        return view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getBoolean("isLogado", false);
    }

    public static void logarUsuario(Activity view, TiposUsuario tipoUsuario, String nomeUsuario){
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putBoolean("isLogado", true);
        editor.putString("tipoUsuario", tipoUsuario.getTexto());
        editor.putString("nomeUsuario", nomeUsuario);
        editor.apply();
    }

    public static void setFotoPref(Activity view, String fotoBase64){
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putString("foto", fotoBase64);
        editor.apply();
    }

    public static void logoutUsuario(Activity view){
        SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
        editor.putBoolean("isLogado", false);
        editor.putString("tipoUsuario", TiposUsuario.INDEFINIDO.getTexto());
        editor.putString("nomeUsuario", null);
        editor.putString("foto", null);
        editor.apply();
    }
//--------------------------------------------------------------------------------------------------
    /**
     * Referente ao usuário: Imagem Perfil / Nome do Usuário
     * */
    public static byte[] getImagemPerfilBytes(){
        String image64 = FindFM.getInstance().getParams().getString("foto", "");
        return Base64.decode(image64, Base64.DEFAULT);
    }

    public static void setImagemPerfilParams(String foto_base64){
        FindFM.getInstance().getParams().putString("foto", foto_base64);
    }

    public static String getImagemPerfilBase64(){
        return FindFM.getInstance().getParams().getString("foto", "");
    }

    public static String getNomeUsuario(Activity view){
        return view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getString("nomeUsuario","Visitante");
    }

    public static String getFotoPrefBase64(Activity view){
        return view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getString("foto","");
    }

    public static byte[] getFotoPrefBytes(Activity view){
        String image64 = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).getString("foto","");
        return Base64.decode(image64, Base64.DEFAULT);
    }

    public static TiposUsuario getTipoUsuario(Activity view){
        return TiposUsuario.fromTexto(
                view.getSharedPreferences("FindFM_param", MODE_PRIVATE)
                        .getString("tipoUsuario","Indefinido")
        );
    }

//--------------------------------------------------------------------------------------------------
    /**
     * Tela atual
     * */
    public static String getTelaAtual(){
        return FindFM.getInstance().getParams().getString("tela", "");
    }

    public static void setTelaAtual(String telaAtual){
        FindFM.getInstance().getParams().putString("tela", telaAtual);
    }
//--------------------------------------------------------------------------------------------------

    public static HashMap<String, Object> getMap(){
        return FindFM.getInstance().getMapp();
    }

}
