package com.example.weatherforecast.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.weather.HourlyWeather;
import com.example.weatherforecast.view.MainViewActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {
    private List<HourlyWeather> hourlyWeatherList;
    private static String tempUnit;

    public HourlyWeatherAdapter(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_weather, parent, false);
        return new HourlyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.tvTime.setText(hourlyWeather.getTime().substring(0,5));

        // Get temperature unit from SharedPreferences
        tempUnit = MainViewActivity.getTempUnit();
        // get temperature unit
        String weatherUnit = tempUnit.equals("metric") ? "°C" : "°F";

        holder.tvTemperature.setText(String.format("%s",Math.round(hourlyWeather.getTemp()))
                + weatherUnit);
        holder.tvDescription.setText(hourlyWeather.getDescription());
        // Load weather icon using a library like Picasso or Glide
        String imageUrl = "https://openweathermap.org/img/wn/"+hourlyWeather.getIcon()+"@2x.png";

        Picasso.get()
                .load(imageUrl)
                .into(holder.ivWeatherIcon);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemperature, tvDescription;
        ImageView ivWeatherIcon;

        public HourlyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
}
