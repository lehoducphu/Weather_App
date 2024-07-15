package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.geocoding.City;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> {

    public CityAdapter(Context context, List<City> cities) {
        super(context, 0, cities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_city, parent, false);
        }

        TextView tvcityname = convertView.findViewById(R.id.tvcityname);
        tvcityname.setText(city.getName());

        TextView tvcountry = convertView.findViewById(R.id.tvcountry);
        // if state is not null, concat with country
        String state = "";
        String country = city.getCountry();
        if(city.getState()!=null){
            country = city.getState() + ", " + country;
        }
        tvcountry.setText(country);


        return convertView;
    }
}
