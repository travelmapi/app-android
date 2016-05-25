package com.travelmapi.app.travelmapi_app.models;

import org.json.JSONObject;
import java.util.Date;

public class TripHelper {

    public static boolean active(Trip trip){
        Date now = new Date();
        if(now.compareTo(trip.getStart()) > 0 && now.compareTo(trip.getEnd()) < 0){
            return true;
        }
        return false;
    }

    public static JSONObject toJson(Trip trip){
        JSONObject json = new JSONObject();
//        json.put("name"
        return null;
    }


}
