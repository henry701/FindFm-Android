package com.fatec.tcc.findfm.Utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * Classe para facilitar operações repetitivas referente à JSONs
 * **/
public class JsonUtils {

    public static final Gson GSON =  new GsonBuilder()
            .registerTypeAdapter(NivelHabilidade.class, new NivelHabilidadeTypeDeserializer())
            .registerTypeAdapter(NivelHabilidade.class, new NivelHabilidadeTypeSerializer())
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

    private static class NivelHabilidadeTypeDeserializer implements
            JsonDeserializer<NivelHabilidade>
    {
        @Override
        public NivelHabilidade deserialize(JsonElement json,
                                           Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException
        {
            int typeInt = json.getAsInt();
            return NivelHabilidade.from(typeInt);
        }
    }

    private static class NivelHabilidadeTypeSerializer implements
            JsonSerializer<NivelHabilidade>
    {

        @Override
        public JsonElement serialize(NivelHabilidade src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getCodigo());
        }
    }

}
