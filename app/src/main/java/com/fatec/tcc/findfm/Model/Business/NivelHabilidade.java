package com.fatec.tcc.findfm.Model.Business;

import com.google.gson.annotations.SerializedName;

public enum NivelHabilidade {

    @SerializedName("1")
    INICIANTE(1, "Iniciante"),
    @SerializedName("2")
    INTERMEDIARIO(2, "Intermediário"),
    @SerializedName("3")
    AVANCADO(3, "Avançado");

    private int codigo;
    private String descricao;

    NivelHabilidade(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static final NivelHabilidade from( final int codigo) {
        for ( final NivelHabilidade item : NivelHabilidade.values() ) {
            if ( item.getCodigo() == codigo ) {
                return item;
            }
        }
        return NivelHabilidade.INICIANTE;
    }

    public static final NivelHabilidade fromDescricao( final String descricao) {
        if ( descricao != null ) {
            for ( final NivelHabilidade item : NivelHabilidade.values() ) {
                if ( item.getdescricao().equalsIgnoreCase( descricao ) ) {
                    return item;
                }
            }
        }

        return NivelHabilidade.INICIANTE;
    }

    @Override public String toString(){
        return descricao;
    }


    public int getCodigo() {
        return codigo;
    }

    public String getdescricao() {
        return descricao;
    }
}
