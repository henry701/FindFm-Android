package com.fatec.tcc.findfm.Model.Business;

public class FileReference {

    private String id;
    private FileInfo fileInfo;
    private byte[] conteudo;

    public String getId() {
        return id;
    }

    public FileReference setId(String id) {
        this.id = id;
        return this;
    }

    public FileInfo getFileInfo() {
        return fileInfo == null ? this.fileInfo = new FileInfo() : fileInfo;
    }

    public FileReference setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        return this;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public FileReference setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
        return this;
    }

    public String getContentType()
    {
        return this.getFileInfo().getFileMetadata().getContentType();
    }

    public FileReference setContentType(String contentType)
    {
        this.getFileInfo().getFileMetadata().setContentType(contentType);
        return this;
    }
}
