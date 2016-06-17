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
import android.widget.ImageView;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;

import java.util.Date;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class TripDetailActivity extends AppCompatActivity implements StampRecyclerViewAdapter.StampRowClickListener, EditNameFragment.OnFragmentCompleteListener, EditDateDialogFragment.DialogCompleteListener, DateTimeDialogFragment.OnDialogCompleteListener, RealmChangeListener {

    private static final int FLAG_START = 0;
    private static final int FLAG_END = 1;
    public static final String ARG_TRIP_ID = "TRIP_ID";
    @BindView(R.id.activity_trip_detail_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.activity_trip_detail_button_trip_name)
    Button mName;

    @BindView(R.id.activity_trip_detail_textview_timestamp)
    Button mTimestamp;

    @BindView((R.id.activity_trip_detail_image_active))
    ImageView mActive;


    private RealmList<TravelStamp> tStamps;

    private StampRecyclerViewAdapter mAdapter;

    private Trip mTrip;

    private String mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        mTripId = getIntent().getStringExtra(TripsViewActivity.ARG_TRIP);

        //get all travel stamps
        getStamps();

        Realm realm = Realm.getDefaultInstance();
        realm.addChangeListener(this);

        if(!TripHelper.active(mTrip)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mActive.setBackground(getDrawable(R.drawable.inactive_trip));
            }else{
                mActive.setBackgroundDrawable(getResources().getDrawable(R.drawable.inactive_trip));
            }
        }

        mAdapter = new StampRecyclerViewAdapter(tStamps, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mName.setText(mTrip.getName());
        String start = new DateHandler(mTrip.getStart()).toShortString();
        String end = new DateHandler(mTrip.getEnd()).toShortString();
        String timestamp = "From: " + start +"\nTo: " + end;
        mTimestamp.setText(timestamp);

    }



    @Override
    public void onStampRowClick(TravelStamp stamp) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", stamp.getLat(), stamp.getLon(), stamp.getLat(), stamp.getLon());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }


    @OnClick(R.id.activity_trip_detail_button_trip_name)
    void onNameClick(){
        android.app.FragmentManager manager = getFragmentManager();
        EditNameFragment edit = new EditNameFragment();
        edit.setOnFragmentCompleteListener(this);
        edit.setName(mTrip.getName());
        edit.show(manager, "edit_name_dialog");
    }

    @OnClick(R.id.activity_trip_detail_button_view_map)
    void mapClick(){
        Intent intent = new Intent(this, TripMapActivity.class);
        intent.putExtra(ARG_TRIP_ID, mTrip.getId());
        startActivity(intent);
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

    @Override
    public void onChange() {
        getStamps();
        mAdapter = new StampRecyclerViewAdapter(tStamps, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void getStamps(){
        Realm realm = Realm.getDefaultInstance();
        mTrip = realm.where(Trip.class).equalTo("id", mTripId).findFirst();
        RealmList<TravelStamp> stamps = mTrip.getStamps();
        tStamps = new RealmList<>();
        //reverse order of stamps
        for(int i = stamps.size() -1; i >= 0; i--){
            tStamps.add(stamps.get(i));
        }


    }
}
