package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.dbmodel.DbCity;
import com.example.weatherforecast.model.geocoding.City;
import com.example.weatherforecast.model.weather.CityWeather;

import java.util.List;

public class DrawerCityAdapter extends RecyclerView.Adapter<DrawerCityAdapter.CityViewHolder> {
    private List<DbCity> cityList;
    private Context context;
    private OnItemClickListener onClickListener;

    public DrawerCityAdapter(Context context, List<DbCity> cityList, OnItemClickListener onClickListener) {
        this.context = context;
        this.cityList = cityList;
        this.onClickListener = onClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }



    public DrawerCityAdapter(List<DbCity> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerCityAdapter.CityViewHolder holder, int position) {
        DbCity city = cityList.get(position);

        String name = city.getName();
        holder.tvcityname.setText(name);

        // if state is not null, concat with country
        String country = city.getCountry();
        if(city.getState()!=null){
            country = city.getState() + ", " + country;
        }
        holder.tvcountry.setText(country);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener != null){
                    onClickListener.onItemClick(view, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return cityList.size();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tvcityname, tvcountry;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvcityname = itemView.findViewById(R.id.tvcityname);
            tvcountry = itemView.findViewById(R.id.tvcountry);
        }
    }
}