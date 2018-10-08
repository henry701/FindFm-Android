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

import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Audio_Fragment;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewItemMeusPostsBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterMeusPosts extends RecyclerView.Adapter<AdapterMeusPosts.ViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private TelaPrincipal activity;

    public AdapterMeusPosts() {
    }

    public AdapterMeusPosts(List<Post> posts, TelaPrincipal activity){
        this.posts = posts;
        this.activity = activity;
    }

    public List<Post> getPosts(){
        return new ArrayList<>(this.posts);
    }

    @Override
    public AdapterMeusPosts.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewItemMeusPostsBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_item_meus_posts, parent, false);

        return new AdapterMeusPosts.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterMeusPosts.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bindingVH.setPost(post);

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

        for(String id : post.getIdFotos()){
            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input = new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            post.setFotoBytes(dados);
                            holder.bindingVH.fotoPublicacao2.setImageBitmap(ext_pic);
                            holder.bindingVH.fotoPublicacao2.setVisibility(View.VISIBLE);
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
            holder.bindingVH.videoView2.setMediaController(m);
            m.setAnchorView(holder.bindingVH.videoView2);
            holder.bindingVH.videoView2.setVideoURI(uri);
            holder.bindingVH.videoView2.setVisibility(View.VISIBLE);
            holder.bindingVH.videoView2.seekTo(100);
        }

        if(post.getIdAudio() != null) {
            try {
                Uri uri = Uri.parse(HttpUtils.buildUrl(activity.getResources(), "resource/" + post.getIdAudio()));
                holder.bindingVH.frameAudio.setVisibility(View.VISIBLE);
                activity.getFragmentManager().beginTransaction().replace(R.id.frame_audio,
                        new Audio_Fragment(activity, uri))
                        .commit();
            } catch (Exception e){
                e.printStackTrace();
                AlertDialogUtils.newSimpleDialog__OneButton(activity,
                        "Ops!", R.drawable.ic_error,
                        "Não foi possível obter a música." +
                                "\nVerifique sua conexão com a Internet e tente novamente","OK",
                        (dialog, id1) -> { }).create().show();
            }
        }

        holder.bindingVH.setClickListener(v -> {
            String path = "CriarPost";
            Bundle param = new Bundle();
            FindFM.getMap().put("post", post);
            param.putString("telaMode", "editavel");
            Util.open_form_withParam(activity, CriarPost.class, path, param);
        });
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

        public ViewItemMeusPostsBinding bindingVH;

        ViewHolder(ViewItemMeusPostsBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}