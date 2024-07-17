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

public class CityMgtAdapter extends RecyclerView.Adapter<CityMgtAdapter.CityViewHolder> {
    private List<DbCity> cityList;
    private Context context;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;

    public CityMgtAdapter(Context context, List<DbCity> cityList, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.cityList = cityList;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }



    public CityMgtAdapter(List<DbCity> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_mgt, parent, false);
        return new CityViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        DbCity city = cityList.get(position);

        holder.tvcityname.setText(city.getName());

        // if state is not null, concat with country
        String state = "";
        String country = city.getCountry();
        if(city.getState()!=null){
            country = city.getState() + ", " + country;
        }
        holder.tvcountry.setText(country);
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(v, position);
                return true;
            }
            return false;
        });

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(view, position);
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
