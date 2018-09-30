package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
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
import com.fatec.tcc.findfm.Views.Adapters.AdapterFeed;
import com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios;
import com.fatec.tcc.findfm.databinding.ActivityHomeFragmentBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Home_Fragment extends Fragment {

    private ProgressDialog dialog;
    private TelaPrincipal activity;
    private ActivityHomeFragmentBinding binding;
    private View view;
    private Usuario usuario;

    private List<Post> postList;

    public Home_Fragment(){}

    @SuppressLint("ValidFragment")
    public Home_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activity.getSupportActionBar().setTitle("FindFM - Home");
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity, this, activity.getDialog()));

        binding.listaPosts.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaPosts.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("HOME");
        super.onActivityCreated(savedInstanceState);
        setFoto();
        if(FindFM.isLogado(getActivity())) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            lb_nomeUsuario.setText(FindFM.getNomeUsuario(getActivity()));
        }
    }

    private void setFoto(){
        byte[] image = FindFM.getFotoPrefBytes(getActivity());
        ImageView imagemUsuarioHeader = getActivity().findViewById(R.id.fotoPerfil);
        if(image != null && image.length != 0) {
            imagemUsuarioHeader.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else {
            imagemUsuarioHeader.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getActivity().getTheme()));
            Toast.makeText(getActivity().getApplicationContext(), "Você pode adicionar uma foto acessando o menu Meu Perfil ;)", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFeed( ) {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> getFeed = new JsonTypedRequest<>
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
                                if(!resp.get("postagens").isEmpty()) {
                                    for (Map<String, Object> post : resp.get("postagens")) {
                                        PostResponse postResponse = JsonUtils.jsonConvert(post, PostResponse.class);
                                        postList.add(new Post(postResponse)
                                        );
                                    }
                                    binding.textView4.setVisibility(View.GONE);
                                    binding.listaPosts.setAdapter(new AdapterFeed(postList, activity));
                                } else
                                {
                                    binding.textView4.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterMeusAnuncios(postList, activity));
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterMeusAnuncios(postList, activity));
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        postList = new ArrayList<>();

        getFeed.execute();
        activity.getDialog().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( !FindFM.getTelaAtual().equals("CRIAR_POST")) {
            getFeed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getFeed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
