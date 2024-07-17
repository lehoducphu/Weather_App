package com.example.weatherforecast.model.dbmodel;

public class DbCity {
    private int id;
    private String name;

    private double lon;

    private double lat;

    private String country;

    private String state;

    public DbCity(String name, double lon, double lat, String country, String state) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.country = country;
        this.state = state;
    }

    public DbCity(int id, String name, double lon, double lat, String country, String state) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.country = country;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
