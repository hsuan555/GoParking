package com.example.goparking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParkingInfo {

    @SerializedName("CarParks")
    @Expose
    private List<CarPark> carParks = null;

    public List<CarPark> getCarParks() {
        return carParks;
    }

    public void setCarParks(List<CarPark> carParks) {
        this.carParks = carParks;
    }
}