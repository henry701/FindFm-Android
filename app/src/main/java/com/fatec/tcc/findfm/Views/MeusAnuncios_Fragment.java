package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Model.Business.Anuncio;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios;

import java.util.Arrays;
import java.util.List;

public class MeusAnuncios_Fragment extends Fragment {

    private ProgressDialog dialog;
    private AppCompatActivity activity;
    private View view;

    public MeusAnuncios_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public MeusAnuncios_Fragment(AppCompatActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_meus_anuncios_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.getInstance().getParams().putString("tela", "MEUS_ANUNCIOS");
        super.onActivityCreated(savedInstanceState);

        ImagemUtils.setImagemHeader(activity);
        if(FindFM.isLogado(getActivity())) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            lb_nomeUsuario.setText(FindFM.getNomeUsuario(getActivity()));
        }

        updateList();
    }

    private void updateList() {
        //TODO: server retornar uma lista mais completa
        List<Anuncio> anuncios = Arrays.asList(
                new Anuncio("Ser humano procura banda de pagode para tocar heavy metal",
                        "Cansei de tocar pagode, quero metal, é nois", "São Paulo"),
                new Anuncio("Procuro bandas para tocarem no meu quiosque na praia",
                        "Toque aqui cara, pago bem", "São Vicente"),
                new Anuncio("Procuro guitarrista que toque bateria com os pés",
                        "Tocamos heavy metal japones em lingua árabe", "São Longe de muito longe")
               );

        RecyclerView rc = view.findViewById(R.id.listaAnuncios);
        rc.setAdapter( new AdapterMeusAnuncios(anuncios,activity) );
        RecyclerView.LayoutManager layout = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);
        rc.setLayoutManager( layout );
    }


}
