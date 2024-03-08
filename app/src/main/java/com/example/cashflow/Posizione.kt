package com.example.cashflow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.example.cashflow.dataClass.City;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Posizione {
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Context context;

    public Posizione(Context context) {
        this.context = context;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void requestDeviceLocation(final DeviceLocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationFetchFailed(new Exception("Permission not granted"));
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        callback.onLocationFetchFailed(new Exception("Location is null"));
                        return;
                    }

                    float latitude = (float) location.getLatitude();
                    float longitude = (float) location.getLongitude();
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            String cityName = addresses.get(0).getLocality();
                            callback.onLocationFetched(new City(cityName, latitude, longitude));
                        }
                    } catch (IOException e) {
                        callback.onLocationFetchFailed(e);
                    }
                });
    }

    public interface DeviceLocationCallback {
        void onLocationFetched(City city);

        void onLocationFetchFailed(Exception e);
    }
}