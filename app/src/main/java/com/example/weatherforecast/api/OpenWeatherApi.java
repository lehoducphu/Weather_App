package com.example.weatherforecast.api;

import com.example.weatherforecast.model.CurrentWeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherApi {
    @GET("weather")
    Call<CurrentWeatherResponse> getCurrentWeather(
            @Query("id") String cityId,
            @Query("lang") String lang,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}