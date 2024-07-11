package com.example.weatherforecast.model.weather;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    private float temp;
    @SerializedName("feels_like")
    private float feelsLike;
    @SerializedName("temp_min")
    private float tempMin;
    @SerializedName("temp_max")
    private float tempMax;
    @SerializedName("pressure")
    private int pressure;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("sea_level")
    private int seaLevel;
    @SerializedName("grnd_level")
    private int grndLevel;

    public float getTemp() {
        return temp;
    }

    public float getFeelsLike() {
        return feelsLike;
    }

    public float getTempMin() {
        return tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getSeaLevel() {
        return seaLevel;
    }

    public int getGrndLevel() {
        return grndLevel;
    }
}
