package com.ifalot.tripzor.utils;

public class Stuff {

    public static String ucfirst(String string){
        if(string.length() > 1) return string.substring(0,1).toUpperCase() + string.substring(1);
        return string.toUpperCase();
    }

}
