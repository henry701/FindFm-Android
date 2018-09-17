package com.fatec.tcc.findfm.Controller.Perfil;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Request.AtualizarUsuarioRequest;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.TokenData;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Adapters.AdapterInstrumentos;
import com.fatec.tcc.findfm.Views.Perfil_Fragment;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PerfilViewModel {

    public ObservableField<String> confirmaSenha = new ObservableField<>();

    public ObservableField<String> nascimento = new ObservableField<>();
    private Date nascimentoDate;
    public ObservableField<String> inauguracao = new ObservableField<>();
    private Date inauguracaoDate;

    private HttpTypedRequest<AtualizarUsuarioRequest, ResponseBody, ErrorResponse> registrarRequest;
    private String UF;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;
    private RecyclerView rc;

    private ProgressDialog dialog;
    private Bundle param = new Bundle();
    private TelaPrincipal view;
    private Perfil_Fragment fragment;

    public PerfilViewModel(TelaPrincipal v, Perfil_Fragment fragment, ImageView imageView, ImageButton imageButton, ProgressDialog dialog, RecyclerView rc){
        this.view = v;
        this.fragment = fragment;
        this.imageView = imageView;
        this.btnRemoverImagem = imageButton;
        this.dialog = dialog;
        this.rc = rc;
    }

    public void init(){
        ImagemUtils.setImagemToImageView_FromPref(imageView, view, btnRemoverImagem);
        initRequests();
    }

    public void pickImage(Intent data) throws FileNotFoundException {
        this.imageView.setImageBitmap(BitmapFactory.decodeStream(view.getApplicationContext()
                .getContentResolver().openInputStream(Objects.requireNonNull(data.getData()))));
        this.btnRemoverImagem.setVisibility(View.VISIBLE);

        ImagemUtils.setImagemToParams(this.imageView);
    }

    public void registrar(Contratante contratante) {

        if( !this.validarCampos(contratante))
            return;
        else {
            contratante.setTelefone(this.tratarTelefone(contratante.getTelefone()));

            this.param.putString("telefone",    contratante.getTelefone());
            this.param.putString("email",       contratante.getEmail());
            this.param.putString("senha",       contratante.getSenha());

        }
    }

    public void registrar(Musico musico) {

        if( !this.validarCampos(musico))
            return;
        else {
            musico.setTelefone(this.tratarTelefone(musico.getTelefone()));

            this.param.putString("telefone",    musico.getTelefone());
            this.param.putString("email",       musico.getEmail());
            this.param.putString("senha",       musico.getSenha());

        }
    }

    private String tratarTelefone(String fone){
        String telefone = null;

        if(fone != null ) {
            String[] telefoneBuild = fone.trim().split("\\(");
            telefone = telefoneBuild[1];
            telefoneBuild = telefone.split("\\)");
            telefone = telefoneBuild[0];
            telefoneBuild = telefoneBuild[1].trim().split("-");
            telefone += telefoneBuild[0] + telefoneBuild[1];
        }
        return telefone;
    }

    public void removerImagem() {
        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        FindFM.setImagemPerfilParams(null);
    }

    public void btnFotoPerfil_Click(View v){
        fragment.btnFotoPerfil_Click(v);
    }

    public void btnRegistrar_Click(View v){
        fragment.btnRegistrar_Click(v);
    }

    private boolean validarCampos(Usuario usuario){
        if(usuario == null) {
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if ( usuario.getEmail() == null || usuario.getSenha() == null ||
                this.confirmaSenha.get() == null || usuario.getTelefone() == null || this.UF == null){
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if(!Formatadores.validarTelefone(usuario.getTelefone())){
            Toast.makeText(view.getApplicationContext(), "Insira um telefone válido!", Toast.LENGTH_SHORT).show();
        }
        else if (!Formatadores.validarEmail(usuario.getEmail())) {
            Toast.makeText(view.getApplicationContext(), "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
        }
        else if (usuario.getSenha().length() < 6){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ter menos de 6 caracteres!", Toast.LENGTH_SHORT).show();
        }
        else if(usuario.getSenha().trim().isEmpty() || this.confirmaSenha.get().trim().isEmpty() ){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ser vazia ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( !usuario.getSenha().equals(this.confirmaSenha.get())) {
            Toast.makeText(view.getApplicationContext(), "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
        }
        else if (usuario instanceof Contratante ){
            return validarCampos_Contratante((Contratante) usuario);
        }
        else if (usuario instanceof Musico ){
            return validarCampos_Musico((Musico) usuario);
        }
        else{
            return true;
        }

        return false;
    }


    private boolean validarCampos_Contratante(Contratante contratante){
        if ( contratante.getNomeCompleto() == null || this.inauguracao.get() == null || contratante.getCapacidadeLocal() == 0 ||
                contratante.getCidade() == null || contratante.getEndereco() == null || contratante.getNumero() == 0) {
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        } else if (contratante.getNomeCompleto().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O nome não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else if (inauguracao.get().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "Preencha uma data válida!", Toast.LENGTH_SHORT).show();
        } else if (inauguracaoDate.after(new Date())) {
            Toast.makeText(view.getApplicationContext(), "Não é permitido selecionar uma data futura!", Toast.LENGTH_SHORT).show();
        } else if (contratante.getCidade().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O nome da cidade não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else if (contratante.getEndereco().trim().isEmpty()) {
            Toast.makeText(view.getApplicationContext(), "O endereço não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private boolean validarCampos_Musico(Musico musico){
        AdapterInstrumentos adapter = (AdapterInstrumentos) rc.getAdapter();
        List<Instrumento> instrumentos = new ArrayList<>();

        if(adapter != null)
            instrumentos.addAll(adapter.getInstrumentos());

        if( musico.getNomeCompleto() == null || this.nascimento.get() == null || musico.getCidade() == null) {
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
        else if( musico.getNomeCompleto().trim().isEmpty()){
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
        else if( musico.getCidade().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "O nome da cidade não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( instrumentos.isEmpty() && adapter != null ) {
            Toast.makeText(view.getApplicationContext(), "Selecione ao menos um instrumento!", Toast.LENGTH_SHORT).show();
        }
        else {
            return true;
        }
        return false;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    private void initRequests() {
        registrarRequest = new HttpTypedRequest<>
                (       view,
                        Request.Method.POST,
                        AtualizarUsuarioRequest.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                TokenData tokenData = JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("tokenData"), TokenData.class);
                                FindFM.setTokenData(view, tokenData);
                                FindFM.logarUsuario(view, TiposUsuario.MUSICO, param.getString("nomeCompleto", ""));
                                FindFM.setFotoPref(view, FindFM.getImagemPerfilBase64());

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

    public void txtNascimento_Click(View v){
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

    public void txtInauguracao_Click(View v) {
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

    public void updateList(List<Instrumento> instrumentosUsuario) {
        HttpTypedRequest<Instrumento, ResponseBody, ErrorResponse> instrumentoRequest = new HttpTypedRequest<>
                (       view,
                        Request.Method.GET,
                        Instrumento.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                List<Instrumento> instrumentos = new ArrayList<>();
                                for(String instrumento : (ArrayList<String>) response.getData()) {
                                    instrumentos.add(
                                            new Instrumento(instrumento, NivelHabilidade.INICIANTE)
                                    );
                                }

                                rc.setAdapter( new AdapterInstrumentos(instrumentos, view).setInstrumentosUsuario(instrumentosUsuario) );
                                RecyclerView.LayoutManager layout = new LinearLayoutManager(view,
                                        LinearLayoutManager.VERTICAL, false);
                                rc.setLayoutManager( layout );
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

                            TextView lb_selecione_titulo = view.findViewById(R.id.lb_selecione_titulo);
                            TextView lb_instrumento = view.findViewById(R.id.lb_instrumento);
                            TextView lb_habilidade = view.findViewById(R.id.lb_habilidade);

                            lb_selecione_titulo.setVisibility(View.GONE);
                            lb_instrumento.setVisibility(View.GONE);
                            lb_habilidade.setVisibility(View.GONE);
                            rc.setVisibility(View.GONE);

                            AlertDialogUtils.newSimpleDialog__OneButton(view,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        instrumentoRequest.setFullUrl(HttpUtils.buildUrl(view.getResources(),"instruments"));
        dialog.show();
        instrumentoRequest.execute(view);
    }

    public void setNascimento(String nascimento) {
        this.nascimento.set(nascimento);
    }

    public void setNascimentoDate(Date nascimentoDate) {
        this.nascimentoDate = nascimentoDate;
    }

    public void setInauguracao(String inauguracao) {
        this.inauguracao.set(inauguracao);
    }

    public void setInauguracaoDate(Date inauguracaoDate) {
        this.inauguracaoDate = inauguracaoDate;
    }
}
