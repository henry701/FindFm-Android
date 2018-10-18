package com.fatec.tcc.findfm.Model.Http.Request;

import com.fatec.tcc.findfm.Model.Business.FileReference;

import java.util.List;

public class PostRequest {

    private String titulo;
    private String descricao;
    private List<FileReference> midias;

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

    public List<FileReference> getMidias() {
        return midias;
    }

    public PostRequest setMidias(List<FileReference> midias) {
        this.midias = midias;
        return this;
    }
}
