package com.fatec.tcc.findfm.Views.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Musica;
import com.fatec.tcc.findfm.Model.Http.Request.Denuncia;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpMethod;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.databinding.FragmentAudioBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMusica extends RecyclerView.Adapter<AdapterMusica.ViewHolder> {

    private List<Musica> Musicas = new ArrayList<>();
    private List<AdapterMusica.ViewHolder> holders = new ArrayList<>();
    private Activity activity;
    private boolean isCadastro;
    private boolean isVisitante;

    public AdapterMusica() { }

    public AdapterMusica(List<Musica> Musicas, Activity activity, boolean isCadastro, boolean isVisitante){
        this.Musicas = Musicas;
        this.activity = activity;
        this.isCadastro = isCadastro;
        this.isVisitante = isVisitante;
    }

    public List<Musica> getMusicas(){
        return new ArrayList<>(this.Musicas);
    }

    @Override
    public AdapterMusica.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        FragmentAudioBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.fragment_audio, parent, false);

        return new AdapterMusica.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(AdapterMusica.ViewHolder holder, int position) {
        Musica musica = Musicas.get(position);
        Uri uri = musica.getUri();
        holders.add(holder);
        holder.bindingVH.setMusica(musica);
        holder.bindingVH.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);

        if(holder.mediaPlayer != null){
            holder.mediaPlayer.release();
        }
        holder.mediaPlayer = MediaPlayer.create(activity, uri);

        // holder.bindingVH.duracaoBar Control media
        holder.bindingVH.duracaoBar.setMax(holder.mediaPlayer.getDuration());
        holder.bindingVH.duracaoBar.setOnTouchListener((v, event) -> {
            holder.UpdateDuracaoChange(v);
            return false;
        });

        // Start
        holder.bindingVH.btnPlay.setOnClickListener(v -> {
            holder.mediaPlayer.start();
            holder.startPlayProgressUpdater();

            holder.bindingVH.btnPlay.setEnabled(false);
            holder.bindingVH.btnPlay.setBackgroundResource(R.drawable.ic_play_dark);

            holder.bindingVH.btnPause.setEnabled(true);
            holder.bindingVH.btnPause.setBackgroundResource(R.drawable.ic_pause);

            holder.bindingVH.btnStop.setEnabled(true);
            holder.bindingVH.btnStop.setBackgroundResource(R.drawable.ic_stop);
        });

        // Pause
        holder.bindingVH.btnPause.setOnClickListener(v -> {
            holder.mediaPlayer.pause();
            holder.bindingVH.btnPlay.setEnabled(true);
            holder.bindingVH.btnPlay.setBackgroundResource(R.drawable.ic_play);

            holder.bindingVH.btnPause.setEnabled(false);
            holder.bindingVH.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);

            holder.bindingVH.btnStop.setEnabled(false);
            holder.bindingVH.btnStop.setBackgroundResource(R.drawable.ic_stop_dark);
        });

        // Stop
        holder.bindingVH.btnStop.setOnClickListener(v -> {
            holder.mediaPlayer.stop();
            holder.bindingVH.btnPlay.setEnabled(true);
            holder.bindingVH.btnPlay.setBackgroundResource(R.drawable.ic_play);

            holder.bindingVH.btnPause.setEnabled(false);
            holder.bindingVH.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);

            holder.bindingVH.btnStop.setEnabled(false);
            holder.bindingVH.btnStop.setBackgroundResource(R.drawable.ic_stop_dark);

            try {
                holder.mediaPlayer.prepare();
                holder.mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if(isCadastro) {
            holder.bindingVH.btnRemoverMusica.setVisibility(View.VISIBLE);
            holder.bindingVH.btnRemoverMusica.setOnClickListener(v -> {
                stopMedia();
                Musicas.remove(musica);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, Musicas.size());
            });
        } else {
            if(!isVisitante) {
                holder.bindingVH.relativeLayout.setOnLongClickListener(v -> {
                    denunciar("Música", musica.getIdResource());
                    return true;
                });
            }
        }
    }

    public void stopMedia() {
        for ( ViewHolder holder : holders){
            holder.mediaPlayer.stop();
        }
    }

    @Override
    public int getItemCount() {
        if (Musicas == null) {
            return 0;
        }
        return Musicas.size();
    }

    public void setMusicas(List<Musica> Musicas, Activity activity) {
        this.Musicas = Musicas;
        this.activity = activity;
        notifyDataSetChanged();
    }

    private void denunciar(String tipo, String idItem){
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        EditText input = new EditText(activity);
        AlertDialogUtils.newTextDialog(activity, "Denunciar " + tipo + " ?", R.drawable.ic_report, "Diga-nos o que está errado e tomaremos as devidas providências!",
                "Denunciar", "Cancelar",
                (dialog1, which) -> {
                    try {
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
                                                (dialog2, id) -> dialog.setMessage("Carregando...")).create().show();
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
                                .setContato(FindFM.getUsuario().getId())
                                .setMotivo(input.getText().toString())
                                .setTipo(tipo)
                        );
                        dialog.setMessage("Enviando denúncia, aguarde...");
                        dialog.show();
                        reportRequest.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                (dialog2, which) -> { }, input).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FragmentAudioBinding bindingVH;
        MediaPlayer mediaPlayer;
        private Handler handler = new Handler();

        ViewHolder(FragmentAudioBinding binding){
            super(binding.getRoot());
            this.bindingVH = binding;
        }

        void startPlayProgressUpdater() {
            try {
                bindingVH.duracaoBar.setProgress(mediaPlayer.getCurrentPosition());

                if (mediaPlayer.isPlaying()) {
                    Runnable notification = () -> startPlayProgressUpdater();
                    handler.postDelayed(notification, 1000);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private void UpdateDuracaoChange(View v){
            if(mediaPlayer.isPlaying()){
                SeekBar sb = (SeekBar)v;
                mediaPlayer.seekTo(sb.getProgress());
            }
        }
    }
}
