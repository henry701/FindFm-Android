package com.fatec.tcc.findfm.Controller.Registrar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterInstrumentos;
import com.fatec.tcc.findfm.Views.RegistrarMusico;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RegistrarMusicoViewModel {

    private RegistrarMusico view;
    private RecyclerView rc;
    private HttpTypedRequest<Musico, ResponseBody, ErrorResponse> registrarRequest;
    private ProgressDialog dialog;
    private Bundle param = new Bundle();

    public ObservableField<String> nomeCompleto = new ObservableField<>();
    public ObservableField<String> nascimento = new ObservableField<>();
    public ObservableField<String> cidade = new ObservableField<>();

    private String UF;
    private Date nascimentoDate;

    public RegistrarMusicoViewModel(RegistrarMusico view){
        this.view = view;
    }

    public void init(){
        initRequests();
        this.rc = view.findViewById(R.id.listaInstrumentos);
        EditText txtNascimento = view.findViewById(R.id.txtNascimento);
        txtNascimento.setShowSoftInputOnFocus(false);
        txtNascimento.setInputType(InputType.TYPE_NULL);
        this.param = view.getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Registrar");
        this.dialog = new ProgressDialog(view);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    private void initRequests() {
        registrarRequest = new HttpTypedRequest<>
                (
                        Request.Method.POST,
                        Musico.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                TokenData tokenData = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("tokenData"), TokenData.class);
                                FindFM.setTokenData(tokenData);

                                SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                                editor.putBoolean("isLogado", true);
                                editor.putString("tipoUsuario", "MUSICO");
                                editor.putString("nomeUsuario", param.getString("nomeCompleto"));
                                editor.apply();
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
        registrarRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(),"register/musician"));
    }

    public void registrar(){
        AdapterInstrumentos adapter = (AdapterInstrumentos) rc.getAdapter();
        List<Instrumento> instrumentos = new ArrayList<>();
        instrumentos.addAll(adapter.getInstrumentos());

        if(this.nomeCompleto.get() == null || this.nascimento.get() == null || this.cidade.get() == null) {
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
        else if(this.nomeCompleto.get().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "Seu nome não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( this.nascimento.get().isEmpty() ) {
            Toast.makeText(view.getApplicationContext(), "Preencha uma data válida!", Toast.LENGTH_SHORT).show();
        }
        else if ( nascimentoDate.after(new Date())) {
            Toast.makeText(view.getApplicationContext(), "Não é permitido selecionar uma data futura!", Toast.LENGTH_SHORT).show();
        }
        else if ( Formatadores.converterData(nascimentoDate).get(Calendar.YEAR) + 18 > Calendar.getInstance().get(Calendar.YEAR)) {
            Toast.makeText(view.getApplicationContext(), "O usuário não pode ser menor de 18 anos!", Toast.LENGTH_SHORT).show();
        }
        else if(this.cidade.get().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "O nome da cidade não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( instrumentos.isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "Selecione ao menos um instrumento!", Toast.LENGTH_SHORT).show();
        }
        else {
            this.dialog.show();
            this.param.putString("nomeCompleto", this.nomeCompleto.get());
            this.param.putString("cidade", this.cidade.get());
            this.param.putString("uf", UF);

            Musico musico = new Musico(
                    param.getString("nomeUsuario"),
                    param.getString("senha"),
                    param.getString("email"),
                    param.getString("telefone"),
                    FindFM.getInstance().getParams().getByteArray("foto"),
                    false,
                    false,
                    param.getString("nomeCompleto"),
                    nascimentoDate,
                    instrumentos,
                    param.getString("cidade"),
                    param.getString("uf")
            );
            registrarRequest.setRequestObject(musico);
            registrarRequest.execute(view.getApplicationContext());
        }
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

    public void txtNascimento_Click(){
        Util.hideSoftKeyboard(view);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            this.nascimentoDate = myCalendar.getTime();
            this.nascimento.set(sdf.format(myCalendar.getTime()));
        };
        new DatePickerDialog(view, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setUF(String UF) {
        this.UF = UF;
    }
}
