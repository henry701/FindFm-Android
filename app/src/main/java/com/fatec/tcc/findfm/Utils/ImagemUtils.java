package com.fatec.tcc.findfm.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fatec.tcc.findfm.Infrastructure.Request.ImageRequest;
import com.fatec.tcc.findfm.R;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class ImagemUtils {

    public static Intent pickImageIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public static Intent pickVideoIntent(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public static void setImagemToParams(ImageView imagemView){
        Bitmap bitmap = ((BitmapDrawable) imagemView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT), Charset.forName("UTF-8"));
        FindFM.setImagemPerfilParams(base64);
    }

    public static void setImagemToParams(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT), Charset.forName("UTF-8"));
        FindFM.setImagemPerfilParams(base64);
    }

    public static void setImagemToPref(Activity view, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT), Charset.forName("UTF-8"));
        FindFM.setFotoPref(view, base64);
    }

    public static void setImagemPerfilToImageView(ImageView imageView, AppCompatActivity view){
        byte[] image = FindFM.getFotoPrefBytes(view);
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        }
    }

    public static void setImagemPerfilToImageView(ImageView imageView, AppCompatActivity view, ImageButton btnRemoverImagem){
        byte[] image = FindFM.getImagemPerfilBytes();
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
            btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

    public static void setImagemToImageView_FromPref(ImageView imageView, AppCompatActivity view, ImageButton btnRemoverImagem){
        byte[] image = FindFM.getFotoPrefBytes(view);
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
            btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

    public static void setImagemToImageView_FromPref(ImageView imageView, AppCompatActivity view){
        byte[] image = FindFM.getFotoPrefBytes(view);
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        }
    }

    public static void setImagemHeader(AppCompatActivity v){
        byte[] image = FindFM.getImagemPerfilBytes();
        ImageView imagemUsuarioHeader = v.findViewById(R.id.imageViewHeader);
        if(image != null && image.length != 0) {
            imagemUsuarioHeader.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else {
            imagemUsuarioHeader.setImageDrawable(v.getResources().getDrawable(R.drawable.capaplaceholder_photo, v.getTheme()));
        }
    }

    public static Bitmap getImagemFromEndPoint(Context context, String id_imagem) {
        final Bitmap[] retorno = new Bitmap[1];

        ImageRequest imagemRequest = new ImageRequest(context, id_imagem, 0, 0, ImageView.ScaleType.CENTER_CROP,
                response -> {
                    retorno[0] = response;

                },
                error -> {
                    AlertDialogUtils.newSimpleDialog__OneButton(context, "Ops!", R.drawable.ic_error, error.getMessage(), "OK",
                            (dialog, id) -> {
                            }).create().show();
                }
        );
        imagemRequest.execute();

        return retorno[0];
    }

    public static Bitmap getImagemFromEndPoint(Context context, String id_imagem, ProgressDialog pdialog) {
        pdialog.show();
        final Bitmap[] retorno = new Bitmap[1];

        ImageRequest imagemRequest = new ImageRequest(context, id_imagem, 0, 0, ImageView.ScaleType.CENTER_CROP,
                response -> {
                    retorno[0] = response;
                    pdialog.hide();

                },
                error -> {
                    pdialog.hide();
                    AlertDialogUtils.newSimpleDialog__OneButton(context, "Ops!", R.drawable.ic_error, error.getMessage(), "OK",
                            (dialog, id) -> {
                            }).create().show();
                }
        );
        imagemRequest.execute();

        return retorno[0];
    }

    public static byte[] getFileDataFromDrawable(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getByteArrayFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
