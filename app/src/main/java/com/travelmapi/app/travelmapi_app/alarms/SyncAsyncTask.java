package com.travelmapi.app.travelmapi_app.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by sam on 6/10/16.
 */
public class SyncAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public SyncAsyncTask(Context context){
        mContext = context;
    }
    @Override
    protected Void doInBackground(Void... params) {

        mContext.startService(new Intent(mContext.getApplicationContext(), LogSyncService.class));
        return null;
    }
}
