package com.travelmapi.app.travelmapi_app;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmapi.app.travelmapi_app.models.Trip;
import com.travelmapi.app.travelmapi_app.models.TripHelper;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sam on 5/3/16.
 */
public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripRecyclerViewAdapter.TripViewHolder> implements View.OnClickListener {

    public interface OnTripRowClickListener{
        void onTripRowClicked(Trip trip);
    }


    private static final String TAG = TripRecyclerViewAdapter.class.getSimpleName();
    RealmResults<Trip> mTrips;
    ArrayList<String> selected;
    OnTripRowClickListener mListener;
    public TripRecyclerViewAdapter(OnTripRowClickListener listener){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Trip> query = realm.where(Trip.class);
        mTrips =  query.findAll();
        selected = new ArrayList<>();
        this.mListener = listener;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.row_trip, parent, false);
        return new TripViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, int position) {
        Trip trip = mTrips.get(position);
        holder.title.setText(trip.getName());
        String dates = String.format("From: %s\nTo: %s",new DateHandler(trip.getStart()).toString(), new DateHandler(trip.getEnd()).toString());
        holder.dates.setText(dates);
        holder.check.setChecked(selected.contains(position));
        holder.check.setOnClickListener(this);
        holder.check.setTag(trip);
        if(TripHelper.active(trip)){
            holder.active.setText(R.string.active);
        }else{
            holder.active.setText(R.string.inactive);
        }
        String logs = String.format("%d Logs",trip.getStamps().size());
        holder.logs.setText(logs);
        holder.fullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onTripRowClicked(mTrips.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    @Override
    public void onClick(View v) {
        CheckBox cb = (CheckBox) v;
        Trip trip = (Trip) cb.getTag();
        if (cb.isChecked()){
            selected.add(trip.getId());
        }else{
            selected.remove(trip.getId());
        }
        Log.d(TAG, selected.toString());
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView title, dates, active, logs;
        CheckBox check;
        View fullView;
        public TripViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.row_trip_textview_title);
            dates = (TextView) itemView.findViewById(R.id.row_trip_textview_dates);
            check = (CheckBox) itemView.findViewById(R.id.row_trip_checkbox_delete);
            active = (TextView) itemView.findViewById(R.id.row_trip_textview_active);
            logs = (TextView) itemView.findViewById(R.id.row_trip_textview_logs);
            fullView = itemView;
        }
    }

    public ArrayList<String> getSelected(){
        return selected;
    }
}
