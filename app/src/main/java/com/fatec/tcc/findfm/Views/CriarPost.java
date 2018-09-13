package com.fatec.tcc.findfm.Views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.fatec.tcc.findfm.R;

public class CriarPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_foto);
        fab.setOnClickListener(view -> Snackbar.make(view, "Selecionar foto", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        FloatingActionButton video = findViewById(R.id.fab_video);
        video.setOnClickListener(view -> Snackbar.make(view, "Selecionar video", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        ImageButton info = findViewById(R.id.info_tipo);
        info.setOnClickListener(view -> Toast.makeText(this, "Selecione uma opção: Anúncios tem maior visibilidade, escreva-os bem. Publicações são sobre suas novidades", Toast.LENGTH_SHORT ).show());

        Spinner cb_uf = findViewById(R.id.cb_uf);
        cb_uf.setAdapter( new ArrayAdapter<>(this, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        Spinner cb_tipo_post = findViewById(R.id.cb_tipo_post);
        cb_tipo_post.setAdapter( new ArrayAdapter<>(this, R.layout.simple_custom_list, getResources().getStringArray(R.array.tipo_post)));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
