package com.example.cashflow.dataClass

import java.io.Serializable

class City : Serializable {
    var id: Int = -1
        private set
    var nameCity: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor() {
        nameCity = "Bologna"
        latitude = 44.50
        longitude = 11.0
    }

    constructor(cityName: String?, latitude: Double, longitude: Double) {
        nameCity = cityName
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor(id: Int, cityName: String?, latitude: Double, longitude: Double) {
        setId(id)
        nameCity = cityName
        this.latitude = latitude
        this.longitude = longitude
    }

    fun setId(id: Int) {
        if (id > 0) {
            this.id = id
        } else {
            this.id = -1 // Oppure lascia il valore predefinito se id non è valido.
        }
    }

    override fun toString(): String {
        return "City{" +
                "id=$id" +
                ", nameCity='$nameCity'" +
                ", latitude=$latitude" +
                ", longitude=$longitude" +
                '}'
    }

    fun printOnApp(): String {
        return if (nameCity != null) {
            "Città: $nameCity"
        } else {
            "Città sconosciuta"
        }
    }
}
