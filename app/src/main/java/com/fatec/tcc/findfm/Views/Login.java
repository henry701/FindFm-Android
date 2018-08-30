package com.fatec.tcc.findfm.Views;

import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.fatec.tcc.findfm.Controller.Login.LoginViewModel;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
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
        else{
            ImageView imageView = findViewById(R.id.circularImageView2);
            byte[] image = FindFM.getFotoPrefBytes(this);
            if(image != null && image.length != 0) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            else{
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.getViewModel().dismissDialog();
    }

    public void btnEntrar_Click(View v) {
        binding.getViewModel().btnEntrar_Click(v);
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
