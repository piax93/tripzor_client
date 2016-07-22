package com.ifalot.tripzor.model;

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

    public static ArrayList<Trip> parseTrips(List<String> listResult) {
        ArrayList<Trip> trips = new ArrayList<Trip>();
        for(String line : listResult){
            boolean owned = false;
            if(line.startsWith("*")){
                owned = true;
                line = line.substring(1);
            }
            String[] tmp = line.split(":", 2);
            trips.add(new Trip(Integer.parseInt(tmp[0]), owned, tmp[1]));
        }
        return trips;
    }

}
