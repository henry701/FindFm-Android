package com.fatec.tcc.findfm.Utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Classe para facilitar operações repetitivas referente à JSONs
 * **/
public class JsonUtils {

    public static final Gson GSON = Converters.registerDateTime(new GsonBuilder())
            .registerTypeAdapter(NivelHabilidade.class, new NivelHabilidadeTypeDeserializer())
            .registerTypeAdapter(NivelHabilidade.class, new NivelHabilidadeTypeSerializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

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

    public static String createString(Object param)
    {
        try {
            return toJsonObject(param).toString(0);
        } catch (JSONException e) {
            Log.e("[JSON UTILS]", "Error while creating string from object " + param.toString(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonConvert(Object obj, Class<T> typeToken)
    {
        return jsonConvert((Map<String, Object>) obj, typeToken);
    }

    public static <T> T jsonConvert(Map<String, Object> obj, Class<T> typeToken)
    {
        return GSON.fromJson(GSON.toJson(obj), typeToken);
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

    /*
    String input = "2018-08-30T17:43:14.0689106-03:00";
    SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:s" );

    //this is zero time so we need to add that TZ indicator for
    if ( input.endsWith( "Z" ) ) {
        input = input.substring( 0, input.length() - 1) + "GMT-00:00";
    } else {
        int inset = 6;

        String s0 = input.substring( 0, input.length() - inset );
        String s1 = input.substring( input.length() - inset, input.length() );

        input = s0 + "GMT" + s1;
    }

    return df.parse( input );

     */


}
