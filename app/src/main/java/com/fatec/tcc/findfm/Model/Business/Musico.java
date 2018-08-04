package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;
import java.util.List;

public class Musico extends Usuario {

    private String nomeCompleto;
    private Date nascimento;
    private List<Instrumento> instrumentos;

    public Musico(){}

    public Musico(String nomeUsuario, String senha, String email, String telefone, byte[] foto, boolean confirmado, boolean premium,
                  String nomeCompleto, Date nascimento, List<Instrumento> instrumentos) {
        super(nomeUsuario, senha, email, telefone, foto, confirmado, premium);
        this.nomeCompleto = nomeCompleto;
        this.nascimento = nascimento;
        this.instrumentos = instrumentos;
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
}
