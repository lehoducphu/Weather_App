package com.example.weatherforecast.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecast.R;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.CurrentWeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainView extends AppCompatActivity {
    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    private CurrentWeatherResponse weatherResponse; // Member variable to store the weather response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        // Get API response and store it in the member variable
        getCurrentWeather("1581130", "VI", "metric", API_KEY);
    }

    public void getCurrentWeather(String cityId, String language,String units, String apiKey){
        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(cityId, language,units, apiKey);
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful()) {
                    weatherResponse = response.body();
                    // Set view with the weather response info
                    setCurrentView(weatherResponse);
                    Log.e("Weather", "Request success");
                } else {
                    Log.e("Weather", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Log.e("Weather", "Network error", t);
            }
        });
    }

    public void setCurrentView(CurrentWeatherResponse weatherResponse){
        TextView tvCityname, tvTemperature, tvDescription, tvFeellike, tvWind, tvPressure, tvSunrise, tvSunset;

        tvCityname = findViewById(R.id.tvcityname);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        tvFeellike = findViewById(R.id.tvFeellike);
//        tvWind = findViewById(R.id.tvWind);
//        tvPressure = findViewById(R.id.tvPressure);
//        tvSunrise = findViewById(R.id.tvSunrise);
//        tvSunset = findViewById(R.id.tvSunset);

        tvCityname.setText(weatherResponse.getName());
        tvTemperature.setText(String.valueOf(weatherResponse.getMain().getTemp()));
        tvDescription.setText(weatherResponse.getWeather().get(0).getDescription());
        tvFeellike.setText(String.valueOf(weatherResponse.getMain().getFeelsLike()));
    }
}
