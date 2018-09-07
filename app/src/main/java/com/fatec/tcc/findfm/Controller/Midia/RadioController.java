package com.fatec.tcc.findfm.Controller.Midia;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fatec.tcc.findfm.R;

import java.io.IOException;
import java.util.Observable;

public class RadioController extends Observable{

    private MediaPlayer mediaPlayer;
    private boolean preparado;
    private boolean iniciado;
    private Context context;

    public RadioController(Context context)
    {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setOnErrorListener((MediaPlayer mp, int what, int extra) -> {
            Log.d("[RADIO CONTROLLER]", "mediaPlayer onError what=" + what + ",extra=" + extra);
            return false;
        });
        this.mediaPlayer.setOnCompletionListener((MediaPlayer mp) -> {
            Log.d("[RADIO CONTROLLER]", "mediaPlayer onCompletion");
        });
    }

    public boolean isPreparado(){
        return preparado;
    }

    public void prepare(){
        if(!preparado) {
            new PlayTask().execute(this.context.getString(R.string.radio_url));
        }
    }

    public void toggle()
    {
        if(iniciado) {
            pause();
        } else {
            play();
        }
    }

    public void play()
    {
        if(iniciado)
        {
            return;
        }
        iniciado = true;
        mediaPlayer.start();
    }

    public void pause()
    {
        if(!iniciado)
        {
            return;
        }
        iniciado = false;
        mediaPlayer.pause();
    }

    public void dismiss()
    {
        if(preparado)
        {
            mediaPlayer.release();
        }
    }

    public void rePrepare()
    {
        dismiss();
        prepare();
    }

    private class PlayTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                preparado = true;
                setChanged();
                notifyObservers();
            } catch (Exception e) {
                Log.e("[RADIO CONTROLLER]", "Erro ao executar rádio", e);
                Toast.makeText(context, "Erro ao executar rádio!", Toast.LENGTH_SHORT ).show();
            }
            return preparado;
        }
    }
}
