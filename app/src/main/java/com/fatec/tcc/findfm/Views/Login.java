package com.fatec.tcc.findfm.Views;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Controller.Login.LoginViewModel;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(new LoginViewModel(this));
        binding.executePendingBindings();
        binding.getViewModel().init();

        if(FindFM.isLogado(this)) {
            Util.open_form(getApplicationContext(), TelaPrincipal.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.getViewModel().dismissDialog();
    }

    public void btnEntrar_Click(View v) {
        binding.getViewModel().btnEntrar_Click(v);
        //Temporario
        FindFM.logarUsuario(this, TiposUsuario.MUSICO, "Robervaldo");
        Util.open_form(v.getContext(), TelaPrincipal.class);
    }

    public void lb_recuperarSenha_Click(View v){
        AlertDialogUtils.newSimpleDialog__OneButton(this,
                "Calma cara", R.drawable.ic_error,
                "Recuperar senha - tela em construção",
                "OK",
                (dialog, id) -> { }).create().show();
    }

    public void lb_registrar_Click(View v){
        Util.open_form(getApplicationContext(), Registrar.class);
    }

}
