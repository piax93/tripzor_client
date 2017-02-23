package com.ifalot.tripzor.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trip {

    private int id;
    private boolean owned;
    private String name;

    public Trip(int id, boolean owned, String name){
        this.id = id;
        this.name = name;
        this.owned = owned;
    }

    public boolean isOwned() {
        return owned;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return name;
    }

    public static ArrayList<Trip> parseTrips(JSONArray mytrips, JSONArray parttrips) throws JSONException {
        ArrayList<Trip> trips = new ArrayList<Trip>();
        JSONObject tmp;
        for(int i = 0; i < mytrips.length(); i++) {
            tmp = mytrips.getJSONObject(i);
            trips.add(new Trip(tmp.getInt("tripId"), true, tmp.getString("name")));
        }
        for(int i = 0; i < parttrips.length(); i++) {
            tmp = parttrips.getJSONObject(i);
            trips.add(new Trip(tmp.getInt("tripId"), false, tmp.getString("name")));
        }
        return trips;
    }

}
