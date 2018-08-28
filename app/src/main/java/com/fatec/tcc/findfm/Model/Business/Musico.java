package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;
import java.util.List;

public class Musico extends Usuario {

    private String nomeCompleto;
    private Date nascimento;
    private String cidade;
    private String uf;
    private List<Instrumento> instrumentos;

    public Musico(){}

    public Musico(String nomeUsuario, String senha, String email, String telefone, String foto, boolean confirmado, boolean premium,
                  String nomeCompleto, Date nascimento, List<Instrumento> instrumentos, String cidade, String uf) {
        super(nomeUsuario, senha, email, telefone, foto, confirmado, premium);
        this.nomeCompleto = nomeCompleto;
        this.nascimento = nascimento;
        this.instrumentos = instrumentos;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public List<Instrumento> getInstrumentos() {
        return instrumentos;
    }

    public void setInstrumentos(List<Instrumento> instrumentos) {
        this.instrumentos = instrumentos;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
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
