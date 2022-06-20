package com.example.goparking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarPark {
    @SerializedName("CarParkName")
    @Expose
    private NameType carParkName;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("CarParkPosition")
    @Expose
    private PointType carParkPosition;

    @SerializedName("Address")
    @Expose
    private String address;

    @SerializedName("FareDescription")
    @Expose
    private String fareDescription;


    public NameType getCarParkName() { return carParkName; }

    public void setCarParkName(NameType carParkName) { this.carParkName = carParkName; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public PointType getCarParkPosition() { return carParkPosition; }

    public void setCarParkPosition(PointType carParkPosition) { this.carParkPosition = carParkPosition; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getFareDescription() { return fareDescription; }

    public void setFareDescription(String fareDescription) { this.fareDescription = fareDescription; }
}
