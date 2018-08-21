package com.fatec.tcc.findfm.Views;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.tcc.findfm.Controller.FindFM;
import com.fatec.tcc.findfm.R;

import static android.content.Context.MODE_PRIVATE;

public class Home_Fragment extends Fragment {

    private ProgressDialog dialog;
    View homePage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        homePage = inflater.inflate(R.layout.activity_home_fragment, container, false);
        return homePage;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.getInstance().getParams().putString("tela", "HOME");
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FindFM_param", MODE_PRIVATE);
        boolean isLogado = sharedPreferences.getBoolean("isLogado", false);
        String usuario = sharedPreferences.getString("username","Visitante");
        setFoto();
        if(isLogado) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            lb_nomeUsuario.setText(usuario);
        }
    }

    private void setFoto(){
        byte[] image = FindFM.getInstance().getParams().getByteArray("foto");
        ImageView imagemUsuarioHeader = getActivity().findViewById(R.id.fotoPerfil);
        if(image != null && image.length != 0) {
            imagemUsuarioHeader.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            Toast.makeText(getActivity().getApplicationContext(), "Você pode adicionar uma foto acessando o menu Meu Perfil ;)", Toast.LENGTH_SHORT).show();
        }
        else {
            imagemUsuarioHeader.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getActivity().getTheme()));
        }
    }
}
