package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MeusPosts_Fragment extends Fragment {

    private TelaPrincipal activity;
    private String tipo;
    private ActivityMeusPostsFragmentBinding binding;
    private List<Post> postList;

    public MeusPosts_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public MeusPosts_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            if (FindFM.getUsuario().getTipoUsuario().equals(TiposUsuario.CONTRATANTE)) {
                activity.getSupportActionBar().setTitle("Meus Anúncios");
            } else {
                activity.getSupportActionBar().setTitle("Minhas Publicações");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        tipo = FindFM.getUsuario().getTipoUsuario().equals(TiposUsuario.CONTRATANTE) ? "ad" : "post";
        activity.getOptionsMenu().getItem(1).setVisible(false);
        activity.getOptionsMenu().getItem(2).setVisible(false);
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_meus_posts_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        binding.listaPosts.addItemDecoration(itemDecorator);
        binding.listaPosts.setLayoutManager(new LinearLayoutManager(activity));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( !FindFM.getTelaAtual().equals("CRIAR_POST")) {
            getPost();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("MEUS_POSTS");
        super.onActivityCreated(savedInstanceState);
    }

    public void addButton(View view){
        Util.open_form(activity, CriarPost.class);
    }

    private void getPost( ) {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> getPost = new JsonTypedRequest<>
                (       activity,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(activity.getResources(),tipo, "author", FindFM.getUsuario().getId()),
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                if(((ArrayList) response.getData()).size() != 0) {
                                    for (Map<String, Object> retorno : (ArrayList<Map<String, Object>>) response.getData()) {
                                        FeedResponse feedResponse = JsonUtils.jsonConvert(retorno, FeedResponse.class);
                                        postList.add(new Post(feedResponse));
                                    }
                                    binding.textView4.setVisibility(View.GONE);
                                    binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, false));
                                } else
                                {
                                    binding.textView4.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, false));
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]MeusPost", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, false));
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]MeusPost", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );
        postList = new ArrayList<>();

        getPost.execute();
        activity.getDialog().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getPost();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
