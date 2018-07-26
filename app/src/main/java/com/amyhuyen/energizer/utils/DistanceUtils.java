package com.amyhuyen.energizer.utils;

import android.location.Location;

import java.util.ArrayList;

public class DistanceUtils {

    // method that finds the distance between two locations given their latitude and longitude
    public static float distanceBetween(double latA, double longA, double latB, double longB){
        // set up first location
        Location locationA = new Location("Volunteer Position");
        locationA.setLatitude(latA);
        locationA.setLongitude(longA);

        // set up second location
        Location locationB = new Location("Opportunity Position");
        locationB.setLatitude(latB);
        locationB.setLongitude(longB);

        // find the distance between locationA and locationB
        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    // method that converts the latLong String (as received from firebase) and returns an array
    // that contains them as doubles
    public static ArrayList<Double> getLatLong(String latLong){
        // access the part of the string between the parentheses
        String intermediateString = latLong.replace("(", "");
        intermediateString = intermediateString.replace(")", "");

        // get the latitude and longitude by splitting by the comma
        String[] split = intermediateString.split(",");

        // convert the strings to doubles and add them to the array list
        ArrayList<Double> latLongArray = new ArrayList<>();
        for (String latOrLong : split){
            latLongArray.add(Double.parseDouble(latOrLong));
        }
        return latLongArray;
    }
}
