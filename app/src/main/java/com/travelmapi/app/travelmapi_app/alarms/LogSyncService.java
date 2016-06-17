package com.travelmapi.app.travelmapi_app.alarms;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.travelmapi.app.travelmapi_app.ApplicationSingleton;
import com.travelmapi.app.travelmapi_app.DateHandler;
import com.travelmapi.app.travelmapi_app.SettingsActivity;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class LogSyncService extends Service implements Response.ErrorListener, Response.Listener<JSONArray> {

    public static final String URL = "http://app.travelmapi.com?controller=stamp&action=upload";
    public static final String DEBUG_URL = "http://10.0.2.2?controller=stamp&action=upload";
    private static final String TAG = LogSyncService.class.getSimpleName();
    public static final int TIMEOUT_INTERVAL = 15000;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Syncing to Server");

        if(!isOnline()){
            return START_NOT_STICKY;
        }


        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Trip> trips = realm.where(Trip.class). findAll();
        JSONArray json = new JSONArray();
        String userId = preferences.getString(SettingsActivity.ARG_USER_ID, "");
        String deviceId = preferences.getString(SettingsActivity.ARG_DEVICE_ID, "");
        int count = 0;
        for(Trip trip : trips) {
            for (TravelStamp stamp : trip.getStamps()) {
                if(!stamp.isSync() && count < 50){
                    JSONObject stampJSON = stampToJson(trip,userId, deviceId, stamp );
                    json.put(stampJSON);
                    count++;
                }
            }
        }
        if(count == 0){
            Log.d(TAG, "All Logs Synced");
            Toast.makeText(this, "TravelMapi Sync Complete", Toast.LENGTH_SHORT).show();
            return START_NOT_STICKY;
        }

        Log.d(TAG, json.toString());


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, URL, json, this, this);


        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_INTERVAL,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationSingleton.getInstance().getRequestQueue().add(request);
        return START_STICKY;
    }




    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(TAG, "FAILED");
        Log.d(TAG, error.toString());
    }

    @Override
    public void onResponse(JSONArray response) {
        Log.d(TAG, "SUCESS");
        Log.d(TAG, response.toString());
        Realm realm = Realm.getDefaultInstance();
        try {
            JSONArray results = response;
            for(int i = 0; i < results.length(); i++){
                JSONObject resStamp = results.getJSONObject(i);
                int stampId = resStamp.getInt("stamp_id");
                realm.beginTransaction();
                TravelStamp stamp = realm.where(TravelStamp.class).equalTo("id", stampId).findFirst();
                stamp.setSync(true);
                realm.commitTransaction();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        onStartCommand(null, 0,0);
    }


    public JSONObject stampToJson(Trip trip, String userId, String deviceId, TravelStamp stamp){
        JSONObject json = new JSONObject();
        try {
            json.put("stamp_id", stamp.getId());
            json.put("trip_id", trip.getId());
            json.put("user_id", userId);
            json.put("device_id",deviceId);
            json.put("timestamp", new DateHandler(stamp.getTimestamp()).toShortString());
            json.put("lat", stamp.getLat());
            json.put("long", stamp.getLon());
            json.put("trip_name", trip.getName());
            json.put("start_date", new DateHandler(trip.getStart()).toShortString());
            json.put("end_date", new DateHandler(trip.getEnd()).toShortString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }
}
