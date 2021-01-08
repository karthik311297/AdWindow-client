package com.example.karthik.adwindow_client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
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
    private static final float DEFAULT_ZOOM = 12f;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HashMap<String,Integer> cityIndex;
    private Integer currentCityIndex;
    private String citySelectedInDropdown;
    private Location currentDeviceCityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_map);
        cityIndex=new HashMap<>();
        getCitiesInDropDown();
        getLocationPermission();
        ImageButton currentLocationButton = findViewById(R.id.myLoc);
        ImageButton uploadAdButton = findViewById(R.id.uploadAd);
        uploadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(citySelectedInDropdown!=null)
                {
                    Intent intent = new Intent(AdMap.this, ScreenLocationAdUpload.class);
                    intent.putExtra("CITY", citySelectedInDropdown);
                    startActivity(intent);
                }
            }
        });
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCityIndex!=null)
                {
                    moveCamera(new LatLng(currentDeviceCityLocation.getLatitude(),currentDeviceCityLocation.getLongitude()),DEFAULT_ZOOM);
                    Spinner citySpinner = findViewById(R.id.scity);
                    citySpinner.setSelection(currentCityIndex);
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
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
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
                if(i>0 && currentCityIndex!=null)
                {
                    if(i==currentCityIndex)
                    {
                        moveCamera(new LatLng(currentDeviceCityLocation.getLatitude(),currentDeviceCityLocation.getLongitude()),DEFAULT_ZOOM);
                    }
                    else
                    {
                        moveCameraToSelectedCity(cities.get(i));
                    }
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

    public void addMapMarkerForScreenLocationsInCity(List<String> screenAddresses)
    {
        Geocoder geocoder = new Geocoder(AdMap.this, Locale.getDefault());
        for(String screenAddress:screenAddresses)
        {
            try {
                List<Address> addressList = geocoder.getFromLocationName(screenAddress,1);
                Address address =  addressList.get(0);
                LatLng addressLatLng = new LatLng(address.getLatitude(),address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(addressLatLng));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
