package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios;
import com.fatec.tcc.findfm.databinding.ActivityMeusAnunciosFragmentBinding;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MeusAnuncios_Fragment extends Fragment {

    private ProgressDialog dialog;
    private TelaPrincipal activity;
    private View view;


    public MeusAnuncios_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public MeusAnuncios_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        List<Post> posts = getAnuncios();

        ActivityMeusAnunciosFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.activity_meus_anuncios_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity, this, activity.getDialog()));
        binding.listaAnuncios.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaAnuncios.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );
        binding.listaAnuncios.setAdapter(new AdapterMeusAnuncios(posts, activity));

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("MEUS_ANUNCIOS");
        super.onActivityCreated(savedInstanceState);
    }

    private List<Post> getAnuncios(){
        return Arrays.asList(
                new Post()
                        .setTitulo("Ser humano procura banda de pagode para tocar heavy metal")
                        .setCidade("São Paulo")
                        .setDescricao("Cansei de tocar pagode, quero metal, é nois")
                        .setData(new Date())
                        .setAutor(new Usuario().setNomeCompleto("Robson Robervaldo")),
                new Post()
                        .setTitulo("Procuro bandas para tocarem no meu quiosque na praia")
                        .setCidade("São Vicente")
                        .setDescricao("Toque aqui cara, pago bem")
                        .setData(new Date())
                        .setAutor(new Usuario().setNomeCompleto("Robervaldo Gerson")),
                new Post()
                        .setTitulo("Procuro guitarrista que toque bateria com os pés")
                        .setCidade("São Longe de muito longe")
                        .setDescricao("Tocamos heavy metal japones em lingua árabe")
                        .setData(new Date())
                        .setAutor(new Usuario().setNomeCompleto("Robson Robson Robervaldo")),
                new Post()
                        .setTitulo("Maior Titulo dos titulos muito grande meu Deus que titulo!!!")
                        .setCidade("Maior nome de cidade que vc vera na vida meu amigo")
                        .setDescricao("Maior descrição do universo, muito detalhada estou praticamente contando minha vida inteira nessa descrição de tão grande que é muito obrigado por ler")
                        .setData(new Date())
                        .setAutor(new Usuario().setNomeCompleto("Robervaldo Robervaldo Robervaldo"))
        );
    }

    public void addButton(View view){
        Util.open_form(activity, CriarPost.class);
    }




}
