package com.fatec.tcc.findfm.Utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

/**
 * Classe para facilitar operações repetitivas referente à JSONs
 * **/
public class JsonUtils {

    public static final Gson GSON =  new GsonBuilder()
            .setDateFormat("yyyy-MM-dd").create();

    /**
     * Transforma um Object em JSONObject
     * @param param Objeto que será convertido
     * **/
    @Nullable
    public static JSONObject toJsonObject(Object param) {

        try {
            return new JSONObject(GSON.toJson(param));
        } catch (JSONException e) {
            Log.d("[LOG JSON UTILS]", "Erro ao converter objeto para JSON Object");
            e.printStackTrace();
            return null;
        }
    }

}
