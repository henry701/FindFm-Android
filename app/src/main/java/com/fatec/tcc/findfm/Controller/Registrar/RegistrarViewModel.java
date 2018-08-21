package com.fatec.tcc.findfm.Controller.Registrar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.Formatadores;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.Views.Registrar;
import com.fatec.tcc.findfm.Views.RegistrarBanda;
import com.fatec.tcc.findfm.Views.RegistrarContratante;
import com.fatec.tcc.findfm.Views.RegistrarMusico;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class RegistrarViewModel {
/*
* TextView txtNomeUsuario = view.findViewById(R.id.txtUsuario);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        TextView txtSenha = view.findViewById(R.id.txtSenha);
        TextView txtConfirmaSenha = view.findViewById(R.id.txtConfirmaSenha);
        RadioGroup tipoContaGrupo = view.findViewById(R.id.grupoTipoConta);
* */
    public ObservableField<String> nomeUsuario = new ObservableField<>();
    public ObservableField<String> telefone = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> senha = new ObservableField<>();
    public ObservableField<String> confirmaSenha = new ObservableField<>();
    //public ObservableField<Integer> tipoConta = new ObservableField<>();
    private ImageView imageView;
    private ImageButton btnRemoverImagem;
    private EditText txtTelefone;
    private ProgressDialog dialog;
    private Registrar view;
    private Bundle param = new Bundle();

    public RegistrarViewModel(Registrar v){
        this.view = v;
    }
//TODO: Data binding
    public void init(){
        this.imageView = view.findViewById(R.id.circularImageView);
        this.btnRemoverImagem = view.findViewById(R.id.btnRemoverImagem);
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        this.txtTelefone = view.findViewById(R.id.txtTelefone);

        Formatadores addLineNumberFormatter = new Formatadores(new WeakReference<>(this.txtTelefone));
        this.txtTelefone.addTextChangedListener(addLineNumberFormatter);

        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        FindFM.getInstance().getParams().putByteArray("foto", null);
    }

    public void pickImage(Intent data) throws FileNotFoundException {
        this.imageView.setImageBitmap(BitmapFactory.decodeStream(view.getApplicationContext()
                .getContentResolver().openInputStream(data.getData())));
        this.btnRemoverImagem.setVisibility(View.VISIBLE);

        Bitmap bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        FindFM.getInstance().getParams().putByteArray("foto", baos.toByteArray());
    }

    public void btn_Registrar_Click() {
        TextView txtNomeUsuario = view.findViewById(R.id.txtUsuario);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        TextView txtSenha = view.findViewById(R.id.txtSenha);
        TextView txtConfirmaSenha = view.findViewById(R.id.txtConfirmaSenha);
        RadioGroup tipoContaGrupo = view.findViewById(R.id.grupoTipoConta);
        RadioButton tipoConta;

        int selectedId = -1;
        boolean isTelefonevalido = Formatadores.validarTelefone(this.txtTelefone.getText().toString());
        boolean isEmailValido = Formatadores.validarEmail(txtEmail.getText().toString());

        if( tipoContaGrupo.getCheckedRadioButtonId() != -1 ) {
            selectedId = tipoContaGrupo.getCheckedRadioButtonId();
        }

        if(txtNomeUsuario.getText().toString().trim().isEmpty()){
            Toast.makeText(view.getApplicationContext(), "Seu nome de usuário não pode ser vazio ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if(!isTelefonevalido){
            Toast.makeText(view.getApplicationContext(), "Insira um telefone válido!", Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValido) {
            Toast.makeText(view.getApplicationContext(), "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
        }
        else if (txtSenha.length() < 6){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ter menos de 6 caracteres!", Toast.LENGTH_SHORT).show();
        }
        else if(txtSenha.getText().toString().trim().isEmpty() || txtConfirmaSenha.getText().toString().trim().isEmpty() ){
            Toast.makeText(view.getApplicationContext(), "A senha não pode ser vazia ou conter apenas caracteres de espaço!", Toast.LENGTH_SHORT).show();
        }
        else if ( !txtSenha.getText().toString().equals(txtConfirmaSenha.getText().toString())) {
            Toast.makeText(view.getApplicationContext(), "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
        }
        else if( selectedId == -1 )
        {
            Toast.makeText(view.getApplicationContext(), "Selecione um tipo de conta!", Toast.LENGTH_SHORT).show();
        }
        else {
            String telefone = this.tratarTelefone();
            tipoConta = view.findViewById(selectedId);

            this.param.putString("nomeUsuario", txtNomeUsuario.getText().toString());
            this.param.putString("telefone", telefone);
            this.param.putString("email", txtEmail.getText().toString());
            this.param.putString("senha", txtSenha.getText().toString());

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

    private String tratarTelefone(){
        String telefone;
        String[] telefoneBuild = this.txtTelefone.getText().toString().trim().split("\\(");
        telefone = telefoneBuild[1];
        telefoneBuild = telefone.split("\\)");
        telefone = telefoneBuild[0];
        telefoneBuild = telefoneBuild[1].trim().split("-");
        telefone += telefoneBuild[0] + telefoneBuild[1];
        return telefone;
    }

    public void removerImagem() {
        this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        FindFM.getInstance().getParams().putByteArray("foto", null);
    }

    public void setFoto(){
        byte[] image = FindFM.getInstance().getParams().getByteArray("foto");

        if(image != null && image.length != 0) {
            this.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            this.btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            this.imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.capaplaceholder_photo, view.getTheme()));
            this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }
}
