package com.fatec.tcc.findfm.Views.Adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Model.Business.Comentario;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewComentarioBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.ViewHolder>{

    private List<Comentario> comentarios = new ArrayList<>();
    private Activity activity;

    public AdapterComentario() {
    }

    public AdapterComentario(List<Comentario> comentarios, Activity activity){
        this.comentarios = comentarios;
        this.activity = activity;
    }

    public List<Comentario> getComentarios(){
        return new ArrayList<>(this.comentarios);
    }

    @Override
    public AdapterComentario.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewComentarioBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_comentario, parent, false);

        return new AdapterComentario.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterComentario.ViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.bindingVH.setComentario(comentario);

        if(comentario.getComentador().getFotoID() != null){

            DownloadResourceService downloadService = new DownloadResourceService(activity);
            downloadService.addObserver( (download, arg) -> {
                if(download instanceof DownloadResourceService) {
                    activity.runOnUiThread(() -> {
                        if (arg instanceof BinaryResponse) {
                            byte[] dados = ((BinaryResponse) arg).getData();
                            InputStream input=new ByteArrayInputStream(dados);
                            Bitmap ext_pic = BitmapFactory.decodeStream(input);
                            holder.bindingVH.fotoPerfil.setImageBitmap(ext_pic);
                            holder.bindingVH.executePendingBindings();
                        } else{
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id1) -> { holder.bindingVH.executePendingBindings(); }).create().show();
                        }

                    });
                }
            });
            downloadService.getResource(comentario.getComentador().getFotoID());
        }

        Bundle bundle = new Bundle();
        View.OnClickListener irPerfil = v -> {
            if(comentario.getComentador().getId().equals(FindFM.getUsuario().getId())){
                bundle.putBoolean("euMesmo", true);
                Util.open_form_withParam__no_return(activity, TelaPrincipal.class, "CriarPost", bundle);
            }else{
                bundle.putString("id_usuario", comentario.getComentador().getId());
                Util.open_form_withParam__no_return(activity, TelaPrincipal.class, "CriarPost", bundle);
            }
        };

        holder.bindingVH.fotoPerfil.setOnClickListener(irPerfil);
        holder.bindingVH.txtNome.setOnClickListener(irPerfil);
    }

    @Override
    public int getItemCount() {
        if (comentarios == null) {
            return 0;
        }
        return comentarios.size();
    }


    public void setComentarios(List<Comentario> comentarios, Activity activity) {
        this.comentarios = comentarios;
        this.activity = activity;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewComentarioBinding bindingVH;

        ViewHolder(ViewComentarioBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}
