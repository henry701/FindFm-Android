package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Controller.Registrar.RegistrarMusicoViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Views.Adapters.AdapterInstrumentos;
import com.fatec.tcc.findfm.databinding.ActivityRegistrarMusicoBinding;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RegistrarMusico extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;

    private ProgressDialog dialog;

    private ActivityRegistrarMusicoBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registrar_musico);
        binding.setMusico(new RegistrarMusicoViewModel(this));
        binding.getMusico().init();
        init();
        updateList();
    }

    private void init() {
        this.imageView = findViewById(R.id.circularImageView);
        this.btnRemoverImagem = findViewById(R.id.btnRemoverImagem);
        Spinner cb_uf = findViewById(R.id.cb_uf);
        cb_uf.setAdapter(
                new ArrayAdapter<>(this, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        cb_uf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.getMusico().setUF(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        binding.getMusico().dismissDialog();
        super.onDestroy();
    }

    private void updateList() {
        RecyclerView rc = findViewById(R.id.listaInstrumentos);

        //TODO: validar se der ruim
        //TODO: colocar dialog enquanto carrega a tela toda
        HttpTypedRequest<Instrumento, ResponseBody, ErrorResponse> instrumentoRequest = new HttpTypedRequest<>
                (       this,
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

                                rc.setAdapter( new AdapterInstrumentos(instrumentos, this) );
                                RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                                        LinearLayoutManager.VERTICAL, false);
                                rc.setLayoutManager( layout );
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (Exception error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        instrumentoRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"instruments"));
        dialog.show();
        instrumentoRequest.execute(this);


    }

    public void btnFoto_Click(View v){
        startActivityForResult(Intent.createChooser(ImagemUtils.pickImageIntent(), "Escolha uma foto"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                this.imageView.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(data.getData())));
                this.btnRemoverImagem.setVisibility(View.VISIBLE);

                ImagemUtils.setImagemToParams(this.imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRemoverImagem_Click(View v){
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        FindFM.setImagemPerfilParams(null);
    }

    public void txtNascimento_Click(View v){
       binding.getMusico().txtNascimento_Click();
    }

    public void btnRegistrar_Click(View v){
        binding.getMusico().registrar();
    }

}
