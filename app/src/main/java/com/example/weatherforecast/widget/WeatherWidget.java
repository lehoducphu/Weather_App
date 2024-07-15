package com.example.weatherforecast.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.weatherforecast.R;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.model.weather.CurrentWeatherResponse;
import com.example.weatherforecast.view.MainViewActivity;

import com.example.weatherforecast.api.OpenWeatherApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherWidget extends AppWidgetProvider {
    private static final String API_KEY = "de13238c0859cbbf9d42bacb340bbe2c";
    private static final double LATITUDE = 21.028511;
    private static final double LONGITUDE = 105.804817;
    private static final String UNITS = "metric";
    private static final String LANGUAGE = "vi";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
//            Intent intent = new Intent(context, MainViewActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, -1);
//
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
//
//            // Update the widget UI elements here
//            views.setTextViewText(R.id.widget_temp, "27°C");
//            views.setTextViewText(R.id.widget_conditions, "Sunny");
//
//            appWidgetManager.updateAppWidget(appWidgetId, views);
            fetchCurrentWeather(context, appWidgetManager, appWidgetId);


        }
    }
    private void fetchCurrentWeather(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        OpenWeatherApi apiService = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<CurrentWeatherResponse> call = apiService.getCurrentWeather(LATITUDE, LONGITUDE, LANGUAGE, UNITS, API_KEY);

        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWidget(context, appWidgetManager, appWidgetId, response.body());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, CurrentWeatherResponse weatherResponse) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        views.setTextViewText(R.id.widget_temp, Math.round(weatherResponse.getMain().getTemp()) + "°C");
        views.setTextViewText(R.id.widget_conditions, weatherResponse.getWeather().get(0).getDescription());

        Intent intent = new Intent(context, MainViewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
