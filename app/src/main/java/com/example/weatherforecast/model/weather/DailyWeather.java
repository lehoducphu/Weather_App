package com.example.weatherforecast.model.weather;

public class DailyWeather {
    private String day;
    private String date;
    private int weatherIcon;
    private String temp;
    private String precipitation;

    public DailyWeather(String day, String date, int weatherIcon, String temp, String precipitation) {
        this.day = day;
        this.date = date;
        this.weatherIcon = weatherIcon;
        this.temp = temp;
        this.precipitation = precipitation;
    }

    // Getters
    public String getDay() { return day; }
    public String getDate() { return date; }
    public int getWeatherIcon() { return weatherIcon; }
    public String getTemp() { return temp; }
    public String getPrecipitation() { return precipitation; }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }
}
