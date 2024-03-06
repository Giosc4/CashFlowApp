/*
package com.example.cashflow.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cashflow.BuildConfig;
import com.example.cashflow.R;
import com.example.cashflow.dataClass.*;


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
        if (accounts.isEmpty()) {
            return;
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        ArrayList<Marker> markers = getCitiesFromAccounts(); // Assumendo che getCitiesFromAccounts ora restituisca ArrayList<Marker>

        for (Marker marker : markers) {
            GeoPoint position = marker.getPosition();

            double lat = position.getLatitude();
            double lon = position.getLongitude();

            // Aggiorna i valori per il bounding box
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate(); // Aggiorna la mappa con i nuovi marcatori

        // Calcola il bounding box e adatta la mappa
        if (!markers.isEmpty()) {
            IGeoPoint southWest = new GeoPoint(minLat, minLon);
            IGeoPoint northEast = new GeoPoint(maxLat, maxLon);
            BoundingBox boundingBox = new BoundingBox(northEast.getLatitude(), northEast.getLongitude(), southWest.getLatitude(), southWest.getLongitude());

            mapView.zoomToBoundingBox(boundingBox, true); // Zoom immediato al bounding box
        }
    }

    public ArrayList<Marker> getCitiesFromAccounts() {
        ArrayList<Marker> markersList = new ArrayList<>();

        for (Account account : accounts) {
            ArrayList<Transactions> transactions = account.getListTrans();
            if (transactions != null) {
                for (Transactions transaction : transactions) {
                    String city = transaction.getCity().getNameCity();
                    System.out.println("city " + city);
                    if (city != null) {
                        GeoPoint cityGeoPoint = new GeoPoint(transaction.getCity().getLatitude(), transaction.getCity().getLongitude());
                        System.out.println("cityGeoPoint " + cityGeoPoint.toString());
                        Marker marker = new Marker(mapView);
                        marker.setPosition(cityGeoPoint);
                        marker.setTitle(city);
                        markersList.add(marker);
                    }
                }
            }
        }
        return markersList;
    }
}
*/
