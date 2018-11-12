package com.fatec.tcc.findfm.Model.Business;

public class FileInfo {
    private FileMetadata fileMetadata;
    private long size;

    public FileMetadata getFileMetadata() {
        return fileMetadata == null ? this.fileMetadata = new FileMetadata() : fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
