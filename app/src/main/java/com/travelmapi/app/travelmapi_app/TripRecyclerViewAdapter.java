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

import java.security.cert.TrustAnchor;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sam on 5/3/16.
 */
public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripRecyclerViewAdapter.TripViewHolder> implements View.OnClickListener {

    private static final String TAG = TripRecyclerViewAdapter.class.getSimpleName();
    RealmResults<Trip> mTrips;
    ArrayList<String> selected;

    public TripRecyclerViewAdapter(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Trip> query = realm.where(Trip.class);
        mTrips =  query.findAll();
        selected = new ArrayList<>();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.row_trip, parent, false);
        return new TripViewHolder(row);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Trip trip = mTrips.get(position);
        holder.title.setText(trip.getName());
        String dates = String.format("from %s to %s",trip.getStart().toString(), trip.getEnd().toString());
        holder.dates.setText(dates);
        holder.check.setChecked(selected.contains(position));
        holder.check.setOnClickListener(this);
        holder.check.setTag(trip);
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
        TextView title, dates;
        CheckBox check;
        public TripViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.row_trip_textview_title);
            dates = (TextView) itemView.findViewById(R.id.row_trip_textview_dates);
            check = (CheckBox) itemView.findViewById(R.id.row_trip_checkbox_delete);
        }
    }

    public ArrayList<String> getSelected(){
        return selected;
    }
}
