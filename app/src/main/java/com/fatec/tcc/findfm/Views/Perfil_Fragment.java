package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Estados;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityPerfilFragmentBinding;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class Perfil_Fragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    public ActivityPerfilFragmentBinding binding;

    //TODO FORTE: atualizar o objeto na FindFM quando atualizar o carinha

    private Usuario usuario;
    private Contratante contratante;
    private Musico musico;

    private Spinner cb_uf_musico;
    private Spinner cb_uf_contratante;

    private TelaPrincipal activity;

    public Perfil_Fragment(){}

    @SuppressLint("ValidFragment")
    public Perfil_Fragment(TelaPrincipal activity){
        this.activity = activity;
        this.usuario = new Usuario();
        getUser();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activity.getSupportActionBar().setTitle("Meu Perfil");
        FindFM.setTelaAtual("MEU_PERFIL");
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_perfil_fragment, container, false);

        ImageView imageView = binding.getRoot().findViewById(R.id.circularImageView);
        ImageButton btnRemoverImagem =  binding.getRoot().findViewById(R.id.btnRemoverImagem);

        EditText txtTelefone = binding.getRoot().findViewById(R.id.txtTelefone);

        Formatadores addLineNumberFormatter = new Formatadores(new WeakReference<>(txtTelefone));
        txtTelefone.addTextChangedListener(addLineNumberFormatter);

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

        cb_uf_contratante = binding.getRoot().findViewById(R.id.cb_uf_contratante);
        cb_uf_contratante.setAdapter(spinnerAdapter);
        cb_uf_contratante.setOnItemSelectedListener(onItemSelectedListener);

        cb_uf_musico = binding.getRoot().findViewById(R.id.cb_uf_musico);
        cb_uf_musico.setAdapter(spinnerAdapter);

        cb_uf_musico.setOnItemSelectedListener(onItemSelectedListener);

        RecyclerView rc = binding.getRoot().findViewById(R.id.listaInstrumentos);

        binding.setViewModel(new PerfilViewModel(activity, this, imageView, btnRemoverImagem, activity.getDialog(), rc));
        return binding.getRoot();
    }

    public void btnFotoPerfil_Click(View v){
        startActivityForResult(Intent.createChooser(ImagemUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE);
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
        switch (getUsuario().getTipoUsuario()){
            case CONTRATANTE:
                binding.getViewModel().registrar(this.contratante);
                break;
            case MUSICO:
                binding.getViewModel().registrar(this.musico);
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
                        HttpUtils.buildUrl(activity.getResources(),"account", "me"),
                        null,
                        (ResponseBody response) ->
                        {
                            activity.getDialog().hide();
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                User user= JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("usuario"), User.class);
                                this.usuario.setTipoUsuario(TiposUsuario.fromKind(user.getKind()));
                                this.usuario.setNomeCompleto(user.getFullName());
                                this.usuario.setEmail(user.getEmail());
                                this.usuario.setTelefone(user.getTelefone().getStateCode() + user.getTelefone().getNumber());
                                this.usuario.setFoto(FindFM.getFotoPrefBase64(activity));

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
                                        cb_uf_musico.setSelection(Estados.fromSigla(musico.getUf()).getIndex());
                                        binding.setMusico(musico);
                                        binding.getViewModel().setNascimento(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(musico.getNascimento()));
                                        binding.getViewModel().setNascimentoDate(musico.getNascimento());
                                        binding.getViewModel().updateList(musico.getInstrumentos());
                                        break;
                                }

                                binding.executePendingBindings();
                                binding.getViewModel().init();
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
        SharedRequestQueue.addToRequestQueue(activity.getApplicationContext(), registrarRequest);
        activity.getDialog().show();
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
