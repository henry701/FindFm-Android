package com.fatec.tcc.findfm.Model.Business;

public enum TiposUsuario {

    INDEFINIDO(0, "Indefinido"),
    BANDA(1, "Banda"),
    CONTRATANTE(2, "Contratante"),
    MUSICO(3, "Musico");

    private int codigo;
    private String texto;

    TiposUsuario(int codigo, String texto) {
        this.codigo = codigo;
        this.texto = texto;
    }

    public static final TiposUsuario from(final int codigo) {
        for ( final TiposUsuario item : TiposUsuario.values() ) {
            if ( item.getCodigo() == codigo ) {
                return item;
            }
        }
        return TiposUsuario.INDEFINIDO;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }
}
