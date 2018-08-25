package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.Controller.Registrar.RegistrarBandaViewModel;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.databinding.ActivityRegistrarBandaBinding;

import java.io.FileNotFoundException;

public class RegistrarBanda extends AppCompatActivity {

    private ActivityRegistrarBandaBinding binding;

    public static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registrar_banda);
        binding.setViewModelBanda(new RegistrarBandaViewModel(this));
        binding.executePendingBindings();
        binding.getViewModelBanda().init();
        init();
    }

    private void init(){
        this.imageView = findViewById(R.id.circularImageView);
        this.btnRemoverImagem = findViewById(R.id.btnRemoverImagem);
        Spinner cb_uf = findViewById(R.id.cb_uf);
        cb_uf.setAdapter(
                new ArrayAdapter<>(this, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        cb_uf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.getViewModelBanda().setUF(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        binding.getViewModelBanda().dismissDialog();
        super.onDestroy();
    }

    public void txtFormacao_Click (View v) {
        binding.getViewModelBanda().txtFormacaoClick();
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
        FindFM.getInstance().getParams().putByteArray("foto", null);
    }

    public void btnRegistrar_Click (View v) {
        binding.getViewModelBanda().registrar();
    }

}
