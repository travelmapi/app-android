package com.travelmapi.app.travelmapi_app;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class TripDetailActivity extends AppCompatActivity {

    @BindView(R.id.activity_trip_detail_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.activity_trip_detail_textview_trip_name)
    TextView mName;

    @BindView(R.id.activity_trip_detail_textview_timestamp)
    TextView mTimestamp;

    @BindView(R.id.button_list_show_log)
    Button mLog;

    StampRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        String tripId = getIntent().getStringExtra(TripsViewActivity.ARG_TRIP);
        Realm realm = Realm.getDefaultInstance();
        Trip trip = realm.where(Trip.class).equalTo("id", tripId).findFirst();
        RealmList<TravelStamp> stamps = trip.getStamps();
        mAdapter = new StampRecyclerViewAdapter(stamps);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mName.setText(trip.getName());
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
        String start = format.format(trip.getStart());
        String end = format.format(trip.getEnd());
        String timestamp = "From " + start +" to " + end;
        mTimestamp.setText(timestamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mLog.setBackground(getDrawable(R.drawable.bordered_background_active));
            mLog.setTextColor(Color.WHITE);
        }

    }
}
