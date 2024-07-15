package com.example.weatherforecast.model.geographic;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

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

    // Getters and setters
}

