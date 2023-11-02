package com.example.cashflow.dataClass;

public class City {

    private String nameCity;
    private float latitude;
    private float longitude;

    public City() {
        this.nameCity = "Bologna";
        this.latitude = 44;
        this.longitude = 11;
    }

    public City(String nameCity, float latitude, float longitude) {
        this.nameCity = nameCity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "City{" +
                "nameCity='" + nameCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
