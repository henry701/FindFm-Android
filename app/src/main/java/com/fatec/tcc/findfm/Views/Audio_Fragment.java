package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.databinding.FragmentAudioBinding;

import java.io.IOException;

public class Audio_Fragment extends Fragment {

    private FragmentAudioBinding binding;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Activity activity;
    private Uri uri;

    public Audio_Fragment() {}

    @SuppressLint("ValidFragment")
    public Audio_Fragment(Activity activity, Uri uri) {
        this.activity = activity;
        this.uri = uri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio, container, false);

        binding.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);
        //TODO: colocar o botao de stop cinza
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(activity, uri);

        // binding.duracaoBar Control media
        binding.duracaoBar.setMax(mediaPlayer.getDuration());
        binding.duracaoBar.setOnTouchListener((v, event) -> {
            UpdateDuracaoChange(v);
            return false;
        });

        // Start
        binding.btnPlay.setOnClickListener(v -> {
            mediaPlayer.start();
            startPlayProgressUpdater();

            binding.btnPlay.setEnabled(false);
            binding.btnPlay.setBackgroundResource(R.drawable.ic_play_dark);

            binding.btnPause.setEnabled(true);
            binding.btnPause.setBackgroundResource(R.drawable.ic_pause);

            binding.btnStop.setEnabled(true);
        });

        // Pause
        binding.btnPause.setOnClickListener(v -> {
            mediaPlayer.pause();
            binding.btnPlay.setEnabled(true);
            binding.btnPlay.setBackgroundResource(R.drawable.ic_play);

            binding.btnPause.setEnabled(false);
            binding.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);

            binding.btnStop.setEnabled(false);
        });

        // Stop
        binding.btnStop.setOnClickListener(v -> {
            mediaPlayer.stop();
            binding.btnPlay.setEnabled(true);
            binding.btnPlay.setBackgroundResource(R.drawable.ic_play);

            binding.btnPause.setEnabled(false);
            binding.btnPause.setBackgroundResource(R.drawable.ic_pause_dark);

            binding.btnStop.setEnabled(false);

            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return binding.getRoot();
    }

    private void UpdateDuracaoChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    public void startPlayProgressUpdater() {
        binding.duracaoBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = () -> startPlayProgressUpdater();
            handler.postDelayed(notification,1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }
}
