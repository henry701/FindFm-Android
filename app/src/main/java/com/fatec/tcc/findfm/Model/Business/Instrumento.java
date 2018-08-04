package com.fatec.tcc.findfm.Model.Business;

public class Instrumento {
    
    private String nome;
    private NivelHabilidade nivelHabilidade;

    public Instrumento(String nome, NivelHabilidade nivelHabilidade){
        this.nome = nome;
        this.nivelHabilidade = nivelHabilidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public NivelHabilidade getNivelHabilidade() {
        return nivelHabilidade;
    }

    public void setNivelHabilidade(NivelHabilidade nivelHabilidade) {
        this.nivelHabilidade = nivelHabilidade;
    }
}

