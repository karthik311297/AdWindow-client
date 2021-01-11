package com.example.karthik.adwindow_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.karthik.adwindow_client.adapter.ScreenTitleAdapter;

import java.util.Arrays;
import java.util.List;

public class ScreenLocationAdUpload extends AppCompatActivity {

    RecyclerView screenLocations;
    List<String> locationTitles;
    ScreenTitleAdapter screenTitleAdapter;
    private static final int FILE_CHOOSE_CODE = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_location_ad_upload);
        locationTitles = getIntent().getStringArrayListExtra("LOCS");
        populateRecyclerView();
        ImageButton selectAllLocs = findViewById(R.id.selectAllLocs);
        ImageButton uploadContent = findViewById(R.id.uploadContent);
        Button chooseContent = findViewById(R.id.chooseContent);
        selectAllLocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screenTitleAdapter!=null) {
                    if (!screenTitleAdapter.areAllChecked()) {
                        screenTitleAdapter.checkAllLocations();
                        screenTitleAdapter.notifyDataSetChanged();
                    } else {
                        screenTitleAdapter.uncheckAllLocations();
                        screenTitleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        uploadContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ScreenLocationAdUpload.this,"Uploading Your Content",Toast.LENGTH_LONG).show();
            }
        });
        chooseContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try{
                    startActivityForResult(Intent.createChooser(intent, "Select Content"), FILE_CHOOSE_CODE);
                }
                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(ScreenLocationAdUpload.this,"Please install a file manager",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == FILE_CHOOSE_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Uri contentUri;
                if (data != null) {
                    contentUri = data.getData();
                    Toast.makeText(ScreenLocationAdUpload.this,contentUri.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateRecyclerView()
    {
        screenLocations = findViewById(R.id.screen_loc_list);
        if(locationTitles!=null) {
            screenTitleAdapter = new ScreenTitleAdapter(locationTitles);
            screenLocations.setAdapter(screenTitleAdapter);
            screenLocations.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
