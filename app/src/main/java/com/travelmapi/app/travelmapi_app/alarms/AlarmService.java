package com.travelmapi.app.travelmapi_app.alarms;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.travelmapi.app.travelmapi_app.R;
import com.travelmapi.app.travelmapi_app.StartTravelActivity;
import com.travelmapi.app.travelmapi_app.exceptions.CrashHandler;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AlarmService extends Service implements LocationListener {
    private static final String TAG = AlarmService.class.getSimpleName();
    private static final double TOLERANCE = .0002;
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
            locationPermission();
            return START_NOT_STICKY;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CrashHandler.NOTIFICATION_GPS);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            return;
        }
        Log.d(TAG, "LOCATION CHANGES");
        Log.d(TAG, location.toString());
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Trip> trips = realm.where(Trip.class).findAll();

        int count = 0;
        for (int i = 0; i< trips.size(); i++) {
            Trip trip = trips.get(i);
            if (TripHelper.active(trip)) {
                count++;
                RealmList<TravelStamp> stamps = trip.getStamps();


                /**
                 * check and see if the last two logged locations are considered the same location
                 * Bug is most likely comming from comment code
                 */
//                if(stamps.size() > 1 &&
//                        withinDistance(stamps.last().getLat(),stamps.last().getLon(), stamps.get(stamps.size()-2).getLat(), stamps.get(stamps.size()-2).getLon()) &&
//                        withinDistance(location, stamps.last().getLat(),stamps.last().getLon())){
//
//                    //update most recent log
//                        realm.beginTransaction();
//                        trip.getStamps().last().setTimestamp(new Date());
//                        realm.commitTransaction();
//                }else {
                    Log.d(TAG, trip.getName());
                    realm.beginTransaction();
                    TravelStamp stamp = new TravelStamp();
                    stamp.setLat(location.getLatitude());
                    stamp.setLon(location.getLongitude());
                    stamp.setSync(false);
                    stamp.setTimestamp(new Date());
                    stamp.setSyncDate(null);
                    stamp.setId(trip.getStamps().size()+1);
                    stamp.setTrip(trip);
                    trip.getStamps().add(stamp);
                    realm.commitTransaction();
//                }
            }
        }

        if(count == 0){
            /**
             * Probably should cancel alarm service
             * should restart alarm when new trip created
             */
            NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mgr.cancel(CrashHandler.NOTIFICATION_TRIP);
            return;
        }

        Intent gpsOptionsIntent = new Intent(getApplicationContext() ,StartTravelActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        gpsOptionsIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //set notification with number of active trips
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(String.format(getString(R.string.num_trips_active),count))
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent);

        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(CrashHandler.NOTIFICATION_TRIP, mBuilder.build());


        //shows current location for debug purposes
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.launcher)
                        .setContentTitle("Current Location")
                        .setContentText(String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()))
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent);

        mgr.notify(0, builder.build());

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
