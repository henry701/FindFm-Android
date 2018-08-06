package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Adapters.AdapterInstrumentos;
import com.fatec.tcc.findfm.Controller.RegistrarController;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Request.ServerCallBack;
import com.fatec.tcc.findfm.Utils.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrarMusico extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;
    private Bundle param = new Bundle();
    private EditText txtNascimento;
    private RecyclerView rc;
    private RegistrarController controller;
    private Date nascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_musico);
        init();
        updateList();
    }

    private void init(){
        this.controller = new RegistrarController(this, getResources());
        this.rc = findViewById(R.id.listaInstrumentos);
        this.imageView = findViewById(R.id.circularImageView);
        this.btnRemoverImagem = findViewById(R.id.btnRemoverImagem);
        this.txtNascimento = findViewById(R.id.txtNascimento);
        this.txtNascimento.setShowSoftInputOnFocus(false);
        this.txtNascimento.setInputType(InputType.TYPE_NULL);
        this.param = getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Registrar");
        byte[] image = this.param.getByteArray("foto");

        if(image != null && image.length != 0) {
            this.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            this.btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

    private void updateList() {
        //TODO: server retornar uma lista mais completa
        List<Instrumento> instrumentos = Arrays.asList(
                new Instrumento("Guitarra", NivelHabilidade.INICIANTE),
                new Instrumento("Bateria", NivelHabilidade.INICIANTE),
                new Instrumento("Baixo", NivelHabilidade.INICIANTE),
                new Instrumento("Vocal", NivelHabilidade.INICIANTE),
                new Instrumento("Saxofone", NivelHabilidade.INICIANTE),
                new Instrumento("Flauta", NivelHabilidade.INICIANTE),
                new Instrumento("Piano", NivelHabilidade.INICIANTE),
                new Instrumento("Percussão", NivelHabilidade.INICIANTE),
                new Instrumento("Trombone", NivelHabilidade.INICIANTE));

        RecyclerView view = findViewById(R.id.listaInstrumentos);
        view.setAdapter( new AdapterInstrumentos(instrumentos, this) );
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        view.setLayoutManager( layout );

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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRemoverImagem_Click(View v){
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder, getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
    }

    public void txtNascimento_Click(View v){
        Util.hideSoftKeyboard(this);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            this.nascimento = myCalendar.getTime();
            this.txtNascimento.setText(sdf.format(myCalendar.getTime()));
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void btnRegistrar_Click(View v){
        Bitmap bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        this.param.putByteArray("foto", baos.toByteArray());

        TextView txtNomeCompleto = findViewById(R.id.txtNomeCompleto);
        TextView txtNascimento = findViewById(R.id.txtNascimento);

        if( txtNomeCompleto.getText().toString().isEmpty()||
                txtNascimento.getText().toString().isEmpty() )
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        else {
            this.param.putString("nomeCompleto", txtNomeCompleto.getText().toString());
            AdapterInstrumentos adapter = (AdapterInstrumentos) rc.getAdapter();
            List<Instrumento> instrumentos = new ArrayList<>();
            instrumentos.addAll(adapter.getInstrumentos());

            this.controller.registrarMusico(this.param, instrumentos, this.nascimento, new ServerCallBack() {
                @Override
                public void onSucess(JSONObject result) {

                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }
    }

}
