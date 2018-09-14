package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    private HttpTypedRequest<RecuperarSenhaRequest, ResponseBody, ErrorResponse> requestRenovar1;
    private HttpTypedRequest<RecuperarSenhaRequest, ResponseBody, ErrorResponse> requestRenovar2;
    private ProgressDialog dialog;

    private TextView lb_instrucao;
    private EditText email;
    private EditText codigo;
    private boolean primeiraRequest = true;

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

        lb_instrucao  = findViewById(R.id.lb_instrucao);
        email  = findViewById(R.id.txtEmail);
        codigo = findViewById(R.id.txtCodigo);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        requestRenovar1();
        requestRenovar2();
    }

    private void requestRenovar1() {
        requestRenovar1 = new HttpTypedRequest<>
                (       this,
                        Request.Method.POST,
                        RecuperarSenhaRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                // TODO: Mostra uma text falando pro cara olhar o email
                                // TODO: Mostra popup pedindo pro cara botar o código, e ai faz uma request GET q nem ta la no server
                                // TODO: Mostra a senha nova pro cara (ou melhor ainda, mostra a senha e logga o cara automático tbm)
                                lb_instrucao.setText(R.string.instrucao_recuperar_senha_codigo);
                                email.setVisibility(View.INVISIBLE);
                                codigo.setVisibility(View.VISIBLE);
                                primeiraRequest = false;
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
        requestRenovar1.setFullUrl(HttpUtils.buildUrl(getResources(),"passwordRecovery"));
    }

    private void requestRenovar2() {
        requestRenovar2 = new HttpTypedRequest<>
                (       this,
                        Request.Method.POST,
                        RecuperarSenhaRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                this.dialog.dismiss();
                                lb_instrucao.setText(R.string.nova_senha);
                                codigo.setVisibility(View.INVISIBLE);
                                EditText novaSenha = findViewById(R.id.txtNovaSenha);
                                novaSenha.setVisibility(View.VISIBLE);
                                novaSenha.setText("Nova senha");
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
    }

    public void buttonEnviar_Click(View view){
        if(primeiraRequest) {
            if (email == null || email.getText().toString().isEmpty() || !Formatadores.validarEmail(email.getText().toString())) {
                Toast.makeText(this, "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
                return;
            }

            this.dialog.show();
            requestRenovar1.setRequestObject(new RecuperarSenhaRequest().setEmail(email.getText().toString()));
            requestRenovar1.execute(this);
        }
        else {
            if (codigo == null || codigo.getText().toString().isEmpty() ) {
                Toast.makeText(this, "Insira o código!", Toast.LENGTH_SHORT).show();
                return;
            }
            this.dialog.show();
            requestRenovar2.setRequestObject(null);
            requestRenovar2.setFullUrl(HttpUtils.buildUrl(getResources(),codigo.getText().toString()));
            requestRenovar2.execute(this);
        }
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        super.onDestroy();
    }
}
