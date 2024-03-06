package com.example.cashflow.dataClass;

public class City {
    private int id;

    private String nameCity;
    private double latitude;
    private double longitude;

    public City() {

        this.nameCity = "Bologna";
        this.latitude = 44.50;
        this.longitude = 11;
    }

    public City(String cityName, double latitude, double longitude) {
        this.nameCity = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public City(int id, String cityName, double latitude, double longitude) {
        this.id = id;
        this.nameCity = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return "City{" +
                "nameCity='" + nameCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String printOnApp() {
        if (nameCity != null) {
            return "Città: " + nameCity;
        } else {
            return "Città sconosciuta";
        }
    }
}
