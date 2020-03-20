package com.gogrocersm.storemanager.Dashboard;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gogrocersm.storemanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE=101;
    private Button okBtn;
    private float lat,lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapss);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        okBtn =(Button)findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                finish();

            }
        });

    }
    private void fetchLastLocation() {


        Task<Location> task =fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null)
                {
                    currentLocation=location;
                   // Toast.makeText(MapsActivity.this, currentLocation.getLatitude()+"  "+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    float  lat = (float) currentLocation.getLatitude();
                    float lang = (float) currentLocation.getLongitude();
                    SharedPreferences.Editor editor = getSharedPreferences("7", MODE_PRIVATE).edit();
                    editor.putFloat("8", lat);
                    editor.putFloat("9",lang);
                    editor.apply();
                    // editor.putString("long","currentLocation.getLongitude()");


                    SupportMapFragment supportMapFragment =(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googlmap);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("your pick up location"));
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

       // Toast.makeText(MapsActivity.this, latLng.latitude+"  "+latLng.longitude, Toast.LENGTH_SHORT).show();

        float lat =(float)latLng.latitude;
        float lang =(float)latLng.longitude;
        SharedPreferences.Editor editor = getSharedPreferences("7", MODE_PRIVATE).edit();
        editor.putFloat("8",lat );
        editor.putFloat("9",lang);
        editor.apply();

        // editor.putString("lat", "latLng.latitude");
//        editor.putString("long","latLng.longitude");
        String Address ="";
        try {
            List<android.location.Address> list  = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (list!=null&&list.size()>0)
            {
                // if (list.get(0))
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("your location"));
    }
}
