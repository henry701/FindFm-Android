package com.fatec.tcc.findfm.Controller.Posts;

import android.os.Bundle;
import android.view.View;

import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

public class PostViewModel {

    private TelaPrincipal view;

    public PostViewModel(TelaPrincipal v){
        this.view = v;
    }

    public void addButton(View view){
        String path = "CriarPost";
        Bundle param = new Bundle();
        param.putString("telaMode", "criando");
        Util.open_form_withParam(this.view, CriarPost.class, path, param);
    }


}
