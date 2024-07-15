package com.example.weatherforecast.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class CityWeather implements Parcelable {
    private float temp;
    private float temp_min;
    private float temp_max;
    private String description;
    private String icon;
    private String date;
    private String city;

    public CityWeather() {}

    protected CityWeather(Parcel in) {
        temp = in.readFloat();
        temp_min = in.readFloat();
        temp_max = in.readFloat();
        description = in.readString();
        icon = in.readString();
        date = in.readString();
        city = in.readString();
    }

    public static final Creator<CityWeather> CREATOR = new Creator<CityWeather>() {
        @Override
        public CityWeather createFromParcel(Parcel in) {
            return new CityWeather(in);
        }

        @Override
        public CityWeather[] newArray(int size) {
            return new CityWeather[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(temp);
        dest.writeFloat(temp_min);
        dest.writeFloat(temp_max);
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeString(date);
        dest.writeString(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = Math.round( temp);
    }

    public float getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(float temp_min) {
        this.temp_min = Math.round(temp_min);
    }

    public float getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(float temp_max) {
        this.temp_max = Math.round(temp_max);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
