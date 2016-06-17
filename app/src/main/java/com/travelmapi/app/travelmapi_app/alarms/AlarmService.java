package com.travelmapi.app.travelmapi_app.alarms;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.travelmapi.app.travelmapi_app.ApplicationSingleton;
import com.travelmapi.app.travelmapi_app.DateHandler;
import com.travelmapi.app.travelmapi_app.R;
import com.travelmapi.app.travelmapi_app.SettingsActivity;
import com.travelmapi.app.travelmapi_app.StartTravelActivity;
import com.travelmapi.app.travelmapi_app.exceptions.CrashHandler;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AlarmService extends Service implements LocationListener {
    private static final String TAG = AlarmService.class.getSimpleName();
    private static final double TOLERANCE = .00002;
    private long mTimestamp;
    private String id;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        id = UUID.randomUUID().toString();
        Log.d(TAG, "Trigger Alarm");
        ApplicationSingleton.writeToFile("");
        ApplicationSingleton.writeToFile("Timestamp: " + new DateHandler(new Date()).toShortString());
        ApplicationSingleton.writeToFile("Service Started, ID: " + id);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission();
            return START_NOT_STICKY;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CrashHandler.NOTIFICATION_GPS);

        SharedPreferences pref = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        long interval = pref.getLong(SettingsActivity.ARG_TRACKER_INTERVAL, 10 * 1000);

        //change so that location is updated only when moving.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//                requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
        mTimestamp = System.currentTimeMillis();
        return START_STICKY;
    }


    @Override
    public void onLocationChanged(Location location) {

        /**
         * Things to do
         * Check that accuracy is within threshold. Keep Track of logs and hold on to most accurate.
         */
        long curTime = System.currentTimeMillis();

        Log.d(TAG, "Location Found");
        SharedPreferences pref = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
//        Toast.makeText(getBaseContext(), String.valueOf(elapsedTime), Toast.LENGTH_SHORT).show();

        if (!pref.getBoolean(SettingsActivity.ARG_TRACK, true)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                return;
            }
            locationManager.removeUpdates(this);
        }

        //checks timestamp to see if the log interval has passed
        if(location == null || curTime - mTimestamp <  pref.getLong(SettingsActivity.ARG_TRACKER_INTERVAL, 10 * 1000)){
            return;
        }

        //checks accuracy of location, must be within 5 meters
        //TODO: change this and add setting
        if (location.getAccuracy() > 20) {
            Log.d(TAG, "LOG TOO INACCURATE");
            return;
        }

        mTimestamp = System.currentTimeMillis();
        Log.d(TAG, "Logging Location");
        Log.d(TAG, location.toString());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Trip> trips = realm.where(Trip.class).findAll();

        RealmList<Trip> activeTrips = new RealmList<>();
        for(Trip trip : trips ){
            if(TripHelper.active(trip)){
                activeTrips.add(trip);
            }
        }

        if(activeTrips.size() == 0){
            NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mgr.cancel(CrashHandler.NOTIFICATION_TRIP);

            //cancel logging.
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                return;
            }
            locationManager.removeUpdates(this);
            return;
        }

                /** code for updating redundant stamp
                 * check and see if the last two logged locations are considered the same location
                 * Bug is most likely comming from comment code
                 */

                RealmResults<TravelStamp> stamps = realm.where(TravelStamp.class).findAll();
                if(stamps.size() > 1 &&
                        withinDistance(stamps.last().getLat(),stamps.last().getLon(), stamps.get(stamps.size()-2).getLat(), stamps.get(stamps.size()-2).getLon()) &&
                        withinDistance(location, stamps.last().getLat(),stamps.last().getLon())){

                    //update most recent log
                        realm.beginTransaction();
                        stamps.last().setTimestamp(new Date());
                        realm.commitTransaction();
                    Log.d(TAG, "Updating Log");
                }else {

                    realm.beginTransaction();
                    TravelStamp stamp = realm.createObject(TravelStamp.class);
                    stamp.setLat(location.getLatitude());
                    stamp.setLon(location.getLongitude());
                    stamp.setSync(false);
                    stamp.setTimestamp(new Date());
                    stamp.setSyncDate(null);
                    stamp.setId(realm.where(TravelStamp.class).max("id").intValue() + 1);
                    stamp.setTrips(activeTrips);
                    realm.copyToRealmOrUpdate(stamp);
                    realm.commitTransaction();

                    for (int i = 0; i < activeTrips.size(); i++) {
                        Trip trip = activeTrips.get(i);
                        realm.beginTransaction();
                        trip.getStamps().add(stamp);
                        realm.copyToRealmOrUpdate(trip);
                        realm.commitTransaction();
                    }
                }

        Intent startIntent = new Intent(this, StartTravelActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        startIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //set notification with number of active trips
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(String.format(getString(R.string.num_trips_active),activeTrips.size()))
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent);

        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(CrashHandler.NOTIFICATION_TRIP, mBuilder.build());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(status == LocationProvider.OUT_OF_SERVICE){
            Log.d(TAG, "OUT OF SERVICE");
        }else {
            Log.d(TAG, "Status CHANGES");
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Provider enabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled");
        locationPermission();
    }

    //TODO: Figure out actual acceptable tolerance
    private boolean withinDistance(Location location, double lat, double lon){
        if( Math.abs(location.getLongitude() - lon) < TOLERANCE){
            if(Math.abs(location.getLatitude() - lat) < TOLERANCE){
                return true;
            }
        }
        return false;
    }

    private boolean withinDistance(double lat, double lon, double lat2, double lon2){
        if( Math.abs(lon2 - lon) < TOLERANCE){
            if(Math.abs(lat2 - lat) < TOLERANCE){
                return true;
            }
        }
        return false;
    }

    private void locationPermission(){

        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        gpsOptionsIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Please enable GPS")
                        .setContentIntent(resultPendingIntent);

        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(CrashHandler.NOTIFICATION_GPS, mBuilder.build());
    }
}
