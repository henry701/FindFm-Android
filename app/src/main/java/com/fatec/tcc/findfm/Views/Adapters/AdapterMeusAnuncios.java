package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Model.Business.Anuncio;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.databinding.ViewMeusAnunciosBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.ViewHolder> {

    private List<Anuncio> anuncios = new ArrayList<>();
    private Context context;

    public AdapterMeusAnuncios() {
    }

    public AdapterMeusAnuncios(List<Anuncio> anuncios, Context context){
        this.anuncios = anuncios;
        this.context = context;
    }

    public List<Anuncio> getAnuncios(){
        return new ArrayList<>(this.anuncios);
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
        Anuncio anuncio = anuncios.get(position);
        holder.bindingVH.setAnuncio(anuncio);
    }

    @Override
    public int getItemCount() {
        if (anuncios == null) {
            return 0;
        }
        return anuncios.size();
    }

    public void setAnuncios(List<Anuncio> anuncios, Context context){
        this.anuncios = anuncios;
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