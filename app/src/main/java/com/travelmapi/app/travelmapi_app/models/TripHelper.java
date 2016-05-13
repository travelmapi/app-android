package com.travelmapi.app.travelmapi_app.models;

import java.util.Date;

/**
 * Created by sam on 5/13/16.
 */
public class TripHelper {

    public static boolean active(Trip trip){
        Date now = new Date();
        if(now.compareTo(trip.getStart()) > 0 && now.compareTo(trip.getEnd()) < 0){
            return true;
        }
        return false;
    }
    
}
