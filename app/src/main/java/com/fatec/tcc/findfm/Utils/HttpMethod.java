package com.fatec.tcc.findfm.Utils;

/**
 * Enumerada para ser utilizada para identificar método HTTP da requisição
 * **/
public enum HttpMethod {

    DEPRECATED_GET_OR_POST( -1, "DEPRECATED GET OR POST" ),
    GET( 0, "GET" ),
    POST( 1, "POST" ),
    PUT( 2, "PUT" ),
    DELETE( 3, "DELETE" ),
    HEAD( 4, "HEAD" ),
    OPTIONS( 5, "OPTIONS" ),
    TRACE( 6, "TRACE" ),
    PATCH( 7, "PATCH" );

    private int codigo;
    private String nome;

    HttpMethod(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public static final HttpMethod from( final int codigo) {
        for ( final HttpMethod item : HttpMethod.values() ) {
            if ( item.getCodigo() == codigo ) {
                return item;
            }
        }
        return HttpMethod.GET;
    }

    public static final HttpMethod fromDescricao( final String descricao) {
        if ( descricao != null ) {
            for ( final HttpMethod item : HttpMethod.values() ) {
                if ( item.getNome().equalsIgnoreCase( descricao ) ) {
                    return item;
                }
            }
        }

        return HttpMethod.GET;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }
}