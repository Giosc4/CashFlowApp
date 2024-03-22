package com.example.cashflow.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.db.*
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap

class MapFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        mapboxMap = mapView.getMapboxMap().apply {
            loadStyleUri(Style.MAPBOX_STREETS) {
                // Configurazione della mappa completata
                // Qui puoi anche gestire gli eventi della mappa o aggiungere marcatori
                addMarkersToMap()
            }
        }

        return view
    }

    private fun addMarkersToMap() {
        val pointAnnotationManager = mapView?.annotations?.createPointAnnotationManager()

        val accounts = readSQL.getAccounts()

        accounts.forEach { account ->
            val transactions = readSQL.getTransactionsByAccountId(account.id)
            transactions.forEach { transaction ->
                val city = readSQL.getCityById(transaction.cityId)
                city?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage("marker-icon-id")

                    pointAnnotationManager?.create(pointAnnotationOptions)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }
}