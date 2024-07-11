package com.example.weatherforecast.view;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.DailyWeatherAdapter;
import com.example.weatherforecast.api.ApiClient;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.weather.DailyWeather;
import com.example.weatherforecast.model.weather.DailyWeatherResponse;
import com.example.weatherforecast.model.weather.WeatherList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FiveDayWeatherView extends AppCompatActivity {

    private DailyWeatherResponse dailyWeatherResponse; // Member variable to store the hourly weather response
    private SwipeRefreshLayout swiperefresh;
    private List<DailyWeather> dailyWeatherList;
    private DailyWeatherAdapter dailyWeatherAdapter;
    private RecyclerView recyclerViewDaily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day_weather_forecast);

        initDailyWeatherView();

        showWeatherNotification(dailyWeatherList.get(0));
        DailyWeatherAdapter adapter = new DailyWeatherAdapter(dailyWeatherList);
        recyclerViewDaily.setAdapter(adapter);
    }

    public void initDailyWeatherView() {
        dailyWeatherList = new ArrayList<>();

        // Add sample data
        dailyWeatherList.add(new DailyWeather("Today", "09/07", R.drawable.weather_rain, "34°", "87%"));
        dailyWeatherList.add(new DailyWeather("Wed", "10/07", R.drawable.weather_rain, "34°", "97%"));
        dailyWeatherList.add(new DailyWeather("Thu", "11/07", R.drawable.weather_rain, "35°", "81%"));
        dailyWeatherList.add(new DailyWeather("Fri", "12/07", R.drawable.weather_rain, "34°", "55%"));
        dailyWeatherList.add(new DailyWeather("Sat", "13/07", R.drawable.weather_rain, "33°", "68%"));

        recyclerViewDaily = findViewById(R.id.recyclerViewDaily);
        recyclerViewDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DailyWeatherAdapter adapter = new DailyWeatherAdapter(dailyWeatherList);
        recyclerViewDaily.setAdapter(adapter);
        Log.v("Main", "initDailyWeatherView called");
    }


    public void getDailyWeather(double lat, double lon, int cnt, String language, String units, String apiKey) {

        OpenWeatherApi service = ApiClient.getClient().create(OpenWeatherApi.class);
        Call<DailyWeatherResponse> call = service.getDailyWeather(lat, lon, 10, language, units, apiKey);

        call.enqueue(new Callback<DailyWeatherResponse>() {
            @Override
            public void onResponse(Call<DailyWeatherResponse> call, Response<DailyWeatherResponse> response) {
                if (response.isSuccessful()) {
                    // get API response
                    dailyWeatherResponse = response.body();
                    setDailyWeatherView(dailyWeatherResponse);

                    Log.e("getDailyWeather", "Request success");
                } else {
                    Log.e("getDailyWeather", "Request failed");
                }
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DailyWeatherResponse> call, Throwable t) {
                Log.e("Weather", "Network error", t);
                // Stop the refreshing indicator
                swiperefresh.setRefreshing(false);
            }
        });
    }

    private void setDailyWeatherView(DailyWeatherResponse forecastResponse) {
        dailyWeatherList.clear();
        for (WeatherList forecast : forecastResponse.getList()) {

//            DailyWeather hourlyWeather = new DailyWeather();
//            hourlyWeather.setTemp(Math.round(forecast.getMain().getTemp()));
//            hourlyWeather.setDescription(forecast.getWeather().get(0).getDescription());
//            hourlyWeather.setIcon(forecast.getWeather().get(0).getIcon());
//            hourlyWeather.setTime(forecast.getDtTxt().split(" ")[1]);
//            dailyWeatherList.add(hourlyWeather);
        }
        dailyWeatherAdapter.notifyDataSetChanged();
    }

    private void showWeatherNotification(DailyWeather dailyWeather) {
        Intent intent = new Intent(this, MainViewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weatherChannelId")
                //.setSmallIcon(R.drawable.ic_weather_notification) // Replace with your weather icon drawable
                .setSmallIcon(R.drawable.ic_weather_placeholder)
                .setContentTitle("Daily Weather")
                .setContentText(dailyWeather.getDay() + ": " + dailyWeather.getDate() + ", " + dailyWeather.getTemp() + "°C")
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

    private void setCityWeatherView(DailyWeatherResponse forecastResponse){
        // Existing code to update UI
        if (!dailyWeatherList.isEmpty()) {
            showWeatherNotification(dailyWeatherList.get(0)); // Show notification for the first weather in the list
        }
    }
}