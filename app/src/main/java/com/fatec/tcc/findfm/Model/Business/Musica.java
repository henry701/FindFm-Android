package com.fatec.tcc.findfm.Model.Business;

import android.net.Uri;

public class Musica {

    private String id;
    private String idResource;
    private String nome;
    private FileReference audioReference;
    private long duracao;
    private boolean autoral;
    private boolean autorizadoRadio;
    private Long reproducoes;
    private Uri uri;
    private boolean jaIncrementado = false;

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

    @Override
    public boolean equals(Object mus) {
        boolean isEqual = false;

        if(mus != null && mus instanceof Musica) {
            if(this.nome != null && this.uri != null) {
                isEqual = (
                        (this.nome.equals(((Musica) mus).getNome())) ||
                        (this.uri.equals(((Musica) mus).getUri()))
                );
            } else if(this.nome == null) {
                isEqual = (this.uri.equals(((Musica) mus).getUri()));
            } else if(this.uri == null) {
                isEqual = (this.nome.equals(((Musica) mus).getNome()));
            }
        }

        return isEqual;
    }

    public String getReproducoes() {
        return reproducoes == null ? "0" : reproducoes.toString();
    }

    public Musica setReproducoes(long reproducoes) {
        this.reproducoes = reproducoes;
        return this;
    }

    public FileReference getAudioReference() {
        return audioReference;
    }

    public void setAudioReference(FileReference audioReference) {
        this.audioReference = audioReference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isJaIncrementado() {
        return jaIncrementado;
    }

    public void setJaIncrementado(boolean jaIncrementado) {
        this.jaIncrementado = jaIncrementado;
    }
}
