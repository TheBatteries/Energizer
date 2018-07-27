package com.amyhuyen.energizer.utils;

import com.amyhuyen.energizer.models.Opportunity;

public class OppDisplayUtils {

    // method to format the start/end date/time from an opportunity
    public static String formatTime(Opportunity opp){
        if (opp.getStartDate().equals(opp.getEndDate())) {
            return opp.getStartDate() + " " + opp.getStartTime() + " - " + opp.getEndTime();
        } else {
            return opp.getStartDate() + " " + opp.getStartTime() + " - " + opp.getEndDate() + " " + opp.getEndTime();
        }
    }
}
