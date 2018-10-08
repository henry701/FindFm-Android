package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.DownloadResourceService;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Estados;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.Telefone;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.BinaryResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.MidiaUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityPerfilFragmentBinding;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class Perfil_Fragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    public ActivityPerfilFragmentBinding binding;

    //TODO FORTE: atualizar o objeto na FindFM quando atualizar o carinha
    private String URL;

    private Usuario usuario;
    private Contratante contratante;
    private Musico musico;

    private TelaPrincipal activity;

    public Perfil_Fragment(){}

    @SuppressLint("ValidFragment")
    public Perfil_Fragment(TelaPrincipal activity, String URL){
        this.activity = activity;
        this.usuario = new Usuario();
        this.URL = URL;
        getUser();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            activity.getSupportActionBar().setTitle("Meu Perfil");
        } catch (Exception e){
            e.printStackTrace();
        }

        FindFM.setTelaAtual("MEU_PERFIL");
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_perfil_fragment, container, false);

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(activity, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf));

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.getViewModel().setUF(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        Formatadores addLineNumberFormatter = new Formatadores(new WeakReference<>(binding.txtTelefone));
        binding.txtTelefone.addTextChangedListener(addLineNumberFormatter);

        binding.cbUfContratante.setAdapter(spinnerAdapter);
        binding.cbUfContratante.setOnItemSelectedListener(onItemSelectedListener);

        binding.cbUfMusico.setAdapter(spinnerAdapter);
        binding.cbUfMusico.setOnItemSelectedListener(onItemSelectedListener);

        binding.setViewModel(new PerfilViewModel(activity, this, binding.circularImageView, binding.btnRemoverImagem, activity.getDialog(), binding.listaInstrumentos));
        return binding.getRoot();
    }

    public void btnFotoPerfil_Click(View v){
        startActivityForResult(Intent.createChooser(MidiaUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE);
    }

    public void btnRemoverImagem_Click(View v){
        binding.getViewModel().removerImagem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                binding.getViewModel().pickImage(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRegistrar_Click(View v){
        //TODO: validar os campos
        switch (getUsuario().getTipoUsuario()){
            case CONTRATANTE:
                binding.getViewModel().registrar(binding.getContratante());
                break;
            case MUSICO:
                binding.getViewModel().registrar(binding.getMusico());
                break;
        }
    }

    private void getUser() {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> registrarRequest = new JsonTypedRequest<>
                (       activity,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        URL,
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                User user= JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("usuario"), User.class);
                                this.usuario.setId(user.getId());
                                this.usuario.setTipoUsuario(TiposUsuario.fromKind(user.getKind()));
                                this.usuario.setNomeCompleto(user.getFullName());
                                this.usuario.setEmail(user.getEmail());
                                this.usuario.setTelefone(new Telefone(user.getTelefone().getStateCode(), user.getTelefone().getNumber()));
                                this.usuario.setFotoID(null);

                                if(user.getAvatar() != null){
                                    if(user.getAvatar().get_id() != null){
                                        this.usuario.setFotoID(user.getAvatar().get_id());
                                        DownloadResourceService downloadService = new DownloadResourceService(activity);
                                        downloadService.addObserver( (download, arg) -> {
                                            if(download instanceof DownloadResourceService) {
                                                activity.runOnUiThread(() -> {
                                                    if (arg instanceof BinaryResponse) {
                                                        byte[] dados = ((BinaryResponse) arg).getData();
                                                        InputStream input=new ByteArrayInputStream(dados);
                                                        Bitmap ext_pic = BitmapFactory.decodeStream(input);
                                                        binding.circularImageView.setImageBitmap(ext_pic);
                                                    } else{
                                                        AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                                                "Ops!", R.drawable.ic_error,
                                                                "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                                                        "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                                                (dialog, id1) -> { }).create().show();
                                                    }

                                                    activity.getDialog().hide();

                                                });
                                            }
                                        });
                                        downloadService.getResource(user.getAvatar().get_id());
                                        activity.getDialog().show();
                                    }
                                }
                                else {
                                    binding.circularImageView.setImageDrawable(activity.getDrawable(R.drawable.capaplaceholder_photo));
                                }

                                binding.setUsuario(this.usuario);

                                switch (getUsuario().getTipoUsuario()){
                                    case CONTRATANTE:
                                        Contratante contratante = new Contratante(usuario);
                                        contratante.setInauguracao(user.getDate());
                                        contratante.setCidade(user.getEndereco().getCidade());
                                        contratante.setUf(
                                                Estados.fromNome( user.getEndereco().getEstado() ).getSigla()
                                        );
                                        binding.setContratante(new Contratante(usuario));
                                        binding.getViewModel().setInauguracao(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(contratante.getInauguracao()));
                                        binding.getViewModel().setInauguracaoDate(contratante.getInauguracao());
                                        break;
                                    case MUSICO:
                                        Musico musico = new Musico(usuario);
                                        musico.setNascimento(user.getDate());
                                        musico.setCidade(user.getEndereco().getCidade());
                                        musico.setUf(
                                                Estados.fromNome( user.getEndereco().getEstado() ).getSigla()
                                        );
                                        musico.setInstrumentos(user.getIntrumentos());
                                        binding.cbUfMusico.setSelection(Estados.fromSigla(musico.getUf()).getIndex());
                                        binding.setMusico(musico);
                                        binding.getViewModel().setNascimento(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(musico.getNascimento()));
                                        binding.getViewModel().setNascimentoDate(musico.getNascimento());
                                        binding.getViewModel().updateList(musico.getInstrumentos(), usuario.getId().equals(FindFM.getUsuario().getId()));
                                        break;
                                }

                                binding.executePendingBindings();
                                binding.getViewModel().init();
                                this.tratarTela();
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            activity.getDialog().hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            activity.getDialog().hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        registrarRequest.execute();
        activity.getDialog().show();
    }

    private void tratarTela() {
        boolean itsMe = usuario.getId().equals(FindFM.getUsuario().getId());

        binding.circularImageView.setEnabled(itsMe);
        binding.txtNomeCompleto.setEnabled(itsMe);
        binding.txtTelefone.setEnabled(itsMe);
        binding.txtEmail.setEnabled(itsMe);

        binding.txtInauguracao.setEnabled(itsMe);
        binding.txtCapacidadeLocal.setEnabled(itsMe);
        binding.txtCidadeContratante.setEnabled(itsMe);
        binding.cbUfContratante.setEnabled(itsMe);
        binding.txtEndereco.setEnabled(itsMe);
        binding.txtNumeroEndereco.setEnabled(itsMe);

        binding.txtNascimento.setEnabled(itsMe);
        binding.txtCidadeMusico.setEnabled(itsMe);
        binding.cbUfMusico.setEnabled(itsMe);
        binding.listaInstrumentos.setEnabled(itsMe);

        if(itsMe){
            binding.txtSenha.setVisibility(View.VISIBLE);
            binding.txtConfirmaSenha.setVisibility(View.VISIBLE);
            binding.buttonRegistrar.setVisibility(View.VISIBLE);
        } else {
            try {
                activity.getSupportActionBar().setTitle("Perfil de: " + usuario.getNomeCompleto().split("\\s+")[0]);
            } catch (Exception e){
                e.printStackTrace();
            }
            FindFM.setTelaAtual("PERFIL_DE_OUTRO");
            binding.txtSenha.setVisibility(View.GONE);
            binding.txtConfirmaSenha.setVisibility(View.GONE);
            binding.buttonRegistrar.setVisibility(View.GONE);
        }

        if(usuario.getFotoID() != null && itsMe){
            binding.btnRemoverImagem.setVisibility(View.VISIBLE);
        } else {
            binding.btnRemoverImagem.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.hideSoftKeyboard(activity);
        switch (item.getItemId()){
            case R.id.action_refresh:
                getUser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
