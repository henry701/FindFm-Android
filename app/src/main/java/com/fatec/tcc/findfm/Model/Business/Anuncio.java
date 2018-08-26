package com.fatec.tcc.findfm.Model.Business;

public class Anuncio {

    private String titulo;
    private String descricao;
    private String cidade;

    public Anuncio(){}

    public Anuncio(String titulo, String descricao, String cidade){
        this.titulo = titulo;
        this.descricao = descricao;
        this.cidade = cidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
