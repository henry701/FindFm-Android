package com.fatec.tcc.findfm.Controller.Login;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Http.Request.LoginRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Login;
import com.fatec.tcc.findfm.Views.TelaPrincipal;
import com.google.gson.Gson;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoginViewModel {

    public ObservableField<String> usuario = new ObservableField<>();
    public ObservableField<String> senha = new ObservableField<>();
    private HttpTypedRequest<LoginRequest, ResponseBody, ErrorResponse> loginRequest;
    private ProgressDialog dialog;
    private Login view;

    public LoginViewModel(Login view){
        this.view = view;
    }

    public void init() {
        initRequests();
        this.dialog = new ProgressDialog(view);
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
                                SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
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
                                Util.open_form__no_return(view, TelaPrincipal.class );
                            } else if (ResponseCode.from(response.getResponseCode()).equals(ResponseCode.IncorrectPassword)){
                                AlertDialogUtils.newSimpleDialog__OneButton(view,
                                        "Atenção", R.drawable.ic_error,
                                        "Usuário e/ou Senha incorretos","OK",
                                        (dialog, id) -> { }).create().show();
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (Exception error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        loginRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(),"login"));
    }

    public void btnEntrar_Click(View v) {
        String usuario = this.usuario.get();
        String senha = this.senha.get();

        if(usuario == null || usuario.isEmpty() || senha == null || senha.isEmpty() ) {
            Toast.makeText(v.getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }
        LoginRequest requestObject = new LoginRequest(usuario, senha );
        this.dialog.show();
        loginRequest.setRequestObject(requestObject);
        loginRequest.execute(v.getContext());
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

}