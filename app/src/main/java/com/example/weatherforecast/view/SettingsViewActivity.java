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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
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

public class SettingsViewActivity extends AppCompatActivity {
    private Spinner refreshSpinner;
    private Switch locationSwitch;

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

        // Set switch to the value stored in preferences
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

                    //display toast message to user that location is disabled
                    Toast.makeText(SettingsViewActivity.this, "location info deleted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SettingsViewActivity.this, getSharedPreferences("WeatherAppPreferences", MODE_PRIVATE)
                            .getBoolean("LocationEnabled", false)+"", Toast.LENGTH_SHORT).show();

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