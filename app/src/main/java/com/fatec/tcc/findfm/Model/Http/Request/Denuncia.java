package com.fatec.tcc.findfm.Model.Http.Request;

public class Denuncia {
    //Do item denunciado
    private String id;
    private String tipo;
    //Denuncia
    private String motivo;
    //Id do denunciante
    private String contato;

    public String getId() {
        return id;
    }

    public Denuncia setId(String id) {
        this.id = id;
        return this;
    }

    public String getTipo() {
        return tipo;
    }

    public Denuncia setTipo(String tipo) {
        this.tipo = tipo;
        return this;
    }

    public String getMotivo() {
        return motivo;
    }

    public Denuncia setMotivo(String motivo) {
        this.motivo = motivo;
        return this;
    }

    public String getContato() {
        return contato;
    }

    public Denuncia setContato(String contato) {
        this.contato = contato;
        return this;
    }
}
