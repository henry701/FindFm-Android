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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.PostResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios;
import com.fatec.tcc.findfm.databinding.ActivityMeusAnunciosFragmentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeusAnuncios_Fragment extends Fragment {

    private ProgressDialog dialog;
    private TelaPrincipal activity;
    private View view;

    private ActivityMeusAnunciosFragmentBinding binding;
    private List<Post> postList;

    public MeusAnuncios_Fragment(){
    }

    @SuppressLint("ValidFragment")
    public MeusAnuncios_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activity.getSupportActionBar().setTitle("Minhas Publicações");
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_meus_anuncios_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity, this, activity.getDialog()));
        binding.listaAnuncios.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaAnuncios.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );

        dialog = new ProgressDialog(activity);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

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
        FindFM.setTelaAtual("MEUS_ANUNCIOS");
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
                        HttpUtils.buildUrl(activity.getResources(),"post/author/" + FindFM.getUsuario().getId()),
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                if(((ArrayList) response.getData()).size() != 0) {
                                    for (Map<String, Object> retorno : (ArrayList<Map<String, Object>>) response.getData()) {
                                        PostResponse postResponse = JsonUtils.jsonConvert(retorno, PostResponse.class);
                                        postList.add(new Post(postResponse));
                                    }
                                    binding.textView4.setVisibility(View.GONE);
                                    binding.listaAnuncios.setAdapter(new AdapterMeusAnuncios(postList, activity));
                                } else
                                {
                                    binding.textView4.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaAnuncios.setAdapter(new AdapterMeusAnuncios(postList, activity));
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            activity.getDialog().hide();
                            binding.listaAnuncios.setAdapter(new AdapterMeusAnuncios(postList, activity));
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        postList = new ArrayList<>();

        SharedRequestQueue.addToRequestQueue(activity.getApplicationContext(), getPost);
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
