package com.travelmapi.app.travelmapi_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.travelmapi.app.travelmapi_app.alarms.AlarmReceiver;
import com.travelmapi.app.travelmapi_app.alarms.SyncReceiver;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class TripsViewActivity extends AppCompatActivity implements TripRecyclerViewAdapter.OnTripRowClickListener{

    public static final String ARG_TRIP = "ARG_TRIP";
    private static final int PERMISSION_FINE_LOCATION = 0;
    private TripRecyclerViewAdapter mAdapter;

    private PendingIntent pendingIntent;

    @BindView(R.id.activity_trips_view_recycler_view)
    RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        ButterKnife.bind(this);


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
        }else{
            startAlarm();
        }

        //remove when removing logging to file
        if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_FINE_LOCATION);
            ApplicationSingleton.createFileOnDevice(true);
        }

        mAdapter = new TripRecyclerViewAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
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
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

        Intent syncIntent = new Intent(getApplicationContext(), SyncReceiver.class);
        PendingIntent syncPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, syncIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);

        long syncInterval = preferences.getLong(SettingsActivity.ARG_UPDATE_INTERVAL, 60 * 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + syncInterval, syncPendingIntent);
        }else{
            manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingIntent );
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), syncInterval, syncPendingIntent);
        }
    }


    @OnClick(R.id.activity_trips_view_button_delete)
    void deleteClick(){
        ArrayList<String> ids = mAdapter.getSelected();
        Realm realm = Realm.getDefaultInstance();

        for(String id:ids) {
            Trip trip = realm.where(Trip.class).equalTo("id", id).findFirst();

            if(trip != null) {
                RealmList<TravelStamp> stamps = realm.where(Trip.class).equalTo("id", trip.getId()).findFirst().getStamps();

                List<TravelStamp> stampsToDelete = new ArrayList<>();
                for (TravelStamp stamp : stamps) {
                    if (realm.where(Trip.class).equalTo("stamps.id", stamp.getId()).count() == 1) {
                        stampsToDelete.add(stamp);
                    }
                }
                for (TravelStamp stamp : stampsToDelete) {
                    realm.beginTransaction();
                    stamp.removeFromRealm();
                    realm.commitTransaction();
                }
                realm.beginTransaction();
                trip.removeFromRealm(); // delete this object
                realm.commitTransaction();
            }
            mAdapter.notifyDataSetChanged();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onTripRowClicked(Trip trip) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra(ARG_TRIP, trip.getId());
        startActivity(intent);
    }

    @OnClick(R.id.activity_trips_view_button_add)
    void addClick(){
        Intent intent = new Intent(this, StartTravelActivity.class);
        startActivity(intent);
    }
}
