package com.example.cashflow.statistics;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cashflow.JsonReadWrite;
import com.example.cashflow.R;
import com.example.cashflow.dataClass.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<MarkerOptions> markers;
    private ArrayList<Account> accounts;


    public MapFragment(ArrayList<Account> accounts) {

        this.accounts = accounts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        markers = getMarkerData();

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers from the ArrayList
        for (MarkerOptions markerOptions : markers) {
            mMap.addMarker(markerOptions);
            System.out.println("\t" + markerOptions.getPosition());
        }

        // Move camera to a suitable position (e.g., first marker)
        if (!markers.isEmpty()) {
            LatLng firstMarkerPosition = markers.get(0).getPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarkerPosition, 5));
        }
    }

    private ArrayList<MarkerOptions> getMarkerData() {
        ArrayList<MarkerOptions> markerList = getCitiesFromAccounts();

        //adding one more for test
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(37.38, 14.37)).title("PIAZZA ARMERINA");
        markerOptions.contentDescription("questa è una descrizione");
        markerList.add(markerOptions);
        return markerList;

    }

    public ArrayList<MarkerOptions> getCitiesFromAccounts() {
        ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();

        for (Account account : accounts) {
            ArrayList<Transactions> transactions = account.getListTrans();
            if (transactions != null) {
                for (Transactions transaction : transactions) {
                    String city = transaction.getCity().getNameCity();
                    System.out.println("city "+city);
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
