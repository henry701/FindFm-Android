package com.fatec.tcc.findfm.Controller.Registrar;

import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fatec.tcc.findfm.Model.Business.Telefone;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Registrar;
import com.fatec.tcc.findfm.Views.RegistrarContratante;
import com.fatec.tcc.findfm.Views.RegistrarMusico;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class RegistrarViewModel {

    public ObservableField<String> telefone = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> senha = new ObservableField<>();
    public ObservableField<String> confirmaSenha = new ObservableField<>();
    private ImageView imageView;
    private ImageButton btnRemoverImagem;

    private Registrar view;
    private Bundle param = new Bundle();

    public RegistrarViewModel(Registrar v){
        this.view = v;
    }

    public void init(){
        this.imageView = view.findViewById(R.id.circularImageView);
        this.btnRemoverImagem = view.findViewById(R.id.btnRemoverImagem);
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        EditText txtTelefone = view.findViewById(R.id.txtTelefone);

        Formatadores addLineNumberFormatter = new Formatadores(new WeakReference<>(txtTelefone));
        txtTelefone.addTextChangedListener(addLineNumberFormatter);

        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        FindFM.setImagemPerfilParams(null);
    }

    public void pickImage(Intent data) throws FileNotFoundException {
        this.imageView.setImageBitmap(BitmapFactory.decodeStream(view.getApplicationContext()
                .getContentResolver().openInputStream(Objects.requireNonNull(data.getData()))));
        this.btnRemoverImagem.setVisibility(View.VISIBLE);

        ImagemUtils.setImagemToParams(this.imageView);
    }

    public void registrar() {

        RadioGroup tipoContaGrupo = view.findViewById(R.id.grupoTipoConta);
        RadioButton tipoConta;

        int selectedId = -1;
        boolean isTelefonevalido = Formatadores.validarTelefone(telefone.get());
        boolean isEmailValido = Formatadores.validarEmail(this.email.get());

        if( tipoContaGrupo.getCheckedRadioButtonId() != -1 ) {
            selectedId = tipoContaGrupo.getCheckedRadioButtonId();
        }
        if ( this.email.get() == null || this.senha.get() == null || this.confirmaSenha.get() == null){
            Toast.makeText(view.getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if(!isTelefonevalido){
            Toast.makeText(view.getApplicationContext(), "Insira um telefone válido!", Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValido) {
            Toast.makeText(view.getApplicationContext(), "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
        }
        else if (this.senha.get().length() < 6){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ter menos de 6 caracteres!", Toast.LENGTH_SHORT).show();
        }
        else if(this.senha.get().trim().isEmpty() || this.confirmaSenha.get().trim().isEmpty() ){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ser vazia ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( !this.senha.get().equals(this.confirmaSenha.get())) {
            Toast.makeText(view.getApplicationContext(), "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
        }
        else if( selectedId == -1 )
        {
            Toast.makeText(view.getApplicationContext(), "Selecione um tipo de conta!", Toast.LENGTH_SHORT).show();
        }
        else {
            this.telefone.set(this.tratarTelefone());
            tipoConta = view.findViewById(selectedId);
            Telefone telefone = getTelefone();
            this.param.putString("ddd", telefone.getStateCode());
            this.param.putString("telefone",    telefone.getNumber());
            this.param.putString("email",       this.email.get());
            this.param.putString("senha",       this.senha.get());

            String path = "com.fatec.tcc.findfm.Views.Registrar";

            switch (tipoConta.getText().toString()){
                case "Contratante":
                    Util.open_form_withParam(view.getApplicationContext(), RegistrarContratante.class, path, this.param);
                    break;
                case "Artísta":
                    Util.open_form_withParam(view.getApplicationContext(), RegistrarMusico.class, path, this.param);
                    break;
            }
        }
    }

    private String tratarTelefone(){
        String telefone = null;

        if(this.telefone.get() != null ) {
            String[] telefoneBuild = this.telefone.get().trim().split("\\(");
            telefone = telefoneBuild[1];
            telefoneBuild = telefone.split("\\)");
            telefone = telefoneBuild[0];
            telefoneBuild = telefoneBuild[1].trim().split("-");
            telefone += telefoneBuild[0] + telefoneBuild[1];
        }
        return telefone;
    }

    private Telefone getTelefone(){
        Telefone telefone1 = new Telefone();

        if(this.telefone.get() != null ) {
            if(this.telefone.get().length() >= 10) {
                //DDD
                telefone1.setStateCode(this.telefone.get().substring(0,2));
                //NUMERO
                telefone1.setNumber(this.telefone.get().substring(2));
            }
        }
        return telefone1;
    }

    public void removerImagem() {
        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        FindFM.setImagemPerfilParams(null);
    }

    public void setFoto(){
        ImagemUtils.setImagemPerfilToImageView(this.imageView, view, btnRemoverImagem);
    }
}
