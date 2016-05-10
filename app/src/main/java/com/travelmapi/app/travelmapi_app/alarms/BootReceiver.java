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

            Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            SharedPreferences pref = context.getSharedPreferences(SettingsActivity.PREFERENCES, Context.MODE_PRIVATE);
            long interval = pref.getLong(SettingsActivity.ARG_INTERVAL, 15000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
            }else{
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }
            Log.d(TAG, "Alarm Set");
        }
    }
}