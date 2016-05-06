package com.travelmapi.app.travelmapi_app.alarms;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.travelmapi.app.travelmapi_app.models.Trip;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sam on 5/4/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        context.startService(new Intent(context, AlarmService.class));
    }

    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
