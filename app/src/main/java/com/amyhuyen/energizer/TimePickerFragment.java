package com.amyhuyen.energizer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // user the current time as the default values for the picker
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // when the time is chosen by the user, fill the edit text
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // display time for military time users

        if (DateFormat.is24HourFormat(getActivity())) {
            if (getTag().equals("Start Time Picker")){
                EditText etStartTime = (EditText) getActivity().findViewById(R.id.etStartTime);
                etStartTime.setText("Starts:  " + String.valueOf(hourOfDay) + String.valueOf(minute));
            } else if (getTag().equals("End Time Picker")){
                EditText etEndTime = (EditText) getActivity().findViewById(R.id.etEndTime);
                etEndTime.setText("Ends:  " + String.valueOf(hourOfDay) + String.valueOf(minute));
            }
        } else {
            // display time for non-military time users; hourOfDay comes back in 24-hour form, show AM-PM form
            String amPm = (hourOfDay < 12) ? "AM" : "PM";

            // get hour
            String hour = "12";
            if (hourOfDay != 12 && hourOfDay != 0) {
                hour = String.valueOf(hourOfDay % 12);
            }

            // get minute
            String min;
            if (minute == 0){
                min = "00";
            } else{
                min = String.valueOf(minute);
            }

            // set views
            if (getTag().equals("Start Time Picker")) {
                EditText etStartTime = (EditText) getActivity().findViewById(R.id.etStartTime);
                etStartTime.setText("Starts:  " + hour + ":" + min + amPm);
            } else if (getTag().equals("End Time Picker")) {
                EditText etEndTime = (EditText) getActivity().findViewById(R.id.etEndTime);
                etEndTime.setText("Ends:  " + hour + ":" + min + amPm);
            }
        }

    }
}
