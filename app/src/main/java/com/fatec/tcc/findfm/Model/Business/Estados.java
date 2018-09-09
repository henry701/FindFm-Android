package com.fatec.tcc.findfm.Model.Business;

public enum Estados {

    ACRE("Acre","AC", 0),
    ALAGOAS("Alagoas","AL", 1),
    AMAPA("Amapá","AP", 2),
    AMAZONAS("Amazonas","AM", 3),
    BAHIA("Bahia","BA", 4),
    CEARA("Ceará","CE", 5),
    DF("Distrito Federal","DF", 6),
    ESPIRITO_SANTO("Espírito Santo","ES", 7),
    GOIAS("Goiás","GO", 8),
    MARANHAO("Maranhão","MA", 9),
    MATO_GROSSO("Mato Grosso","MT", 10),
    MATO_GROSSO_SUL("Mato Grosso do Sul","MS", 11),
    MINAS_GERAIS("Minas Gerais","MG", 12),
    PARA("Pará","PA", 13),
    PARAIBA("Paraíba","PB", 14),
    PARANA("Paraná","PR", 15),
    PERNAMBUCO("Pernambuco","PE", 16),
    PIAUI("Piauí","PI", 17),
    RIO_JANEIRO("Rio de Janeiro","RJ", 18),
    RIO_GRANDE_NORTE("Rio Grande do Norte","RN", 19),
    RIO_GRANDE_SUL("Rio Grande do Sul","RS", 20),
    RONDONIA("Rondônia","RO", 21),
    RORAIMA("Roraima","RR", 22),
    SANTA_CATARINA("Santa Catarina","SC", 23),
    SAO_PAULO("São Paulo","SP", 24),
    SERGIPE("Sergipe","SE", 25),
    TOCANTINS("Tocantins","TO", 26);

    private String nome;
    private String sigla;
    private int index;

    Estados(String nome, String sigla, int index){
        this.nome = nome;
        this.sigla = sigla;
        this.index = index;
    }

    public static Estados fromSigla(final String sigla) {
        if ( sigla != null ) {
            for (final Estados item : Estados.values()) {
                if (item.getSigla().equals(sigla)) {
                    return item;
                }
            }
        }
        return Estados.ACRE;
    }

    public static Estados fromNome( final String nome) {
        if ( nome != null ) {
            for ( final Estados item : Estados.values() ) {
                if ( item.getNome().equals(nome) ) {
                    return item;
                }
            }
        }
        return Estados.ACRE;
    }

    public static Estados fromIndex( final int index) {
        for ( final Estados item : Estados.values() ) {
            if ( item.getIndex() == index ) {
                return item;
            }
        }
        return Estados.ACRE;
    }

    @Override public String toString(){
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public int getIndex() {
        return index;
    }
}
