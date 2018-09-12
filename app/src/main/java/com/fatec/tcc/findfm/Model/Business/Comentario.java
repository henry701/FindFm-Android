package com.fatec.tcc.findfm.Model.Business;

public class Comentario {

    private Usuario comentador;
    private String texto;
    private int likes;

    public Usuario getComentador() {
        return comentador;
    }

    public Comentario setComentador(Usuario comentador) {
        this.comentador = comentador;
        return this;
    }

    public String getTexto() {
        return texto;
    }

    public Comentario setTexto(String texto) {
        this.texto = texto;
        return this;
    }

    public int getLikes() {
        return likes;
    }

    public Comentario setLikes(int likes) {
        this.likes = likes;
        return this;
    }
}
