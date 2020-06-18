package com.pikopako.Model;


public class AddressModel {
    String locationId;
    String address;
    String latitude;
    String longitude;

    // Empty constructor
    public AddressModel() {

    }

    // constructor
    public AddressModel(String locationId, String address, String latitude, String longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
    }

    // getting name
    public String getAddress() {
        return this.address;
    }

    // setting name
    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationId() {
        return this.locationId;
    }

    // setting name
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    // getting name


}