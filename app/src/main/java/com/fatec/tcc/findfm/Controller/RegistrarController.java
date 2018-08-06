package com.fatec.tcc.findfm.Controller;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Request.HttpRequest;
import com.fatec.tcc.findfm.Request.ServerCallBack;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;

import java.util.Date;
import java.util.List;

public class RegistrarController {

    private Context context;
    private Resources resources;

    public RegistrarController(Context context, Resources resources){
        this.resources = resources;
        this.context = context;
    }


    public void registrarMusico(Bundle param, List<Instrumento> instrumentos, Date nascimento, ServerCallBack callBack){
        Musico musico = new Musico(
                param.getString("nomeUsuario"),
                param.getString("senha"),
                param.getString("email"),
                param.getString("telefone"),
                param.getByteArray("foto"),
                false,
                false,
                param.getString("nomeCompleto"),
                nascimento,
                instrumentos
        );
        final String url = HttpUtils.buildUrl(this.resources,"metro_api/login/registrar");
        HttpRequest request = new HttpRequest(this.context);
        request.executeNewRequest(url, HttpMethod.POST, JsonUtils.toJsonObject(musico), callBack);
    }
}
