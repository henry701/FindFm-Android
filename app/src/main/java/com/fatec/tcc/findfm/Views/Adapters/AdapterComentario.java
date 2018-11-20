package com.fatec.tcc.findfm.Views.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Comentario;
import com.fatec.tcc.findfm.Model.Http.Request.ComentarRequest;
import com.fatec.tcc.findfm.Model.Http.Request.Denuncia;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.fatec.tcc.findfm.databinding.ViewComentarioBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.ViewHolder>{

    private String postId;
    private List<Comentario> comentarios = new ArrayList<>();
    private Activity activity;
    private boolean isVisitante;

    public AdapterComentario() {
    }

    public AdapterComentario(String postId, List<Comentario> comentarios, Activity activity, boolean isVisitante){
        this.postId = postId;
        this.comentarios = comentarios;
        this.activity = activity;
        this.isVisitante = isVisitante;
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
                            Log.e("[ERRO-Download]IMG", "Erro ao baixar binário da imagem");
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente.", "OK",
                                    (dialog, id1) -> {
                                    }).create().show();
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

        holder.bindingVH.btnLike.setOnClickListener(
                v -> {
                    if (!holder.bindingVH.getComentario().getLikes().contains(FindFM.getUsuario().getId())) {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> comentarioRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.GET.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "comment/like/" + postId, comentario.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        comentarioRequest.setRequest(null);
                        comentarioRequest.execute();
                        holder.bindingVH.getComentario().setLikesNumb((Long.parseLong(comentario.getLikesNumb()) +1));
                        holder.bindingVH.getComentario().getLikes().add(FindFM.getUsuario().getId());
                        holder.bindingVH.btnLike.setText(holder.bindingVH.getComentario().getLikesNumb());
                        holder.bindingVH.executePendingBindings();
                    }else {
                        JsonTypedRequest<ComentarRequest, ResponseBody, ErrorResponse> comentarioRequest = new JsonTypedRequest<>(
                                activity,
                                HttpMethod.DELETE.getCodigo(),
                                ComentarRequest.class,
                                ResponseBody.class,
                                ErrorResponse.class,
                                HttpUtils.buildUrl(activity.getResources(), "comment/like/" + postId, comentario.getId()),
                                null,
                                (ResponseBody response) -> {
                                },
                                (ErrorResponse error) -> {
                                },
                                (VolleyError error) -> {
                                }
                        );
                        comentarioRequest.setRequest(null);
                        comentarioRequest.execute();
                        holder.bindingVH.getComentario().setLikesNumb((Long.parseLong(comentario.getLikesNumb()) -1));
                        holder.bindingVH.getComentario().getLikes().remove(FindFM.getUsuario().getId());
                        holder.bindingVH.btnLike.setText(holder.bindingVH.getComentario().getLikesNumb());
                        holder.bindingVH.executePendingBindings();
                    }
                }
        );

        if(isVisitante){
            holder.bindingVH.btnLike.setOnClickListener(view -> AlertDialogUtils.newSimpleDialog__OneButton(activity,
                    "Ops!", R.drawable.ic_error,
                    "Essa ação requer que você esteja logado com uma conta\nLogue ou crie uma conta","OK",
                    (dialog, id) -> { }).create().show());
        } else {
            if (!comentario.getComentador().getId().equals(FindFM.getUsuario().getId())) {
                holder.bindingVH.lbDescricao.setOnLongClickListener(v -> {
                    denunciar("Comentário", comentario.getId());
                    return true;
                });
            }
        }


        if(comentario.getLikes().contains(FindFM.getUsuario().getId())){
            holder.bindingVH.btnLike.setText(R.string.descurtir);
        }else {
            holder.bindingVH.btnLike.setText(R.string.curtir);
        }
    }

    private void denunciar(String tipo, String idItem){
        EditText motivo = new EditText(activity);
        EditText contato = new EditText(activity);
        InputFilter[] filtros =new InputFilter[] {new InputFilter.LengthFilter(60)};
        motivo.setFilters(filtros);
        contato.setFilters(filtros);
        AlertDialogUtils.newTextDialog(activity, "Denunciar " + tipo + " ?", R.drawable.ic_report,
                "Diga-nos o que está errado e tomaremos as devidas providências!\nEscreva aqui sua denúncia:",
                "Denunciar", "Cancelar",
                (dialog, which) ->
                        AlertDialogUtils.newTextDialog(activity, "Denunciar " + tipo + " ?", R.drawable.ic_report,
                                "Diga-nos como podemos te contatar para falar sobre essa denúncia.\nSeu nome e e-mail para contato:",
                                "Denunciar", "Cancelar",
                                (dialog4, which4) -> {
                                    if(motivo.getText() != null && !"".equals(motivo.getText().toString()) &&
                                            contato.getText() != null && !"".equals(contato.getText().toString()) ) {
                                        String denuncia = motivo.getText().toString();
                                        String denunciante = contato.getText().toString();
                                        initDenunciarRequest(idItem, denuncia, denunciante, tipo);
                                    } else {
                                        Toast.makeText(activity, "Preencha todos os campos para enviar denúncia!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                (dialog2, which2) -> { },contato).show(),
                (dialog, which) -> { }, motivo).show();
    }

    private void initDenunciarRequest(String idItem, String motivo, String contato, String tipo){
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        JsonTypedRequest<Denuncia, ResponseBody, ErrorResponse> reportRequest = new JsonTypedRequest<>(
                activity,
                HttpMethod.POST.getCodigo(),
                Denuncia.class,
                ResponseBody.class,
                ErrorResponse.class,
                HttpUtils.buildUrl(activity.getResources(),"report"),
                null,
                (ResponseBody response) -> {
                    dialog.hide();
                    if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                        AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                "Sucesso!", R.drawable.ic_error,
                                "Denúncia enviada com sucesso!","OK",
                                (dialog1, id) -> dialog.setMessage("Carregando...")).create().show();
                    }
                },
                (ErrorResponse errorResponse) ->
                {
                    dialog.hide();
                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                    if(errorResponse != null) {
                        Log.e("[ERRO-Response]Denuncia", errorResponse.getMessage());
                        mensagem = errorResponse.getMessage();
                    }
                    AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                            mensagem, "OK", (dialog2, id) -> { }).create().show();
                },
                (VolleyError errorResponse) ->
                {
                    dialog.hide();
                    String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                    if(errorResponse != null) {
                        Log.e("[ERRO-Volley]Denuncia", errorResponse.getMessage());
                        errorResponse.printStackTrace();
                    }
                    AlertDialogUtils.newSimpleDialog__OneButton(activity, "Ops!", R.drawable.ic_error,
                            mensagem, "OK", (dialog2, id) -> { }).create().show();
                }
        );

        reportRequest.setRequest(new Denuncia()
                .setId(idItem)
                .setContato(contato)
                .setMotivo(motivo)
                .setTipo(tipo)
        );
        dialog.setMessage("Enviando denúncia, aguarde...");
        dialog.show();
        reportRequest.execute();
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
