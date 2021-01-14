package com.example.adwindow.adwindow_client.adapter;

import com.example.adwindow.adwindow_client.ScreenAddress;
import com.example.adwindow.adwindow_client.ScreenFootfall;
import com.example.adwindow.adwindow_client.ScreenRate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ScreenInfoPagerAdapter extends FragmentStatePagerAdapter {

    public ScreenInfoPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            return new ScreenAddress();
        }
        else if(position == 1)
        {
            return new ScreenRate();
        }
        return new ScreenFootfall();
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
