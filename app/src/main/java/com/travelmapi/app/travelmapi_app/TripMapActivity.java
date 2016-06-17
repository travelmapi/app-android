package com.travelmapi.app.travelmapi_app;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class TripMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Trip mTrip;
    private Polyline mLine;

    @BindView(R.id.activity_trip_map_imageview_active)
    ImageView mActive;

    @BindView(R.id.activity_trip_map_textview_name)
    TextView mName;

    @BindView(R.id.activity_trip_map_textview_dates)
    TextView mDates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Realm realm = Realm.getDefaultInstance();
        String tripId = getIntent().getStringExtra(TripDetailActivity.ARG_TRIP_ID);
        mTrip = realm.where(Trip.class).equalTo("id", tripId).findFirst();


        String timestamp = "From: " + new DateHandler(mTrip.getStart()).toShortString() +"\nTo: " + new DateHandler(mTrip.getEnd()).toShortString();
        mDates.setText(timestamp);
        mName.setText(mTrip.getName());

        if(!TripHelper.active(mTrip)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mActive.setBackground(getDrawable(R.drawable.inactive_trip));
            }else{
                mActive.setBackgroundDrawable(getResources().getDrawable(R.drawable.inactive_trip));
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RealmList<TravelStamp> stamps = mTrip.getStamps();
        PolylineOptions polyOpt = new PolylineOptions();

        if(stamps.size() == 0){return;}
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
        final LatLng min = new LatLng(minLat, minLon);
        final LatLng max = new LatLng(maxLat, maxLon);


        double tlat = (minLat+maxLat)/2;
        double tlong = (minLon+maxLon)/2;
        LatLng move = new LatLng(tlat, tlong);
        CameraPosition m = new CameraPosition(move, 5,0,0);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(m));

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = new LatLngBounds(min,max);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
            }
        });

        mLine = mMap.addPolyline(polyOpt);
    }

    @OnClick(R.id.activity_trip_map_button_show_log)
    void showClick(){
        finish();
    }
}
