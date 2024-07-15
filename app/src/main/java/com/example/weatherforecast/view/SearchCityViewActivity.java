package com.example.weatherforecast.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.CityAdapter;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.geocoding.City;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchCityViewActivity extends AppCompatActivity {

    private EditText editTextCity;
    private ListView listViewCities;
    private CityAdapter cityAdapter;
    private List<City> cityList;
    private ImageButton backBtn;

    private String apiKey = MainViewActivity.API_KEY; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        // init view
        editTextCity = findViewById(R.id.editTextCity);
        listViewCities = findViewById(R.id.listViewCities);

        // set adapter
        cityList = new ArrayList<>();
        cityAdapter = new CityAdapter(this, cityList);
        listViewCities.setAdapter(cityAdapter);

        // set event to EditText so that every time user input a char, get list of cities
        editTextCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchCitySuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // set event to listView so that when clicking a item in list, go to main view with lat, lon
        // of the clicked ciy
        listViewCities.setOnItemClickListener((parent, view, position, id) -> {
            City selectedCity = cityAdapter.getItem(position);
            if (selectedCity != null) {
                Intent intent = new Intent(SearchCityViewActivity.this, MainViewActivity.class);
                intent.putExtra("locationSelected", true);
                intent.putExtra("lat", selectedCity.getLat());
                intent.putExtra("lon", selectedCity.getLon());
                startActivity(intent);
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchCityViewActivity.this, MainViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchCitySuggestions(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherApi geocodingService = retrofit.create(OpenWeatherApi.class);
        geocodingService.getCitiesByDirect(query, 5, apiKey).enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cityList.clear();
                    cityList.addAll(response.body());
                    cityAdapter.notifyDataSetChanged();
                }else{
                    Log.e("fetchCitySuggestions", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.e("SearchCity", "Network error", t);
                // Handle failure
            }
        });
    }
}
