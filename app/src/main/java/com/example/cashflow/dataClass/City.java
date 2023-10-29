package com.example.cashflow.dataClass;

public class City {

    private String nameCity;
    private double latitude;
    private double longitude;

    public City() {
        this.nameCity = "Bologna";
        this.latitude = 44;
        this.longitude = 11;
    }

    public City(String nameCity, double latitude, double longitude) {
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


    @Override
    public String toString() {
        return "City{" +
                "nameCity='" + nameCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String cityName(){
        return nameCity;
    }

}
