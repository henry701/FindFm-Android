package com.fatec.tcc.findfm.Model.Business;

public enum TiposUsuario {

    INDEFINIDO(0, "Indefinido", "Unknown"),
    BANDA(1, "Banda", "Band"),
    CONTRATANTE(2, "Contratante", "Contractor"),
    MUSICO(3, "Musico", "Musician");

    private int codigo;
    private String texto;
    private String kind;

    TiposUsuario(int codigo, String texto, String kind) {
        this.codigo = codigo;
        this.texto = texto;
        this.kind = kind;
    }

    public static final TiposUsuario from(final int codigo) {
        for ( final TiposUsuario item : TiposUsuario.values() ) {
            if ( item.getCodigo() == codigo ) {
                return item;
            }
        }
        return TiposUsuario.INDEFINIDO;
    }

    public static final TiposUsuario fromTexto(final String texto) {
        for ( final TiposUsuario item : TiposUsuario.values() ) {
            if ( item.getTexto().equals(texto)) {
                return item;
            }
        }
        return TiposUsuario.INDEFINIDO;
    }

    public static final TiposUsuario fromKind(final String kind) {
        for ( final TiposUsuario item : TiposUsuario.values() ) {
            if ( item.getKind().equals(kind)) {
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

    public String getKind() {
        return kind;
    }
}
