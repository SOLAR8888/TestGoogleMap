package com.example.vereskun.maptest.presenters;

import com.example.vereskun.maptest.contracts.ContractMaps;
import com.example.vereskun.maptest.models.PointList;
import com.example.vereskun.maptest.models.RealmInt;
import com.example.vereskun.maptest.models.RealmLatLng;
import com.example.vereskun.maptest.models.RouteResponse;
import com.example.vereskun.maptest.models.SavedRoute;
import com.example.vereskun.maptest.views.MapsActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vereskun on 10.02.2018.
 */

public class PresenterMaps implements ContractMaps.IMapsPresenter {

    private ContractMaps.IMapsModel mapsModel;
    private ContractMaps.IMapsView view;
    private PointList pointList;
    private ArrayList<RouteResponse> routeResponseArrayList;
    private ArrayList<LatLng> allPoints;
    private List<LatLng> toDraw;
    private ArrayList<Integer> durations;
    private int responseCount = 0;

    public PresenterMaps(ContractMaps.IMapsView view, ContractMaps.IMapsModel mapsModel){
        this.view = view;
        this.mapsModel = mapsModel;
        this.pointList = new PointList();
        this.routeResponseArrayList = new ArrayList<>();
        this.allPoints = new ArrayList<>();
        this.toDraw = new ArrayList<>();
        this.durations = new ArrayList<>();
    }

    public PointList getPointList() {
        return pointList;
    }


    @Override
    public void onClickFrom() {
        view.showSearchWidget(MapsActivity.REQUEST_CODE_FROM);
    }

    @Override
    public void onClickTo() {
        view.showSearchWidget(MapsActivity.REQUEST_CODE_TO);
    }

    @Override
    public void onClickAddWaipoint() {
        view.showSearchWidget(MapsActivity.REQUEST_CODE_ADD);
    }

    @Override
    public void onClickStart() {
        allPoints = pointList.getAllPoints();
        LatLng from = allPoints.get(responseCount);
        LatLng to = allPoints.get(responseCount+1);
        getNextRoute(from,to);
    }
    private void getNextRoute(LatLng from, LatLng to){
        String fromStr = from.latitude+","+from.longitude;
        String toStr = to.latitude+","+to.longitude;
        Observable<RouteResponse> routeResponseObservable = mapsModel
                .getRouteResponseObservable(fromStr,toStr);
        routeResponseObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RouteResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RouteResponse routeResponse) {
                        routeResponseArrayList.add(routeResponse);
                        onRouteResponseGet(routeResponse);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, Place place) {
        switch (requestCode){
            case MapsActivity.REQUEST_CODE_FROM:
                view.setPin(requestCode,place);
                getPointList().setFrom(place.getLatLng());
                break;
            case  MapsActivity.REQUEST_CODE_TO:
                view.setPin(requestCode,place);
                getPointList().setTo(place.getLatLng());
                break;
            case  MapsActivity.REQUEST_CODE_ADD:
                if (getPointList().getWaipoints().size() < 5) {
                    view.setPin(requestCode, place);
                    getPointList().addWaypoint(place.getLatLng());
                }
                else {
                    view.showMessage("max of waypoint: 5");
                }
                break;
        }
    }

    @Override
    public void onEndWay(String from, String to, ArrayList<LatLng> points, ArrayList<Integer> durations) {
        long id = System.currentTimeMillis();
        RealmList<RealmLatLng> realmPoints = new RealmList<>();
        for (int i = 0; i < points.size(); i++) {
            realmPoints.add(new RealmLatLng(points.get(i)));
        }
        RealmList<RealmInt> realmDurations = new RealmList<>();
        for (int i = 0; i < durations.size(); i++) {
            realmDurations.add(new RealmInt(durations.get(i)));
        }
        //RealmLatLng from = new RealmLatLng(getPointList().getFrom());
        //RealmLatLng to = new RealmLatLng(getPointList().getTo());

        RealmList<RealmLatLng> realmMarkers = new RealmList<>();
        for (int i = 0; i < getPointList().getAllPoints().size(); i++) {
            realmMarkers.add(new RealmLatLng(getPointList().getAllPoints().get(i)));
        }
        SavedRoute savedRoute = new SavedRoute(id,from,to,realmPoints,realmDurations,realmMarkers);
        mapsModel.saveRoute(savedRoute);
    }

    @Override
    public void clear() {
        allPoints.clear();
        toDraw.clear();
        routeResponseArrayList.clear();
        durations.clear();
        responseCount = 0;
        pointList = new PointList();
    }


    private void onRouteResponseGet(RouteResponse routeResponse) {
        responseCount+=1;
        int routsCount = routeResponse.getRoutes().size();
        //routsCount = 1; //one way
        for (int i = 0; i < routsCount; i++) {
            for (int j = 0; j < routeResponse.getRoutes().get(i).getLegs().size(); j++) {
                for (int k = 0; k < routeResponse.getRoutes().get(i).getLegs().get(j).getSteps().size(); k++) {
                    String polyline = routeResponse.getRoutes().get(i).getLegs().get(j).getSteps().get(k).getPolyline().getPoints();
                    List<LatLng> points = PolyUtil.decode(polyline);
                    int duration = routeResponse.getRoutes().get(i).getLegs().get(j).getSteps().get(k).getDuration().getValue();
                    toDraw.addAll(points);
                    durations.add(duration);
                }
            }
        }
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        PolylineOptions line = new PolylineOptions();
        for (int n = 0; n < toDraw.size(); n++) {
            line.add((LatLng) toDraw.get(n));
            latLngBuilder.include((LatLng) toDraw.get(n));
        }
        if (responseCount == pointList.getPointsCount()-1) {
            view.drawLines(line, latLngBuilder, durations);
        }
        else {
            LatLng from = allPoints.get(responseCount);
            LatLng to = allPoints.get(responseCount+1);
            getNextRoute(from,to);
        }
    }

    @Override
    public void refreshSpinerAdapter() {
        view.refreshSpinnerAdapter(mapsModel.getSavedRoutes());
    }

}
