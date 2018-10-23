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
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewUsuarioBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class AdapterUsuario extends RecyclerView.Adapter<AdapterUsuario.ViewHolder> {

    private Set<Musico> usuarios = new HashSet<>();
    private Activity activity;

    public AdapterUsuario() {
    }

    public AdapterUsuario(Set<Musico> musicos, Activity activity){
        this.usuarios = musicos;
        this.activity = activity;
    }

    public Set<Musico> getUsuarios(){
        return this.usuarios;
    }

    @Override
    public AdapterUsuario.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewUsuarioBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.view_usuario, parent, false);

        return new AdapterUsuario.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterUsuario.ViewHolder holder, int position) {
        Usuario usuario = (Usuario) usuarios.toArray()[position];
        holder.bindingVH.setUsuario(usuario);

        if(usuario.getFotoID() != null){
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
            downloadService.getResource(usuario.getFotoID());
        }

        holder.bindingVH.layout.setOnClickListener((View.OnClickListener) v -> {
            Util.hideSoftKeyboard(activity);
            FindFM.getMap().put("USUARIO_BUSCA", usuario);
            activity.onBackPressed();
        });
    }

    @Override
    public int getItemCount() {
        if (usuarios == null) {
            return 0;
        }
        return usuarios.size();
    }


    public void setusuarios(Set<Musico> usuarios, Activity activity) {
        this.usuarios = usuarios;
        this.activity = activity;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewUsuarioBinding bindingVH;

        ViewHolder(ViewUsuarioBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }
    }
}
