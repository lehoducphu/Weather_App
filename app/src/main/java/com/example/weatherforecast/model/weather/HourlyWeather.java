package com.example.weatherforecast.model.weather;

public class HourlyWeather {
    private float temp;
    private String description;
    private String icon;
    private String time;

    // Getters and Setters
    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
