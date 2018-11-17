package com.fatec.tcc.findfm.Model.Http.Request;

public class Coordenada {

    private double longitude;

    private double latitude;

    private String ip;

    private String city;

    private String region_code;

    public double getLongitude() {
        return longitude;
    }

    public Coordenada setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public Coordenada setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Coordenada setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Coordenada setCity(String city) {
        this.city = city;
        return this;
    }

    public String getRegion_code() {
        return region_code;
    }

    public Coordenada setRegion_code(String region_code) {
        this.region_code = region_code;
        return this;
    }
}
