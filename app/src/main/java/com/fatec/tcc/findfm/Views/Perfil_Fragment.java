package com.fatec.tcc.findfm.Views;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.R;

import static android.content.Context.MODE_PRIVATE;

public class Perfil_Fragment extends Fragment {

    private ProgressDialog dialog;
    private Spinner cb_uf;
    private String UF;
    View perfil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        perfil = inflater.inflate(R.layout.activity_perfil__fragment, container, false);
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
        return perfil;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.getInstance().getParams().putString("tela", "HOME");
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FindFM_param", MODE_PRIVATE);
        boolean isLogado = sharedPreferences.getBoolean("isLogado", false);
        String usuario = sharedPreferences.getString("username","Visitante");
        if(isLogado) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            //lb_nomeUsuario.setText(usuario);
        }
    }

}
