package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.LoginController;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Parsers.UsuarioParser;
import com.fatec.tcc.findfm.Model.Http.Request.LoginRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.LoginResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Request.ServerCallBack;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private HttpTypedRequest<LoginRequest, ResponseBody, ErrorResponse> loginRequest;
    private LoginController loginController;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        init();
    }

    private void init() {
        initRequests();
        this.loginController = new LoginController(getApplicationContext(), getResources());
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando..");
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
                    editor.putBoolean("isLogado", true);
                    // editor.putString("username", user.getUsuario());
                    // As chaves precisam ser persistidas
                    editor.apply();
                    dialog.dismiss();
                    Util.open_form__no_return(this, HomePage.class );
                } else if (ResponseCode.from(response.getResponseCode()).equals(ResponseCode.IncorrectPassword)){
                    AlertDialogUtils.newSimpleDialog__OneButton(this,
                            "Atenção", R.drawable.ic_error,
                            "Usuário e/ou Senha incorretos","OK",
                            (dialog, id) -> { }).create().show();
                }
            },
            (ErrorResponse errorResponse) ->
            {
                // TODO: On Business Error login (senha errada por ex)
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
        loginRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"metro_api/info_gerais/banheiros"));
    }

    public void btnEntrar_Click(View v) {

        TextView usuario = findViewById(R.id.txtLogin);
        TextView senha = findViewById(R.id.txtSenha);

        if(usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest requestObject = new LoginRequest(
                usuario.getText().toString(),
                senha.getText().toString()
        );
        loginRequest.setRequestObject(requestObject);
        loginRequest.execute(getApplicationContext());
        this.dialog.show();

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
