package com.fatec.tcc.findfm.Model.Business;

public class Telefone {

    private int countryCode;
    private int stateCode;
    private String number;

    public Telefone(){}

    public Telefone(String ddd, String numero){
        this.countryCode = 55;
        this.stateCode = Integer.parseInt(ddd);
        this.number = numero;
    }

    public String getCountryCode() {
        return String.valueOf(countryCode);
    }

    public Telefone setCountryCode(String countryCode) {
        this.countryCode = Integer.parseInt(countryCode);
        return this;
    }

    public String getStateCode() {
        return String.valueOf(stateCode);
    }

    public Telefone setStateCode(String stateCode) {
        this.stateCode = Integer.parseInt(stateCode);
        return this;
    }

    public String getNumber() {
        return number;
    }

    public Telefone setNumber(String number) {
        this.number = number;
        return this;
    }

    public String getTelefoneFormatado(){
        try {
            if(number.length() == 8) {
                return "(0" + stateCode + ")" + this.number.substring(0,4) + "-" + this.number.substring(number.length() - 4, number.length());
            }
            else {
                return "(0" + stateCode + ")" + this.number.substring(0,5) + "-" + this.number.substring(number.length() - 4, number.length());
            }
        }catch (Exception e){
            return "(0" + stateCode + ")" + " " + number;
        }
    }

    public void setTelefoneFormatado(String telefoneFormatado){
        if(telefoneFormatado != null) {
            String[] telefoneBuild = telefoneFormatado.trim().split("\\(");
            String telefone = telefoneBuild.length == 1 ? telefoneBuild[0] : telefoneBuild[1];
            telefoneBuild = telefone.split("\\)");
            telefone = telefoneBuild[0];
            telefoneBuild =  telefoneBuild.length == 1 ? telefoneBuild[0].trim().split("-") : telefoneBuild[1].trim().split("-");
            //PAIS
            countryCode = 55;
            //DDD
            setStateCode(telefone);
            //NUMERO
            setNumber(telefoneBuild.length == 2 ? telefoneBuild[0] + telefoneBuild[1] : telefoneBuild[0]);
        }
    }

}