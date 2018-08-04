package com.fatec.tcc.findfm.Model.Http.Parsers;

import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.Request.Parser;

import org.json.JSONException;
import org.json.JSONObject;

public class InstrumentoParser implements Parser{

    public static Instrumento parse(JSONObject object) {

        try {
            return new Instrumento(
                    object.getJSONObject("data").getString("nome"),
                    NivelHabilidade.from(object.getJSONObject("data").getInt("nivelHabilidade"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return new Instrumento("",NivelHabilidade.INICIANTE);
        }
    }

}
