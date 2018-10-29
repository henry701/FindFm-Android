package com.fatec.tcc.findfm.Views.Adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fatec.tcc.findfm.Model.Business.Musica;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.databinding.FragmentAudioBinding;

import java.util.ArrayList;
import java.util.List;

public class AdapterMusica extends RecyclerView.Adapter<AdapterMusica.ViewHolder> {

    private List<Musica> Musicas = new ArrayList<>();
    private List<AdapterMusica.ViewHolder> holders = new ArrayList<>();
    private Activity activity;
    private boolean isCadastro;

    public AdapterMusica() { }

    public AdapterMusica(List<Musica> Musicas, Activity activity, boolean isCadastro){
        this.Musicas = Musicas;
        this.activity = activity;
        this.isCadastro = isCadastro;
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
