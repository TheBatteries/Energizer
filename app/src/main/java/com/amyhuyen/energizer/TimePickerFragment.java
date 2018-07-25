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

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // note that hourOfDay ranges from 0-24
        // when the time is chosen by the user, fill the edit text
        String amPm = (hourOfDay < 12) ? "AM" : "PM";
        int hour = 12;
        if (hourOfDay != 12 && hourOfDay != 0) {
            hour = hourOfDay % 12;
        }
        EditText etStartTime = (EditText)getActivity().findViewById(R.id.etStartTime);
        etStartTime.setText(String.valueOf(hour) + ":" + String.valueOf(minute) + amPm);
    }
}
