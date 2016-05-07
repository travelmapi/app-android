package com.travelmapi.app.travelmapi_app.alarms;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.travelmapi.app.travelmapi_app.SettingsActivity;
import com.travelmapi.app.travelmapi_app.StartTravelActivity;
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
        context.startService(new Intent(context.getApplicationContext(), AlarmService.class));

        Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences pref = context.getSharedPreferences(SettingsActivity.PREFERENCES, Context.MODE_PRIVATE);
        long interval = pref.getLong(SettingsActivity.ARG_INTERVAL, 15000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+interval, pendingIntent);
        }else{
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }

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
