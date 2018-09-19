package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Model.Business.Post;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.CriarPost;
import com.fatec.tcc.findfm.databinding.ViewMeusAnunciosBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.ViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private Context context;

    public AdapterMeusAnuncios() {
    }

    public AdapterMeusAnuncios(List<Post> posts, Context context){
        this.posts = posts;
        this.context = context;
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
        holder.bindingVH.setClickListener((View.OnClickListener) v -> {
            String path = "com.fatec.tcc.findfm.Views.Adapters.AdapterMeusAnuncios";
            Bundle param = new Bundle();
            param.putString("titulo", post.getTitulo());
            param.putString("descricao", post.getDescricao());
            Util.open_form_withParam(context, CriarPost.class, path, param);
        });
    }

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    public void setAnuncios(List<Post> posts, Context context){
        this.posts = posts;
        this.context = context;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewMeusAnunciosBinding bindingVH;

        ViewHolder(ViewMeusAnunciosBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
            //TODO: Abrir tela do anuncio ao clicar
        }
    }
}