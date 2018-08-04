package com.fatec.tcc.findfm.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.fatec.tcc.findfm.Request.HttpPostRequest;
import com.fatec.tcc.findfm.Request.ServerCallBack;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private HttpPostRequest<LoginRequest, LoginResponse, ErrorResponse> loginRequest;
    private LoginController loginController;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        init();
    }

    private void init() {
    //  initRequests();
        this.loginController = new LoginController(getApplicationContext(), getResources());
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando..");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }
    /*
    private void initRequests() {
        loginRequest = new HttpPostRequest<>
                (
                        LoginRequest.class,
                        LoginResponse.class,
                        ErrorResponse.class,
                        (LoginResponse response) ->
                        {
                            // TODO: On Success login
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            // TODO: On Business Error login
                        },
                        (Exception error) ->
                        {
                            // TODO: On Critical Error login
                        }
                );
        loginRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"metro_api/info_gerais/banheiros"));
    }
    */
    public void btnEntrar_Click(View v) {
        /*LoginRequest requestObject = new LoginRequest();
        // TODO: Settar usuario e senha
        loginRequest.setRequestObject(requestObject);
        loginRequest.execute(getApplicationContext());
        */

        TextView usuario = findViewById(R.id.txtLogin);
        TextView senha = findViewById(R.id.txtSenha);

        if(usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else {
            Usuario user = new Usuario(
                    usuario.getText().toString(),
                    senha.getText().toString()
            );
            this.dialog.show();
            Context context = this;
            this.loginController.logar(user, new ServerCallBack() {

                @Override
                public void onSucess(JSONObject result) {
                    dialog.hide();
                    ResponseBody resposta = JsonUtils.GSON.fromJson(result.toString(), ResponseBody.class);

                    if(ResponseCode.from(resposta.getResponseCode()).equals(ResponseCode.GenericSuccess)) {

                        Usuario user = UsuarioParser.parse(result);
                        // Compartilhado com toda a aplicação, acessado pela Key abaixo \/
                        SharedPreferences.Editor editor = getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                        editor.putBoolean("isLogado", true);
                        editor.putString("username", user.getUsuario());
                        // As chaves precisam ser persistidas
                        editor.apply();
                        dialog.dismiss();
                        Util.open_form__no_return(context, HomePage.class );
                    } else if (ResponseCode.from(resposta.getResponseCode()).equals(ResponseCode.IncorrectPassword)){
                        AlertDialogUtils.newSimpleDialog__OneButton(context,
                                "Atenção", R.drawable.ic_error,
                                "Usuário e/ou Senha incorretos","OK",
                                (dialog, id) -> { }).create().show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    dialog.hide();
                    error.printStackTrace();
                    AlertDialogUtils.newSimpleDialog__OneButton(context,
                            "Ops!", R.drawable.ic_error,
                            "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                    "\nVerifique sua conexão com a Internet e tente novamente","OK",
                            (dialog, id) -> { }).create().show();
                }
            });
        }
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
