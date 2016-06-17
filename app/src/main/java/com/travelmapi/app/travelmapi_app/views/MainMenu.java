package com.travelmapi.app.travelmapi_app.views;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.travelmapi.app.travelmapi_app.R;
import com.travelmapi.app.travelmapi_app.SettingsActivity;
import com.travelmapi.app.travelmapi_app.TripsViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sam on 6/6/16.
 */
public class MainMenu extends LinearLayout{

    public static final String TAG = MainMenu.class.getSimpleName();
    Button mIcon, mSettings, mTrips;
    RelativeLayout layout;
    boolean open;

    public MainMenu(Context context) {
        super(context);
    }

    public MainMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public MainMenu(final Context context, AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this);
        init(context);


    }

    private void init(final Context context){
        inflate(context, R.layout.main_menu, this);
        this.mIcon = (Button) findViewById(R.id.main_menu_show);
        this.mSettings = (Button) findViewById(R.id.main_menu_settings);
        this.mTrips = (Button) findViewById(R.id.main_menu_trip_list);

        mSettings.setVisibility(GONE);
        mTrips.setVisibility(GONE);
        open = false;

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus(FOCUSABLES_ALL);

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "FOCUS CHANGE");
                if(!hasFocus){
                    mSettings.setVisibility(GONE);
                    mTrips.setVisibility(GONE);
                }
            }
        });
        mIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!open) {
                    mSettings.setVisibility(VISIBLE);
                    mTrips.setVisibility(VISIBLE);
                    open = true;
                }else{
                    mSettings.setVisibility(GONE);
                    mTrips.setVisibility(GONE);
                    open = false;
                }
            }
        });

        mSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
            }
        });

        mTrips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TripsViewActivity.class);
                context.startActivity(intent);
            }
        });
    }


    public void toFront(){
        mIcon.bringToFront();
        mSettings.bringToFront();
        mTrips.bringToFront();
    }
}
