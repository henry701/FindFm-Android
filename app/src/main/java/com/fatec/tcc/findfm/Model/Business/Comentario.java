package com.fatec.tcc.findfm.Model.Business;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

public class Comentario {

    private String id;
    private Usuario comentador;
    private String comentario;
    private DateTime dataComentario;
    private Set<String> likes;
    private Long likesNumb;

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
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

    public Set<String> getLikes() {
        return likes;
    }

    public void setLikes(Set<String> likes) {
        this.likes = likes;
        this.likesNumb = (long) likes.size();
    }

    public String getLikesNumb() {
        this.likesNumb = (long) likes.size();
        return likesNumb.toString();
    }

    public void setLikesNumb(Long likesNumb) {
        this.likesNumb = likesNumb;
    }

    public void setLikesNumb(String likesNumb) {
        this.likesNumb = Long.parseLong(likesNumb);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
