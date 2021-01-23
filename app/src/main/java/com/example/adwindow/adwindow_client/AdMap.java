package com.example.adwindow.adwindow_client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.example.adwindow.adwindow_client.Parcels.ScreenParcelable;
import com.example.adwindow.adwindow_client.model.Screen;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AdMap extends AppCompatActivity implements OnMapReadyCallback {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final float DEFAULT_ZOOM = 11f;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HashMap<String,Integer> cityIndex;
    private Integer currentCityIndex;
    private String citySelectedInDropdown;
    private Location currentDeviceCityLocation;
    private com.example.adwindow.adwindow_client.model.Location dropDownSelectedCity;
    private boolean moveToMyLocation = false;
    ArrayList<MultiSelectModel> screenLocationTitles;
    ArrayList<String> locationsToUploadAd;
    ArrayList<Integer> alreadySelectedInLocationPicker;
    List<com.example.adwindow.adwindow_client.model.Location> allCities;
    List<String> cityNames;
    Map<String, Screen> allScreensInCurrentSelectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ImageButton uploadAdButton = findViewById(R.id.uploadAd);
        Button screensDialogOpener =  findViewById(R.id.screensDialogOpener);
        Spinner citySpinner = findViewById(R.id.scity);
        getLocationPermission();
        uploadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(citySelectedInDropdown!=null)
                {
                    Intent intent = new Intent(AdMap.this, ScreenLocationAdUpload.class);
                    intent.putExtra("CITY", citySelectedInDropdown);
                    if(locationsToUploadAd!=null)
                    {
                        intent.putStringArrayListExtra("LOCS",locationsToUploadAd);
                    }
                    startActivity(intent);
                }
            }
        });
        screensDialogOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(citySelectedInDropdown!=null) {
                    populateScreensDialog();
                    if (allScreensInCurrentSelectedCity != null && allScreensInCurrentSelectedCity.size() == screenLocationTitles.size()) {
                        MultiSelectDialog multiSelectDialog = new MultiSelectDialog()
                                .title("Choose Screens")
                                .titleSize(25)
                                .positiveText("Ok")
                                .negativeText("Cancel")
                                .preSelectIDsList(alreadySelectedInLocationPicker)
                                .multiSelectList(screenLocationTitles)
                                .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                                    @Override
                                    public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String s) {
                                        mMap.clear();
                                        addMapMarkerToScreenLocations(selectedNames);
                                        locationsToUploadAd = selectedNames;
                                        if (selectedIds != null) {
                                            alreadySelectedInLocationPicker = selectedIds;
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(AdMap.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
                    }
                }
            }
        });
    }

    private void initMap()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(AdMap.this);
    }

    private void getLocationPermission()
    {
        String [] permissions = {COARSE_LOCATION, FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationPermissionsGranted = true;
            initMap();
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        locationPermissionsGranted = false;

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length>0)
            {
                for (int grantResult : grantResults)
                {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                locationPermissionsGranted = true;
                //initialize map
                initMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(locationPermissionsGranted)
        {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Screen screenToView = allScreensInCurrentSelectedCity.get(marker.getTitle());
                    ScreenParcelable screenParcelable = new ScreenParcelable(screenToView.getLocationName(), screenToView.getScreenLocationTitle(),
                            screenToView.getScreenAddress(), screenToView.getScreenPlaceImageUrl(), screenToView.getPricing(), screenToView.getFootfall(), screenToView.getNumScreens());
                    Intent intent = new Intent(AdMap.this, ScreenInformation.class);
                    intent.putExtra("SDET", screenParcelable);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    private void getDeviceLocation()
    {
        if(locationPermissionsGranted)
        {
            Task locationFetcher = fusedLocationProviderClient.getLastLocation();
            locationFetcher.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Location currentLocation = (Location) task.getResult();
                        currentDeviceCityLocation = currentLocation;
                        getCitiesInDropDown();
                    }
                }
            });
        }
    }

    private void moveCamera(LatLng latLng, float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void getCitiesInDropDown()
    {
        allCities = new ArrayList<>();
        cityNames = new ArrayList<>();
        cityIndex=new HashMap<>();
        cityNames.add("Select City");
        final DatabaseReference citiesReference = databaseReference.child("Cities");
        citiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    com.example.adwindow.adwindow_client.model.Location location = dataSnapshot.getValue(com.example.adwindow.adwindow_client.model.Location.class);
                    allCities.add(location);
                    cityNames.add(location.getLocationName());
                }
                for(int i =1; i<cityNames.size();i++)
                {
                    cityIndex.put(cityNames.get(i),i);
                }
                Spinner citySpinner = findViewById(R.id.scity);
                ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(AdMap.this,android.R.layout.simple_spinner_item,cityNames);
                citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(citiesAdapter);
                if(currentDeviceCityLocation!=null) {
                    getCityFromLocation(currentDeviceCityLocation.getLatitude(), currentDeviceCityLocation.getLongitude());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void moveCameraToSelectedCity(String city)
    {
        Geocoder geocoder = new Geocoder(AdMap.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(city,1);
            Address cityLoc =  addressList.get(0);
            moveCamera(new LatLng(cityLoc.getLatitude(),cityLoc.getLongitude()),DEFAULT_ZOOM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCityFromLocation(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(AdMap.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude,1);
            String cityName = addressList.get(0).getLocality();
            Spinner citySpinner = findViewById(R.id.scity);
            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (moveToMyLocation) {
                        citySelectedInDropdown = cityNames.get(i);
                        alreadySelectedInLocationPicker = new ArrayList<>();
                        collectScreenLocationsInCity(citySelectedInDropdown);
                        locationsToUploadAd = null;
                        dropDownSelectedCity = allCities.get(i-1);
                        Toast.makeText(AdMap.this, cityNames.get(i), Toast.LENGTH_SHORT).show();
                        moveToMyLocation = false;
                    } else if (i > 0) {
                        moveCameraToSelectedCity(cityNames.get(i));
                        citySelectedInDropdown = cityNames.get(i);
                        alreadySelectedInLocationPicker = new ArrayList<>();
                        collectScreenLocationsInCity(citySelectedInDropdown);
                        locationsToUploadAd = null;
                        dropDownSelectedCity = allCities.get(i-1);
                        Toast.makeText(AdMap.this, cityNames.get(i), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            currentCityIndex = cityIndex.get(cityName);
            if(currentCityIndex!=null) {
                citySpinner.setSelection(currentCityIndex);
                ImageButton currentLocationButton = findViewById(R.id.myLoc);
                currentLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveToMyLocation = true;
                        moveCamera(new LatLng(currentDeviceCityLocation.getLatitude(),currentDeviceCityLocation.getLongitude()),DEFAULT_ZOOM);
                        if(currentCityIndex!=null) {
                            Spinner citySpinner = findViewById(R.id.scity);
                            citySpinner.setSelection(currentCityIndex);
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void collectScreenLocationsInCity(String city)
    {
        allScreensInCurrentSelectedCity = new HashMap<>();
            DatabaseReference singleScreenRef = databaseReference.child("Screens").child(city);
            singleScreenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot ds: snapshot.getChildren())
                    {
                        Screen screen = ds.getValue(Screen.class);
                        allScreensInCurrentSelectedCity.put(screen.getScreenLocationTitle(), screen);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public void addMapMarkerToScreenLocations(final List<String> screensSelected) {
        final Handler handler = new Handler();
        final Geocoder geocoder = new Geocoder(AdMap.this, Locale.getDefault());
        final List<MarkerOptions> locationToAddMarker = new ArrayList<>();
        final Runnable uiRunnable = new Runnable() {
            @Override
            public void run() {
                for (MarkerOptions markerOptions : locationToAddMarker) {
                    mMap.addMarker(markerOptions);
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(String sc : screensSelected)
                {
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(allScreensInCurrentSelectedCity.get(sc).getScreenAddress(), 1);
                        Address address = addressList.get(0);
                        LatLng addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                        locationToAddMarker.add(new MarkerOptions().position(addressLatLng).title(sc));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(uiRunnable);
            }
        });
        thread.start();
    }
    public void populateScreensDialog()
    {
        screenLocationTitles = new ArrayList<>();
        ArrayList<String> screenLocations = new ArrayList(dropDownSelectedCity.getScreenLocationTitles().values());
        for(int i=0;i<screenLocations.size();i++)
        {
            screenLocationTitles.add(new MultiSelectModel(i+1, screenLocations.get(i)));
        }
    }
}
