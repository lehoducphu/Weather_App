package com.example.weatherforecast.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.CityWeatherAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.weather.City;
import com.example.weatherforecast.model.weather.CityWeather;
import com.example.weatherforecast.model.weather.HourlyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CitiesWeatherViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CityWeatherAdapter weatherAdapter;
    private List<CityWeather> cityWeatherList = new ArrayList<>();
    private HourlyWeatherResponse hourlyWeatherResponse; // Member variable to store the hourly weather response

    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c"; // Replace with your API key
    private static final String UNITS = "metric";


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnOpenSidebar;
    private SwipeRefreshLayout swiperefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cities_weather_view);

        initCityWeatherView();

        getCityWeather(21.0285, 105.8542, 1, "vi", "metric", API_KEY);


        // Swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {
            // Fetch the weather data again
            getCityWeather(21.0285, 105.8542, 1, "vi", "metric", API_KEY);
            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });




    }

    private void initCityWeatherView(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherAdapter = new CityWeatherAdapter(cityWeatherList, this::onWeatherItemClick);

        recyclerView.setAdapter(weatherAdapter);
        swiperefresh = findViewById(R.id.swiperefresh);


        // Set button to open sidebar
        btnOpenSidebar.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(CitiesWeatherViewActivity.this, SettingsViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private void setCityWeatherView(HourlyWeatherResponse forecastResponse){
        cityWeatherList.clear();
        if (!forecastResponse.getList().isEmpty()) {
            WeatherList forecast = forecastResponse.getList().get(0);

            CityWeather dailyWeather = new CityWeather();
            dailyWeather.setCity(forecastResponse.getCity().getName());
            dailyWeather.setTemp(Math.round(forecast.getMain().getTemp()));
            dailyWeather.setTemp_min(Math.round(forecast.getMain().getTempMin()));
            dailyWeather.setTemp_max(Math.round(forecast.getMain().getTempMax()));
            dailyWeather.setDescription(forecast.getWeather().get(0).getDescription());
            dailyWeather.setIcon(forecast.getWeather().get(0).getIcon());
            dailyWeather.setDate(forecast.getDtTxt().split(" ")[0]);

            cityWeatherList.add(dailyWeather);
            weatherAdapter.notifyDataSetChanged();
        }

    }

    private void onWeatherItemClick(CityWeather dailyWeather) {
        Intent intent = new Intent(this, MainViewActivity.class);
        intent.putExtra("dailyWeather", dailyWeather);
        startActivity(intent);
    }


    private void getCityWeather(double lat, double lon, int cnt, String language, String units, String apiKey) {
        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<HourlyWeatherResponse> call = service.getHourlyWeather(lat, lon, 1, language, units, apiKey);

        call.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful()) {
                    // get API response
                    hourlyWeatherResponse = response.body();
                    setCityWeatherView(hourlyWeatherResponse);

                    Log.e("HourlyWeather", "Request success");
                } else {
                    Log.e("HourlyWeather", "Request failed");
                }
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<HourlyWeatherResponse> call, Throwable t) {
                Log.e("Weather", "Network error", t);
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }
        });
    }
}