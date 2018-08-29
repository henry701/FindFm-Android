package com.fatec.tcc.findfm.Controller.Midia;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.Observable;

public class RadioController extends Observable{

    private MediaPlayer mediaPlayer;
    private boolean preparado;
    private boolean iniciado;
    private Context context;

    public RadioController(Context context){
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        preparado = false;
        iniciado = false;
    }

    public boolean isPreparado(){
        return preparado;
    }

    public void iniciar(){
        String stream = "http://fs-west.theblast.fast-serv.com:80/blastozoic56ogg.opus";
        new PlayTask().execute(stream);
    }

    public void play(){
        if(iniciado) {
            iniciado = false;
            mediaPlayer.pause();
        } else {
            iniciado = true;
            mediaPlayer.start();
        }
    }

    public void dismiss(){
        if(preparado){
            mediaPlayer.release();
        }
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
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Erro ao executar rádio!", Toast.LENGTH_SHORT ).show();
            }

            return preparado;
        }
    }
}
