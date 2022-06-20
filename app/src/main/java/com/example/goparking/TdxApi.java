package com.example.goparking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TdxApi {
    @GET("Taipei")
    Call<ParkingInfo> getStations(
            @Query("$top") Integer top,
            @Query("$format") String format
    );
}
