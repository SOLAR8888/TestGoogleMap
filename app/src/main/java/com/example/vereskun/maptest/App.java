package com.example.vereskun.maptest;

import android.app.Application;

import com.example.vereskun.maptest.api.RoutesAPI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vereskun on 10.02.2018.
 */

public class App extends Application {

    private static RoutesAPI routesAPI;
    private Retrofit retrofit;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(RoutesAPI.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(JacksonConverterFactory.create())
                .build();
        routesAPI = retrofit.create(RoutesAPI.class);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    public static RoutesAPI getRoutesAPI() {
        return routesAPI;
    }
    public static GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mGoogleApiClient.disconnect();
    }
}
