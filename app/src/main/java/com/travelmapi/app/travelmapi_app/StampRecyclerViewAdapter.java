package com.travelmapi.app.travelmapi_app;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.travelmapi.app.travelmapi_app.models.TravelStamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import io.realm.RealmList;

/**
 * Created by sam on 5/5/16.
 */
public class StampRecyclerViewAdapter extends RecyclerView.Adapter<StampRecyclerViewAdapter.StampViewHolder>{

    private RealmList<TravelStamp> mStamps;
    private StampRowClickListener mListener;
    public StampRecyclerViewAdapter(RealmList<TravelStamp> stamps, StampRowClickListener listener){
      mStamps = stamps;
        mListener = listener;
    }

    public interface StampRowClickListener{
        void onStampRowClick(TravelStamp stamp);
    }


    @Override
    public StampViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.row_stamp, parent, false);
        return new StampViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final StampViewHolder holder, int position) {
        TravelStamp stamp = mStamps.get(position);
        holder.timeStamp.setText( new DateHandler(stamp.getTimestamp()).toShortString());
        NumberFormat format = new DecimalFormat("#00.0000");
        holder.lat.setText(format.format(stamp.getLat()));
        holder.lon.setText(format.format(stamp.getLon()));
        holder.id.setText(String.valueOf(mStamps.size() - position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (stamp.isSync()) {
                holder.fullview.setBackground(holder.fullview.getContext().getDrawable(R.drawable.background_stamp_row_active));

            } else {
                holder.fullview.setBackground(holder.fullview.getContext().getDrawable(R.drawable.background_stamp_row));
            }
        }
        holder.fullview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onStampRowClick(mStamps.get(holder.getAdapterPosition()));
                }
            }
        });
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
