package com.example.vereskun.maptest.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vereskun.maptest.R;
import com.example.vereskun.maptest.contracts.ContractMaps;
import com.example.vereskun.maptest.models.ModelMaps;
import com.example.vereskun.maptest.models.RealmLatLng;
import com.example.vereskun.maptest.models.SavedRoute;
import com.example.vereskun.maptest.presenters.PresenterMaps;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ContractMaps.IMapsView, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private ContractMaps.IMapsPresenter mapsPresenter;
    private ContractMaps.IMapsModel mapsModel;
    private TextView tvFrom;
    private TextView tvTo;
    private Button btnAddWaypoint;
    private Button btnStart;
    private Button btnClear;
    public static final int REQUEST_CODE_FROM = 1;
    public static final int REQUEST_CODE_TO = 2;
    public static final int REQUEST_CODE_ADD = 3;
    private Marker marker;
    private ArrayList<LatLng> points;
    private ArrayList<Integer> durations;
    private Spinner spinner;
    private ArrayList<SavedRoute> routesList;
    private AsyncDrawMap asyncDrawMap;
    private boolean doNotSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvFrom = findViewById(R.id.tvFrom);
        tvTo = findViewById(R.id.tvTo);
        btnStart = findViewById(R.id.btnStart);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnAddWaypoint = findViewById(R.id.btnAddWaypoint);
        tvFrom.setOnClickListener(this);
        tvTo.setOnClickListener(this);
        btnAddWaypoint.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        mapsModel = new ModelMaps();
        mapsPresenter = new PresenterMaps(this,mapsModel);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelected(false);
        mapsPresenter.refreshSpinerAdapter();
    }

    @Override
    public void refreshSpinnerAdapter(ArrayList<SavedRoute> list) {
        routesList = list;
        String [] routes = new String[list.size()+1];
        routes[0] = "";
        for (int i = 0; i < list.size(); i++) {
            routes[i+1] = list.get(i).getFrom()+" - "+list.get(i).getTo();
        }
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,routes);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvFrom: mapsPresenter.onClickFrom();
                break;
            case  R.id.tvTo: mapsPresenter.onClickTo();
                break;
            case R.id.btnAddWaypoint: mapsPresenter.onClickAddWaipoint();
                break;
            case R.id.btnStart: mapsPresenter.onClickStart();
                btnAddWaypoint.setEnabled(false);
                btnStart.setEnabled(false);
                doNotSave = false;
                break;
            case R.id.btnClear: clear();
                break;
        }
    }

    @Override
    public void showSearchWidget(int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            System.out.println("showSearchWidget: "+e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            System.out.println("showSearchWidget: "+e.getMessage());
        }
    }

    @Override
    public void setPin(int requestCode, Place place) {
        LatLng pin = place.getLatLng();

        switch (requestCode){
            case REQUEST_CODE_FROM:
                mMap.addMarker(new MarkerOptions().position(pin).title("From")).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pin,14));
                tvFrom.setText(place.getName());
                tvFrom.setEnabled(false);
                break;
            case REQUEST_CODE_TO:
                mMap.addMarker(new MarkerOptions().position(pin).title("To")).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pin,14));
                tvTo.setText(place.getName());
                tvTo.setEnabled(false);
                break;
            case REQUEST_CODE_ADD:
                mMap.addMarker(new MarkerOptions().position(pin).title("point"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pin,14));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CODE_TO:
                if (resultCode == RESULT_OK){
                    Place place = PlaceAutocomplete.getPlace(this,data);
                    mapsPresenter.onActivityResult(REQUEST_CODE_TO,place);
                }else if (resultCode == PlaceAutocomplete.RESULT_ERROR){
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    System.out.println(status.getStatusMessage());
                }else if (resultCode == RESULT_CANCELED){
                    System.out.println("user canceled");
                }
                break;
            case REQUEST_CODE_FROM:
                if (resultCode == RESULT_OK){
                    Place place = PlaceAutocomplete.getPlace(this,data);
                    mapsPresenter.onActivityResult(REQUEST_CODE_FROM,place);
                }else if (resultCode == PlaceAutocomplete.RESULT_ERROR){
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    System.out.println(status.getStatusMessage());
                }else if (resultCode == RESULT_CANCELED){
                    System.out.println("user canceled");
                }
                break;
            case REQUEST_CODE_ADD:
                if (resultCode == RESULT_OK){
                    Place place = PlaceAutocomplete.getPlace(this,data);
                    mapsPresenter.onActivityResult(REQUEST_CODE_ADD,place);
                }else if (resultCode == PlaceAutocomplete.RESULT_ERROR){
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    System.out.println(status.getStatusMessage());
                }else if (resultCode == RESULT_CANCELED){
                    System.out.println("user canceled");
                }
                break;
        }

    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void drawLines(PolylineOptions line, LatLngBounds.Builder builder, ArrayList<Integer> durations) {
        line.width(8f).color(getResources().getColor(R.color.colorPrimary));
        mMap.addPolyline(line);
        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = builder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        mMap.moveCamera(track);
        startMove(line, durations);
    }

    public void startMove(PolylineOptions polyline, ArrayList<Integer> durations){
        points = (ArrayList<LatLng>) polyline.getPoints();
        this.durations = durations;
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(points.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car30)));
            //marker.setTag("car");
            marker.setTitle("car");
            marker.showInfoWindow();
        }
        asyncDrawMap = new AsyncDrawMap();
        asyncDrawMap.execute(durations);
        //asyncDrawMap.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private void nextPoint(int progress){
        float percent = (float) progress/(durations.size()*1000);
        int per = Math.round(percent*100);
        System.out.println(per+"%");
        marker.setTitle(per + "%");
        marker.showInfoWindow();
        int cur_point = Math.round(points.size()*percent);
        if (cur_point < points.size()) {
            marker.setPosition(points.get(cur_point));
        }
    }

    private void endWay(){
        System.out.println("end");
        if (!doNotSave) {
            mapsPresenter.onEndWay(tvFrom.getText().toString(), tvTo.getText().toString(), points, durations);
            mapsPresenter.refreshSpinerAdapter();
        }
    }

    private void clear(){
        mapsPresenter.clear();
        mMap.clear();
        btnClear.setEnabled(false);
        btnAddWaypoint.setEnabled(true);
        btnStart.setEnabled(true);
        tvTo.setEnabled(true);
        tvTo.setText("Select To");
        tvFrom.setEnabled(true);
        tvFrom.setText("Select From");
        if (asyncDrawMap != null) asyncDrawMap.cancel(true);
        marker = null;
        spinner.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            clear();
            fillData(i-1);
            tvFrom.setEnabled(false);
            tvTo.setEnabled(false);
            btnStart.setEnabled(false);
            btnAddWaypoint.setEnabled(false);
            doNotSave = true;
        }
    }
    private  void fillData(int position){
        btnClear.setEnabled(true);
        RealmList<RealmLatLng> realmMarkers = routesList.get(position).getMarkers();
        for (int i = 0; i < realmMarkers.size(); i++) {
            RealmLatLng marker = realmMarkers.get(i);
            mMap.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(),marker.getLongitude())));
        }
        tvFrom.setText(routesList.get(position).getFrom());
        tvTo.setText(routesList.get(position).getTo());
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        PolylineOptions line = new PolylineOptions();
        List<RealmLatLng> realmPoints = routesList.get(position).getPoints();
        for (int n = 0; n < realmPoints.size(); n++) {
            line.add(new LatLng(realmPoints.get(n).getLatitude(),realmPoints.get(n).getLongitude()));
            latLngBuilder.include(new LatLng(realmPoints.get(n).getLatitude(),realmPoints.get(n).getLongitude()));
        }
        ArrayList<Integer> durations = new ArrayList<>();
        for (int i = 0; i < routesList.get(position).getDurations().size(); i++) {
            durations.add(routesList.get(position).getDurations().get(i).getInteger());
        }
        drawLines(line,latLngBuilder,durations);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class AsyncDrawMap extends AsyncTask<ArrayList<Integer>,Integer,Void>{

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            nextPoint(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endWay();
        }

        @Override
        protected Void doInBackground(ArrayList<Integer>... list) {
            for (int i = 0; i < list[0].size()*1000; i++) {
                try {
                    if (isCancelled()) return null;
                    int duration = list[0].get(Math.round(i/1000));
                    publishProgress(i);
                    TimeUnit.MILLISECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
