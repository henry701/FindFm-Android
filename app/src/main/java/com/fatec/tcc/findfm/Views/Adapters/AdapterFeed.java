package com.fatec.tcc.findfm.Views.Adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Http.Request.ComentarRequest;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewFeedBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.ViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private TelaPrincipal activity;

    public AdapterFeed() {
    }

    public AdapterFeed(List<Post> posts, TelaPrincipal activity){
        this.posts = posts;
        this.activity = activity;
    }

    public List<Post> getPosts(){
        return new ArrayList<>(this.posts);
    }

    @Override
    public AdapterFeed.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewFeedBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_feed, parent, false);

        return new AdapterFeed.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterFeed.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bindingVH.setPost(post);

        for(String id : post.getIdFotos()){

            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            post.setFotoBytes(dados);
                            holder.bindingVH.fotoPublicacao.setImageBitmap(ext_pic);
                            holder.bindingVH.fotoPublicacao.setVisibility(View.VISIBLE);
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { }).create().show();
                        }

                        activity.getDialog().hide();

                    });
                }
            });
            downloadService.getResource(id);
            activity.getDialog().show();
        }

        for(String id : post.getIdVideos()){
            Uri uri = Uri.parse(HttpUtils.buildUrl(activity.getResources(),"resource/" + id));
            MediaController m = new MediaController(activity);
            holder.bindingVH.videoView.setMediaController(m);
            m.setAnchorView(holder.bindingVH.videoView);
            holder.bindingVH.videoView.setVideoURI(uri);
            holder.bindingVH.videoView.setVisibility(View.VISIBLE);
            holder.bindingVH.videoView.seekTo(100);
        }

        if(post.getAutor().getFotoID() != null){

            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            holder.bindingVH.fotoPerfil.setImageBitmap(ext_pic);
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { }).create().show();
                        }

                        activity.getDialog().hide();

                    });
                }
            });
            downloadService.getResource(post.getAutor().getFotoID());
            activity.getDialog().show();
        }

        holder.bindingVH.btnLike.setOnClickListener(
                v -> {
                    if (!post.getLikesId().contains(FindFM.getUsuario().getId())) {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.GET.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "post/like/" + post.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        postRequest.setRequest(null);
                        postRequest.execute();
                        post.setLikes(post.getLikes() + 1);
                        holder.bindingVH.btnLike.setText(R.string.curti);
                    }else {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> postRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.DELETE.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "post/like/" + post.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        postRequest.setRequest(null);
                        postRequest.execute();
                        post.setLikes(post.getLikes() + 1);
                        holder.bindingVH.btnLike.setText(R.string.curtir);
                    }
                }
        );

        holder.bindingVH.setClickListener(v -> {
            if(post.getAutor().getId().equals(FindFM.getUsuario().getId())){
                String path = "CriarPost";
                Bundle param = new Bundle();
                FindFM.getMap().put("post", post);
                param.putString("telaMode", "editavel");
                Util.open_form_withParam(activity, CriarPost.class, path, param);
            } else {
                String path = "CriarPost";
                Bundle param = new Bundle();
                FindFM.getMap().put("post", post);
                param.putString("telaMode", "visualizar");
                Util.open_form_withParam(activity, CriarPost.class, path, param);
            }
        });

        if(post.getLikesId().contains(FindFM.getUsuario().getId())){
            holder.bindingVH.btnLike.setText(R.string.curti);
        }else {
            holder.bindingVH.btnLike.setText(R.string.curtir);
        }

    }

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    public void setAnuncios(List<Post> posts, TelaPrincipal activity){
        this.posts = posts;
        this.activity = activity;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewFeedBinding bindingVH;

        ViewHolder(ViewFeedBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}