package com.fatec.tcc.findfm.Model.Business;

import java.util.List;

public class Post {

    private String titulo;
    private String descricao;
    private String cidade;
    private String[] fotos;
    //TODO: ver se video transforma pra base64
    private String video;
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

    public String[] getFotos() {
        return fotos;
    }

    public Post setFotos(String[] fotos) {
        this.fotos = fotos;
        return this;
    }

    public String getVideo() {
        return video;
    }

    public Post setVideo(String video) {
        this.video = video;
        return this;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public Post setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        return this;
    }
}
