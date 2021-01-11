package com.example.karthik.adwindow_client;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AdMap extends AppCompatActivity implements OnMapReadyCallback {

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
    private boolean moveToMyLocation = false;
    HashMap<String,String> selectedLocationAddressMapper;
    ArrayList<MultiSelectModel> screenLocationTitles;
    ArrayList<String> locationsToUploadAd;
    ArrayList<Integer> alreadySelectedInLocationPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_map);
        cityIndex=new HashMap<>();
        alreadySelectedInLocationPicker = new ArrayList<>();
        getCitiesInDropDown();
        getLocationPermission();
        ImageButton currentLocationButton = findViewById(R.id.myLoc);
        ImageButton uploadAdButton = findViewById(R.id.uploadAd);
        Button screensDialogOpener =  findViewById(R.id.screensDialogOpener);
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
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCityIndex!=null)
                {
                    moveToMyLocation = true;
                    moveCamera(new LatLng(currentDeviceCityLocation.getLatitude(),currentDeviceCityLocation.getLongitude()),DEFAULT_ZOOM);
                    Spinner citySpinner = findViewById(R.id.scity);
                    citySpinner.setSelection(currentCityIndex);
                }
            }
        });
        screensDialogOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(citySelectedInDropdown!=null)
                {
                    populateScreensDialog();
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
                                    addMapMarkerForScreenLocationsInCity(selectedNames);
                                    locationsToUploadAd = selectedNames;
                                    if(selectedIds!=null) {
                                        alreadySelectedInLocationPicker = selectedIds;
                                    }
                                }
                                @Override
                                public void onCancel() {
                                    Toast.makeText(AdMap.this,"Cancelled",Toast.LENGTH_SHORT).show();
                                }
                            });
                    multiSelectDialog.show(getSupportFragmentManager(),"multiSelectDialog");
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
                    Intent intent = new Intent(AdMap.this, ScreenInformation.class);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    private void getDeviceLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                        getCityFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
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
        Spinner citySpinner = findViewById(R.id.scity);
        final List<String> cities = Arrays.asList("Select City","Mysuru","Bengaluru","Mandya");
        cityIndex.put("Mysuru",1);
        cityIndex.put("Bengaluru",2);
        cityIndex.put("Mandya",3);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cities);
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citiesAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(moveToMyLocation)
                {
                    citySelectedInDropdown = cities.get(i);
                    Toast.makeText(AdMap.this, cities.get(i), Toast.LENGTH_SHORT).show();
                    moveToMyLocation = false;
                }
                else if(i>0 && currentCityIndex!=null)
                {
                    moveCameraToSelectedCity(cities.get(i));
                    citySelectedInDropdown = cities.get(i);
                    Toast.makeText(AdMap.this, cities.get(i), Toast.LENGTH_SHORT).show();
                }
             }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            currentCityIndex = cityIndex.get(cityName);
            citySpinner.setSelection(cityIndex.get(cityName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMapMarkerForScreenLocationsInCity(final ArrayList<String> selectedNames)
    {
        final Handler handler = new Handler();
        final List<MarkerOptions> locationToAddMarker = new ArrayList<>();
        final Runnable uiRunnable = new Runnable() {
            @Override
            public void run() {
                for(MarkerOptions markerOptions : locationToAddMarker)
                {
                    mMap.addMarker(markerOptions);
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                locationToAddMarker.addAll(collectMapMarkerForScreenLocationsInCity(selectedNames));
                handler.post(uiRunnable);
            }
        });
        thread.start();
    }

    public List<MarkerOptions> collectMapMarkerForScreenLocationsInCity(List<String> screenLocationTitles)
    {
        List<MarkerOptions> locationsToAddMarker = new ArrayList<>();
        Geocoder geocoder = new Geocoder(AdMap.this, Locale.getDefault());
        for(String screenTitle:screenLocationTitles)
        {
            try {
                List<Address> addressList = geocoder.getFromLocationName(selectedLocationAddressMapper.get(screenTitle),1);
                Address address =  addressList.get(0);
                LatLng addressLatLng = new LatLng(address.getLatitude(),address.getLongitude());
                locationsToAddMarker.add(new MarkerOptions().position(addressLatLng));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return locationsToAddMarker;
    }

    public void populateScreensDialog()
    {
        screenLocationTitles = new ArrayList<>();
        selectedLocationAddressMapper = new HashMap<>();
        screenLocationTitles.add(new MultiSelectModel(1,"MSRIT College"));
        selectedLocationAddressMapper.put("MSRIT College", "MSRIT Post, M S Ramaiah Nagar,MSR Nagar, Bengaluru, Karnataka 560054");
        screenLocationTitles.add(new MultiSelectModel(2,"RV College"));
        selectedLocationAddressMapper.put("RV College", "Mysore Rd, RV Vidyaniketan, Post, Bengaluru, Karnataka 560059");
        screenLocationTitles.add(new MultiSelectModel(3,"Forum Neighbourhood Mall"));
        selectedLocationAddressMapper.put("Forum Neighbourhood Mall", "No.62, Whitefield Main Rd, Prestige Ozone, Whitefield, Bengaluru, Karnataka 560066");
        screenLocationTitles.add(new MultiSelectModel(4,"Gold's Gym Whitefield"));
        selectedLocationAddressMapper.put("Gold's Gym Whitefield","48, 1st Floor, Regent Prime, Whitefield Main Road, Bengaluru, Karnataka 560066");
    }
}
