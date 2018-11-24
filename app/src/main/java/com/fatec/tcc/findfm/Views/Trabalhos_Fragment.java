package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterTrabalhos;
import com.fatec.tcc.findfm.databinding.ActivityTrabalhosListaBinding;

public class Trabalhos_Fragment extends Fragment {

    private ProgressDialog dialog;
    private TelaPrincipal activity;
    private View view;
    private ActivityTrabalhosListaBinding binding;
    //TODO: adapter de trabalhos
    public Trabalhos_Fragment(){}

    @SuppressLint("ValidFragment")
    public Trabalhos_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_trabalhos_lista, container, false);
        try {
            activity.getSupportActionBar().setTitle("Meus Trabalhos");
            activity.getOptionsMenu().getItem(1).setVisible(false);
            activity.getOptionsMenu().getItem(2).setVisible(false);
        } catch (Exception e){
            e.printStackTrace();
        }
        binding.adicionarTrabalho.setOnClickListener(this.adicionar_trabalho_click());

        binding.listaTrabalhos.setAdapter( new AdapterTrabalhos(FindFM.getMusico().getTrabalhos(), activity, true));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("TRABALHOS");
        super.onActivityCreated(savedInstanceState);
    }

    public View.OnClickListener adicionar_trabalho_click(){
        return view -> Util.open_form(activity, CriarTrabalho.class);
    }
}
