package com.example.weatherforecast;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView city_name;
    TextView temperature;
    TextView unit;
    TextView description;
    TextView minmax_feellike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        init();
        event();
    }

    public void init(){
        city_name = findViewById(R.id.city_name);
        temperature = findViewById(R.id.temperature);
        unit = findViewById((R.id.unit));
        description = findViewById((R.id.description));
        minmax_feellike = findViewById(R.id.minmax_feellike);
    }

    public void event(){
        //link goi api
        String url = "https://api.openweathermap.org/data/2.5/weather?id=1566083&lang=vi&units=metric&appid=de13238c0859cbbf9d42bacb340bbe2c";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        //tempTextView.setText("Response: " + response.toString());
                        Log.v("WEATHER", "Response: " + responseObject.toString());

                        try
                        {

                            //lay ra cac thong tin tra ve tu API
                            JSONObject mainJSONObject = responseObject.getJSONObject("main");
                            JSONArray weatherArray = responseObject.getJSONArray("weather");
                            JSONObject firstWeatherObject = weatherArray.getJSONObject(0);

                            String temp = Integer.toString((int) Math.round(mainJSONObject.getDouble("temp")));
                            String weatherDescription = firstWeatherObject.getString("description");
                            String city = responseObject.getString("name");
                            String temp_min = Integer.toString((int) Math.round(mainJSONObject.getDouble("temp_min")));
                            String temp_max = Integer.toString((int) Math.round(mainJSONObject.getDouble("temp_max")));
                            String feels_like = Integer.toString((int) Math.round(mainJSONObject.getDouble("feels_like")));

                            String min_max_feelslike = temp_min + " - " + temp_max + " cảm giác như " + feels_like;

                            //set gia tri cho cac View trong layout
                            temperature.setText(temp);
                            description.setText(weatherDescription);
                            city_name.setText(city);
                            minmax_feellike.setText(min_max_feelslike);

//                            int iconResourceId = getResources().getIdentifier("icon_" + weatherDescription.replace(" ", ""), "drawable", getPackageName());
//                            weatherImageView.setImageResource(iconResourceId);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
    }


}