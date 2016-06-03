package com.travelmapi.app.travelmapi_app.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.travelmapi.app.travelmapi_app.SettingsActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver{

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String ARG_ALARM_READY = "ALARM_READY";
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences(SettingsActivity.PREFERENCES, Context.MODE_PRIVATE);

        if(!pref.getBoolean(SettingsActivity.ARG_TRACK, true)){
            return;
        }


        context.startService(new Intent(context.getApplicationContext(), AlarmService.class));

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

    }

    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(pendingIntent);
        }
    }
}
