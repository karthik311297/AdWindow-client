package com.example.karthik.adwindow_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.karthik.adwindow_client.adapter.ScreenTitleAdapter;

import java.util.Arrays;
import java.util.List;

public class ScreenLocationAdUpload extends AppCompatActivity {

    RecyclerView screenLocations;
    List<String> locationTitles;
    ScreenTitleAdapter screenTitleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_location_ad_upload);
        populateRecyclerView();
        ImageButton selectAllLocs = findViewById(R.id.selectAllLocs);
        selectAllLocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!screenTitleAdapter.areAllChecked())
                {
                    screenTitleAdapter.checkAllLocations();
                    screenTitleAdapter.notifyDataSetChanged();
                }
                else
                {
                    screenTitleAdapter.uncheckAllLocations();
                    screenTitleAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void populateRecyclerView()
    {
        screenLocations = findViewById(R.id.screen_loc_list);
        locationTitles = Arrays.asList("City Central Mall","Dominoes","MSRIT College","RV College","Gold's Gym","Forum Mall","PVR Cinemas","Prestige Park","Brigade Township");
        screenTitleAdapter = new ScreenTitleAdapter(locationTitles);
        screenLocations.setAdapter(screenTitleAdapter);
        screenLocations.setLayoutManager(new LinearLayoutManager(this));
    }
}
