package com.example.mysavedrive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class ShowUserLocaion extends FragmentActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap;
    //
    Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    final Handler handler = new Handler();
    private LocationCallback locationCallback;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_locaion);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();

//
////        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                fetchLastLocation();
//            }
//        }, 5000);  //the time is in miliseconds



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //          .findFragmentById(R.id.map);
        //  mapFragment.getMapAsync(this);


    }
    //-------------------------------------------------------------------------------------------------------//


    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ShowUserLocaion.this);

                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------//


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//         // Add a marker in Sydney and move the camera
        //  LatLng sydney = new LatLng(-34, 151);



        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Taxi Driver Here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        Toast.makeText(getApplicationContext(), "Your current Location:" + currentLocation.getLatitude()+ "/" + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();








        //-------------------------------------------------------------------------------------------------------//

        //-------------------------------------------------------------------------------------------------------//
    }


    @Override
    public void onLocationChanged(Location location) {
        fetchLastLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}



