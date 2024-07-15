package com.example.weatherforecast.model.weather;

public class DailyWeather {
    private String day;
    private String date;
    private String weatherIcon;
    private int temp;
    private String humidity;

    public DailyWeather() {
    }

    public DailyWeather(String day, String date, String weatherIcon, int temp, String humidity) {
        this.day = day;
        this.date = date;
        this.weatherIcon = weatherIcon;
        this.temp = temp;
        this.humidity = humidity;
    }

    // Getters
    public String getDay() { return day; }
    public String getDate() { return date; }
    public String getWeatherIcon() { return weatherIcon; }
    public int getTemp() { return temp; }
    public String getHumidity() { return humidity; }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
