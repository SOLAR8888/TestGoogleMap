package com.example.vereskun.maptest.models;

import com.example.vereskun.maptest.App;
import com.example.vereskun.maptest.contracts.ContractMaps;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

/**
 * Created by vereskun on 10.02.2018.
 */

public class ModelMaps implements ContractMaps.IMapsModel {

    private Realm realm = Realm.getDefaultInstance();

    public ModelMaps(){

    }

    @Override
    public Observable<RouteResponse> getRouteResponseObservable(String from, String to) {
        Observable<RouteResponse> routeResponseObservable = App.getRoutesAPI()
                .getRouteResponseObservable(from,to,true);
        System.out.println("from: "+from+"; to: "+to);
        return routeResponseObservable;
    }

    @Override
    public void saveRoute(final SavedRoute route) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(route);
            }
        });
    }

    @Override
    public ArrayList<SavedRoute> getSavedRoutes() {
        RealmResults<SavedRoute> realmResults = realm.where(SavedRoute.class).findAllSorted("id", Sort.DESCENDING);
        ArrayList<SavedRoute> savedRouteList = new ArrayList<>(realmResults);
        System.out.println("savedRouteList.size() = "+savedRouteList.size());
        return savedRouteList;
    }

}
