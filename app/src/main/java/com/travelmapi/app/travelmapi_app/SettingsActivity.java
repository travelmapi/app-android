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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.alarms.AlarmReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    public static final String ARG_USER_ID = "USER_ID";
    public static final String ARG_DEVICE_ID = "DEVICE_ID";
    public static final String ARG_INTERVAL = "INTERVAL";
    public static final String PREFERENCES = "SETTINGS";

    @BindView(R.id.activity_setting_edittext_device_id)
    EditText mDeviceId;

    @BindView(R.id.activity_setting_edittext_user_id)
    EditText mUserId;

    @BindView(R.id.activity_setting_spinner_tracking_speed)
    Spinner mTrackingSpeed;

    @BindView(R.id.button_list_settings)
    Button mSettings;


    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.interval_values,
                R.layout.support_simple_spinner_dropdown_item);
        mTrackingSpeed.setAdapter(spinnerAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSettings.setBackground(getDrawable(R.drawable.bordered_background_active));
            mSettings.setTextColor(Color.WHITE);
        }

    }

    @OnClick(R.id.activity_setting_button_save)
    void saveClick(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        editor.putString(ARG_DEVICE_ID, android_id);

        if(!mUserId.getText().toString().equals("")){
            editor.putString(ARG_USER_ID, mUserId.getText().toString());
        }
        editor.putLong(ARG_INTERVAL, intervalMapper(mTrackingSpeed.getSelectedItem().toString()));
        editor.commit();
        startAlarm();
    }

    /**
     *
     * @param interval the string value from the spinner
     * @return the amount of time in miliseconds
     */
    private long intervalMapper(String interval){
        switch (interval) {
            case "10 s":
                return 10 * 1000;
            case "30 s":
                return 30 * 1000;
            case "1 min":
                return 60 * 1000;
            case "5 min":
                return 5 * 60 * 1000;
            case "10 min":
                return 10 * 60 * 1000;
            case "15 min":
                return 15 * 60 * 1000;
            case "30 min":
                return 30 * 60 * 1000;
            default:
                return 10 * 1000;
        }

    }

    private void startAlarm() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);

        long interval = preferences.getLong(SettingsActivity.ARG_INTERVAL, 15000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, pendingIntent);
        }else{

            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
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
}
