package com.example.weatherforecast.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.CityWeatherAdapter;
import com.example.weatherforecast.adapter.DrawerCityAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.dbmodel.DbCity;
import com.example.weatherforecast.model.weather.City;
import com.example.weatherforecast.model.weather.CityWeather;
import com.example.weatherforecast.model.weather.HourlyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;
import com.example.weatherforecast.util.ConnectDbUtil;
import com.example.weatherforecast.util.util;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitiesWeatherViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CityWeatherAdapter weatherAdapter;
    private List<CityWeather> cityWeatherList = new ArrayList<>();
    private List<DbCity> userCityList;
    private HourlyWeatherResponse hourlyWeatherResponse; // Member variable to store the hourly weather response

    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c"; // Replace with your API key
    private static final String UNITS = "metric";


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnOpenSidebar;
    private SwipeRefreshLayout swiperefresh;
    private TextView tvcityname;
    private ImageButton navSearchButton;
    private ImageButton navSettingsButton;
    private RecyclerView lvcity;
    private Button citymngbtn;
    private DrawerCityAdapter drawerCityAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cities_weather_view);

        // init view
        initCityWeatherView();
        initDrawerLayout();

        cityWeatherList = new ArrayList<>(); // Ensure this list is initialized before setting the adapter
        weatherAdapter = new CityWeatherAdapter(cityWeatherList, this::onWeatherItemClick);
        recyclerView.setAdapter(weatherAdapter); // Set the adapter early


        // get user saved city list from database
        userCityList = util.getUserSavedcities(this);



        // Fetch the weather data for each city
        for(DbCity dbCity : userCityList) {
            getCityWeather(dbCity.getLon(), dbCity.getLat(), 1, "vi", "metric", API_KEY);
        }

        // Set the drawer layout
        setDrawerLayout();

        // Swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {
            cityWeatherList.clear();
            // Fetch the weather data again
            for(DbCity dbCity : userCityList) {
                getCityWeather(dbCity.getLon(), dbCity.getLat(), 1, "vi", "metric", API_KEY);
            }

            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });




    }

    private void initCityWeatherView(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
        tvcityname = findViewById(R.id.tvcityname);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherAdapter = new CityWeatherAdapter(cityWeatherList, this::onWeatherItemClick);

        tvcityname.setText("");
        swiperefresh = findViewById(R.id.swiperefresh);

    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Ensure the navigation view has been properly initialized
        View headerView = navigationView.getHeaderView(0); // Assuming the buttons are in the header


        if (headerView != null) {
            navSearchButton = headerView.findViewById(R.id.nav_search);
            navSettingsButton = headerView.findViewById(R.id.nav_settings);
            lvcity = headerView.findViewById(R.id.lvcity);
            citymngbtn = headerView.findViewById(R.id.citymngbtn);
        } else {
            Log.e("MainViewActivity", "Header view is null");
        }

        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
    }


    private void setCityWeatherView(HourlyWeatherResponse forecastResponse, double lon, double lat) {
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
            dailyWeather.setLon(lon);
            dailyWeather.setLat(lat);

            cityWeatherList.add(dailyWeather);
            weatherAdapter.notifyDataSetChanged();
        }

    }


    private void setDrawerLayout() {
        btnOpenSidebar.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        if (navSearchButton != null) {
            navSearchButton.setOnClickListener(v -> {
                Intent intent = new Intent(new Intent(CitiesWeatherViewActivity.this, SearchCityViewActivity.class));
                intent.putExtra("fromActivity", "main");
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
            });
        } else {
            Log.e("CitiesWeatherViewActivity", "navSearchButton is null");
        }

        if (navSettingsButton != null) {
            navSettingsButton.setOnClickListener(v -> {
                startActivity(new Intent(CitiesWeatherViewActivity.this, SettingsViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

        } else {
            Log.e("CitiesWeatherViewActivity", "navSettingsButton is null");
        }

        if (lvcity == null) {
            Log.e("MainViewActivity", "lvcity is null");
        } else {
            lvcity.setLayoutManager(new LinearLayoutManager(this));
            drawerCityAdapter = new DrawerCityAdapter(this, userCityList, new DrawerCityAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    DbCity city = userCityList.get(position);
                    Intent intent = new Intent(CitiesWeatherViewActivity.this, MainViewActivity.class);
                    intent.putExtra("locationSelected", true);
                    intent.putExtra("cityName", city.getName());
                    intent.putExtra("lon", city.getLon());
                    intent.putExtra("lat", city.getLat());
                    startActivity(intent);
                    finish();
                }
            });

            lvcity.setAdapter(drawerCityAdapter);
        }

        if (citymngbtn != null) {
            citymngbtn.setOnClickListener(v -> {
                startActivity(new Intent(CitiesWeatherViewActivity.this, LocationManagementViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        } else {
            Log.e("MainViewActivity", "citymngbtn is null");
        }


    }


    private void onWeatherItemClick(CityWeather dailyWeather) {
        Intent intent = new Intent(CitiesWeatherViewActivity.this, MainViewActivity.class);
        intent.putExtra("locationSelected", true);
        intent.putExtra("cityName",dailyWeather.getCity());
        intent.putExtra("lon", dailyWeather.getLon());
        intent.putExtra("lat", dailyWeather.getLat());
        startActivity(intent);
        finish();
    }


    private void getCityWeather(double lon, double lat , int cnt, String language, String units, String apiKey) {
        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<HourlyWeatherResponse> call = service.getHourlyWeather(lat, lon, 1, language, units, apiKey);

        call.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful()) {
                    // get API response
                    hourlyWeatherResponse = response.body();
                    setCityWeatherView(hourlyWeatherResponse, lon, lat);

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