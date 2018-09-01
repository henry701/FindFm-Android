package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.fatec.tcc.findfm.Controller.Perfil.PerfilViewModel;
import com.fatec.tcc.findfm.Model.Business.Banda;
import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Musico;
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
    private Banda banda;
    private Contratante contratante;
    private Musico musico;

    private TelaPrincipal activity;

    public Perfil_Fragment(){}

    @SuppressLint("ValidFragment")
    public Perfil_Fragment(TelaPrincipal activity){
        this.activity = activity;
        this.usuario = new Usuario();
        this.getUsuario().setNomeCompleto(FindFM.getNomeUsuario(activity));
        this.getUsuario().setFoto(FindFM.getFotoPrefBase64(activity));
        this.getUsuario().setTipoUsuario(FindFM.getTipoUsuario(activity));
    }

    public Usuario getUsuario() {
        return usuario;
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

        Spinner cb_uf_banda = binding.getRoot().findViewById(R.id.cb_uf_banda);
        cb_uf_banda.setAdapter(spinnerAdapter);
        cb_uf_banda.setOnItemSelectedListener(onItemSelectedListener);

        Spinner cb_uf_contratante = binding.getRoot().findViewById(R.id.cb_uf_contratante);
        cb_uf_contratante.setAdapter(spinnerAdapter);
        cb_uf_contratante.setOnItemSelectedListener(onItemSelectedListener);

        Spinner cb_uf_musico = binding.getRoot().findViewById(R.id.cb_uf_musico);
        cb_uf_musico.setAdapter(spinnerAdapter);
        cb_uf_musico.setOnItemSelectedListener(onItemSelectedListener);

        RecyclerView rc = binding.getRoot().findViewById(R.id.listaInstrumentos);

        binding.setViewModel(new PerfilViewModel(activity, this, imageView, btnRemoverImagem, activity.getDialog(), rc));
        binding.setUsuario(this.usuario);

        switch (getUsuario().getTipoUsuario()){
            case BANDA:
                binding.setBanda(this.banda);
                break;
            case CONTRATANTE:
                binding.setContratante(this.contratante);
                break;
            case MUSICO:
                binding.setMusico(this.musico);
                binding.getViewModel().updateList();
                break;
        }

        binding.executePendingBindings();
        binding.getViewModel().init();
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
            case BANDA:
                binding.getViewModel().registrar(this.banda);
                break;
            case CONTRATANTE:
                binding.getViewModel().registrar(this.contratante);
                break;
            case MUSICO:
                binding.getViewModel().registrar(this.musico);
                break;
        }
    }
}
