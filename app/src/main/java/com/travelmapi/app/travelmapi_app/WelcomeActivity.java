package com.travelmapi.app.travelmapi_app;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {
    public final static int LOAD_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        //show welcome for LOAD_TIME seconds then start StartTravelActivity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, TripsViewActivity.class);
                startActivity(intent);
                finish();
            }
        }, LOAD_TIME);
    }
}
