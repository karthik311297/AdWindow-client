package com.example.adwindow.adwindow_client.adapter.model;

public class ScreenAdStatus {
    private String screenTitle;
    private boolean advertisedStatus;

    public ScreenAdStatus(String screenTitle, boolean advertisedStatus) {
        this.screenTitle = screenTitle;
        this.advertisedStatus = advertisedStatus;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public boolean isAdvertisedStatus() {
        return advertisedStatus;
    }
}
