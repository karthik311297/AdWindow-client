package com.example.adwindow.adwindow_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.example.adwindow.adwindow_client.adapter.ScreenInfoPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ScreenInformation extends AppCompatActivity implements ScreenRate.OnFragmentInteractionListener,ScreenAddress.OnFragmentInteractionListener,ScreenFootfall.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen_information);
        ViewPager viewPager = findViewById(R.id.screenInfoView);
        ScreenInfoPagerAdapter screenInfoPagerAdapter = new ScreenInfoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(screenInfoPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.screenInfoTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
