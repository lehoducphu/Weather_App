package com.example.weatherforecast.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.weather.DailyWeather;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder> {
    private List<DailyWeather> forecastList;
    public DailyWeatherAdapter(List<DailyWeather> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_forecast, parent, false);
        view.getLayoutParams().width = parent.getMeasuredWidth() / 5;
        return new DailyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {
        DailyWeather forecast = forecastList.get(position);
        holder.tvDay.setText(forecast.getDay());
        holder.tvDate.setText(forecast.getDate());
        String imageUrl = "https://openweathermap.org/img/wn/"+forecast.getWeatherIcon()+"@2x.png";
        Picasso.get()
                .load(imageUrl)
                .into(holder.ivWeatherIcon);

        holder.tvTemp.setText(Integer.toString(forecast.getTemp()) + "°");
        holder.tvPrecipitation.setText(forecast.getHumidity() + "%");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class DailyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        TextView tvDate;
        ImageView ivWeatherIcon;
        TextView tvTemp;
        TextView tvPrecipitation;

        public DailyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            tvPrecipitation = itemView.findViewById(R.id.tv_precipitation);
        }
    }
}
