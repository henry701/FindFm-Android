package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Model.Business.Anuncio;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios;
import com.fatec.tcc.findfm.databinding.ActivityMeusAnunciosFragmentBinding;

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
        List<Anuncio> anuncios = getAnuncios();

        ActivityMeusAnunciosFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.activity_meus_anuncios_fragment, container, false);

        binding.listaAnuncios.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaAnuncios.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );
        binding.listaAnuncios.setAdapter(new AdapterMeusAnuncios(anuncios, activity));

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("MEUS_ANUNCIOS");
        super.onActivityCreated(savedInstanceState);
    }

    private List<Anuncio> getAnuncios(){
        return Arrays.asList(
                new Anuncio("Ser humano procura banda de pagode para tocar heavy metal",
                        "Cansei de tocar pagode, quero metal, é nois", "São Paulo"),
                new Anuncio("Procuro bandas para tocarem no meu quiosque na praia",
                        "Toque aqui cara, pago bem", "São Vicente"),
                new Anuncio("Procuro guitarrista que toque bateria com os pés",
                        "Tocamos heavy metal japones em lingua árabe", "São Longe de muito longe"),
                new Anuncio("Maior Titulo dos titulos muito grande meu Deus que titulo!!!",
                        "Maior descrição do universo, muito detalhada estou praticamente contando minha vida inteira nessa descrição de tão grande que é muito obrigado por ler",
                        "Maior nome de cidade que vc vera na vida meu amigo")
        );
    }



}
