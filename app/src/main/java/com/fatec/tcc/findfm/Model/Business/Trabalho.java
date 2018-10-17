package com.fatec.tcc.findfm.Model.Business;

import java.util.List;

public class Trabalho {

    private String id;
    private String nome;
    private String descricao;
    private boolean original;
    private List<String> idAudios;
    private List<String> idFotos;
    private List<String> idVideos;
    private List<Musico> musicos;

    public String getNome() {
        return nome;
    }

    public Trabalho setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public boolean isOriginal() {
        return original;
    }

    public Trabalho setOriginal(boolean original) {
        this.original = original;
        return this;
    }

    public List<String> getIdAudios() {
        return idAudios;
    }

    public Trabalho setIdAudios(List<String> idAudios) {
        this.idAudios = idAudios;
        return this;
    }

    public List<String> getIdFotos() {
        return idFotos;
    }

    public Trabalho setIdFotos(List<String> idFotos) {
        this.idFotos = idFotos;
        return this;
    }

    public List<String> getIdVideos() {
        return idVideos;
    }

    public Trabalho setIdVideos(List<String> idVideos) {
        this.idVideos = idVideos;
        return this;
    }

    public List<Musico> getMusicos() {
        return musicos;
    }

    public Trabalho setMusicos(List<Musico> musicos) {
        this.musicos = musicos;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public Trabalho setId(String id) {
        this.id = id;
        return this;
    }
}
