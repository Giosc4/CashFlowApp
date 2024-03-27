package com.example.cashflow.fragments.statistics

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cashflow.R
import com.example.cashflow.dataClass.*
import com.example.cashflow.db.*
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager

class MapFragment(private val readSQL: ReadSQL, private val writeSQL: WriteSQL) : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private val annotationToTransactionsMap = mutableMapOf<PointAnnotation, List<Transactions>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)

        mapboxMap = mapView.getMapboxMap().apply {
            loadStyleUri(Style.MAPBOX_STREETS) { style ->
                // Converti Vector Drawable in Bitmap
                val vectorDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.position)
                val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                vectorDrawable?.apply {
                    setBounds(0, 0, canvas.width, canvas.height)
                    draw(canvas)
                } ?: throw IllegalArgumentException("Drawable not found")

                style.addImage("my-marker-icon", bitmap)
                pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
                    configureMarkerClickListener(this)
                }
                addMarkersToMap()
            }
        }

        return view
    }

    private fun addMarkersToMap() {
        val accounts = readSQL.getAccounts()

        accounts.forEach { account ->
            val transactions = readSQL.getTransactionsByAccountId(account.id)
            transactions.forEach { transaction ->
                val city = readSQL.getCityById(transaction.cityId)
                city?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    val pointAnnotation = pointAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage("my-marker-icon")
                    )
                    annotationToTransactionsMap[pointAnnotation] = transactions
                }
            }
        }
    }

    private fun configureMarkerClickListener(pointAnnotationManager: PointAnnotationManager) {
        pointAnnotationManager.addClickListener { pointAnnotation ->
            val transactions = annotationToTransactionsMap[pointAnnotation]
            transactions?.let {
                showTransactionsDialog(it)
            }
            true
        }
    }

    private fun showTransactionsDialog(transactions: List<Transactions>) {
        val transactionDetails = transactions.joinToString(separator = "\n") { transaction ->
            val category = readSQL.getCategoryById(transaction.categoryId)
            val account = readSQL.getAccountById(transaction.accountId)
            "\u2022 Valore: €${transaction.amountValue}, Conto: ${account?.name}, Categoria: ${category?.name}.\n"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Transazioni")
            .setMessage(transactionDetails)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}
