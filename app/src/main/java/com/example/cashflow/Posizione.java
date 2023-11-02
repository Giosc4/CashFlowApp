package com.example.cashflow;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.content.Context;
import android.Manifest;
import android.net.DnsResolver;

import androidx.core.app.ActivityCompat;

import com.example.cashflow.dataClass.City;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Posizione {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;

    public Posizione(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void requestDeviceLocation(final DeviceLocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                float latitude = (float) location.getLatitude();
                                System.out.println("location.getLatitude() " + location.getLatitude());
                                System.out.println("latitude "+ latitude);
                                float longitude = (float) location.getLongitude();
                                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        String cityName = addresses.get(0).getLocality();
                                        System.out.println("cityName " + cityName);
                                        callback.onLocationFetched(new City(cityName, latitude, longitude));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    callback.onLocationFetchFailed(e);
                                }
                            } else {
                                callback.onLocationFetchFailed(new Exception("Location is null"));
                            }
                        }
                    });
        } else {
            callback.onLocationFetchFailed(new Exception("Permission not granted"));
        }
    }

    public interface DeviceLocationCallback {
        void onLocationFetched(City city);
        void onLocationFetchFailed(Exception e);
    }
}