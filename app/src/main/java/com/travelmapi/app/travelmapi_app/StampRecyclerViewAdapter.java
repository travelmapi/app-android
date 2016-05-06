package com.travelmapi.app.travelmapi_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by sam on 5/5/16.
 */
public class StampRecyclerViewAdapter extends RecyclerView.Adapter<StampRecyclerViewAdapter.StampViewHolder> {

    private RealmList<TravelStamp> mStamps;
    public StampRecyclerViewAdapter(RealmList<TravelStamp> stamps){
      mStamps = stamps;
    }


    @Override
    public StampViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.row_stamp, parent, false);
        return new StampViewHolder(row);
    }

    @Override
    public void onBindViewHolder(StampViewHolder holder, int position) {
        TravelStamp stamp = mStamps.get(position);
        holder.timeStamp.setText(stamp.getTimestamp().toString());
        holder.lat.setText(String.valueOf(stamp.getLat()));
        holder.lon.setText(String.valueOf(stamp.getLon()));
    }

    @Override
    public int getItemCount() {
        return mStamps.size();
    }

    class StampViewHolder extends RecyclerView.ViewHolder{
        View fullview;
        TextView timeStamp, lat, lon;
        public StampViewHolder(View itemView) {
            super(itemView);
            lat = (TextView) itemView.findViewById(R.id.row_stamp_textview_lat);
            lon = (TextView) itemView.findViewById(R.id.row_stamp_textview_lon);
            timeStamp = (TextView) itemView.findViewById(R.id.row_stamp_textview_time_stamp);
            fullview = itemView;
        }


    }
}
