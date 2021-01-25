package com.example.adwindow.adwindow_client.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Screen {
    String locationName;
    String screenLocationTitle;
    String screenAddress;
    String screenPlaceImageUrl;
    Map<String, Boolean> advertisementsStatus;
    String pricing;
    String footfall;
    int numScreens;

    public Screen() {
    }

    public Screen(String locationName, String screenLocationTitle, String screenAddress, String screenPlaceImageUrl, String pricing, String footfall, int numScreens) {
        this.locationName = locationName;
        this.screenLocationTitle = screenLocationTitle;
        this.screenAddress = screenAddress;
        this.screenPlaceImageUrl = screenPlaceImageUrl;
        this.pricing = pricing;
        this.footfall = footfall;
        this.numScreens = numScreens;
        this.advertisementsStatus = new HashMap<>();
    }

    public String getLocationName() {
        return locationName;
    }

    public String getScreenLocationTitle() {
        return screenLocationTitle;
    }

    public String getScreenAddress() {
        return screenAddress;
    }

    public String getScreenPlaceImageUrl() {
        return screenPlaceImageUrl;
    }

    public String getPricing() {
        return pricing;
    }

    public String getFootfall() {
        return footfall;
    }

    public int getNumScreens() {
        return numScreens;
    }

    public Map<String, Boolean> getAdvertisementsStatus() {
        return advertisementsStatus;
    }
}
