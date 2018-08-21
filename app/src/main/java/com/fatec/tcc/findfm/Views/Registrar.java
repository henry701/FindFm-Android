package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fatec.tcc.findfm.Controller.Registrar.RegistrarViewModel;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.databinding.ActivityRegistrarBinding;

import java.io.FileNotFoundException;

public class Registrar extends AppCompatActivity {

    private ActivityRegistrarBinding binding;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registrar);
        binding.setViewModel(new RegistrarViewModel(this));
        binding.executePendingBindings();
        binding.getViewModel().init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getViewModel().setFoto();
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
                binding.getViewModel().pickImage(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRegistrar_Click(View v){
        binding.getViewModel().btn_Registrar_Click();
    }

    public void btnRemoverImagem_Click(View v){
        binding.getViewModel().removerImagem();
    }
}
