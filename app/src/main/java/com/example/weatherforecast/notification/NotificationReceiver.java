package com.example.weatherforecast.notification;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.util.Log;

import com.example.weatherforecast.R;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.weather.CurrentWeatherResponse;
import com.example.weatherforecast.view.FiveDayWeatherView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "Caothuludau";
    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    private static final double LATITUDE = 21.028511;
    private static final double LONGITUDE = 105.804817;
    private static final String UNITS = "metric";
    private static final String LANGUAGE = "vi";

    private CharSequence notificationTitle = "temp - temp";
    private String notificationText = "Chào ngày mới, thời tiết location hôm nay date: condition";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        fetchNotificationWeather(context);
    }

    private void createNotificationChannel(Context context) {
        CharSequence name = "Weather Daily Notification Channel";
        String description = "Provide daily notification and tips for weather";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    private void fetchNotificationWeather(Context context) {
        OpenWeatherApi apiService = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<CurrentWeatherResponse> call = apiService.getCurrentWeather(LATITUDE, LONGITUDE, LANGUAGE, UNITS, API_KEY);

        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setNotificationBody(response.body());
                    buildAndShowNotification(context);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setNotificationBody(CurrentWeatherResponse response) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM", Locale.getDefault());
        String dateFormatted = LocalDateTime.now().format(dateFormat);
        notificationTitle = Math.round(response.getMain().getTempMin()) + "°C - " + Math.round(response.getMain().getTempMax()) + "°C";
        notificationText = "Chào ngày mới, thời tiết Hà Nội hôm nay " + dateFormatted + ": " + response.getWeather().get(0).getDescription();
    }

    private void buildAndShowNotification(Context context) {
        Intent notificationIntent = new Intent(context, FiveDayWeatherView.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.weather_clear_day)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            return;
        }

        notificationManager.notify(1, builder.build());
    }
}
