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
import android.widget.Spinner;
import android.widget.TextView;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.databinding.ActivityPerfilFragmentBinding;

public class Perfil_Fragment extends Fragment {

    public ActivityPerfilFragmentBinding binding;

    private ProgressDialog dialog;
    private Spinner cb_uf;
    private String UF;
    AppCompatActivity activity;

    public Perfil_Fragment(){}

    @SuppressLint("ValidFragment")
    public Perfil_Fragment(AppCompatActivity activity){
        this.activity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_perfil_fragment, container, false);
        binding.setViewModel(new PerfilViewModel());
        binding.executePendingBindings();
        //binding.getViewModel().init();
        FindFM.getInstance().getParams().putString("tela", "MEU_PERFIL");
        /*
        this.cb_uf = getActivity().findViewById(R.id.cb_uf);
        this.cb_uf.setAdapter(
                new ArrayAdapter<>(getActivity(), R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        this.cb_uf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UF = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.getInstance().getParams().putString("tela", "MEU_PERFIL");
        super.onActivityCreated(savedInstanceState);

        if(FindFM.isLogado(getActivity())) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            //lb_nomeUsuario.setText(usuario);
        }
    }

}
