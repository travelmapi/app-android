package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.travelmapi.app.travelmapi_app.models.Trip;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class TripsViewActivity extends AppCompatActivity implements TripRecyclerViewAdapter.OnTripRowClickListener{

    public static final String ARG_TRIP = "ARG_TRIP";
    TripRecyclerViewAdapter mAdapter;

    @BindView(R.id.activity_trips_view_recycler_view)
    RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        ButterKnife.bind(this);
        mAdapter = new TripRecyclerViewAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.activity_trips_view_button_delete)
    void deleteClick(){
        ArrayList<String> ids = mAdapter.getSelected();
        Realm realm = Realm.getDefaultInstance();

        for(String id:ids) {
            RealmResults<Trip> results = realm.where(Trip.class).equalTo("id", id).findAll();
            realm.beginTransaction();
            if(results.size() > 0) {
                results.remove(0);
            }
            realm.commitTransaction();
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onTripRowClicked(Trip trip) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra(ARG_TRIP, trip.getId());
        startActivity(intent);
    }
}
