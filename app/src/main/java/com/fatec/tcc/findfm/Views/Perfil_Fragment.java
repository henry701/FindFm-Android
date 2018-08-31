package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.databinding.ActivityPerfilFragmentBinding;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class Perfil_Fragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    public ActivityPerfilFragmentBinding binding;

    private Usuario usuario;
    private TiposUsuario tipoUsuario;

    private ProgressDialog dialog;
    private Spinner cb_uf;
    private String UF;
    private TelaPrincipal activity;

    public Perfil_Fragment(){}

    @SuppressLint("ValidFragment")
    public Perfil_Fragment(TelaPrincipal activity){
        this.activity = activity;
        this.usuario = new Usuario();
        this.getUsuario().setNomeCompleto(FindFM.getNomeUsuario(activity));
        this.getUsuario().setFoto(FindFM.getFotoPrefBase64(activity));
        this.tipoUsuario = FindFM.getTipoUsuario(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FindFM.setTelaAtual("MEU_PERFIL");

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_perfil_fragment, container, false);

        ImageView imageView = binding.getRoot().findViewById(R.id.circularImageView);
        ImageButton btnRemoverImagem =  binding.getRoot().findViewById(R.id.btnRemoverImagem);

        EditText txtTelefone = binding.getRoot().findViewById(R.id.txtTelefone);

        Formatadores addLineNumberFormatter = new Formatadores(new WeakReference<>(txtTelefone));
        txtTelefone.addTextChangedListener(addLineNumberFormatter);

        this.cb_uf = binding.getRoot().findViewById(R.id.cb_uf);
        this.cb_uf.setAdapter(
                new ArrayAdapter<>(activity, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        this.cb_uf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UF = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.setViewModel(new PerfilViewModel(activity, this, imageView, btnRemoverImagem));
        binding.setUsuario(this.usuario);
        binding.executePendingBindings();
        binding.getViewModel().init();

        //Switch do tipo de perfil para incluir resto da tela
        if(tipoUsuario.equals(TiposUsuario.MUSICO)){
            EditText endereco = binding.getRoot().findViewById(R.id.txtEndereco);
            endereco.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();
    }

    public void btnFotoPerfil_Click(View v){
        startActivityForResult(Intent.createChooser(ImagemUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE);
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
        binding.getViewModel().registrar(this.usuario);
    }

    public void btnRemoverImagem_Click(View v){
        binding.getViewModel().removerImagem();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TiposUsuario getTipoUsuario() {
        return tipoUsuario;
    }
}
