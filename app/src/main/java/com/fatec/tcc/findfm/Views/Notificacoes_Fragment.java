package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;

public class Notificacoes_Fragment extends Fragment {

    private ProgressDialog dialog;
    private AppCompatActivity activity;
    private View view;

    public Notificacoes_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public Notificacoes_Fragment(AppCompatActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("NOTIFICACOES");
        super.onActivityCreated(savedInstanceState);
    }
}
