package com.travelmapi.app.travelmapi_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class TripDetailActivity extends AppCompatActivity {

    @BindView(R.id.activity_trip_detail_recycler_view)
    RecyclerView mRecyclerView;
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
    }
}
