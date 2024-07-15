package com.example.weatherforecast.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.CityAdapter;
import com.example.weatherforecast.adapter.CityMgtAdapter;
import com.example.weatherforecast.adapter.DrawerCityAdapter;
import com.example.weatherforecast.model.geocoding.City;

import java.util.ArrayList;
import java.util.List;

public class LocationManagementViewActivity extends AppCompatActivity {

    private RecyclerView rvcity;
    private CityMgtAdapter adapter;
    private List<City> locations;
    private ImageButton backBtn;
    private ImageButton navSearchButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_management_view);

        rvcity = findViewById(R.id.rvcity);
        rvcity.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        locations = new ArrayList<>();
        locations.add(new City("Hà Nội", 21.0285, 105.8542, "VN", null));
        locations.add(new City("Hồ Chí Minh", 10.762622, 106.660172, "VN", null));

        adapter = new CityMgtAdapter(this, locations, this::onItemLongClick);
        rvcity.setAdapter(adapter);

        // Set back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationManagementViewActivity.this, MainViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set nav search button
        navSearchButton = findViewById(R.id.nav_search);
        navSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationManagementViewActivity.this, SearchCityViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onItemLongClick(View view, int position) {
        showPopupMenu(view, position);
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.location_management_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                locations.remove(position);
                adapter.notifyItemRemoved(position);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}