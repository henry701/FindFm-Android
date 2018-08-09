package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;

public class Contratante extends Usuario {

    private String nomeEstabelecimento;
    private int capacidadeLocal;
    private Date inauguracao;

    public Contratante(){}

    public Contratante(String nomeUsuario, String senha, String email, String telefone, byte[] foto, boolean confirmado, boolean premium,
                 String nomeEstabelecimento, Date inauguracao, int capacidadeLocal) {
        super(nomeUsuario, senha, email, telefone, foto, confirmado, premium);
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.inauguracao = inauguracao;
        this.capacidadeLocal = capacidadeLocal;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public Date getInauguracao() {
        return inauguracao;
    }

    public void setInauguracao(Date inauguracao) {
        this.inauguracao = inauguracao;
    }

    public int getCapacidadeLocal() {
        return capacidadeLocal;
    }

    public void setCapacidadeLocal(int capacidadeLocal) {
        this.capacidadeLocal = capacidadeLocal;
    }
}
