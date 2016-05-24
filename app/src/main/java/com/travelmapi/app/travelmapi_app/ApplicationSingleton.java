package com.travelmapi.app.travelmapi_app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.travelmapi.app.travelmapi_app.exceptions.CrashHandler;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application Configuration.
 */
public class ApplicationSingleton extends Application {
    private static ApplicationSingleton mInstance;
    private RequestQueue mRequestQueue;
    private CrashHandler crashHandler;

    /**
     * creates instances of the Application Singleton and Volley Request Queue
     * Configures and initialized Realm
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        mInstance = this;
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        crashHandler = new CrashHandler(this);
    }

    /**
     *
     * @return returns a static instance of the Applicaiton
     */
    public synchronized static ApplicationSingleton getInstance() {
        return mInstance;
    }

    /**
     *
     * @return returns the Volley Request queue
     */
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
