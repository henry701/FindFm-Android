package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
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
import com.fatec.tcc.findfm.databinding.ActivityFeedFragmentBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Feed_Fragment extends Fragment {

    private TelaPrincipal activity;
    private ActivityFeedFragmentBinding binding;
    private boolean isVisitante;
    private List<Post> postList;

    public Feed_Fragment(){}

    @SuppressLint("ValidFragment")
    public Feed_Fragment(TelaPrincipal activity, boolean isVisitante){
        this.activity = activity;
        this.isVisitante = isVisitante;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            activity.getSupportActionBar().setTitle("FindFM - Home");
            activity.getOptionsMenu().getItem(3).setVisible(true);
        } catch (Exception e){
            e.printStackTrace();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_feed_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity));

        binding.listaPosts.setLayoutManager(new LinearLayoutManager(activity));
        binding.listaPosts.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        );
        byte[] imagePerfil = FindFM.getFotoPrefBytes(getActivity());
        if(imagePerfil != null) {
            binding.fotoPerfil.setImageBitmap(BitmapFactory.decodeByteArray(imagePerfil, 0, imagePerfil.length));
        }
        else {
            binding.fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, activity.getTheme()));
        }
        if(isVisitante)
            binding.adicionarPost.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("HOME");
        super.onActivityCreated(savedInstanceState);
        setFoto();
        TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
        lb_nomeUsuario.setText(FindFM.getUsuario().getNomeCompleto());

    }

    private void setFoto(){
        byte[] image = FindFM.getFotoPrefBytes(getActivity());
        ImageView imagemUsuarioHeader = getActivity().findViewById(R.id.fotoPerfil);
        if(image != null && image.length != 0) {
            imagemUsuarioHeader.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else {
            imagemUsuarioHeader.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getActivity().getTheme()));
            if(!TiposUsuario.VISITANTE.equals(FindFM.getTipoUsuario(activity))) {
                Toast.makeText(getActivity().getApplicationContext(), "Você pode adicionar uma foto acessando o menu Meu Perfil ;)", Toast.LENGTH_SHORT).show();
            }
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
                                //Intercalar posts e anuncios
                                if(!resp.get("postagens").isEmpty()) {
                                    if(!resp.get("anuncios").isEmpty()) {

                                        int postSize = resp.get("postagens").size();
                                        int adSize  = resp.get("anuncios").size();
                                        int adIndex = 0;

                                        List<Map<String, Object>> posts = (List<Map<String, Object>>) resp.get("postagens");
                                        List<Map<String, Object>> ads   = (List<Map<String, Object>>) resp.get("anuncios");

                                        for (Map<String, Object> post : posts) {
                                            FeedResponse feedResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                            postList.add(new Post(feedResponse));
                                            postSize--;
                                            if(postSize%2 == 0){
                                                if(adIndex < adSize) {
                                                    post = ads.get(adIndex);
                                                    feedResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                                    postList.add(new Post(feedResponse));
                                                    adIndex++;
                                                }
                                            }
                                        }

                                        if(adIndex < adSize){
                                            for(; adIndex < adSize; adIndex ++){
                                                Map<String, Object> post = ads.get(adIndex);
                                                FeedResponse feedResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                                postList.add(new Post(feedResponse));
                                            }
                                        }
                                    } else {
                                        for (Map<String, Object> post : resp.get("postagens")) {
                                            FeedResponse feedResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                            postList.add(new Post(feedResponse)
                                            );
                                        }
                                    }

                                    binding.textView4.setVisibility(View.GONE);
                                    binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, isVisitante));
                                } else
                                {
                                    if(!resp.get("anuncios").isEmpty()) {
                                        for (Map<String, Object> post : resp.get("anuncios")) {
                                            FeedResponse feedResponse = JsonUtils.jsonConvert(post, FeedResponse.class);
                                            postList.add(new Post(feedResponse)
                                            );
                                            binding.textView4.setVisibility(View.GONE);
                                            binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, isVisitante));
                                        }
                                    }
                                    else {
                                        binding.textView4.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, isVisitante));
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            activity.getDialog().dismiss();
                            binding.listaPosts.setAdapter(new AdapterFeed(postList, activity, isVisitante));
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
        if( !FindFM.getTelaAtual().equals("CRIAR_POST") && !FindFM.getTelaAtual().equals("VIDEO")) {
            getFeed();
        }
        if(FindFM.getMap().containsKey("USUARIO_BUSCA")){
            Usuario usuario = (Usuario) FindFM.getMap().get("USUARIO_BUSCA");
            activity.getFragmentManager().beginTransaction().replace(R.id.frame_content, new Perfil_Fragment(activity, HttpUtils.buildUrl(getResources(),"account", usuario.getId())))
                    .commit();
            FindFM.getMap().remove("USUARIO_BUSCA");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getFeed();
                return true;
            case R.id.action_busca:
                try {
                    activity.getOptionsMenu().getItem(2).setVisible(true);
                } catch (Exception e){

                }
                String path = "BuscaUsuario";
                Bundle param = new Bundle();
                param.putString("origem", "perfil");
                Util.open_form_withParam(activity, SearchUsuario.class, path, param);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
