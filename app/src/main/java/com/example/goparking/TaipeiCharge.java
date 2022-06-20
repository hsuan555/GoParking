package com.example.goparking;

public class TaipeiCharge {

    private String parkName;
    private String address;
    private String lat;
    private String lon;
    private String carNum;

    public String getParkName() {return parkName;}

    public void setParkName(String parkName) {this.parkName = parkName;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getLat() {return lat;}

    public void setLat(String lat) {this.lat = lat;}

    public String getLon() {return lon;}

    public void setLon(String lon) {this.lon = lon;}

    public String getCarNum() {return carNum;}

    public void setCarNum(String carNum) {this.carNum = carNum;}

    @Override
    public String toString() {
        return "TaipeiCharge{" +
                "parkName='" + parkName + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", carNum=" + carNum +
                '}';
    }
}
