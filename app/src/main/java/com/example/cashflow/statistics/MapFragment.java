package com.example.cashflow.statistics;

import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cashflow.BuildConfig;
import com.example.cashflow.JsonReadWrite;
import com.example.cashflow.R;
import com.example.cashflow.dataClass.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private MapView mapView;
    private ArrayList<Account> accounts;


    public MapFragment(ArrayList<Account> accounts) {

        this.accounts = accounts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Imposta il percorso della cache prima di utilizzare la mappa
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Carica il layout del fragment, assicurati che sia stato aggiornato per osmdroid
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        addMarkers();

        return view;
    }

    private void addMarkers() {
        // Controlla se la lista dei conti o delle città è vuota
        if (accounts.isEmpty() || getCitiesFromAccounts().isEmpty()) {
            return;
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (MarkerOptions markerOptions : getCitiesFromAccounts()) {
            double lat = markerOptions.getPosition().latitude;
            double lon = markerOptions.getPosition().longitude;

            // Aggiorna i valori per il bounding box
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;

            // Crea e aggiungi il marcatore alla mappa
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(lat, lon));
            marker.setTitle(markerOptions.getTitle());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
        }

        mapView.invalidate(); // Aggiorna la mappa con i nuovi marcatori

        // Calcola il bounding box e adatta la mappa
        IGeoPoint southWest = new GeoPoint(minLat, minLon);
        IGeoPoint northEast = new GeoPoint(maxLat, maxLon);
        BoundingBox boundingBox = new BoundingBox(northEast.getLatitude(), northEast.getLongitude(), southWest.getLatitude(), southWest.getLongitude());

        new Handler().postDelayed(() -> mapView.zoomToBoundingBox(boundingBox, true), 1000); // Ritarda lo zoom di 1 secondo
    }

    public ArrayList<MarkerOptions> getCitiesFromAccounts() {
        ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();

        for (Account account : accounts) {
            ArrayList<Transactions> transactions = account.getListTrans();
            if (transactions != null) {
                for (Transactions transaction : transactions) {
                    String city = transaction.getCity().getNameCity();
                    System.out.println("city " + city);
                    if (city != null) {
                        LatLng cityLatLng = new LatLng(transaction.getCity().getLatitude(), transaction.getCity().getLongitude());
                        System.out.println("cityLatLng " + cityLatLng.toString());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(cityLatLng)
                                .title(city);
                        markerOptionsList.add(markerOptions);
                    }
                }
            }
        }
        return markerOptionsList;
    }
}
