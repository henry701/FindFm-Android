package com.fatec.tcc.findfm.Views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.tcc.findfm.Controller.Posts.PostViewModel;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.databinding.ActivityHomeFragmentBinding;

public class Home_Fragment extends Fragment {

    private ProgressDialog dialog;
    private TelaPrincipal activity;
    private ActivityHomeFragmentBinding binding;
    private View view;
    private Usuario usuario;

    public Home_Fragment(){}

    @SuppressLint("ValidFragment")
    public Home_Fragment(TelaPrincipal activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activity.getSupportActionBar().setTitle("FindFM - Home");
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_home_fragment, container, false);
        binding.setPostViewModel(new PostViewModel(activity, this, activity.getDialog()));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FindFM.setTelaAtual("HOME");
        super.onActivityCreated(savedInstanceState);
        setFoto();
        if(FindFM.isLogado(getActivity())) {
            TextView lb_nomeUsuario = getActivity().findViewById(R.id.lb_nomeUsuario);
            lb_nomeUsuario.setText(FindFM.getNomeUsuario(getActivity()));
        }
    }

    private void setFoto(){
        byte[] image = FindFM.getFotoPrefBytes(getActivity());
        ImageView imagemUsuarioHeader = getActivity().findViewById(R.id.fotoPerfil);
        if(image != null && image.length != 0) {
            imagemUsuarioHeader.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else {
            imagemUsuarioHeader.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getActivity().getTheme()));
            Toast.makeText(getActivity().getApplicationContext(), "Você pode adicionar uma foto acessando o menu Meu Perfil ;)", Toast.LENGTH_SHORT).show();
        }
    }


}
