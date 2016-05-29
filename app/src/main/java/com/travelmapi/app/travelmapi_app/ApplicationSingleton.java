package com.travelmapi.app.travelmapi_app;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.travelmapi.app.travelmapi_app.exceptions.CrashHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application Configuration.
 */
public class ApplicationSingleton extends Application {
    private static ApplicationSingleton mInstance;
    private RequestQueue mRequestQueue;
    private CrashHandler crashHandler;
    public static BufferedWriter out;
    private static File logFile;

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
        createFileOnDevice(true);
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


    public static void createFileOnDevice(Boolean append) {
                /*
                 * Function to initially create the log file and it also writes the time of creation to file.
                 */
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canWrite()){
              logFile = new File(Root, "Log.txt");
            try  {
                FileWriter fileWriter = new FileWriter(logFile, true);
                out = new BufferedWriter(fileWriter);
                out.write("LOGS FOR TRAVELMAPI \n");
                out.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToFile(String message) {
        if(out == null){
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            out = new BufferedWriter(fileWriter);
            out.write(message + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
