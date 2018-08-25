package com.fatec.tcc.findfm.Utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.R;

import java.io.ByteArrayOutputStream;

public class ImagemUtils {

    public static Intent pickImageIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public static void setImagemToParams(ImageView imagemView){
        Bitmap bitmap = ((BitmapDrawable) imagemView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        FindFM.getInstance().getParams().putByteArray("foto", baos.toByteArray());
    }

    public static void setImagemToImageView(ImageView imageView, AppCompatActivity view, ImageButton btnRemoverImagem){

        byte[] image = FindFM.getInstance().getParams().getByteArray("foto");

        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
            btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

}