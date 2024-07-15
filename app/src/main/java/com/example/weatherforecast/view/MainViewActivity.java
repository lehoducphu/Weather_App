package com.example.weatherforecast.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.DrawerCityAdapter;
import com.example.weatherforecast.adapter.HourlyWeatherAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.CityResponseCallback;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.geocoding.City;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewActivity extends AppCompatActivity {

    public static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    public static final int REQUEST_LOCATION = 1; // Unique request code for location permission

    private static double lat = 1;
    private static double lon = 1;
    private static String tempUnit = "metric";
    private static String speedUnit = "km/h";
    private static String pressureUnit = "mb";

    private static String language = "vi";
    private static int cnt = 10;
    private List<HourlyWeather> hourlyWeatherList;
    private List<City> userCityList;
    private DrawerCityAdapter drawerCityAdapter;


    private static boolean locationEnabled = false;
    private static boolean locationSelected = false;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private City currentCity;


    private CurrentWeatherResponse weatherResponse; // Member variable to store the weather response
    private HourlyWeatherResponse hourlyWeatherResponse; // Member variable to store the hourly weather response


    private TextView tvCityname, tvTemperature, tvDescription, tvFeellike, tvLastupdate;
    private ImageView ivWeatherIcon;
    private SwipeRefreshLayout swiperefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnOpenSidebar;
    private LocalDateTime callTime;
    private RecyclerView recyclerViewHourly;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private ImageButton navSearchButton;
    private ImageButton navSettingsButton;
    private Button citymngbtn;
    private RecyclerView lvcity;

    private TextView aqi_label;
    private TextView aqi_value;
    private TextView aqi_status;
    private TextView wind_value;
    private TextView humidity_value;
    private TextView pressure_value;
    private TextView visibility_value;
    private TextView sunrise_value;
    private TextView sunset_value;

    private final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        // Get view
        initDrawerLayout();
        initCurrentWeatherView();
        initHourlyWeatherView();
        initExtraWeatherInfo();

        // Set drawer layout
        setDrawerLayout();


    }


    public void getCurrentWeather(double lat, double lon, String language, String units, String apiKey) {
        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);

        // Call API
        Call<CurrentWeatherResponse> call = service.getCurrentWeather(lat, lon, language, tempUnit, apiKey);

        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful()) {

                    // get API response
                    weatherResponse = response.body();

                    // get response time
//                    long currentTimeMillis = System.currentTimeMillis() / 1000; // Convert to seconds
//                    callTime = util.convertUnixToLocalDateTime(currentTimeMillis, ZoneId.systemDefault());

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
        Call<HourlyWeatherResponse> call = service.getHourlyWeather(lat, lon, 10, language, tempUnit, apiKey);

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

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Ensure the navigation view has been properly initialized
        View headerView = navigationView.getHeaderView(0); // Assuming the buttons are in the header

        userCityList = new ArrayList<>();
        userCityList.add(new City("Hà Nội", 21.0285, 105.8542, "VN", null));
        userCityList.add(new City("Hồ Chí Minh", 10.762622, 106.660172, "VN", null));

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

    private void initExtraWeatherInfo() {
        aqi_label = findViewById(R.id.aqi_label);
        aqi_value = findViewById(R.id.aqi_value);
        aqi_status = findViewById(R.id.aqi_status);
        wind_value = findViewById(R.id.wind_value);
        humidity_value = findViewById(R.id.humidity_value);
        pressure_value = findViewById(R.id.pressure_value);
        visibility_value = findViewById(R.id.visibility_value);
        sunrise_value = findViewById(R.id.sunrise_value);
        sunset_value = findViewById(R.id.sunset_value);
    }


    public void setCurrentWeatherView(CurrentWeatherResponse weatherResponse) {

        // set info from api to view
        // set city name
        tvCityname.setText(weatherResponse.getName());

        // get temperature unit
        String weatherUnit = tempUnit.equals("metric") ? "°C" : "°F";

        // set temperature
        tvTemperature.setText(String.valueOf(Math.round(weatherResponse.getMain().getTemp()) + weatherUnit));

        // set description
        tvDescription.setText(weatherResponse.getWeather().get(0).getDescription());

        String minMaxTemp = Math.round(weatherResponse.getMain().getTempMax()) + weatherUnit
                + "/" + Math.round(weatherResponse.getMain().getTempMin()) + weatherUnit;

        String feelTemp = "\nCảm giác như " + Math.round(weatherResponse.getMain().getFeelsLike()) + weatherUnit;
        // set feel like temperature
        tvFeellike.setText(minMaxTemp + feelTemp);
//        tvFeellike.setText(lat + ", " + lon);


        String imageUrl = "https://openweathermap.org/img/wn/" + weatherResponse.getWeather().get(0).getIcon() + "@2x.png";
        Picasso.get()
                .load(imageUrl)
                .into(ivWeatherIcon);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        Log.d("Weather", "Converted LocalDateTime: " + callTime.format(formatter));
//        tvLastupdate.setText("" + callTime.format(formatter));
    }

    private void setHourlyWeatherView(HourlyWeatherResponse forecastResponse) {
        hourlyWeatherList.clear();
        for (WeatherList forecast : forecastResponse.getList()) {

            HourlyWeather hourlyWeather = new HourlyWeather();

            // get temperature unit
            String weatherUnit = tempUnit.equals("metric") ? "°C" : "°F";
            hourlyWeather.setTemp(Math.round(forecast.getMain().getTemp()));
            hourlyWeather.setDescription(forecast.getWeather().get(0).getDescription());
            hourlyWeather.setIcon(forecast.getWeather().get(0).getIcon());
            hourlyWeather.setTime(forecast.getDtTxt().split(" ")[1]);
            hourlyWeatherList.add(hourlyWeather);
        }
        hourlyWeatherAdapter.notifyDataSetChanged();
    }

    private void setDrawerLayout() {
        btnOpenSidebar.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        if (navSearchButton != null) {
            navSearchButton.setOnClickListener(v -> {
                startActivity(new Intent(MainViewActivity.this, SearchCityViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        } else {
            Log.e("CitiesWeatherViewActivity", "navSearchButton is null");
        }

        if (navSettingsButton != null) {
            navSettingsButton.setOnClickListener(v -> {
                startActivity(new Intent(MainViewActivity.this, SettingsViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

        } else {
            Log.e("CitiesWeatherViewActivity", "navSettingsButton is null");
        }

        if (lvcity == null) {
            Log.e("MainViewActivity", "lvcity is null");
        } else {
            lvcity.setLayoutManager(new LinearLayoutManager(this));
             drawerCityAdapter = new DrawerCityAdapter(userCityList);
             lvcity.setAdapter(drawerCityAdapter);
        }

        if(citymngbtn != null) {
            citymngbtn.setOnClickListener(v -> {
                startActivity(new Intent(MainViewActivity.this, LocationManagementViewActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        } else {
            Log.e("MainViewActivity", "citymngbtn is null");
        }


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
                    currentLocation = location;
                    lat = currentLocation.getLatitude();
                    lon = currentLocation.getLongitude();

                    getCityResponseByReverse(lat, lon, 1, API_KEY, new CityResponseCallback() {
                        @Override
                        public void onCityResponseReceived(City city) {
                            if (city != null) {
                                currentCity = city;
                                MainViewActivity.setLat(currentCity.getLat());
                                MainViewActivity.setLon(currentCity.getLon());

                                // Get API response and store it in weatherResponse variable
                                getCurrentWeather(lat, lon, language, tempUnit, API_KEY);

                                // Get Hourly Weather Forecast
                                getHourlyWeather(lat, lon, cnt, language, tempUnit, API_KEY);

                                // Get extra weather info
                                getAQI(lat, lon, API_KEY);
                                getExtraWeatherInfo(lat, lon, language, tempUnit, API_KEY);

                                Log.d("getCurrentLocationWeather", "get current Weather success");

                            }
                        }
                    });
                }
            }
        });
    }


    private void getCityResponseByReverse(double lat, double lon, int limit, String apiKey, CityResponseCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherApi geocodingService = retrofit.create(OpenWeatherApi.class);
        geocodingService.getCitiesByReverse(lat, lon, limit, apiKey).enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    City currentCity = response.body().get(0);
                    Log.e("getCityResponseByReverse", "Request success: " + currentCity.getName());

                    callback.onCityResponseReceived(currentCity);

                } else {
                    Log.e("getCityResponseByReverse", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.e("getCityResponseByReverse", "Network error", t);
                // Handle failure
            }
        });
    }

    public void getAQI(double lat, double lon, String apiKey) {
        String url = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    if (list.length() > 0) {
                        JSONObject mainObject = list.getJSONObject(0);
                        JSONObject main = mainObject.getJSONObject("main");
                        JSONObject components = mainObject.getJSONObject("components");
                        int aqi = main.getInt("aqi");
                        String aqiStatus = getAQIStatus(aqi) + " (" + aqi + ")";
                        aqi_value.setText(aqiStatus);
                        aqi_status.setText("");
                    }
                } catch (JSONException e) {
                    Log.e("getExtraWeatherInfo", "JSON parsing error", e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getExtraWeatherInfo", "Volley error", error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private String getAQIStatus(int aqi) {
        if (aqi == 1) return "Tốt";
        if (aqi == 2 || aqi == 3) return "Trung bình";
        if (aqi == 4) return "Xấu";
        if (aqi == 5) return "Rất xấu";
        return "Unknown";
    }

    private void getExtraWeatherInfo(double lat, double lon, String language, String tempUnit, String apiKey) {

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon
                + "&appid=" + apiKey + "&lang=" + language + "&units=" + tempUnit;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            JSONObject weatherObj = weatherArray.getJSONObject(0);

                            JSONObject main = jsonObject.getJSONObject("main");


                            JSONObject wind = jsonObject.getJSONObject("wind");
                            String speeed = wind.getString("speed"); // tốc độ gió
                            if (speedUnit.equals("mph")) {
                                Double SpeeedUnit = util.kmhToMph(Double.parseDouble(speeed));
                                wind_value.setText(String.format("%.2f", SpeeedUnit) + "mph");
                            } else if (speedUnit.equals("m/s")) {
                                Double SpeeedUnit = util.kmhToMs(Double.parseDouble(speeed));
                                wind_value.setText(String.format("%.2f", SpeeedUnit) + "m/s");
                            } else {
                                wind_value.setText(String.format("%.2f", Double.parseDouble(speeed)) + "km/h");
                            }


                            String humidity = main.getString("humidity"); // độ ẩm
                            humidity_value.setText(humidity + "%");


                            String pressure = main.getString("pressure"); // áp suất
                            if (pressureUnit.equals("inHg")) {
                                Double PressureUnit = util.mbToInHg(Double.parseDouble(pressure));
                                pressure_value.setText(String.format("%.2f", PressureUnit) + "inHg");
                            }
                            if (pressureUnit.equals("bar")) {
                                Double PressureUnit = util.mbToBar(Double.parseDouble(pressure));
                                pressure_value.setText(String.format("%.2f", PressureUnit) + "bar");
                            }
                            if (pressureUnit.equals("psi")) {
                                Double PressureUnit = util.mbToPsi(Double.parseDouble(pressure));
                                pressure_value.setText(String.format("%.2f", PressureUnit) + "psi");
                            }
                            if (pressureUnit.equals("mb")) {
                                pressure_value.setText(String.format("%.2f", Double.parseDouble(pressure)) + "mb");
                            }


                            String visibility = jsonObject.getString("visibility"); // tầm nhìn
                            visibility_value.setText(Integer.parseInt(visibility) / 1000 + "km");
                            JSONObject sys = jsonObject.getJSONObject("sys");
                            String sunrise = sys.getString("sunrise"); // Bình minh
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                            Date date = new Date(Long.parseLong(sunrise) * 1000);
                            sunrise = sdf.format(date);
                            sunrise_value.setText(sunrise);
                            String sunset = sys.getString("sunset"); // hoàng hôn
                            date = new Date(Long.parseLong(sunset) * 1000);
                            sunset = sdf.format(date);
                            sunset_value.setText(sunset);
//                            Toast.makeText(MainActivity.this, "" + icon, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + temperState, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + temp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + speeed, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + humidity, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + pressure, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + all, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, "" + currentTime, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainViewActivity.this, "" + volleyError.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("MyError1: ", volleyError.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

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
//        boolean firstStart = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
//                .getBoolean("FirstStart", true);
//
//        //
//        if (!firstStart) {
//            boolean isLocationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
//                    .getBoolean("LocationEnabled", false);
//
//            // get temperature unit
//            tempUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
//                    .getString("tempUnit", "metric");
//
//            // get speed unit
//            speedUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
//                    .getString("speedUnit", "km/h");
//
//            // get pressure unit
//            pressureUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
//                    .getString("pressureUnit", "mb");
//
//            if (locationSelected) {
//                getCurrentWeather(lat, lon, language, tempUnit, API_KEY);
//                getHourlyWeather(lat, lon, cnt, language, tempUnit, API_KEY);
//                getExtraWeatherInfo(lat, lon, language, tempUnit, API_KEY);
//            } else {
//                // If location is enabled, fetch the current location and weather
//                if (isLocationEnabled) {
//                    getCurrentLocationWeather();
//                } else {
//                    // If location is disabled, go to cities weather screen
//                    startActivity(new Intent(MainViewActivity.this, CitiesWeatherViewActivity.class));
//                }
//            }
//
//            scheduleWeatherRefresh();
//        }
        // get temperature unit
        tempUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("tempUnit", "metric");

        // get speed unit
        speedUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("speedUnit", "km/h");

        // get pressure unit
        pressureUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("pressureUnit", "mb");


        // Check if the app is started for the first time
        boolean firstStart = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("FirstStart", true);


        locationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("LocationEnabled", false);


        // Get the intent that started this activity
        Intent intent = getIntent();

        // Intent may contain the location data so we will get data in here
        locationSelected = intent.getBooleanExtra("locationSelected", false);


        // Check if the intent contains the location data
        if (!locationSelected) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            // Check permission status and request if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission from the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                // Check if location access is enabled
                if (locationEnabled) {
                    // Permission already granted, proceed with location fetching
                    getCurrentLocationWeather();
                    Log.v("Main", "Permission already granted");
                } else {
                    startActivity(new Intent(MainViewActivity.this, CitiesWeatherViewActivity.class));
                }


            }
        } else {
            Log.e("MainViewActivity", "Location selected");
            //get latitude of selected location
            lat = intent.getDoubleExtra("lat", 1);
            //get longitude of selected location
            lon = intent.getDoubleExtra("lon", 1);

            // Get API response and store it in weatherResponse variable
            getCurrentWeather(lat, lon, language, tempUnit, API_KEY);
            getHourlyWeather(lat, lon, cnt, language, tempUnit, API_KEY);


            getAQI(lat, lon, API_KEY);
            getExtraWeatherInfo(lat, lon, language, tempUnit, API_KEY);
        }


        // Swipe to refresh feature
        swiperefresh.setOnRefreshListener(() -> {

            // get temperature unit
            tempUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                    .getString("tempUnit", "metric");

            // get speed unit
            speedUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                    .getString("speedUnit", "km/h");

            // get pressure unit
            pressureUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                    .getString("pressureUnit", "mb");

            // Call API to refresh content
            getCurrentWeather(lat, lon, language, tempUnit, API_KEY);
            getHourlyWeather(lat, lon, cnt, language, tempUnit, API_KEY);
            getExtraWeatherInfo(lat, lon, language, tempUnit, API_KEY);

            Log.i("Main", "onRefresh called from SwipeRefreshLayout");
        });

        // Schedule weather refresh (can be changed in settings view)
        scheduleWeatherRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .edit()
                .putBoolean("locationSelected", false)
                .apply();
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

    public static String getTempUnit() {
        return tempUnit;
    }
}
