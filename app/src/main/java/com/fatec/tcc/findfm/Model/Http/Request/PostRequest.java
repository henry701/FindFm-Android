package com.fatec.tcc.findfm.Model.Http.Request;

public class PostRequest {

    private String titulo;
    private String descricao;
    private String imagemId;
    private String videoId;

    public String getTitulo() {
        return titulo;
    }

    public PostRequest setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public PostRequest setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public String getImagemId() {
        return imagemId;
    }

    public PostRequest setImagemId(String imagemId) {
        this.imagemId = imagemId;
        return this;
    }

    public String getVideoId() {
        return videoId;
    }

    public PostRequest setVideoId(String videoId) {
        this.videoId = videoId;
        return this;
    }
}
