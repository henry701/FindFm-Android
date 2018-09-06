package com.fatec.tcc.findfm.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

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
        String base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT), Charset.forName("UTF-8"));
        FindFM.setImagemPerfilParams(base64);
    }

    public static void setImagemToImageView(ImageView imageView, AppCompatActivity view){
        byte[] image = FindFM.getImagemPerfilBytes();
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else{
            imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        }
    }

    public static void setImagemToImageView(ImageView imageView, AppCompatActivity view, ImageButton btnRemoverImagem){
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

    public static void getImagemFromEndPoint(String id_imagem, Context view, ProgressDialog progressDialog){
        HttpTypedRequest<ResponseBody, ResponseBody, ErrorResponse> imagemRequest= new HttpTypedRequest<>
                (
                        Request.Method.GET,
                        ResponseBody.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            progressDialog.hide();
                            if (ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {

                                progressDialog.dismiss();
                                Util.open_form__no_return(view, TelaPrincipal.class);
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            progressDialog.hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (Exception error) ->
                        {
                            progressDialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                                    (dialog, id) -> {
                                    }).create().show();
                        }
                );
        imagemRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(), "resource/" + id_imagem));
        imagemRequest.execute(view);
    }

}
