package com.fatec.tcc.findfm.Model.Business;

public class Comentario {

    private Usuario comentador;
    private String comentario;

    public Usuario getComentador() {
        return comentador;
    }

    public Comentario setComentador(Usuario comentador) {
        this.comentador = comentador;
        return this;
    }

    public String getComentario() {
        return comentario;
    }

    public Comentario setComentario(String comentario) {
        this.comentario = comentario;
        return this;
    }
}
