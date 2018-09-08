package com.fatec.tcc.findfm.Infrastructure.Request;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Utils.HttpUtils;

public class ImageRequest extends com.android.volley.toolbox.ImageRequest {

    private Context context;

    public ImageRequest(Context context, String id_imagem, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener) {
        super(HttpUtils.buildUrl(context.getResources(), "resource/" + id_imagem), listener, maxWidth, maxHeight, scaleType, null, errorListener);
        this.context = context;
    }

    public void execute() {
        Log.i("[LOG CHAMADAS IMAGEM]", "Iniciando chamada!");
        Log.i("[LOG CHAMADAS IMAGEM]", "URL: " + this.getUrl());
        SharedRequestQueue.addToRequestQueue(context, this);
    }
}
