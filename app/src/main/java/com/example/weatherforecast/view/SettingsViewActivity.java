package com.example.weatherforecast.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherforecast.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class SettingsViewActivity extends AppCompatActivity {
    private Spinner refreshSpinner;
    private Spinner temperatureSpinner;
    private Spinner windSpeedSpinner;
    private Spinner pressureSpinner;
    private Switch locationSwitch;

    private ImageButton backBtn;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public static final int REQUEST_LOCATION = 1; // Unique request code for location permission


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // set view Refresh Option
        setRefreshOptionView();

        // set view Allow Location Option
        setLocationOptionView();

        // set view Temperature Option
        setTemperatureOptionView();

        // set view Wind Speed Option
        setWindSpeedOptionView();

        // set view Pressure Option
        setPressureOptionView();

        // Set back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsViewActivity.this, MainViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setPressureOptionView() {
        pressureSpinner = findViewById(R.id.pressureSpinner);

        List<String> options = new ArrayList<>();
        options.add("mb");
        options.add("bar");
        options.add("psi");
        options.add("inHg");

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_spinner_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pressureSpinner.setAdapter(adapter);

        // set item from shared preference to spinner
        String pressureUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("pressureUnit", "mb");
        pressureSpinner.setSelection(adapter.getPosition(pressureUnit));

        pressureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Change Preference according to user input
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putString("pressureUnit", adapter.getItem(position).toString())
                        .apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setWindSpeedOptionView() {
        windSpeedSpinner = findViewById(R.id.windSpeedSpinner);

        List<String> options = new ArrayList<>();
        options.add("km/h");
        options.add("mph");
        options.add("m/s");

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_spinner_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windSpeedSpinner.setAdapter(adapter);

        // set item from shared preference to spinner
        String speedUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("speedUnit", "km/h");
        windSpeedSpinner.setSelection(adapter.getPosition(speedUnit));

        windSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Change Preference according to user input
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putString("speedUnit", adapter.getItem(position).toString())
                        .apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTemperatureOptionView() {
        temperatureSpinner = findViewById(R.id.temperatureSpinner);

        List<String> options = new ArrayList<>();
        options.add("°C");
        options.add("°F");

        List<String> tempUnits = new ArrayList<>();
        tempUnits.add("metric");
        tempUnits.add("imperial");

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_spinner_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temperatureSpinner.setAdapter(adapter);

        // set item from shared preference to spinner
        String tempUnit = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getString("tempUnit", "metric") == "metric" ? "°C" : "°F";

        temperatureSpinner.setSelection(adapter.getPosition(tempUnit));

        temperatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Change Preference according to user input
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putString("tempUnit", tempUnits.get(position))
                        .apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setRefreshOptionView() {
        refreshSpinner = findViewById(R.id.refreshSpinner);

        List<String> options = new ArrayList<>();
        options.add("Không bao giờ");
        options.add("Mỗi giờ");
        options.add("Mỗi 3 giờ");


        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_spinner_layout, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshSpinner.setAdapter(adapter);

        // set item from shared preference to spinner
        int refreshInterval = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getInt("refreshInterval", 0);
        refreshSpinner.setSelection(refreshInterval);


        refreshSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                // Change Preference according to user input
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putInt("refreshInterval", position)
                        .apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setLocationOptionView() {
        locationSwitch = findViewById(R.id.locationSwitch);

        // Set switch to the value stored in preferences
        boolean isLocationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("LocationEnabled", false);
        locationSwitch.setChecked(isLocationEnabled);

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", isChecked)
                        .apply();    // Get current location if switch is checked

                if (!isChecked) {
                    MainViewActivity.setLat(0);
                    Log.d("Settings", "Lat: " + MainViewActivity.getLat());
                    MainViewActivity.setLon(0);
                    Log.d("Settings", "Lon: " + MainViewActivity.getLon());


                } else {

                    // Check if permission is granted. If not, request permission, else start MainViewActivity
                    if (ContextCompat.checkSelfPermission(SettingsViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

                    } else {
                        startActivity(new Intent(SettingsViewActivity.this, MainViewActivity.class));
                    }

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", true)
                        .apply();
                // Permission was granted, start MainViewActivity
                startActivity(new Intent(SettingsViewActivity.this, MainViewActivity.class));
            } else {
                locationSwitch.setChecked(false);
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", false)
                        .apply();
                // Permission was denied, handle the case
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}