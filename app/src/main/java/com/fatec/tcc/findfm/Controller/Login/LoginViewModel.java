package com.fatec.tcc.findfm.Controller.Login;

import android.app.ProgressDialog;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Request.ImageRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Http.Request.LoginRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Views.Login;

import java.util.Map;

public class LoginViewModel {

    private JsonTypedRequest<LoginRequest, ResponseBody, ErrorResponse> loginRequest;
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();

    private ProgressDialog dialog;
    private Login view;

    public LoginViewModel(Login view){
        this.view = view;
        this.dialog = new ProgressDialog(view);
    }

    public void init() {
        initRequests();
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    private void initRequests() {
        loginRequest = new JsonTypedRequest<>
                (
                        view,
                        Request.Method.POST,
                        LoginRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(view.getResources(),"login"),
                        null,
                        (ResponseBody response) ->
                        {
                            dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {

                                TokenData tokenData = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("tokenData"), TokenData.class);
                                User usuario = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("user"), User.class);

                                FindFM.setTokenData(view, tokenData);
                                FindFM.logarUsuario(view, TiposUsuario.fromKind(usuario.getKind()), usuario.getFullName());

                                if(usuario.getAvatar() != null) {
                                    ImageRequest imagemRequest = new ImageRequest(view, usuario.getAvatar().get_id(), 0, 0, ImageView.ScaleType.CENTER_CROP,
                                            (Bitmap bitmap) -> {
                                                dialog.dismiss();
                                                MidiaUtils.setImagemToParams(bitmap);
                                                MidiaUtils.setImagemToPref(view, bitmap);
                                                view.getUser(view);
                                            },
                                            error -> {
                                                dialog.dismiss();
                                                AlertDialogUtils.newSimpleDialog__OneButton(view, "Ops!", R.drawable.ic_error, error.getMessage(), "OK",
                                                        (dialog, id) -> {
                                                        }).create().show();
                                                view.getUser(view);
                                            }
                                    );
                                    imagemRequest.execute();
                                }
                                else{
                                    dialog.dismiss();
                                    view.getUser(view);
                                }
                            } else if (ResponseCode.from(response.getCode()).equals(ResponseCode.IncorrectPassword)){
                                dialog.hide();
                                AlertDialogUtils.newSimpleDialog__OneButton(view,
                                        "Atenção", R.drawable.ic_error,
                                        "Usuário e/ou Senha incorretos","OK",
                                        (dialog, id) -> { }).create().show();
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Response]Login", errorResponse.getMessage());
                                mensagem = errorResponse.getMessage();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(view, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        },
                        (VolleyError errorResponse) ->
                        {
                            dialog.hide();
                            String mensagem = "Ocorreu um erro ao tentar conectar com nossos servidores.\nVerifique sua conexão com a Internet e tente novamente.";
                            if(errorResponse != null) {
                                Log.e("[ERRO-Volley]Login", errorResponse.getMessage());
                                errorResponse.printStackTrace();
                            }
                            AlertDialogUtils.newSimpleDialog__OneButton(view, "Ops!", R.drawable.ic_error,
                                    mensagem, "OK", (dialog, id) -> { }).create().show();
                        }
                );
    }

    public void btnEntrar_Click(View v) {
        String usuario = this.email.get();
        String senha = this.password.get();

        if(usuario == null || usuario.isEmpty() || senha == null || senha.isEmpty() ) {
            Toast.makeText(v.getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.dialog.show();
        loginRequest.setRequest(new LoginRequest(usuario, senha));
        loginRequest.execute();
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

}
