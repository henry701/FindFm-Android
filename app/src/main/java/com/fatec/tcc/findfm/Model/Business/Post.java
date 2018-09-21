package com.fatec.tcc.findfm.Model.Business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Post {

    private Usuario autor;
    private String titulo;
    private String descricao;
    private String cidade;
    private String idFoto;
    private String idVideo;
    private Date data;
    private List<Comentario> comentarios;

    public Post(){}

    public String getTitulo() {
        return titulo;
    }

    public Post setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public Post setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public String getCidade() {
        return cidade;
    }

    public Post setCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Post setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        return this;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Post setAutor(Usuario autor) {
        this.autor = autor;
        return this;
    }

    public String getData() {
        if(data != null) {
            return new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss", Locale.US).format(data);
        }else
        {
            return new SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss", Locale.US).format(new Date());
        }
    }

    public Post setData(Date data) {
        this.data = data;
        return this;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public Post setIdFoto(String idFoto) {
        this.idFoto = idFoto;
        return this;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public Post setIdVideo(String idVideo) {
        this.idVideo = idVideo;
        return this;
    }
}
