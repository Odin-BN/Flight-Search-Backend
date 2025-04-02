package com.secondbreakabletoy.Flight_Search.model;

public class Airport {
    private String iata;
    private String name;
    private String city;

    public Airport() {
    }

    public Airport(String iata, String name, String city) {
        this.iata = iata;
        this.name = name;
        this.city = city;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
