package com.travelmapi.app.travelmapi_app.alarms;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class AlarmService extends Service implements LocationListener {
    private static final String TAG = AlarmService.class.getSimpleName();

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Trigger Alarm");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return START_NOT_STICKY;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper() );
        return START_STICKY;
    }



    public boolean active(Trip trip){
        Date now = new Date();
        if(now.compareTo(trip.getStart()) > 0 && now.compareTo(trip.getEnd()) < 0){
            return true;
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
    Log.d(TAG, "LOCATION CHANGES");
        Log.d(TAG, location.toString());
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Trip> trips = realm.where(Trip.class).findAll();
        for (int i = 0; i< trips.size(); i++) {
            Trip trip = trips.get(i);
            if (active(trip)) {
                if(trip.getStamps().last().getLat() == location.getLatitude() && trip.getStamps().last().getLon() == location.getLongitude()){
                    realm.beginTransaction();
                    trip.getStamps().last().setTimestamp(new Date());
                    realm.commitTransaction();
                }else {
                    Log.d(TAG, trip.getName());
                    realm.beginTransaction();
                    TravelStamp stamp = new TravelStamp();
                    stamp.setLat(location.getLatitude());
                    stamp.setLon(location.getLongitude());
                    stamp.setSync(false);
                    stamp.setTimestamp(new Date());
                    stamp.setSyncDate(null);
                    stamp.setId(trip.getStamps().size()+1);
                    trip.getStamps().add(stamp);
                    realm.commitTransaction();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Status CHANGES");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Provider enabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled");

    }
}
