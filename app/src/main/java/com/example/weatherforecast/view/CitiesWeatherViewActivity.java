package com.example.weatherforecast.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.dbmodel.DbCity;
import com.example.weatherforecast.model.weather.CityWeather;
import com.example.weatherforecast.model.weather.HourlyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;
import com.example.weatherforecast.util.ConnectDbUtil;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cities_weather_view);

        // init view
        initCityWeatherView();
        initDrawerLayout();

        // get user saved city list from database
        List<DbCity> dbCityList = getUserSavedcities();

        // Set the drawer layout
        setDrawerLayout();

        // Fetch the weather data for each city
        for(DbCity dbCity : dbCityList) {
            getCityWeather(dbCity.getLon(), dbCity.getLat(), 1, "vi", "metric", API_KEY);
        }

        // Swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {
            cityWeatherList.clear();
            // Fetch the weather data again
            for(DbCity dbCity : dbCityList) {
                getCityWeather(dbCity.getLon(), dbCity.getLat(), 1, "vi", "metric", API_KEY);
            }

            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });




    }

    private List<DbCity> getUserSavedcities() {
        // Get the list of cities from the database
        List<DbCity> dbCityList = new ArrayList<>();
        ConnectDbUtil dbUtil = new ConnectDbUtil(this); // 'this' is the Activity context
        dbUtil.processCopy();
        SQLiteDatabase database = dbUtil.openDatabase();

        // query data
        Cursor c = database.rawQuery(
                "SELECT id, city_name, city_longitude, city_latitude, country, state " +
                        "FROM city " +
                        "where id IN " +
                        "(SELECT city_id FROM users_city WHERE users_id = 1)", null);

        c.moveToFirst();

        // get data
        while (c.isAfterLast() == false) {
            DbCity dbCity = new DbCity(c.getInt(0), c.getString(1),
                    c.getDouble(2), c.getDouble(3),
                    c.getString(4), c.getString(5));

            dbCityList.add(dbCity);
            c.moveToNext();
        }
        c.close();
        return dbCityList;
    }

    private void initCityWeatherView(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
        tvcityname = findViewById(R.id.tvcityname);

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

        tvcityname.setText("");
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Ensure the navigation view has been properly initialized
        View headerView = navigationView.getHeaderView(0); // Assuming the buttons are in the header

        if (headerView != null) {
            navSearchButton = headerView.findViewById(R.id.nav_search);
            navSettingsButton = headerView.findViewById(R.id.nav_settings);
        } else {
            Log.e("CitiesWeatherViewActivity", "Header view is null");
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
                Toast.makeText(CitiesWeatherViewActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("MainViewActivity", "navSearchButton is null");
        }

        if (navSettingsButton != null) {
            navSettingsButton.setOnClickListener(v -> {
                startActivity(new Intent(CitiesWeatherViewActivity.this, SettingsViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        } else {
            Log.e("MainViewActivity", "navSettingsButton is null");
        }
    }


    private void onWeatherItemClick(CityWeather dailyWeather) {
        Intent intent = new Intent(CitiesWeatherViewActivity.this, MainViewActivity.class);
        intent.putExtra("locationSelected", true);
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