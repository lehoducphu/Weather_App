package com.example.weatherforecast.api;

import com.example.weatherforecast.model.geocoding.City;

public interface CityResponseCallback {
    void onCityResponseReceived(City city);
}