package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatec.tcc.findfm.Model.Business.Anuncio;
import com.fatec.tcc.findfm.R;

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
        Context context = parent.getContext();
        int layoutForItem = R.layout.view_meus_anuncios;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutForItem, parent, false);
        return new AdapterMeusAnuncios.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterMeusAnuncios.ViewHolder holder, int position) {
        holder.bind(position);
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


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView cidade;
        TextView descricao;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById( R.id.lb_titulo_anuncio );
            cidade = itemView.findViewById( R.id.lb_cidade );
            descricao = itemView.findViewById(R.id.lb_descricao);

            itemView.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                Anuncio anuncio = anuncios.get(adapterPosition);
                //TODO: Abrir tela do anuncio
            });
        }

        void bind(int position) {
            // use the sparse boolean array to check
            titulo.setText(String.valueOf(anuncios.get(position).getTitulo()));
            cidade.setText(String.valueOf(anuncios.get(position).getCidade()));
            descricao.setText(String.valueOf(anuncios.get(position).getDescricao()));
        }
    }
}