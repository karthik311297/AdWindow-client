package com.example.adwindow.adwindow_client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageView imageView = findViewById(R.id.locationImgView);
        Glide.with(getApplicationContext()).load(screenParcelable.getScreenPlaceImageUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ScreenInformation.this, "Failed to load the image, check your internet connectivity",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        TextView textView = findViewById(R.id.sctitle);
        textView.setText(screenParcelable.getScreenLocationTitle());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
