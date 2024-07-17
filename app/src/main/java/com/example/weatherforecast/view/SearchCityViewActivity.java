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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.CityAdapter;
import com.example.weatherforecast.api.OpenWeatherApi;
import com.example.weatherforecast.model.dbmodel.DbCity;
import com.example.weatherforecast.model.geocoding.City;
import com.example.weatherforecast.util.util;

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
    private List<DbCity> userCityList;

    private ImageButton backBtn;

    private String apiKey = MainViewActivity.API_KEY; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        // init view
        editTextCity = findViewById(R.id.editTextCity);
        listViewCities = findViewById(R.id.listViewCities);

        // get user city list from database
        userCityList = util.getUserSavedcities(this);

        // set adapter
        cityList = new ArrayList<>();
        cityAdapter = new CityAdapter(this, cityList);
        listViewCities.setAdapter(cityAdapter);

        // get intent to know if user is coming from main view or LocationManagementViewActivity
        Intent intent = getIntent();
        String fromActivity = intent.getStringExtra("fromActivity");


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
                // check if user is coming from main or management view
                if(fromActivity.equals("main")) {
                    // get vi name
                    String cityName = selectedCity.getLocalNames().getVi()!=null?
                            selectedCity.getLocalNames().getVi() : selectedCity.getName();

                    // add to database
                    //check if city is already saved
                    if(!checkCityIsInList(selectedCity)){
                        util.addCity(SearchCityViewActivity.this, cityName,
                                selectedCity.getLon(), selectedCity.getLat(), selectedCity.getCountry(),
                                selectedCity.getState());
                        Toast.makeText(SearchCityViewActivity.this, "Location saved",
                                Toast.LENGTH_SHORT).show();
                    }

                    // send intent to main view
                    Intent sendIntent = new Intent(SearchCityViewActivity.this,
                            MainViewActivity.class);
                    sendIntent.putExtra("locationSelected", true);
                    sendIntent.putExtra("cityName", cityName);
                    sendIntent.putExtra("lat", selectedCity.getLat());
                    sendIntent.putExtra("lon", selectedCity.getLon());
                    startActivity(sendIntent);
                }else{
                    // get vi name
                    String cityName = selectedCity.getLocalNames().getVi()!=null?
                            selectedCity.getLocalNames().getVi() : selectedCity.getName();

                    // add to database
                    if(!checkCityIsInList(selectedCity)){
                        util.addCity(SearchCityViewActivity.this, cityName,
                                selectedCity.getLon(), selectedCity.getLat(), selectedCity.getCountry(),
                                selectedCity.getState());
                        Toast.makeText(SearchCityViewActivity.this, "Location saved",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SearchCityViewActivity.this, "Location already saved",
                                Toast.LENGTH_SHORT).show();
                    }

//                    // send intent to management view
                    Intent sendIntent = new Intent(SearchCityViewActivity.this,
                            LocationManagementViewActivity.class);
                    startActivity(sendIntent);
                }
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

    public boolean checkCityIsInList(City city){
        for(DbCity dbCity : userCityList){
            if(dbCity.getLat()==city.getLat()
                    && dbCity.getLon()==city.getLon()){

                return true;
            }
        }
        return false;
    }
}
