package com.example.weatherforecast.view;

import static com.example.weatherforecast.view.MainViewActivity.REQUEST_LOCATION;

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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherforecast.R;

public class SettingsViewActivity extends AppCompatActivity {
    private Spinner refreshSpinner;
    private Switch locationSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set view Refresh Option
        setRefreshOptionView();
        // set view Allow Location Option
        setLocationOptionView();

    }

    private void setRefreshOptionView() {
        refreshSpinner = findViewById(R.id.refreshSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.refresh_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshSpinner.setAdapter(adapter);

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
        boolean isLocationEnabled = getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                .getBoolean("LocationEnabled", false);

        locationSwitch.setChecked(isLocationEnabled);

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("LocationEnabled", isChecked)
                        .apply();

                // Get current location if switch is checked
                if(isChecked) {
                    getCurrentLocation();
                    startActivity(new Intent(SettingsViewActivity.this, MainViewActivity.class));
                }else{
                    MainViewActivity.setLat(0);
                    Log.d("Settings", "Lat: " + MainViewActivity.getLat());
                    MainViewActivity.setLon(0);
                    Log.d("Settings", "Lon: " + MainViewActivity.getLon());

                    //display toast message to user that location is disabled
                    Toast.makeText(SettingsViewActivity.this, "location info deleted", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            locationManager.requestLocationUpdates(locationProvider, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Get latitude and longitude
                    MainViewActivity.setLat(location.getLatitude());
                    Log.d("Settings", "Lat: " + MainViewActivity.getLat());

                    MainViewActivity.setLon(location.getLongitude());
                    Log.d("Settings", "Lon: " + MainViewActivity.getLon());

//                     Optionally, remove location updates if you only need the location once
                    locationManager.removeUpdates(this);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }


}