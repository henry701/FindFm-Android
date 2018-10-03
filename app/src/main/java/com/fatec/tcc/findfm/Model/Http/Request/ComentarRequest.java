package com.fatec.tcc.findfm.Model.Http.Request;

public class ComentarRequest {

    public ComentarRequest(){}

    public ComentarRequest(String comentario){
        this.comentario = comentario;
    }

    private String comentario;

    public String getComentario() {
        return comentario;
    }

    public ComentarRequest setComentario(String comentario) {
        this.comentario = comentario;
        return this;
    }
}
