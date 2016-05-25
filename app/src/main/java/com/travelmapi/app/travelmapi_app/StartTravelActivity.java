package com.travelmapi.app.travelmapi_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.travelmapi.app.travelmapi_app.alarms.AlarmReceiver;
import com.travelmapi.app.travelmapi_app.models.Trip;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class StartTravelActivity extends AppCompatActivity implements DateTimeDialogFragment.OnDialogCompleteListener {


    private static final String TAG = StartTravelActivity.class.getSimpleName();
    private static final int PERMISSION_FINE_LOCATION = 0;
    public static final int FLAG_START = 0;
    public static final int FLAG_END = 1;

    @BindView(R.id.activity_start_travel_edittext_trip_end)
    public Button mEditEnd;

    @BindView(R.id.activity_start_travel_edittext_trip_start)
    public Button mEditStart;

    @BindView(R.id.activity_start_travel_edittext_trip_name)
    public EditText mEditName;

    @BindView(R.id.button_list_start_travel)
    Button mTravel;

    private PendingIntent pendingIntent;
    private Date mStartDate, mEndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_travel);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTravel.setBackground(getDrawable(R.drawable.bordered_background_active));
            mTravel.setTextColor(Color.WHITE);
        }

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

        long interval = preferences.getLong(SettingsActivity.ARG_TRACKER_INTERVAL, 15000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, pendingIntent);
        }else{

            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
    }

    /**
     *
     * TODO Make sure there are no duplicate trips
     */
    @OnClick(R.id.activity_start_travel_button_save)
    void saveClick(){
        if(mEndDate == null || mStartDate == null || mEditName.getText().toString().equals("")){
            Toast.makeText(this, R.string.enter_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if(mEndDate.compareTo(mStartDate) < 0){
            Toast.makeText(this, R.string.start_date_before_end, Toast.LENGTH_SHORT).show();
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Trip> results = realm.where(Trip.class).equalTo("start", mStartDate).equalTo("end", mEndDate).findAll();
        if(results.size() > 0){
          Toast.makeText(this, R.string.trip_exists, Toast.LENGTH_SHORT).show();
            return;
        }
        realm.beginTransaction();
        Trip trip = realm.createObject(Trip.class);
        trip.setName(mEditName.getText().toString());
        trip.setStart(mStartDate);
        trip.setEnd(mEndDate);


        trip.setId(UUID.randomUUID().toString());
        realm.commitTransaction();

        mEditEnd.setText("");
        mEditName.setText("");
        mEditStart.setText("");
        mStartDate = null;
        mEndDate = null;
        Toast.makeText(this, R.string.trip_created, Toast.LENGTH_SHORT).show();

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

    @OnClick(R.id.button_list_start_travel)
    void travelClick(){

    }

    @OnClick(R.id.button_list_show_log)
    void logClick(){

    }

    @OnClick(R.id.activity_start_travel_edittext_trip_start)
    void startClick(){
        android.app.FragmentManager manager = getFragmentManager();
        DateTimeDialogFragment dialog = new DateTimeDialogFragment();
        dialog.setOnDateTimeSetListener(this);
        dialog.setFlag(FLAG_START);
        dialog.setDate(mStartDate);
        dialog.show(manager, "date_time_dialog_fragment");
    }
    @OnClick(R.id.activity_start_travel_edittext_trip_end)
    void endClick(){
        android.app.FragmentManager manager = getFragmentManager();
        DateTimeDialogFragment dialog = new DateTimeDialogFragment();
        dialog.setOnDateTimeSetListener(this);
        dialog.setFlag(FLAG_END);
        dialog.setDate(mEndDate);
        dialog.show(manager, "date_time_dialog_fragment");
    }
    @Override
    public void dialogComplete(Date date, int flag) {
        switch (flag){
            case FLAG_START :
                mEditStart.setText(new DateHandler(date).toString());
                mStartDate = date;
                break;
            case FLAG_END :
                mEditEnd.setText(new DateHandler(date).toString());
                mEndDate = date;
                break;
        }
    }
}
