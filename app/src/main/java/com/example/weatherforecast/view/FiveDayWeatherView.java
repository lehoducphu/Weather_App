package com.example.weatherforecast.view;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.view.View;
import android.widget.Button;

import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.DailyWeatherAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.weather.DailyWeather;
import com.example.weatherforecast.model.weather.DailyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;
import com.example.weatherforecast.notification.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiveDayWeatherView extends AppCompatActivity {

    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    private static double lat = 21.028511;
    private static double lon = 105.804817;
    private static String units = "metric";
    private static String language = "vi";
    private static int cnt = 50;
    String targetTime = "09:00:00"; // Target time to filter data


    private List<DailyWeather> dailyWeatherList;
    private DailyWeatherAdapter dailyWeatherAdapter;
    private RecyclerView recyclerViewDaily;
    List<String> addedDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day_weather_forecast);

        initDailyWeather();
        Log.v("Main", "initDailyWeather called");

        fetchDailyWeather();
        Log.v("Main", "fetchDailyWeather called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 1001);
            }
        }
        scheduleNotification();
        Log.v("Main", "scheduleNotification called");

        Button shareButton = findViewById(R.id.button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWeatherInfo();
            }
        });
    }

    private void initDailyWeather() {
        dailyWeatherList = new ArrayList<>();
        recyclerViewDaily = findViewById(R.id.recyclerViewDaily);
        recyclerViewDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dailyWeatherAdapter = new DailyWeatherAdapter(dailyWeatherList);
        recyclerViewDaily.setAdapter(dailyWeatherAdapter);
    }

    private void fetchDailyWeather() {
        OpenWeatherApi apiService = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<DailyWeatherResponse> call = apiService.getDailyWeather(lat, lon, cnt, language, units, API_KEY);

        call.enqueue(new Callback<DailyWeatherResponse>() {
            @Override
            public void onResponse(Call<DailyWeatherResponse> call, Response<DailyWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setDailyWeatherView(response.body());
                }
            }

            @Override
            public void onFailure(Call<DailyWeatherResponse> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void setDailyWeatherView(DailyWeatherResponse response) {
        dailyWeatherList.clear();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE                                                                                                                                                                     ", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        for (WeatherList weatherData : response.getList()) {
            try {
                Date date = inputFormat.parse(weatherData.getDtTxt());
                String day = dayFormat.format(date);
                String dateFormatted = dateFormat.format(date);
                String time = timeFormat.format(date);

                if (time.equals(targetTime) && !addedDays.contains(dateFormatted)) {
                    dailyWeatherList.add(new DailyWeather(
                            day,
                            dateFormatted,
                            weatherData.getWeather().get(0).getIcon(),
                            (int) weatherData.getMain().getTemp(),
                            Integer.toString(weatherData.getMain().getHumidity())
                    ));
                    addedDays.add(dateFormatted);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dailyWeatherAdapter.notifyDataSetChanged();
        }
    }

    private void scheduleNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 30 * 1000;
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    private void shareWeatherInfo() {
        // Generate the weather information string
        String weatherInfo = generateWeatherInfo();

        // Create an intent to share the weather information
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Weather Forecast");
        shareIntent.putExtra(Intent.EXTRA_TEXT, weatherInfo);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Share weather via"));
    }

    private String generateWeatherInfo() {
        // You can customize this method to format your weather information
        StringBuilder weatherInfo = new StringBuilder();
        weatherInfo.append("Weather Forecast:\n\n");
        for (DailyWeather weather : dailyWeatherList) {
            weatherInfo.append(weather.getDay()).append(", ")
                    .append(weather.getDate()).append(": ")
                    .append(weather.getTemp()).append("°C, ")
                    .append(weather.getHumidity()).append(" Humidity\n");
        }
        return weatherInfo.toString();
    }
}