package com.fatec.tcc.findfm.Model.Http.Response;

public class BinaryResponse {

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public BinaryResponse setData(byte[] data) {
        this.data = data;
        return this;
    }
}
