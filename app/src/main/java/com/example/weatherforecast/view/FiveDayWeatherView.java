package com.example.weatherforecast.view;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import com.example.weatherforecast.notification.DailyNotification;
import com.example.weatherforecast.notification.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

        notifyDailyWeather();
        Log.v("Main", "notifyDailyWeather called");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
//            if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 1001);
//            }
//        }
//        scheduleNotification();


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

    private void notifyDailyWeather() {
        DailyNotification dailyNotification = new DailyNotification(this);
        dailyNotification.createNotificationChannel();

        // Find the button and set an OnClickListener to trigger the notification
        Button notifyButton = findViewById(R.id.notify_button);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();  // Call the method to send the notification
            }
        });
    }

    private void sendNotification() {
        Intent intent = new Intent(this, FiveDayWeatherView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Caothuludau")
                .setSmallIcon(R.drawable.weather_clear_day)
                .setContentTitle("Example Notification")
                .setContentText("This is an example notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void scheduleNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 5 * 60 * 1000; // 5 minutes in milliseconds

        // Cancel any existing alarms to avoid duplicates
        alarmManager.cancel(pendingIntent);

        // Set a repeating alarm that triggers every 5 minutes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
}