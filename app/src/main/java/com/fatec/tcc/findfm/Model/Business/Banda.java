package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;
import java.util.List;

public class Banda extends Usuario {

    private String nomeBanda;
    private int numeroIntegrantes;
    private Date formacao;
    private String cidade;
    private String uf;
    private List<Musico> musicos;

    public Banda(){}

    public Banda(String nomeUsuario, String senha, String email, String telefone, String foto, boolean confirmado, boolean premium,
                  String nomeBanda, Date formacao, List<Musico> musicos, int numeroIntegrantes, String cidade, String uf) {
        super(nomeUsuario, senha, email, telefone, foto, confirmado, premium);
        this.nomeBanda = nomeBanda;
        this.formacao = formacao;
        this.musicos = musicos;
        this.numeroIntegrantes = numeroIntegrantes;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getNomeBanda() {
        return nomeBanda;
    }

    public void setNomeBanda(String nomeBanda) {
        this.nomeBanda = nomeBanda;
    }

    public List<Musico> getMusicos() {
        return musicos;
    }

    public void setMusicos(List<Musico> musicos) {
        this.musicos = musicos;
    }

    public Date getFormacao() {
        return formacao;
    }

    public void setFormacao(Date formacao) {
        this.formacao = formacao;
    }

    public int getNumeroIntegrantes() {
        return numeroIntegrantes;
    }

    public void setNumeroIntegrantes(int numeroIntegrantes) {
        this.numeroIntegrantes = numeroIntegrantes;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
