package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Opportunity {
    String name;
    String description;
    String oppId;
    String startDate;
    String startTime;
    String endDate;
    String endTime;

    public Opportunity(){}

    public Opportunity(String name, String description, String oppId, String startDate, String startTime, String endDate, String endTime) {
        this.name = name;
        this.description = description;
        this.oppId = oppId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() { return description; }

    // the accessor for the opportunity id
    public String getOppId() { return oppId; }

    public String getStartDate() { return startDate; }

    public String getStartTime() { return startTime; }

    public String getEndDate() { return endDate; }

    public String getEndTime() { return endTime; }
}
