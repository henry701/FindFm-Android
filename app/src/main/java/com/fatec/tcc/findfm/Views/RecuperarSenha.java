package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Http.Request.RecuperarSenhaRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.HttpUtils;

public class RecuperarSenha extends AppCompatActivity {

    private HttpTypedRequest<RecuperarSenhaRequest, ResponseBody, ErrorResponse> recuperarRequest;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        ImageView imageView = findViewById(R.id.circularImageView2);
        byte[] image = FindFM.getFotoPrefBytes(this);
        if(image != null && image.length != 0) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else{
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
        }
        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        initRequests();
    }

    private void initRequests() {
        recuperarRequest = new HttpTypedRequest<>
                (
                        Request.Method.POST,
                        RecuperarSenhaRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                //TODO: sei la oq fazer
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (Exception error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        recuperarRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"forgetpwd"));
    }

    public void buttonEnviar_Click(View view){
        EditText email = findViewById(R.id.txtEmail);

        if( email == null || email.getText().toString().isEmpty() || !Formatadores.validarEmail(email.getText().toString())) {
            Toast.makeText(this, "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.dialog.show();
        recuperarRequest.setRequestObject(new RecuperarSenhaRequest().setEmail(email.getText().toString()));
        recuperarRequest.execute(this);
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        super.onDestroy();
    }
}
