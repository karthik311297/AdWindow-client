package com.example.adwindow.adwindow_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adwindow.adwindow_client.Parcels.ScreenParcelable;
import com.example.adwindow.adwindow_client.adapter.ScreenInfoPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ScreenInformation extends AppCompatActivity implements ScreenRate.OnFragmentInteractionListener,ScreenAddress.OnFragmentInteractionListener,ScreenFootfall.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen_information);
        Intent fromAdMap =  getIntent();

        ScreenParcelable screenParcelable = fromAdMap.getParcelableExtra("SDET");

        ViewPager viewPager = findViewById(R.id.screenInfoView);
        ScreenInfoPagerAdapter screenInfoPagerAdapter = new ScreenInfoPagerAdapter(getSupportFragmentManager(), screenParcelable);
        viewPager.setAdapter(screenInfoPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.screenInfoTabs);
        tabLayout.setupWithViewPager(viewPager);

        ImageView imageView = findViewById(R.id.locationImgView);
        Glide.with(getApplicationContext()).load(screenParcelable.getScreenPlaceImageUrl()).into(imageView);

        TextView textView = findViewById(R.id.sctitle);
        textView.setText(screenParcelable.getScreenLocationTitle());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
