package com.fatec.tcc.findfm.Model.Http.Request;

public class LoginRequest {

    private final String nomeUsuario;
    private final String senha;

    public LoginRequest(String nomeUsuario, String senha)
    {
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

}
