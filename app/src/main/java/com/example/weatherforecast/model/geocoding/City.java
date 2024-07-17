package com.example.weatherforecast.model.geocoding;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("name")
    private String name;

    @SerializedName("local_names")
    private LocalNames localNames;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    @SerializedName("country")
    private String country;

    @SerializedName("state")
    private String state;
    // Getters and setters


    public City(String name, double lat, double lon, String country, String state) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.country = country;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public LocalNames getLocalNames() {
        return localNames;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }
    public void setName(String name) {
        this.name = name;
    }
}

