package com.fatec.tcc.findfm.Controller.Login;

import android.app.ProgressDialog;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fatec.tcc.findfm.Infrastructure.Request.ImageRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
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
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Login;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

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
        dialog.setMessage("Carregando...");
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
                                                ImagemUtils.setImagemToParams(bitmap);
                                                ImagemUtils.setImagemToPref(view, bitmap);
                                                Util.open_form__no_return(view, TelaPrincipal.class );
                                            },
                                            error -> {
                                                dialog.dismiss();
                                                AlertDialogUtils.newSimpleDialog__OneButton(view, "Ops!", R.drawable.ic_error, error.getMessage(), "OK",
                                                        (dialog, id) -> {
                                                        }).create().show();
                                                Util.open_form__no_return(view, TelaPrincipal.class );
                                            }
                                    );
                                    imagemRequest.execute();
                                }
                                else{
                                    dialog.dismiss();
                                    Util.open_form__no_return(view, TelaPrincipal.class );
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
                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
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
        SharedRequestQueue.addToRequestQueue(v.getContext(), loginRequest);
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

}
