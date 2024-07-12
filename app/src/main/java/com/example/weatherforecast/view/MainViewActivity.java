package com.example.weatherforecast.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.HourlyWeatherAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.weather.CurrentWeatherResponse;
import com.example.weatherforecast.model.weather.HourlyWeather;
import com.example.weatherforecast.model.weather.HourlyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;
import com.example.weatherforecast.util.util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewActivity extends AppCompatActivity {

    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    public static final int REQUEST_LOCATION = 1; // Unique request code for location permission

    private static double lat = 1;
    private static double lon = 1;
    private static String units = "metric";
    private static String language = "vi";
    private static int cnt = 10;
    private List<HourlyWeather> hourlyWeatherList;


    private static boolean locationEnabled = false;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;


    private CurrentWeatherResponse weatherResponse; // Member variable to store the weather response
    private HourlyWeatherResponse hourlyWeatherResponse; // Member variable to store the hourly weather response


    private TextView tvCityname, tvTemperature, tvDescription, tvFeellike, tvLastupdate;
    private ImageView ivWeatherIcon;
    private SwipeRefreshLayout swiperefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageButton btnOpenSidebar;
    private LocalDateTime callTime;
    private RecyclerView recyclerViewHourly;
    private HourlyWeatherAdapter hourlyWeatherAdapter;


    private final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);


        // Get view
        initCurrentWeatherView();
        initHourlyWeatherView();


        boolean firstStart = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("FirstStart", true);

            locationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("LocationEnabled", false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Intent may contain the location data so we will get data in here
        boolean locationSelected = false;

        // Check if the intent contains the location data
        if (!locationSelected) {
            // Check permission status and request if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request permission from the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {

                // Set location enabled preference to true
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", true)
                        .apply();

                // Permission already granted, proceed with location fetching
                getCurrentLocationWeather();

                Log.v("Main", "Permission already granted");

            }
        } else {
            //get latitude of selected location

        }


        // Swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {

                // Call API to refresh content
                getCurrentWeather(lat, lon, language, units, API_KEY);
                getHourlyWeather(lat, lon, cnt, language, units, API_KEY);

            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });

        // Schedule weather refresh (can be changed in settings view)
        scheduleWeatherRefresh();


    }

    public void getCurrentWeather(double lat, double lon, String language, String units, String apiKey) {
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
                    setCurrentWeatherView(weatherResponse);
                    Log.e("getCurrentWeather", "Request success");
                } else {
                    Log.e("getCurrentWeather", "Request failed");
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

    public void getHourlyWeather(double lat, double lon, int cnt, String language, String units, String apiKey) {

        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<HourlyWeatherResponse> call = service.getHourlyWeather(lat, lon, 10, language, units, apiKey);

        call.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful()) {
                    // get API response
                    hourlyWeatherResponse = response.body();
                    setHourlyWeatherView(hourlyWeatherResponse);

                    Log.e("getHourlyWeather", "Request success");
                } else {
                    Log.e("getHourlyWeather", "Request failed");
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

    public void initCurrentWeatherView() {
        tvCityname = findViewById(R.id.tvcityname);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        tvFeellike = findViewById(R.id.tvFeellike);
//        tvLastupdate = findViewById(R.id.tvLastupdate);
        swiperefresh = findViewById(R.id.swiperefresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnOpenSidebar = findViewById(R.id.btnOpenSidebar);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        Log.v("Main", "initCurrentWeatherView called");
    }

    public void initHourlyWeatherView() {
        hourlyWeatherList = new ArrayList<>();

        recyclerViewHourly = findViewById(R.id.recyclerViewHourly);
        recyclerViewHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
        recyclerViewHourly.setAdapter(hourlyWeatherAdapter);
        Log.v("Main", "initHourlyWeatherView called");
    }

    public void setCurrentWeatherView(CurrentWeatherResponse weatherResponse) {

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
        tvTemperature.setText(String.valueOf(weatherResponse.getMain().getTemp() + "°"));
        tvDescription.setText(weatherResponse.getWeather().get(0).getDescription());
//        tvFeellike.setText(String.valueOf(weatherResponse.getMain().getFeelsLike()));
        tvFeellike.setText(lat+", "+lon);



        String imageUrl = "https://openweathermap.org/img/wn/"+weatherResponse.getWeather().get(0).getIcon()+"@2x.png";
        Picasso.get()
                .load(imageUrl)
                .into(ivWeatherIcon);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Log.d("Weather", "Converted LocalDateTime: " + callTime.format(formatter));
//        tvLastupdate.setText("" + callTime.format(formatter));
    }

    private void setHourlyWeatherView(HourlyWeatherResponse forecastResponse) {
        hourlyWeatherList.clear();
        for (WeatherList forecast : forecastResponse.getList()) {

            HourlyWeather hourlyWeather = new HourlyWeather();
            hourlyWeather.setTemp(Math.round(forecast.getMain().getTemp()));
            hourlyWeather.setDescription(forecast.getWeather().get(0).getDescription());
            hourlyWeather.setIcon(forecast.getWeather().get(0).getIcon());
            hourlyWeather.setTime(forecast.getDtTxt().split(" ")[1]);
            hourlyWeatherList.add(hourlyWeather);
        }
        hourlyWeatherAdapter.notifyDataSetChanged();
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
                getCurrentWeather(lat, lon, language, "metric", API_KEY);
                handler.postDelayed(this, finalDelayMillis);
            }
        };

        handler.postDelayed(refreshRunnable, delayMillis);
    }


    public void getCurrentLocationWeather() {

        // check if access location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            // Request permission from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        // Get current location weather
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    MainViewActivity.setLat(location.getLatitude());
                    MainViewActivity.setLon(location.getLongitude());


                    Log.d("getCurrentLocationWeather", "Lat: " + MainViewActivity.getLat());
                    Log.d("getCurrentLocationWeather", "Lon: " + MainViewActivity.getLon());

                    // Get API response and store it in weatherResponse variable
                    getCurrentWeather(lat, lon, language, units, API_KEY);

                    // Get Hourly Weather Forecast
                    getHourlyWeather(lat, lon, cnt, language, units, API_KEY);

                    Log.d("getCurrentLocationWeather", "get current Weather success");


                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission request is for location
        if (requestCode == REQUEST_LOCATION) {
            // Check if the permission is granted if
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Set location enabled preference to true
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", true)
                        .apply();

                Toast.makeText(this, "Got Permission", Toast.LENGTH_SHORT).show();
                getCurrentLocationWeather();
            }
        } else {

            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainViewActivity.this, CitiesWeatherViewActivity.class));
        }

        getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .edit()
                .putBoolean("FirstStart", false)
                .apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean firstStart = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("FirstStart", true);

        if (!firstStart) {
            boolean isLocationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                    .getBoolean("LocationEnabled", false);


            // If location is enabled, fetch the current location and weather
            if (isLocationEnabled) {
                getCurrentLocationWeather();
            } else {
                // If location is disabled, go to cities weather screen
                startActivity(new Intent(MainViewActivity.this, CitiesWeatherViewActivity.class));
            }

            scheduleWeatherRefresh();
        }
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
