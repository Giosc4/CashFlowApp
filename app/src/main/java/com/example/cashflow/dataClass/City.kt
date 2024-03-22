package com.example.cashflow.dataClass

import java.io.Serializable

class City : Serializable {
    var id = 0
        private set
    var nameCity: String?
    var latitude: Double
    var longitude: Double

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
        this.id = id
        nameCity = cityName
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun toString(): String {
        return "City{" +
                "nameCity='" + nameCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
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
