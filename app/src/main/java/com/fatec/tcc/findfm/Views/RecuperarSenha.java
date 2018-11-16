package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Http.Request.RecuperarSenhaRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.databinding.ActivityRecuperarSenhaBinding;

public class RecuperarSenha extends AppCompatActivity {

    private JsonTypedRequest<RecuperarSenhaRequest, ResponseBody, ErrorResponse> requestRenovar1;
    private JsonTypedRequest<RecuperarSenhaRequest, ResponseBody, ErrorResponse> requestRenovar2;
    private ActivityRecuperarSenhaBinding binding;
    private ProgressDialog dialog;

    private boolean primeiraRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recuperar_senha);
        binding.circularImageView2.setImageDrawable(getResources().getDrawable(R.drawable.appname, getTheme()));

        dialog = new ProgressDialog(this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        requestRenovar1();
    }

    private void requestRenovar1() {
        requestRenovar1 = new JsonTypedRequest<>
                (       this,
                        Request.Method.POST,
                        RecuperarSenhaRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(),"passwordRecovery"),
                        null,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                binding.lbInstrucao.setText(R.string.instrucao_recuperar_senha_codigo);
                                binding.txtEmail.setVisibility(View.INVISIBLE);
                                binding.txtCodigo.setVisibility(View.VISIBLE);
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
                        (VolleyError error) ->
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

    private void requestRenovar2(String cod) {
        requestRenovar2 = new JsonTypedRequest<>
                (       this,
                        Request.Method.GET,
                        RecuperarSenhaRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(getResources(),"passwordRecovery", cod),
                        null,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                String senha = (String) response.getData();

                                this.dialog.dismiss();
                                binding.lbInstrucao.setText(R.string.nova_senha);
                                binding.txtCodigo.setVisibility(View.INVISIBLE);
                                binding.buttonEnviar.setVisibility(View.INVISIBLE);
                                EditText novaSenha = findViewById(R.id.txtNovaSenha);
                                novaSenha.setVisibility(View.VISIBLE);
                                novaSenha.setText(senha);

                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Senha", senha);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(this, "A senha foi copiada para a área de transferência, use-a na tela de login", Toast.LENGTH_LONG).show();
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
                        (VolleyError error) ->
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
            if (binding.txtEmail == null || binding.txtEmail.getText().toString().isEmpty() || !Formatadores.validarEmail(binding.txtEmail.getText().toString())) {
                Toast.makeText(this, "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
                return;
            }

            this.dialog.show();
            requestRenovar1.setRequest(new RecuperarSenhaRequest().setEmail(binding.txtEmail.getText().toString()));
            requestRenovar1.execute();
        }
        else {
            if (binding.txtCodigo == null || binding.txtCodigo.getText().toString().isEmpty() ) {
                Toast.makeText(this, "Insira o código!", Toast.LENGTH_SHORT).show();
                return;
            }
            this.dialog.show();
            requestRenovar2(binding.txtCodigo.getText().toString());
            requestRenovar2.execute();
        }
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        super.onDestroy();
    }
}
