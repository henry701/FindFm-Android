package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.FeedResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterFeed;
import com.fatec.tcc.findfm.databinding.ActivityMeusPostsFragmentBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AnunciosSugeridos_Fragment extends Fragment {

    private TelaPrincipal activity;
    private ActivityMeusPostsFragmentBinding binding;
    private List<Post> anunciosList;

    public AnunciosSugeridos_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public AnunciosSugeridos_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            activity.getSupportActionBar().setTitle("Anúncios Sugeridos para você");
        } catch (Exception e){
            e.printStackTrace();
        }
        
        activity.getOptionsMenu().getItem(1).setVisible(false);
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_meus_posts_fragment, container, false);
        binding.adicionarPost.setVisibility(View.GONE);
        binding.setPostViewModel(new PostViewModel(activity));
        binding.listaPosts.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaPosts.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( !FindFM.getTelaAtual().equals("CRIAR_POST")) {
            getAnuncios();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("ANUNCIOS_SUGERIDOS");
        super.onActivityCreated(savedInstanceState);
    }

    private void getAnuncios( ) {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> getAnuncios = new JsonTypedRequest<>
                (       activity,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(activity.getResources(),"feed"),
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                Map<String, Collection<Map<String, Object>>> resp = (Map<String, Collection<Map<String, Object>>>) response.getData();
                                if(!resp.get("anuncios").isEmpty()) {
                                    for (Map<String, Object> post : resp.get("anuncios")) {
                                        FeedResponse postResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                        anunciosList.add(new Post(postResponse)
                                        );
                                    }
                                    binding.textView4.setVisibility(View.GONE);
                                    binding.listaPosts.setAdapter(new AdapterFeed(anunciosList, activity, false));
                                } else
                                {
                                    binding.textView4.setText(R.string.nao_ha_anuncios);
                                    binding.textView4.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterFeed(anunciosList, activity, false));
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterFeed(anunciosList, activity, false));
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        anunciosList = new ArrayList<>();

        getAnuncios.execute();
        activity.getDialog().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getAnuncios();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
