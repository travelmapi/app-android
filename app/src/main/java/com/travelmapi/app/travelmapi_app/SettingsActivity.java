package com.travelmapi.app.travelmapi_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.alarms.AlarmReceiver;
import com.travelmapi.app.travelmapi_app.alarms.LogSyncService;
import com.travelmapi.app.travelmapi_app.alarms.SyncReceiver;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsActivity extends AppCompatActivity {
    public static final String ARG_USER_ID = "USER_ID";
    public static final String ARG_DEVICE_ID = "DEVICE_ID";
    public static final String ARG_TRACKER_INTERVAL = "TRACKER_INTERVAL";
    public static final String ARG_UPDATE_INTERVAL = "UPDATE_INTERVAL";
    public static final String PREFERENCES = "SETTINGS";

    @BindView(R.id.activity_setting_edittext_device_id)
    EditText mDeviceId;

    @BindView(R.id.activity_setting_edittext_user_id)
    EditText mUserId;

    @BindView(R.id.activity_setting_spinner_tracking_speed)
    Spinner mTrackingSpeed;

    @BindView(R.id.activity_setting_spinner_update_speed)
    Spinner mUpdateSpeed;

    @BindView(R.id.activity_setting_textview_items_sync)
    TextView mItemsSync;

    @BindView(R.id.button_list_settings)
    Button mSettings;


    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ArrayAdapter<CharSequence> trackingAdapter = ArrayAdapter.createFromResource(this,
                R.array.interval_values,
                R.layout.support_simple_spinner_dropdown_item);
        mTrackingSpeed.setAdapter(trackingAdapter);
        mDeviceId.setText(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));


        ArrayAdapter<CharSequence> updateAdapter = ArrayAdapter.createFromResource(this,
                R.array.update_intervals,
                R.layout.support_simple_spinner_dropdown_item);
        mUpdateSpeed.setAdapter(updateAdapter);

        //show user ID in edit text
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String userId = preferences.getString(ARG_USER_ID, "");
        mUserId.setText(userId);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<TravelStamp> stamps = realm.where(TravelStamp.class).findAll();
        int count = 0;
        for(TravelStamp stamp : stamps){
            if(stamp.isSync()){
                count++;
            }
        }
        mItemsSync.setText(String.format("%d of %d Items are synchronized", count, stamps.size()));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSettings.setBackground(getDrawable(R.drawable.bordered_background_active));
            mSettings.setTextColor(Color.WHITE);
        }

    }

    @OnClick(R.id.activity_setting_button_save)
    void saveClick(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String android_id = mDeviceId.getText().toString();

        editor.putString(ARG_DEVICE_ID, android_id);

        if(!mUserId.getText().toString().equals("")){
            editor.putString(ARG_USER_ID, mUserId.getText().toString());
        }
        editor.putLong(ARG_TRACKER_INTERVAL, trackerIntervalMapper(mTrackingSpeed.getSelectedItem().toString()));
        editor.putLong(ARG_UPDATE_INTERVAL, updateIntervalMapper(mUpdateSpeed.getSelectedItem().toString()));
        editor.apply();
        startAlarm();
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param interval the string value from the spinner
     * @return the amount of time in miliseconds
     */
    private long trackerIntervalMapper(String interval){
        switch (interval) {
            case "10 seconds":
                return 10 * 1000;
            case "30 seconds":
                return 30 * 1000;
            case "1 minute":
                return 60 * 1000;
            case "5 minutes":
                return 5 * 60 * 1000;
            case "10 minutes":
                return 10 * 60 * 1000;
            case "15 minutes":
                return 15 * 60 * 1000;
            case "30 minutes":
                return 30 * 60 * 1000;
            default:
                return 10 * 1000;
        }

    }

    private long updateIntervalMapper(String interval){
        switch (interval) {
            case "30 seconds":
                return 30 * 1000;
            case "1 minute":
                return 60 * 1000;
            case "5 minutes":
                return 5 * 60 * 1000;
            case "10 minutes":
                return 10 * 60 * 1000;
            case "30 minutes":
                return 30 * 60 * 1000;
            case "1 hour":
                return 60 * 60 * 1000;
            case "6 hours":
                return 6 * 60 * 60 * 1000;
            case "12 hours":
                return 12 * 60 * 60 * 1000;
            case "24 hours":
                return 24 * 60 * 60 * 1000;
            default:
                return 6 * 60 * 60 * 1000;
        }

    }

    private void startAlarm() {
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

        Intent syncIntent = new Intent(getApplicationContext(), SyncReceiver.class);
        PendingIntent syncPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, syncIntent, 0);


        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);

        long interval = preferences.getLong(SettingsActivity.ARG_TRACKER_INTERVAL, 15000);
        long syncInterval = preferences.getLong(ARG_UPDATE_INTERVAL, 60 * 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, pendingIntent);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+syncInterval, syncPendingIntent);
        }else{

            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), syncInterval, syncPendingIntent);
        }
    }

    @OnClick(R.id.button_list_travel_list)
    void listClick(){
        Intent intent = new Intent(this, TripsViewActivity.class);
        startActivity(intent);
        finish();

    }

    @OnClick(R.id.button_list_settings)
    void settingsClick(){

    }

    @OnClick(R.id.button_list_start_travel)
    void travelClick(){
        finish();
    }

    @OnClick(R.id.button_list_show_log)
    void logClick(){
    }

    @OnClick(R.id.activity_setting_button_sync)
    void syncClick(){
        startService(new Intent(getApplicationContext(), LogSyncService.class));
    }
}
