package com.fatec.tcc.findfm.Model.Http.Request;

public class LoginRequest {

    private final String usuario;
    private final String senha;

    public LoginRequest(String nomeUsuario, String senha)
    {
        this.usuario = nomeUsuario;
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

}
