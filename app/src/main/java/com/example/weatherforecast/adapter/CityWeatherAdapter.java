package com.example.weatherforecast.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.weather.CityWeather;

import java.util.List;

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.WeatherViewHolder>{
    private List<CityWeather> dailyWeatherList;
    private final OnWeatherItemClickListener onWeatherItemClickListener;

    public interface OnWeatherItemClickListener {
        void onWeatherItemClick(CityWeather dailyWeather);
    }

    public CityWeatherAdapter(List<CityWeather> dailyWeatherList, OnWeatherItemClickListener onWeatherItemClickListener) {
        this.dailyWeatherList = dailyWeatherList;
        this.onWeatherItemClickListener = onWeatherItemClickListener;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        CityWeather dailyWeather = dailyWeatherList.get(position);
        holder.tvTemperature.setText(String.format("%s°", Math.round(dailyWeather.getTemp())));
        holder.tvCity.setText(dailyWeather.getCity());
        holder.tvDescription.setText(dailyWeather.getDescription());
        holder.tvTempRange.setText(String.format("H: %s° L: %s°",Math.round( dailyWeather.getTemp_max()), Math.round(dailyWeather.getTemp_min())));
        // Load weather icon using a library like Picasso or Glide
        holder.itemView.setOnClickListener(v -> onWeatherItemClickListener.onWeatherItemClick(dailyWeather));
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemperature, tvCity, tvDescription, tvTempRange;
        ImageView ivWeatherIcon;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTempRange = itemView.findViewById(R.id.tvTempRange);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
}
