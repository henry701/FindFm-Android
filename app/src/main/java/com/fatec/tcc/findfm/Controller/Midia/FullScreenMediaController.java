package com.fatec.tcc.findfm.Controller.Midia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Views.FullScreenVideoActivity;

public class FullScreenMediaController extends MediaController {

    private ImageButton fullScreen;
    private String isFullScreen;
    private String url;

    public FullScreenMediaController(Context context) {
        super(context);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        //fullscreen indicator from intent
        isFullScreen =  ((Activity)getContext()).getIntent().
                getStringExtra("fullScreenInd");

        if("y".equals(isFullScreen)){
            fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }else{
            fullScreen.setImageResource(R.drawable.ic_fullscreen);
        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(v -> {
            if(getContext() instanceof FullScreenVideoActivity){
                ((FullScreenVideoActivity) getContext()).onBackPressed();
                ((FullScreenVideoActivity) getContext()).onBackPressed();
            } else {
                Intent intent = new Intent(getContext(), FullScreenVideoActivity.class);
                intent.putExtra("url", url);
                if ("y".equals(isFullScreen)) {
                    intent.putExtra("fullScreenInd", "");
                } else {
                    intent.putExtra("fullScreenInd", "y");
                }
                getContext().startActivity(intent);
            }
        });
    }
}