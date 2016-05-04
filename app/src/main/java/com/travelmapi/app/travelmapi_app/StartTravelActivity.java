package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.models.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class StartTravelActivity extends AppCompatActivity  {


    private static final String TAG = StartTravelActivity.class.getSimpleName();

    @BindView(R.id.activity_start_travel_edittext_trip_end)
    public EditText mEditEnd;

    @BindView(R.id.activity_start_travel_edittext_trip_start)
    public EditText mEditStart;

    @BindView(R.id.activity_start_travel_edittext_trip_name)
    public EditText mEditName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_travel);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_start_travel_button_save)
    void saveClick(){
        if(mEditEnd.getText().toString().equals("") || mEditStart.getText().toString().equals("") || mEditName.getText().toString().equals("")){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy.kk.mm.ss", Locale.US);

        Date sDate;
        Date eDate;
        try {
            sDate = format.parse(mEditStart.getText().toString());
            eDate = format.parse(mEditEnd.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, sDate.toString());
        Log.d(TAG, eDate.toString());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Trip trip = realm.createObject(Trip.class);
        trip.setName(mEditName.getText().toString());
        trip.setStart(sDate);
        trip.setEnd(eDate);
        trip.setId(UUID.randomUUID().toString());
        realm.commitTransaction();
    }

    @OnClick(R.id.activity_start_travel_button_list)
    void listClick(){
        Intent intent = new Intent(this, TripsViewActivity.class);
        startActivity(intent);

    }

}
