package com.example.weatherforecast.model.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherList {
    @SerializedName("dt")
    private long dt;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("pop")
    private double pop;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("dt_txt")
    private String dtTxt;

    public long getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getPop() {
        return pop;
    }

    public Sys getSys() {
        return sys;
    }

    public String getDtTxt() {
        return dtTxt;
    }
}
