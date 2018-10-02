package com.fatec.tcc.findfm.Views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.VideoView;

import com.fatec.tcc.findfm.Controller.Midia.FullScreenMediaController;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;

public class FullScreenVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        FindFM.setTelaAtual("VIDEO");

        VideoView videoView = findViewById(R.id.videoView);

        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        Uri uri = Uri.parse(getIntent().getStringExtra("url"));

        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.hide();
        }

        videoView.setVideoURI(uri);

        FullScreenMediaController mediaController = new FullScreenMediaController(this);
        mediaController.setUrl(getIntent().getStringExtra("url"));
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
