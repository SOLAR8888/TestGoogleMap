package com.example.vereskun.maptest.contracts;

import com.example.vereskun.maptest.models.RouteResponse;
import com.example.vereskun.maptest.models.SavedRoute;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by vereskun on 10.02.2018.
 */

public class ContractMaps {

    public interface IMapsView{
        void showSearchWidget(int requestCode);
        void setPin(int requestCode, Place place);
        void showMessage(String text);
        void drawLines(PolylineOptions line, LatLngBounds.Builder builder, ArrayList<Integer> durations);
        void refreshSpinnerAdapter(ArrayList<SavedRoute> list);
    }
    public interface IMapsPresenter{
        void onClickFrom();
        void onClickTo();
        void onClickAddWaipoint();
        void onClickStart();
        void onActivityResult(int requestCode, Place place);
        void onEndWay(String from, String to,ArrayList<LatLng> points,ArrayList<Integer> durations);
        void clear();
        void refreshSpinerAdapter();
    }
    public interface IMapsModel{
        Observable<RouteResponse> getRouteResponseObservable(String from, String to);
        void saveRoute(SavedRoute route);
        ArrayList<SavedRoute> getSavedRoutes ();
    }

}
