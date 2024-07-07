package com.example.weatherforecast.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.CurrentWeatherResponse;
import com.example.weatherforecast.util.util;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewActivity extends AppCompatActivity {

    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    public static final int REQUEST_LOCATION = 1; // Unique request code for location permission
    private static double lat;
    private static double lon;
    private static String units = "metric";

    private CurrentWeatherResponse weatherResponse; // Member variable to store the weather response

    private TextView tvCityname, tvTemperature, tvDescription, tvFeellike, tvLastupdate;
    private SwipeRefreshLayout swiperefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Button btnOpenSidebar;
    private LocalDateTime callTime;

    private final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        // Get view
        init();

        // Check if location access is permitted by the user, if not, go to cities weather screen
        if (!getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("LocationEnabled", false)) {
            startActivity(new Intent(MainViewActivity.this, CitiesWeatherViewActivity.class));
        }


        // Get API response and store it in weatherResponse variable
        getCurrentWeather(lat, lon, "VI", units, API_KEY);

        // swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {
            // Call API to refresh content
            getCurrentWeather(lat, lon, "VI", "metric", API_KEY);
            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });

        // Schedule weather refresh
        scheduleWeatherRefresh();

    }

    public void getCurrentWeather(double lat,double lon, String language, String units, String apiKey) {
        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(lat, lon, language, units, apiKey);
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful()) {
                    // get API response
                    weatherResponse = response.body();

                    // get response time
                    long currentTimeMillis = System.currentTimeMillis() / 1000; // Convert to seconds
                    callTime = util.convertUnixToLocalDateTime(currentTimeMillis, ZoneId.systemDefault());

                    // Set view with the weather response info
                    setCurrentView(weatherResponse);
                    Log.e("Weather", "Request success");
                } else {
                    Log.e("Weather", "Request failed");
                }
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Log.e("Weather", "Network error", t);
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }
        });
    }

    public void init() {
        tvCityname = findViewById(R.id.tvcityname);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        tvFeellike = findViewById(R.id.tvFeellike);
        tvLastupdate = findViewById(R.id.tvLastupdate);
        swiperefresh = findViewById(R.id.swiperefresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
    }

    public void setCurrentView(CurrentWeatherResponse weatherResponse) {

        // Set button to open sidebar
        btnOpenSidebar.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainViewActivity.this, SettingsViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        // set info from api to view
        tvCityname.setText(weatherResponse.getName());
        tvTemperature.setText(String.valueOf(weatherResponse.getMain().getTemp()));
        tvDescription.setText(weatherResponse.getWeather().get(0).getDescription());
        tvFeellike.setText(String.valueOf(weatherResponse.getMain().getFeelsLike()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Log.d("Weather", "Converted LocalDateTime: " + callTime.format(formatter));
        tvLastupdate.setText("" + callTime.format(formatter));
    }

    public void scheduleWeatherRefresh() {
        int interval = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE).getInt("refreshInterval", 0); // Default to 'Do not refresh'
        long delayMillis = 0;

        switch (interval) {
            case 1: // Every hour
                delayMillis = 3600000;
                break;
            case 2: // Every 3 hours
                delayMillis = 10800000;
                break;
            case 0: // Do not automatically refresh
            default:
                handler.removeCallbacks(refreshRunnable);
                return;
        }

        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }

        long finalDelayMillis = delayMillis;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                getCurrentWeather(lat, lon, "VI", "metric", API_KEY);
                handler.postDelayed(this, finalDelayMillis);
            }
        };

        handler.postDelayed(refreshRunnable, delayMillis);
    }


    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        MainViewActivity.lat = lat;
    }

    public static double getLon() {
        return lon;
    }

    public static void setLon(double lon) {
        MainViewActivity.lon = lon;
    }
}
