package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;
import java.util.List;

public class Banda extends Usuario {

    private String nomeBanda;
    private int numeroIntegrantes;
    private Date formacao;
    private String cidade;
    private String uf;
    private List<Instrumento> instrumentos;

    public Banda(){}

    public Banda(String nomeUsuario, String senha, String email, String telefone, byte[] foto, boolean confirmado, boolean premium,
                  String nomeBanda, Date formacao, List<Instrumento> instrumentos, int numeroIntegrantes, String cidade, String uf) {
        super(nomeUsuario, senha, email, telefone, foto, confirmado, premium);
        this.nomeBanda = nomeBanda;
        this.formacao = formacao;
        this.instrumentos = instrumentos;
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

    public List<Instrumento> getInstrumentos() {
        return instrumentos;
    }

    public void setInstrumentos(List<Instrumento> instrumentos) {
        this.instrumentos = instrumentos;
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
