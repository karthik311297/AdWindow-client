package com.example.adwindow.adwindow_client.Parcels;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenParcelable implements Parcelable {
    private String locationName;
    private String screenLocationTitle;
    private String screenAddress;
    private String screenPlaceImageUrl;
    private String pricing;
    private String footfall;
    private int numScreens;

    public ScreenParcelable(String locationName, String screenLocationTitle, String screenAddress, String screenPlaceImageUrl, String pricing, String footfall, int numScreens) {
        this.locationName = locationName;
        this.screenLocationTitle = screenLocationTitle;
        this.screenAddress = screenAddress;
        this.screenPlaceImageUrl = screenPlaceImageUrl;
        this.pricing = pricing;
        this.footfall = footfall;
        this.numScreens = numScreens;
    }

    protected ScreenParcelable(Parcel in) {
        locationName = in.readString();
        screenLocationTitle = in.readString();
        screenAddress = in.readString();
        screenPlaceImageUrl = in.readString();
        pricing = in.readString();
        footfall = in.readString();
        numScreens = in.readInt();
    }

    public static final Creator<ScreenParcelable> CREATOR = new Creator<ScreenParcelable>() {
        @Override
        public ScreenParcelable createFromParcel(Parcel in) {
            return new ScreenParcelable(in);
        }

        @Override
        public ScreenParcelable[] newArray(int size) {
            return new ScreenParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(locationName);
        parcel.writeString(screenLocationTitle);
        parcel.writeString(screenAddress);
        parcel.writeString(screenPlaceImageUrl);
        parcel.writeString(pricing);
        parcel.writeString(footfall);
        parcel.writeInt(numScreens);
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getScreenLocationTitle() {
        return screenLocationTitle;
    }

    public void setScreenLocationTitle(String screenLocationTitle) {
        this.screenLocationTitle = screenLocationTitle;
    }

    public String getScreenAddress() {
        return screenAddress;
    }

    public void setScreenAddress(String screenAddress) {
        this.screenAddress = screenAddress;
    }

    public String getScreenPlaceImageUrl() {
        return screenPlaceImageUrl;
    }

    public void setScreenPlaceImageUrl(String screenPlaceImageUrl) {
        this.screenPlaceImageUrl = screenPlaceImageUrl;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getFootfall() {
        return footfall;
    }

    public void setFootfall(String footfall) {
        this.footfall = footfall;
    }

    public int getNumScreens() {
        return numScreens;
    }

    public void setNumScreens(int numScreens) {
        this.numScreens = numScreens;
    }
}
