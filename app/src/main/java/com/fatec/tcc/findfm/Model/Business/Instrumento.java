package com.fatec.tcc.findfm.Model.Business;

public class Instrumento {
    
    private String nome;
    private NivelHabilidade nivelHabilidade;
    private int quantidade;

    public Instrumento(String nome, NivelHabilidade nivelHabilidade){
        this.nome = nome;
        this.nivelHabilidade = nivelHabilidade;
    }

    public Instrumento(String nome, NivelHabilidade nivelHabilidade, int quantidade){
        this.nome = nome;
        this.nivelHabilidade = nivelHabilidade;
        this.quantidade = quantidade;
    }

    public NivelHabilidade getNivelHabilidade() {
        return nivelHabilidade;
    }

    public void setNivelHabilidade(NivelHabilidade nivelHabilidade) {
        this.nivelHabilidade = nivelHabilidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade(){
        return quantidade;
    }

    public void setQuantidade(int quantidade){
        this.quantidade = quantidade;
    }

}

