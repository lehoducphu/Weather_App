package com.example.weatherforecast.api;

import com.example.weatherforecast.model.weather.CurrentWeatherResponse;

import com.example.weatherforecast.model.geocoding.City;
import com.example.weatherforecast.model.weather.HourlyWeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherApi {
    @GET("data/2.5/weather")
    Call<CurrentWeatherResponse> getCurrentWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("lang") String lang,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("data/2.5/forecast")
    Call<HourlyWeatherResponse> getHourlyWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("cnt") int cnt,
            @Query("lang") String lang,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("geo/1.0/reverse")
    Call<List<City>> getCitiesByReverse(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );

    @GET("geo/1.0/direct")
    Call<List<City>> getCitiesByDirect(
            @Query("q") String cityName,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );


}