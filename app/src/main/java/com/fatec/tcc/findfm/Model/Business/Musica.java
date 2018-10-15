package com.fatec.tcc.findfm.Model.Business;

public class Musica {
    
    private String idResource;
    private String nome;
    private long duracao;
    private boolean autoral;
    private boolean autorizadoRadio;

    public Musica(){}

    public Musica(String idResource, String nome){
        this.idResource = idResource;
        this.nome = nome;
    }

    public String getIdResource() {
        return idResource;
    }

    public void setIdResource(String idResource) {
        this.idResource = idResource;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getDuracao() {
        return duracao;
    }

    public void setDuracao(long duracao) {
        this.duracao = duracao;
    }

    public boolean isAutoral() {
        return autoral;
    }

    public void setAutoral(boolean autoral) {
        this.autoral = autoral;
    }

    public boolean isAutorizadoRadio() {
        return autorizadoRadio;
    }

    public void setAutorizadoRadio(boolean autorizadoRadio) {
        this.autorizadoRadio = autorizadoRadio;
    }
}
