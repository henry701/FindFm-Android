package com.fatec.tcc.findfm.Model.Business;

import android.net.Uri;

public class Musica {
    
    private String idResource;
    private String nome;
    private long duracao;
    private boolean autoral;
    private boolean autorizadoRadio;
    private Uri uri;

    public Musica(){}

    public Musica(String idResource, String nome){
        this.idResource = idResource;
        this.nome = nome;
    }

    public String getIdResource() {
        return idResource;
    }

    public Musica setIdResource(String idResource) {
        this.idResource = idResource;
        return this;
    }

    public String getNome() {
        return nome;
    }

    public Musica setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public long getDuracao() {
        return duracao;
    }

    public Musica setDuracao(long duracao) {
        this.duracao = duracao;
        return this;
    }

    public boolean isAutoral() {
        return autoral;
    }

    public Musica setAutoral(boolean autoral) {
        this.autoral = autoral;
        return this;
    }

    public boolean isAutorizadoRadio() {
        return autorizadoRadio;
    }

    public Musica setAutorizadoRadio(boolean autorizadoRadio) {
        this.autorizadoRadio = autorizadoRadio;
        return this;
    }

    public Uri getUri() {
        return uri;
    }

    public Musica setUri(Uri uri) {
        this.uri = uri;
        return this;
    }
}