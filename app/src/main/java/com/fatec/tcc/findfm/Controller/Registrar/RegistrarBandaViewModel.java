package com.fatec.tcc.findfm.Controller.Registrar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Banda;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.RegistrarBanda;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class RegistrarBandaViewModel {

    public ObservableField<String> nomeBanda = new ObservableField<>();
    public ObservableField<String> formacao = new ObservableField<>();
    public ObservableField<String> cidade = new ObservableField<>();
    public ObservableField<String> uf = new ObservableField<>();
    public ObservableField<String> numeroIntegrantes = new ObservableField<>();

    private Bundle param = new Bundle();

    private HttpTypedRequest<Banda, ResponseBody, ErrorResponse> registrarRequest;
    private Date formacaoDate;
    private ProgressDialog dialog;
    private String UF;

    private RegistrarBanda view;

    public RegistrarBandaViewModel(RegistrarBanda registrarBanda) {
        this.view = registrarBanda;
    }

    public void init(){
        initRequests();
        ImageView imageView = view.findViewById(R.id.circularImageView);
        ImageButton btnRemoverImagem = view.findViewById(R.id.btnRemoverImagem);
        EditText txtFormacao = view.findViewById(R.id.txtFormacao);
        txtFormacao.setShowSoftInputOnFocus(false);
        txtFormacao.setInputType(InputType.TYPE_NULL);
        this.param = view.getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Registrar");
        this.dialog = new ProgressDialog(view);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        ImagemUtils.setImagemToImageView(imageView, view, btnRemoverImagem);
    }

    private void initRequests() {
        registrarRequest = new HttpTypedRequest<>
                (
                        Request.Method.POST,
                        Banda.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                TokenData tokenData = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("tokenData"), TokenData.class);
                                FindFM.setTokenData(tokenData);
                                FindFM.logarUsuario(view, TiposUsuario.BANDA, param.getString("nomeCompleto", ""));
                                FindFM.setFotoPref(view, FindFM.getImagemPerfilBase64());
                                dialog.dismiss();
                                Util.open_form__no_return(view, TelaPrincipal.class);
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
        registrarRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(),"register/band"));
    }

    public void registrar () {

        if( this.nomeBanda.get() == null || this.formacao.get() == null || this.numeroIntegrantes.get() == null || this.cidade.get() == null){
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if(this.nomeBanda.get().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "O nome não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( this.formacao.get().isEmpty() ) {
            Toast.makeText(view.getApplicationContext(), "Preencha uma data válida!", Toast.LENGTH_SHORT).show();
        }
        else if ( formacaoDate.after(new Date()) ) {
            Toast.makeText(view.getApplicationContext(), "Não é permitido selecionar uma data futura!", Toast.LENGTH_SHORT).show();
        }
        else if( this.cidade.get().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "O nome da cidade não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if( this.numeroIntegrantes.get().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "Informe o número de integrantes!", Toast.LENGTH_SHORT).show();
        }
        else {
            this.dialog.show();
            this.param.putString("nomeCompleto", this.nomeBanda.get());
            this.param.putString("cidade", this.cidade.get());
            this.param.putString("uf", UF);

            Banda banda = new Banda(
                    param.getString("nomeUsuario"),
                    param.getString("senha"),
                    param.getString("email"),
                    param.getString("telefone"),
                    FindFM.getImagemPerfilBase64(),
                    false,
                    false,
                    param.getString("nomeCompleto"),
                    formacaoDate,
                    null,
                    Integer.parseInt(this.numeroIntegrantes.get()),
                    param.getString("cidade"),
                    param.getString("uf")
            );

            registrarRequest.setRequestObject(banda);
            registrarRequest.execute(view.getApplicationContext());
        }
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

    public void setUF(String uf){
        this.UF = uf;
    }

    public void txtFormacaoClick() {
        Util.hideSoftKeyboard(view);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            this.formacaoDate = myCalendar.getTime();
            this.formacao.set(sdf.format(myCalendar.getTime()));
        };
        new DatePickerDialog(view, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
