package com.fatec.tcc.findfm.Model.Business;

public class FileReference {

    private String id;
    private String contentType;
    private byte[] conteudo;

    public String getId() {
        return id;
    }

    public FileReference setId(String id) {
        this.id = id;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public FileReference setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public FileReference setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
        return this;
    }
}
