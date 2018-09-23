package com.fatec.tcc.findfm.Controller.Posts;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

public class PostViewModel {

    private TelaPrincipal view;
    private Fragment fragment;
    private ProgressDialog dialog;

    public PostViewModel(TelaPrincipal v, Fragment fragment, ProgressDialog dialog){
        this.view = v;
        this.fragment = fragment;
        this.dialog = dialog;
    }

    public void addButton(View view){
        String path = "CriarPost";
        Bundle param = new Bundle();
        param.putString("telaMode", "criando");
        Util.open_form_withParam(this.view, CriarPost.class, path, param);
    }


}
