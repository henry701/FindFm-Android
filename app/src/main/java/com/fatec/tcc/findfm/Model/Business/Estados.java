package com.fatec.tcc.findfm.Model.Business;

public enum Estados {

    ACRE("Acre","AC"),
    ALAGOAS("Alagoas","AL"),
    AMAPA("Amapá","AP"),
    AMAZONAS("Amazonas","AM"),
    BAHIA("Bahia","BA"),
    CEARA("Ceará","CE"),
    DF("Distrito Federal","DF"),
    ESPIRITO_SANTO("Espírito Santo","ES"),
    GOIAS("Goiás","GO"),
    MARANHAO("Maranhão","MA"),
    MATO_GROSSO("Mato Grosso","MT"),
    MATO_GROSSO_SUL("Mato Grosso do Sul","MS"),
    MINAS_GERAIS("Minas Gerais","MG"),
    PARA("Pará","PA"),
    PARAIBA("Paraíba","PB"),
    PARANA("Paraná","PR"),
    PERNAMBUCO("Pernambuco","PE"),
    PIAUI("Piauí","PI"),
    RIO_JANEIRO("Rio de Janeiro","RJ"),
    RIO_GRANDE_NORTE("Rio Grande do Norte","RN"),
    RIO_GRANDE_SUL("Rio Grande do Sul","RS"),
    RONDONIA("Rondônia","RO"),
    RORAIMA("Roraima","RR"),
    SANTA_CATARINA("Santa Catarina","SC"),
    SAO_PAULO("São Paulo","SP"),
    SERGIPE("Sergipe","SE"),
    TOCANTINS("Tocantins","TO");

    private String nome;
    private String sigla;
    
    Estados(String nome, String sigla){
        this.nome = nome;
        this.sigla = sigla;
    }

    public static Estados from(final String sigla) {
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
                if ( item.getSigla().equals(nome) ) {
                    return item;
                }
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
}
