package com.fatec.tcc.findfm.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fatec.tcc.findfm.R;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SharedPreferences sharedPreferences = getSharedPreferences("FindFM_param", MODE_PRIVATE);
        boolean isLogado = sharedPreferences.getBoolean("isLogado", false);
        String usuario = sharedPreferences.getString("username","Visitante");

        if(isLogado) {

            TextView lb_nomeUsuario = findViewById(R.id.lb_nomeUsuario);
            lb_nomeUsuario.setVisibility(View.VISIBLE);
            lb_nomeUsuario.setText("Bem-vindo: " + usuario);

        }
    }
}
