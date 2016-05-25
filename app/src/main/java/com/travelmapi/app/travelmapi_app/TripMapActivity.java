package com.travelmapi.app.travelmapi_app;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import io.realm.Realm;
import io.realm.RealmList;

public class TripMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Trip mTrip;
    private Polyline mLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Realm realm = Realm.getDefaultInstance();
        String tripId = getIntent().getStringExtra(TripDetailActivity.ARG_TRIP_ID);
        mTrip = realm.where(Trip.class).equalTo("id", tripId).findFirst();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RealmList<TravelStamp> stamps = mTrip.getStamps();
        PolylineOptions polyOpt = new PolylineOptions();

        double minLat = stamps.first().getLat();
        double maxLat = stamps.first().getLat();
        double minLon = stamps.first().getLon();
        double maxLon = stamps.first().getLon();

        for(TravelStamp stamp : stamps){
            if(minLat > stamp.getLat()){
                minLat = stamp.getLat();
            }else if(maxLat < stamp.getLat()){
                maxLat = stamp.getLat();
            }

            if(minLon > stamp.getLon()){
                minLon = stamp.getLon();
            }else if(maxLon < stamp.getLon()){
                maxLon = stamp.getLon();
            }
            LatLng loc = new LatLng(stamp.getLat(), stamp.getLon());
            polyOpt.add(loc);
        }

        polyOpt.color(Color.BLUE);
        LatLng min = new LatLng(minLat, minLon);
        LatLng max = new LatLng(maxLat, maxLon);
        LatLngBounds bounds = new LatLngBounds(min, max);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        mLine = mMap.addPolyline(polyOpt);


    }
}
