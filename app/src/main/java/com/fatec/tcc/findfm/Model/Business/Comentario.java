package com.fatec.tcc.findfm.Model.Business;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Comentario {

    private Usuario comentador;
    private String comentario;
    private DateTime dataComentario;

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

    public String getDataComentario() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        if(dataComentario == null)
        {
            dataComentario = new DateTime();
        }
        return sdf.format(dataComentario.toLocalDateTime().toDate());
    }

    public Comentario setDataComentario(DateTime dataComentario) {
        this.dataComentario = dataComentario;
        return this;
    }
}
