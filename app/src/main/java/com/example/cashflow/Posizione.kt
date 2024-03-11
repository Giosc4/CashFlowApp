package com.example.cashflow

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale
import com.example.cashflow.dataClass.*

class Posizione(private val context: Context) {
    private val fusedLocationProviderClient: FusedLocationProviderClient

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun requestDeviceLocation(callback: DeviceLocationCallback) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback.onLocationFetchFailed(Exception("Permission not granted"))
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    callback.onLocationFetchFailed(Exception("Location is null"))
                    return@addOnSuccessListener
                }
                val latitude = location.latitude
                val longitude = location.longitude
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses =
                        geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
                    if (addresses != null && !addresses.isEmpty()) {
                        val cityName = addresses[0].locality
                        callback.onLocationFetched(City(cityName, latitude, longitude))
                    }
                } catch (e: IOException) {
                    callback.onLocationFetchFailed(e)
                }
            }
    }

    interface DeviceLocationCallback {
        fun onLocationFetched(city: City?)
        fun onLocationFetchFailed(e: Exception?)
    }
}