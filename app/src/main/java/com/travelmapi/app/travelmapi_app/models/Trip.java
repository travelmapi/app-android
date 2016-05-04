package com.travelmapi.app.travelmapi_app.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sam on 5/3/16.
 */
public class Trip extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private Date start;
    private Date end;
    private RealmList<TravelStamp> stamps;

    public RealmList<TravelStamp> getStamps() {
        return stamps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStamps(RealmList<TravelStamp> stamps) {
        this.stamps = stamps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
