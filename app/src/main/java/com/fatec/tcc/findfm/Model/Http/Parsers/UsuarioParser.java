package com.fatec.tcc.findfm.Model.Http.Parsers;

import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Request.Parser;

import org.json.JSONException;
import org.json.JSONObject;

public class UsuarioParser implements Parser {

    public static Usuario parse(JSONObject object) {

        try {
            byte[] array = null;
            //String nomeUsuario, String senha, String email, String telefone, Byte[] foto, boolean confirmado, boolean premium
            return new Usuario(
                    object.getJSONObject("data").getString("usuario"),
                    object.getJSONObject("data").getString("senha"),
                    object.getJSONObject("data").getString("email"),
                    object.getJSONObject("data").getString("telefone"),
                    array,
                    object.getJSONObject("data").getBoolean("confirmado"),
                    object.getJSONObject("data").getBoolean("premium")

            );
        } catch (JSONException e) {
            e.printStackTrace();
            return new Usuario();
        }

    }

}
