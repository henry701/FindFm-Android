package com.fatec.tcc.findfm.Controller.Perfil;

import android.databinding.ObservableField;

import com.fatec.tcc.findfm.Model.Business.Usuario;

public class PerfilViewModel {

    public ObservableField<Usuario> usuario = new ObservableField<>();
    public ObservableField<String> cidade = new ObservableField<>();
    public ObservableField<String> uf = new ObservableField<>();
    public ObservableField<String> endereco = new ObservableField<>();
    public ObservableField<String> numero = new ObservableField<>();
    public ObservableField<String> confirmaSenha = new ObservableField<>();


}
