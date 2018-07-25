package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Opportunity {
    String name;
    String description;
    String oppId;

    public Opportunity() {
    }

    public Opportunity(String name, String description, String oppId) {

        // assign values to the new instance of opportunity
        this.name = name;
        this.description = description;
        this.oppId = oppId;
    }


    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() {
        return description;
    }

    // the accessor for the opportunity id
    public String getOppId() {
        return oppId;
    }
}
