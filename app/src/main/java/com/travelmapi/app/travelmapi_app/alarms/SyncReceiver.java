package com.travelmapi.app.travelmapi_app.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.travelmapi.app.travelmapi_app.SettingsActivity;


public class SyncReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private  PendingIntent pendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences(SettingsActivity.PREFERENCES, Context.MODE_PRIVATE);

        if(!pref.getBoolean(SettingsActivity.ARG_TRACK, true)){
            return;
        }

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        context.startService(new Intent(context.getApplicationContext(), LogSyncService.class));

        Intent alarmIntent = new Intent(context.getApplicationContext(), SyncReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        long interval = pref.getLong(SettingsActivity.ARG_UPDATE_INTERVAL, 60 * 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+interval, pendingIntent);
        }else{
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
    }

    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(pendingIntent);
        }
    }

}
