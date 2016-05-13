package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;
import io.realm.RealmList;

public class TripDetailActivity extends AppCompatActivity implements StampRecyclerViewAdapter.StampRowClickListener, EditNameFragment.OnFragmentCompleteListener, EditDateDialogFragment.DialogCompleteListener, DateTimeDialogFragment.OnDialogCompleteListener {

    private static final int FLAG_START = 0;
    private static final int FLAG_END = 1;
    @BindView(R.id.activity_trip_detail_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.activity_trip_detail_button_trip_name)
    Button mName;

    @BindView(R.id.activity_trip_detail_textview_timestamp)
    Button mTimestamp;

    @BindView(R.id.button_list_show_log)
    Button mLog;

    @BindView(R.id.activity_trip_detail_textview_logs)
    TextView mNumLogs;

    @BindView(R.id.activity_trip_detail_textview_active)
    TextView mActive;

    StampRecyclerViewAdapter mAdapter;
    Trip mTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        String tripId = getIntent().getStringExtra(TripsViewActivity.ARG_TRIP);

        //get all travel stamps
        Realm realm = Realm.getDefaultInstance();
        mTrip = realm.where(Trip.class).equalTo("id", tripId).findFirst();
        RealmList<TravelStamp> stamps = mTrip.getStamps();
        RealmList<TravelStamp> tStamps = new RealmList<>();
        //reverse order of stamps
        for(int i = stamps.size() -1; i >= 0; i--){
            tStamps.add(stamps.get(i));
        }

        if(TripHelper.active(mTrip)){
            mActive.setText(R.string.active);
        }else{
            mActive.setText(R.string.inactive);
        }

        mAdapter = new StampRecyclerViewAdapter(tStamps, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mName.setText(mTrip.getName());
        mNumLogs.setText(String.format(getString(R.string.num_logs), mTrip.getStamps().size()));
        String start = new DateHandler(mTrip.getStart()).toString();
        String end = new DateHandler(mTrip.getEnd()).toString();
        String timestamp = "From: " + start +"\nTo: " + end;
        mTimestamp.setText(timestamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLog.setBackground(getDrawable(R.drawable.bordered_background_active));
            mLog.setTextColor(Color.WHITE);
        }

    }

    @Override
    public void onStampRowClick(TravelStamp stamp) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", stamp.getLat(), stamp.getLon(), stamp.getLat(), stamp.getLon());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @OnClick(R.id.button_list_travel_list)
    void listClick(){
        finish();

    }

    @OnClick(R.id.button_list_settings)
    void settingsClick(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.activity_trip_detail_button_trip_name)
    void onNameClick(){
        android.app.FragmentManager manager = getFragmentManager();
        EditNameFragment edit = new EditNameFragment();
        edit.setOnFragmentCompleteListener(this);
        edit.setName(mTrip.getName());
        edit.show(manager, "edit_name_dialog");
    }

    @OnClick(R.id.button_list_start_travel)
    void travelClick(){
        finish();
    }


    @Override
    public void fragmentComplete(String name) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Trip trip = realm.where(Trip.class).equalTo("id", mTrip.getId()).findFirst();
        trip.setName(name);
        realm.commitTransaction();
        mName.setText(name);
    }

    @OnClick(R.id.activity_trip_detail_textview_timestamp)
    void timestampClick(){
        android.app.FragmentManager manager = getFragmentManager();
        EditDateDialogFragment fragment = new EditDateDialogFragment();
        fragment.setOnDialogCompleteListener(this);
        fragment.show(manager, "edit_date_fragment");
    }

    @Override
    public void onDialogComplete(int flag) {
        if(flag == EditDateDialogFragment.FLAG_START){
            android.app.FragmentManager manager = getFragmentManager();
            DateTimeDialogFragment dialog = new DateTimeDialogFragment();
            dialog.setOnDateTimeSetListener(this);
            dialog.setFlag(FLAG_START);
            dialog.setDate(mTrip.getStart());
            dialog.show(manager, "date_time_dialog_fragment");    
        }else{

            android.app.FragmentManager manager = getFragmentManager();
            DateTimeDialogFragment dialog = new DateTimeDialogFragment();
            dialog.setOnDateTimeSetListener(this);
            dialog.setFlag(FLAG_END);
            dialog.setDate(mTrip.getEnd());
            dialog.show(manager, "date_time_dialog_fragment");
        }
    }

    @Override
    public void dialogComplete(Date date, int flag) {
        if(date == null){return;}
        if(flag == FLAG_START){
            Realm realm = Realm.getDefaultInstance();
            Trip trip = realm.where(Trip.class).equalTo("id", mTrip.getId()).findFirst();
            realm.beginTransaction();
            trip.setStart(date);
            realm.commitTransaction();

            String start = new DateHandler(date).toString();
            String end = new DateHandler(mTrip.getEnd()).toString();
            String timestamp = "From: " + start +"\nTo: " + end;
            mTimestamp.setText(timestamp);

        }else {
            Realm realm = Realm.getDefaultInstance();
            Trip trip = realm.where(Trip.class).equalTo("id", mTrip.getId()).findFirst();
            realm.beginTransaction();
            trip.setEnd(date);
            realm.commitTransaction();
            String start = new DateHandler(mTrip.getStart()).toString();
            String end = new DateHandler(date).toString();
            String timestamp = "From: " + start +"\nTo: " + end;
            mTimestamp.setText(timestamp);
        }
    }
}
