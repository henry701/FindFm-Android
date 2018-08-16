package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
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
import com.fatec.tcc.findfm.Utils.FormatadorTelefoneBR;
import com.fatec.tcc.findfm.Utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class Registrar extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;
    private String path = "com.fatec.tcc.findfm.Views.Registrar";
    private Bundle globalParams = FindFM.getInstance().getParams();
    private Bundle param = new Bundle();
    private EditText txtTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFoto();
    }

    private void init(){
        this.imageView = findViewById(R.id.circularImageView);
        this.btnRemoverImagem = findViewById(R.id.btnRemoverImagem);
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        this.txtTelefone = findViewById(R.id.txtTelefone);
        FormatadorTelefoneBR addLineNumberFormatter = new FormatadorTelefoneBR(new WeakReference<>(this.txtTelefone));
        this.txtTelefone.addTextChangedListener(addLineNumberFormatter);
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
        this.globalParams.putByteArray("foto", null);
        FindFM.getInstance().setParams(this.globalParams);
    }

    public void btnFoto_Click(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                this.imageView.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(data.getData())));
                this.btnRemoverImagem.setVisibility(View.VISIBLE);

                Bitmap bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                this.globalParams.putByteArray("foto", baos.toByteArray());
                FindFM.getInstance().setParams(this.globalParams);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRegistrar_Click(View v){
        TextView txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        TextView txtEmail = findViewById(R.id.txtEmail);
        TextView txtSenha = findViewById(R.id.txtSenha);
        TextView txtConfirmaSenha = findViewById(R.id.txtConfirmaSenha);
        RadioGroup tipoContaGrupo = findViewById(R.id.grupoTipoConta);
        RadioButton tipoConta;

        int selectedId = -1;
        boolean isTelefonevalido = FormatadorTelefoneBR.validarTelefone(this.txtTelefone.getText().toString());
        boolean isEmailValido = FormatadorTelefoneBR.validarEmail(txtEmail.getText().toString());

        if( tipoContaGrupo.getCheckedRadioButtonId() != -1 ) {
            selectedId = tipoContaGrupo.getCheckedRadioButtonId();
        }

        if(!isTelefonevalido){
            Toast.makeText(getApplicationContext(), "Insira um telefone válido!", Toast.LENGTH_SHORT).show();
        }
        else if (!isEmailValido) {
            Toast.makeText(getApplicationContext(), "Insira um e-mail válido!", Toast.LENGTH_SHORT).show();
        }
        else if( txtNomeUsuario.getText().toString().isEmpty() || this.txtTelefone.getText().toString().isEmpty() ||
                txtEmail.getText().toString().isEmpty() || txtSenha.getText().toString().isEmpty() ||
                txtConfirmaSenha.getText().toString().isEmpty() || selectedId == -1 )
        {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else if ( !txtSenha.getText().toString().equals(txtConfirmaSenha.getText().toString())) {
            Toast.makeText(getApplicationContext(), "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
        }
        else {
            String telefone = this.tratarTelefone();
            tipoConta = findViewById(selectedId);
            this.param.putString("nomeUsuario", txtNomeUsuario.getText().toString());
            this.param.putString("telefone", telefone);
            this.param.putString("email", txtEmail.getText().toString());
            this.param.putString("senha", txtSenha.getText().toString());

            switch (tipoConta.getText().toString()){
                case "Banda":
                    Util.open_form_withParam(getApplicationContext(), RegistrarBanda.class, this.path, this.param);
                    break;
                case "Contratante":
                    Util.open_form_withParam(getApplicationContext(), RegistrarContratante.class, this.path, this.param);
                    break;
                case "Músico":
                    Util.open_form_withParam(getApplicationContext(), RegistrarMusico.class, this.path, this.param);
                    break;
            }
        }
    }

    public void btnRemoverImagem_Click(View v){
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        this.globalParams.putByteArray("foto", null);
        FindFM.getInstance().setParams(this.globalParams);
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

    private void setFoto(){
        byte[] image = this.globalParams.getByteArray("foto");

        if(image != null && image.length != 0) {
            this.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            this.btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
            this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

}
