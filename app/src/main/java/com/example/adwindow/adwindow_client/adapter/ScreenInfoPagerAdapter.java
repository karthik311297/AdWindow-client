package com.example.adwindow.adwindow_client.adapter;

import android.os.Bundle;

import com.example.adwindow.adwindow_client.Parcels.ScreenParcelable;
import com.example.adwindow.adwindow_client.ScreenAddress;
import com.example.adwindow.adwindow_client.ScreenFootfall;
import com.example.adwindow.adwindow_client.ScreenRate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ScreenInfoPagerAdapter extends FragmentStatePagerAdapter {

    private ScreenParcelable screenParcelable;

    public ScreenInfoPagerAdapter(@NonNull FragmentManager fm, ScreenParcelable screenParcelable) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.screenParcelable = screenParcelable;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        if (position == 0)
        {
            bundle.putString("ADDR", screenParcelable.getScreenAddress());
            ScreenAddress screenAddress = new ScreenAddress();
            screenAddress.setArguments(bundle);
            return screenAddress;
        }
        else if(position == 1)
        {
            bundle.putString("RATE", screenParcelable.getPricing());
            ScreenRate screenRate = new ScreenRate();
            screenRate.setArguments(bundle);
            return screenRate;
        }
        bundle.putString("FOOT", screenParcelable.getFootfall());
        ScreenFootfall screenFootfall = new ScreenFootfall();
        screenFootfall.setArguments(bundle);
        return screenFootfall;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
        {
            return "Address";
        }
        else if(position == 1)
        {
            return "Pricing";
        }
        return "Footfall";
     }
}
