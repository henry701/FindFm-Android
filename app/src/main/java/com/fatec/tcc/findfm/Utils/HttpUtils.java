package com.fatec.tcc.findfm.Utils;

import android.content.res.Resources;
import android.text.TextUtils;

import com.fatec.tcc.findfm.R;

/**
 * Classe para facilitar operações repetitivas referente à requisições
 * **/
public class HttpUtils {

    /**
     * Constrói URL da requisição
     * @param res Resourses da Activity, use getResources()
     * @param components String com o caminho relativo para requisição
     * **/
    public static String buildUrl(Resources res, String... components) {
        return res.getString(R.string.base_url) + "/" + TextUtils.join("/", components);
    }
}
