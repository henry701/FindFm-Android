package com.fatec.tcc.findfm.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class VideoUtil extends Activity {

    private MediaController ctlr;


    VideoView videoView = null;

    Context context = null;
    long totalRead = 0;
    int bytesToRead = 50 * 1024;

    private int mPlayerPosition;
    private File mBufferFile;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);


        //videoView = (VideoView) findViewById(R.id.videoview);


        ctlr = new MediaController(this);

        ctlr.setMediaPlayer(videoView);
        videoView.setMediaController(ctlr);
        videoView.requestFocus();

        new GetYoutubeFile().start();

    }


    private class GetYoutubeFile extends Thread {
        private String mUrl;
        private String mFile;

        public GetYoutubeFile() {

        }

        @Override
        public void run() {
            super.run();
            try {

                File bufferingDir = new File(
                        Environment.getExternalStorageDirectory()
                                + "/YoutubeBuff");
                InputStream stream = getAssets().open("famous.3gp");
                if (stream == null)
                    throw new RuntimeException("stream is null");
                File temp = File.createTempFile("test", "mp4");
                System.out.println("hi");
                temp.deleteOnExit();
                String tempPath = temp.getAbsolutePath();

                File bufferFile = File.createTempFile("test", "mp4");

                BufferedOutputStream bufferOS = new BufferedOutputStream(
                        new FileOutputStream(bufferFile));


                InputStream is = getAssets().open("famous.3gp");
                BufferedInputStream bis = new BufferedInputStream(is, 2048);

                byte[] buffer = new byte[16384];
                int numRead;
                boolean started = false;
                while ((numRead = bis.read(buffer)) != -1) {

                    bufferOS.write(buffer, 0, numRead);
                    bufferOS.flush();
                    totalRead += numRead;
                    if (totalRead > 120000 && !started) {
                        Log.e("Player", "BufferHIT:StartPlay");
                        setSourceAndStartPlay(bufferFile);
                        started = true;
                    }

                }
                mBufferFile = bufferFile;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSourceAndStartPlay(File bufferFile) {
        try {

            mPlayerPosition = videoView.getCurrentPosition();
            videoView.setVideoPath(bufferFile.getAbsolutePath());

            videoView.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCompletion(MediaPlayer mp) {
        mPlayerPosition = mp.getCurrentPosition();
        try {
            mp.reset();
            videoView.setVideoPath(new File("mnt/sdcard/YoutubeBuff/"
                    + mBufferFile).getAbsolutePath());
            mp.seekTo(mPlayerPosition);
            videoView.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}