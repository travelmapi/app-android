package com.travelmapi.app.travelmapi_app.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.SettingsActivity;

/**
 * Deals with restarting Stamp Alarm after a reboot
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            //get intents to for Alarm reciever
            Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, alarmIntent, 0);

            //get intents for sync reciever

            Intent syncIntent = new Intent(context.getApplicationContext(), SyncReceiver.class);
            PendingIntent syncPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, syncIntent, 0);

        //get alarm manager to set alarms
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            SharedPreferences pref = context.getSharedPreferences(SettingsActivity.PREFERENCES, Context.MODE_PRIVATE);

            long logInterval = pref.getLong(SettingsActivity.ARG_TRACKER_INTERVAL, 15000);
            long syncInterval = pref.getLong(SettingsActivity.ARG_UPDATE_INTERVAL, 60 * 1000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //set exact alarms for kitkat and above
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+logInterval, pendingIntent);
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+syncInterval, syncPendingIntent);
            }else{
                //set exact alarm for bellow kitkat
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), logInterval, pendingIntent);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), syncInterval, syncPendingIntent);
            }
            Log.d(TAG, "Alarm Set");
        }
    }
}