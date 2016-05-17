package com.travelmapi.app.travelmapi_app.alarms;

import android.os.AsyncTask;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import com.travelmapi.app.travelmapi_app.models.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by sam on 5/15/16.
 */
public class VolleyResponseTask extends AsyncTask<JSONObject, String , String> {
    @Override

    protected String doInBackground(JSONObject... params) {
        Realm realm = Realm.getDefaultInstance();
        try {
            JSONArray results = params[0].getJSONArray("data");
            for(int i = 0; i< results.length(); i++){
                String id = results.getJSONObject(i).getString("story_id");
                int stampId = results.getJSONObject(i).getInt("stamp_id");
                TravelStamp stamp = realm.where(TravelStamp.class).equalTo("id", stampId).findFirst();
                realm.beginTransaction();
                stamp.setSync(true);
                realm.commitTransaction();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
