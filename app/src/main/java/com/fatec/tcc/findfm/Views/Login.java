package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Http.Request.LoginRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FormatadorTelefoneBR;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class Login extends AppCompatActivity {

    private HttpTypedRequest<LoginRequest, ResponseBody, ErrorResponse> loginRequest;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        init();
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        super.onDestroy();
    }

    private void init() {
        initRequests();
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    private void initRequests() {
        loginRequest = new HttpTypedRequest<>
        (
            Request.Method.POST,
            LoginRequest.class,
            ResponseBody.class,
            ErrorResponse.class,
            (ResponseBody response) ->
            {
                this.dialog.hide();
                if(ResponseCode.from(response.getResponseCode()).equals(ResponseCode.GenericSuccess)) {
                    // Compartilhado com toda a aplicação, acessado pela Key abaixo \/
                    SharedPreferences.Editor editor = getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                    Map<String, Object> dataMap = ((Map<String, Object>) response.getData());
                    editor.putString("tokenData", new Gson().toJson(dataMap.get("tokenData")));
                    editor.putBoolean("isLoggedIn", true);
                    // As chaves precisam ser persistidas
                    editor.apply();
                    dialog.dismiss();
                    //Pegar imagem retornada do server
                    //Bitmap bitmap = (Bitmap) ((Map) response.getData()).get("foto");
                    //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    //FindFM.getInstance().getParams().putByteArray("foto", baos.toByteArray());
                    Util.open_form__no_return(this, TelaPrincipal.class );
                } else if (ResponseCode.from(response.getResponseCode()).equals(ResponseCode.IncorrectPassword)){
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Atenção", R.drawable.ic_error,
                            "Usuário e/ou Senha incorretos","OK",
                            (dialog, id) -> { }).create().show();
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
        loginRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"login"));
    }

    public void btnEntrar_Click(View v)
    {
        Util.open_form(getApplicationContext(), TelaPrincipal.class);
        TextView usuario = findViewById(R.id.txtLogin);
        TextView senha = findViewById(R.id.txtSenha);
        boolean isEmailValido = FormatadorTelefoneBR.validarEmail(usuario.getText().toString());
        if(usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty() || !isEmailValido) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest requestObject = new LoginRequest(
                usuario.getText().toString(),
                senha.getText().toString()
        );
        this.dialog.show();
        loginRequest.setRequestObject(requestObject);
        loginRequest.execute(getApplicationContext());
    }

    public void lb_recuperarSenha_Click(View v){
        AlertDialogUtils.newSimpleDialog__OneButton(this,
                "Calma cara", R.drawable.ic_error,
                "Recuperar senha - tela em construção",
                "OK",
                (dialog, id) -> { }).create().show();
    }

    public void lb_registrar_Click(View v){
        Util.open_form(getApplicationContext(), Registrar.class);
    }

}
