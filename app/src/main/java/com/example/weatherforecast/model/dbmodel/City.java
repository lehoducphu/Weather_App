package com.example.weatherforecast.model.dbmodel;

public class City {
    private int id;
    private String name;

    private double lon;

    private double lat;

    public City(int id, String name, double lon, double lat) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
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

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
