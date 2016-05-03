package com.travelmapi.app.travelmapi_app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sam on 5/3/16.
 */
public class ApplicationSingleton extends Application {
    private static ApplicationSingleton mInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        mInstance = this;

    }

    public synchronized static ApplicationSingleton getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
