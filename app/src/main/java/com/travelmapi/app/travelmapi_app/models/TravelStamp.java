package com.travelmapi.app.travelmapi_app.models;

import java.util.Date;

import io.realm.RealmObject;

public class TravelStamp extends RealmObject {

    private int id;
    private Date timestamp;
    private double lat;
    private double lon;
    private boolean sync;
    private Date syncDate;
    private Trip trip;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }


    public Date getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    public Date getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
