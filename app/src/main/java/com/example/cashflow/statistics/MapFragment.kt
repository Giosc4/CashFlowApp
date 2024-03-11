package com.example.cashflow.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashflow.BuildConfig
import com.example.cashflow.R
import com.example.cashflow.dataClass.Account
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment(private val accounts: ArrayList<Account>) : Fragment() {
    private var mapView: MapView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Imposta il percorso della cache prima di utilizzare la mappa
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        // Carica il layout del fragment, assicurati che sia stato aggiornato per osmdroid
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setBuiltInZoomControls(true)
        mapView?.setMultiTouchControls(true)
        addMarkers()
        return view
    }

    private fun addMarkers() {
        if (accounts.isEmpty()) {
            return
        }
        var minLat = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = Double.MIN_VALUE
        val markers =
            citiesFromAccounts // Assumendo che getCitiesFromAccounts ora restituisca ArrayList<Marker>
        for (marker in markers) {
            val position = marker.position
            val lat = position.latitude
            val lon = position.longitude

            // Aggiorna i valori per il bounding box
            if (lat < minLat) minLat = lat
            if (lat > maxLat) maxLat = lat
            if (lon < minLon) minLon = lon
            if (lon > maxLon) maxLon = lon
            mapView!!.overlays.add(marker)
        }
        mapView!!.invalidate() // Aggiorna la mappa con i nuovi marcatori

        // Calcola il bounding box e adatta la mappa
        if (!markers.isEmpty()) {
            val southWest: IGeoPoint = GeoPoint(minLat, minLon)
            val northEast: IGeoPoint = GeoPoint(maxLat, maxLon)
            val boundingBox = BoundingBox(
                northEast.latitude,
                northEast.longitude,
                southWest.latitude,
                southWest.longitude
            )
            mapView!!.zoomToBoundingBox(boundingBox, true) // Zoom immediato al bounding box
        }
    }

    val citiesFromAccounts: ArrayList<Marker>
        get() {
            val markersList = ArrayList<Marker>()
            for (account in accounts) {
                val transactions = account.listTrans
                if (transactions != null) {
                    for (transaction in transactions) {
                        val city = transaction.city.nameCity
                        println("city $city")
                        if (city != null) {
                            val cityGeoPoint =
                                GeoPoint(transaction.city.latitude, transaction.city.longitude)
                            println("cityGeoPoint $cityGeoPoint")
                            val marker = Marker(mapView)
                            marker.setPosition(cityGeoPoint)
                            marker.title = city
                            markersList.add(marker)
                        }
                    }
                }
            }
            return markersList
        }
}