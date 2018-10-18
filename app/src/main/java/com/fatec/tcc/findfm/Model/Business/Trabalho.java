package com.fatec.tcc.findfm.Model.Business;

import java.util.List;

public class Trabalho {

    private String id;
    private String nome;
    private String descricao;
    private boolean original;
    private List<FileReference> midias;
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

    public String getOriginal() {
        return String.valueOf(original);
    }

    public Trabalho setOriginal(boolean original) {
        this.original = original;
        return this;
    }

    public Trabalho setOriginal(String original) {
        this.original = Boolean.valueOf(original);
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

    public List<FileReference> getMidias() {
        return midias;
    }

    public Trabalho setMidias(List<FileReference> midias) {
        this.midias = midias;
        return this;
    }
}
