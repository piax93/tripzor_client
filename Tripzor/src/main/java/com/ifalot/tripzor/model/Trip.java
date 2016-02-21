package com.ifalot.tripzor.model;

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

}
