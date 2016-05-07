package com.travelmapi.app.travelmapi_app;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travelmapi.app.travelmapi_app.models.TravelStamp;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
        String tStamp = dateFormat.format(stamp.getTimestamp());
        holder.timeStamp.setText(tStamp);
        NumberFormat format = new DecimalFormat("#00.0000");
        holder.lat.setText(format.format(stamp.getLat()));
        holder.lon.setText(format.format(stamp.getLon()));
        holder.id.setText(String.valueOf(stamp.getId()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (stamp.isSync()) {
                holder.fullview.setBackground(holder.fullview.getContext().getDrawable(R.drawable.background_stamp_row_active));

            } else {
                holder.fullview.setBackground(holder.fullview.getContext().getDrawable(R.drawable.background_stamp_row));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mStamps.size();
    }

    class StampViewHolder extends RecyclerView.ViewHolder{
        View fullview;
        TextView timeStamp, lat, lon, id;
        public StampViewHolder(View itemView) {
            super(itemView);
            lat = (TextView) itemView.findViewById(R.id.row_stamp_textview_lat);
            lon = (TextView) itemView.findViewById(R.id.row_stamp_textview_lon);
            timeStamp = (TextView) itemView.findViewById(R.id.row_stamp_textview_time_stamp);
            fullview = itemView;
            id = (TextView) itemView.findViewById(R.id.row_stamp_textview_id);
        }


    }
}
