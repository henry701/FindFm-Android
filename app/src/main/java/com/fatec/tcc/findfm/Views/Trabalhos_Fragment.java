package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.databinding.ActivityTrabalhosListaBinding;

public class Trabalhos_Fragment extends Fragment {

    private ProgressDialog dialog;
    private AppCompatActivity activity;
    private View view;
    private ActivityTrabalhosListaBinding binding;
    public Trabalhos_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public Trabalhos_Fragment(AppCompatActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_trabalhos_lista, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("NOTIFICACOES");
        super.onActivityCreated(savedInstanceState);
    }

    public void adicionar_trabalho_click(View v){

    }
}
