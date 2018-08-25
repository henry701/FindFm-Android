package com.fatec.tcc.findfm.Controller.Registrar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Contratante;
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
import com.fatec.tcc.findfm.Views.RegistrarContratante;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RegistrarContratanteViewModel {

    private RegistrarContratante view;

    public ObservableField<String> nomeEstabelecimento = new ObservableField<>();
    public ObservableField<String> inauguracao = new ObservableField<>();
    public ObservableField<String> capacidade = new ObservableField<>();
    public ObservableField<String> cidade = new ObservableField<>();
    public ObservableField<String> endereco = new ObservableField<>();
    public ObservableField<String> numeroEndereco = new ObservableField<>();

    private HttpTypedRequest<Contratante, ResponseBody, ErrorResponse> registrarRequest;
    private ProgressDialog dialog;
    private Bundle param = new Bundle();

    private String UF;
    private Date inauguracaoDate;

    public RegistrarContratanteViewModel(RegistrarContratante view){
        this.view = view;
    }

    public void init() {
        initRequests();
        ImageView imageView = view.findViewById(R.id.circularImageView);
        ImageButton btnRemoverImagem = view.findViewById(R.id.btnRemoverImagem);
        EditText txtInauguracao = view.findViewById(R.id.txtInauguracao);

        txtInauguracao.setShowSoftInputOnFocus(false);
        txtInauguracao.setInputType(InputType.TYPE_NULL);
        this.param = view.getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Registrar");
        this.dialog = new ProgressDialog(view);dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        ImagemUtils.setImagemToImageView(imageView, view, btnRemoverImagem);
    }

    private void initRequests() {
        registrarRequest = new HttpTypedRequest<>
                (
                        Request.Method.POST,
                        Contratante.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if (ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                TokenData tokenData = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("tokenData"), TokenData.class);
                                FindFM.setTokenData(tokenData);

                                SharedPreferences.Editor editor = view.getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                                editor.putBoolean("isLogado", true);
                                editor.putString("tipoUsuario", "CONTRATANTE");
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
                                            "\nVerifique sua conexão com a Internet e tente novamente", "OK",
                                    (dialog, id) -> {
                                    }).create().show();
                        }
                );
        registrarRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(), "metro_api/login/registrar"));
    }

    public void dismissDialog(){
        this.dialog.dismiss();
    }

    public void txtInauguracao_Click() {
        Util.hideSoftKeyboard(view);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            this.inauguracaoDate = myCalendar.getTime();
            this.inauguracao.set(sdf.format(myCalendar.getTime()));
        };
        new DatePickerDialog(view, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public void registrar() {
        if (this.nomeEstabelecimento.get() == null || this.inauguracao.get() == null || this.capacidade.get() == null || this.cidade.get() == null || this.endereco.get() == null || this.numeroEndereco.get() == null) {
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        } else if (nomeEstabelecimento.get().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O nome não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else if (inauguracao.get().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "Preencha uma data válida!", Toast.LENGTH_SHORT).show();
        } else if (inauguracaoDate.after(new Date())) {
            Toast.makeText(view.getApplicationContext(), "Não é permitido selecionar uma data futura!", Toast.LENGTH_SHORT).show();
        } else if (this.capacidade.get().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "Informe a capacidade do local!", Toast.LENGTH_SHORT).show();
        } else if (this.cidade.get().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O nome da cidade não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else if (this.endereco.get().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O endereço não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else if (this.numeroEndereco.get().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "Informe o número!", Toast.LENGTH_SHORT).show();
        } else {
            this.dialog.show();
            this.param.putString("nomeCompleto", this.nomeEstabelecimento.get());
            this.param.putString("cidade", this.cidade.get());
            this.param.putString("uf", UF);
            int capacidade = Integer.parseInt(this.capacidade.get());

            Contratante contratante = new Contratante(
                    param.getString("nomeUsuario"),
                    param.getString("senha"),
                    param.getString("email"),
                    param.getString("telefone"),
                    FindFM.getInstance().getParams().getByteArray("foto"),
                    false,
                    false,
                    param.getString("nomeCompleto"),
                    inauguracaoDate,
                    capacidade,
                    param.getString("cidade"),
                    param.getString("uf"),
                    this.endereco.get(),
                    Integer.parseInt(this.numeroEndereco.get())
            );

            registrarRequest.setRequestObject(contratante);
            registrarRequest.execute(view.getApplicationContext());
        }
    }
}
