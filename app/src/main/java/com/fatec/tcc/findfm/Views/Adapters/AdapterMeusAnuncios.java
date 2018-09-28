package com.fatec.tcc.findfm.Views.Adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewMeusAnunciosBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.ViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private TelaPrincipal activity;

    public AdapterMeusAnuncios() {
    }

    public AdapterMeusAnuncios(List<Post> posts, TelaPrincipal activity){
        this.posts = posts;
        this.activity = activity;
    }

    public List<Post> getPosts(){
        return new ArrayList<>(this.posts);
    }

    @Override
    public AdapterMeusAnuncios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewMeusAnunciosBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_meus_anuncios, parent, false);

        return new AdapterMeusAnuncios.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterMeusAnuncios.ViewHolder holder, int position) {
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

        public ViewMeusAnunciosBinding bindingVH;

        ViewHolder(ViewMeusAnunciosBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}