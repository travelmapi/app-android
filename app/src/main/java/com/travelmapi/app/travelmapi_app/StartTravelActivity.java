package com.travelmapi.app.travelmapi_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.alarms.AlarmReceiver;
import com.travelmapi.app.travelmapi_app.models.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class StartTravelActivity extends AppCompatActivity  {


    private static final String TAG = StartTravelActivity.class.getSimpleName();
    private static final int PERMISSION_FINE_LOCATION = 0;

    @BindView(R.id.activity_start_travel_edittext_trip_end)
    public EditText mEditEnd;

    @BindView(R.id.activity_start_travel_edittext_trip_start)
    public EditText mEditStart;

    @BindView(R.id.activity_start_travel_edittext_trip_name)
    public EditText mEditName;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_travel);
        ButterKnife.bind(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
        }else{
            startAlarm();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 0:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startAlarm();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }

    private void startAlarm() {
        Intent alarmIntent = new Intent(StartTravelActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(StartTravelActivity.this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(this, 0, updateServiceIntent, 0);
        manager.cancel(pendingUpdateIntent);

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);

        long interval = preferences.getLong(SettingsActivity.ARG_INTERVAL, 15000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, pendingIntent);
        }else{

            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.activity_start_travel_button_save)
    void saveClick(){
        if(mEditEnd.getText().toString().equals("") || mEditStart.getText().toString().equals("") || mEditName.getText().toString().equals("")){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy.kk.mm.ss", Locale.US);

        Date sDate;
        Date eDate;
        try {
            sDate = format.parse(mEditStart.getText().toString());
            eDate = format.parse(mEditEnd.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, sDate.toString());
        Log.d(TAG, eDate.toString());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Trip trip = realm.createObject(Trip.class);
        trip.setName(mEditName.getText().toString());
        trip.setStart(sDate);
        trip.setEnd(eDate);
        trip.setId(UUID.randomUUID().toString());
        realm.commitTransaction();
    }

    @OnClick(R.id.button_list_travel_list)
    void listClick(){
        Intent intent = new Intent(this, TripsViewActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.button_list_settings)
    void settingsClick(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
