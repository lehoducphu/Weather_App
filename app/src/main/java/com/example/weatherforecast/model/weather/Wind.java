package com.example.weatherforecast.model.weather;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    private float speed;
    @SerializedName("deg")
    private int deg;
    @SerializedName("gust")
    private float gust;

    public float getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }

    public float getGust() {
        return gust;
    }
}
