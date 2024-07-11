package com.example.weatherforecast.model.weather;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("1h")
    private float oneHour;

    public float getOneHour() {
        return oneHour;
    }
}
