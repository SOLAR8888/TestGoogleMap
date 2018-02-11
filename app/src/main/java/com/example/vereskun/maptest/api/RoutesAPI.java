package com.example.vereskun.maptest.api;

import com.example.vereskun.maptest.models.RouteResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by vereskun on 10.02.2018.
 */

public interface RoutesAPI {

    public String BASE_URL = "https://maps.googleapis.com/";

    @GET("maps/api/directions/json")
    Observable<RouteResponse> getRouteResponseObservable(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("sensor") boolean sensor
    );


}
