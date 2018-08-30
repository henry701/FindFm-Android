package com.fatec.tcc.findfm.Controller.Perfil;

import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Perfil_Fragment;
import com.fatec.tcc.findfm.Views.RegistrarBanda;
import com.fatec.tcc.findfm.Views.RegistrarContratante;
import com.fatec.tcc.findfm.Views.RegistrarMusico;
import com.fatec.tcc.findfm.Views.TelaPrincipal;

import java.io.FileNotFoundException;
import java.util.Objects;

public class PerfilViewModel {

    public ObservableField<String> cidade = new ObservableField<>();
    public ObservableField<String> uf = new ObservableField<>();
    public ObservableField<String> endereco = new ObservableField<>();
    public ObservableField<String> numero = new ObservableField<>();
    public ObservableField<String> confirmaSenha = new ObservableField<>();

    private ImageView imageView;
    private ImageButton btnRemoverImagem;

    private Bundle param = new Bundle();
    private TelaPrincipal view;
    private Perfil_Fragment fragment;

    public PerfilViewModel(TelaPrincipal v, Perfil_Fragment fragment, ImageView imageView, ImageButton imageButton){
        this.view = v;
        this.fragment = fragment;
        this.imageView = imageView;
        this.btnRemoverImagem = imageButton;
    }

    public void init(){
        ImagemUtils.setImagemToImageView(imageView, view, btnRemoverImagem);

        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        FindFM.setImagemPerfilParams(null);
    }

    public void pickImage(Intent data) throws FileNotFoundException {
        this.imageView.setImageBitmap(BitmapFactory.decodeStream(view.getApplicationContext()
                .getContentResolver().openInputStream(Objects.requireNonNull(data.getData()))));
        this.btnRemoverImagem.setVisibility(View.VISIBLE);

        ImagemUtils.setImagemToParams(this.imageView);
    }

    public void registrar(Usuario usuario) {

        RadioGroup tipoContaGrupo = view.findViewById(R.id.grupoTipoConta);
        RadioButton tipoConta;

        int selectedId = -1;
        boolean isTelefonevalido = Formatadores.validarTelefone(usuario.getTelefone());
        boolean isEmailValido = Formatadores.validarEmail(usuario.getEmail());

        if( tipoContaGrupo.getCheckedRadioButtonId() != -1 ) {
            selectedId = tipoContaGrupo.getCheckedRadioButtonId();
        }
        if (usuario.getUsuario() == null || usuario.getEmail() == null || usuario.getSenha() == null || this.confirmaSenha.get() == null){
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if(usuario.getUsuario().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "Seu nome de usuário não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if(!isTelefonevalido){
            Toast.makeText(view.getApplicationContext(), "Insira um telefone válido!", Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValido) {
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
        else if( selectedId == -1 )
        {
            Toast.makeText(view.getApplicationContext(), "Selecione um tipo de conta!", Toast.LENGTH_SHORT).show();
        }
        else {
            usuario.setTelefone(this.tratarTelefone(usuario.getTelefone()));
            tipoConta = view.findViewById(selectedId);

            this.param.putString("nomeUsuario", usuario.getUsuario());
            this.param.putString("telefone",    usuario.getTelefone());
            this.param.putString("email",       usuario.getEmail());
            this.param.putString("senha",       usuario.getSenha());

            String path = "com.fatec.tcc.findfm.Views.Registrar";

            switch (tipoConta.getText().toString()){
                case "Banda":
                    Util.open_form_withParam(view.getApplicationContext(), RegistrarBanda.class, path, this.param);
                    break;
                case "Contratante":
                    Util.open_form_withParam(view.getApplicationContext(), RegistrarContratante.class, path, this.param);
                    break;
                case "Músico":
                    Util.open_form_withParam(view.getApplicationContext(), RegistrarMusico.class, path, this.param);
                    break;
            }
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

    public void setFoto(){
        ImagemUtils.setImagemToImageView(this.imageView, view, btnRemoverImagem);
    }
}
