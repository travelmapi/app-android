package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;
import io.realm.RealmList;

public class TripDetailActivity extends AppCompatActivity implements StampRecyclerViewAdapter.StampRowClickListener, EditNameFragment.OnFragmentCompleteListener{

    @BindView(R.id.activity_trip_detail_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.activity_trip_detail_button_trip_name)
    Button mName;

    @BindView(R.id.activity_trip_detail_textview_timestamp)
    TextView mTimestamp;

    @BindView(R.id.button_list_show_log)
    Button mLog;

    StampRecyclerViewAdapter mAdapter;
    Trip mTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        String tripId = getIntent().getStringExtra(TripsViewActivity.ARG_TRIP);
        Realm realm = Realm.getDefaultInstance();
        mTrip = realm.where(Trip.class).equalTo("id", tripId).findFirst();
        RealmList<TravelStamp> stamps = mTrip.getStamps();
        mAdapter = new StampRecyclerViewAdapter(stamps, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mName.setText(mTrip.getName());
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
        String start = format.format(mTrip.getStart());
        String end = format.format(mTrip.getEnd());
        String timestamp = "From " + start +" to " + end;
        mTimestamp.setText(timestamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLog.setBackground(getDrawable(R.drawable.bordered_background_active));
            mLog.setTextColor(Color.WHITE);
        }

    }

    @Override
    public void onStampRowClick(TravelStamp stamp) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", stamp.getLat(), stamp.getLon());
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
}
