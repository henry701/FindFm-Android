package com.fatec.tcc.findfm.Model.Http.Request;

public class RecuperarSenhaRequest {

    private String email;
    private String codigo;

    public String getEmail() {
        return email;
    }

    public RecuperarSenhaRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getCodigo() {
        return codigo;
    }

    public RecuperarSenhaRequest setCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }
}
