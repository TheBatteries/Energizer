package com.amyhuyen.energizer;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

public class AutocompletUtils {

    // launches google place autocomplete widget
    public static void callPlaceAutocompleteActivityIntent(Activity activity) {

        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

        try{
            // launches intent to the google place autocomplete widget
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity);
            activity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch(GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
