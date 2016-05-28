package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class TripsViewActivity extends AppCompatActivity implements TripRecyclerViewAdapter.OnTripRowClickListener{

    public static final String ARG_TRIP = "ARG_TRIP";
    TripRecyclerViewAdapter mAdapter;

    @BindView(R.id.activity_trips_view_recycler_view)
    RecyclerView mRecycler;

    @BindView(R.id.button_list_travel_list)
    Button mTravel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_view);
        ButterKnife.bind(this);
        mAdapter = new TripRecyclerViewAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setLayoutManager(new LinearLayoutManager(this));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTravel.setBackground(getDrawable(R.drawable.bordered_background_active));
            mTravel.setTextColor(Color.WHITE);
        }
    }

    @OnClick(R.id.activity_trips_view_button_delete)
    void deleteClick(){
        ArrayList<String> ids = mAdapter.getSelected();
        Realm realm = Realm.getDefaultInstance();

        for(String id:ids) {
            Trip trip = realm.where(Trip.class).equalTo("id", id).findFirst();

            //Change to handle realm list of trips
            //TODO: this is deleting timestamp no matter what.
            //stamp.getTrips() == null
            if(trip.isValid()) {
                RealmList<TravelStamp> stamps = realm.where(Trip.class).findFirst().getStamps();
                for(int i = 0; i< stamps.size(); i++){
                    realm.beginTransaction();
                    TravelStamp stamp = stamps.get(i);
                    if(stamp.getTrips().size() > 1){
                        stamp.getTrips().remove(trip);
                    }else{
                        stamp.removeFromRealm();
                    }
                    realm.commitTransaction();
                }
                realm.beginTransaction();
                trip.removeFromRealm();
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
    @OnClick(R.id.button_list_travel_list)
    void listClick(){
    }

    @OnClick(R.id.button_list_settings)
    void settingsClick(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_list_start_travel)
    void travelClick(){
        finish();
    }

    @OnClick(R.id.button_list_show_log)
    void logClick(){

    }
}
