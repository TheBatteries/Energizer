package com.amyhuyen.energizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStaet) {
        // use the curent date as teh default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // create a new instance of DatePi.kerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        String date = String.valueOf(month) + "-" + String.valueOf(day) + "-" + String.valueOf(year);

        if (getTag().equals("Start Date Picker")){
            EditText etStartDate = getActivity().findViewById(R.id.etStartDate);
            etStartDate.setText(date);
        } else if (getTag().equals("End Date Picker")){
            EditText etEndDate = getActivity().findViewById(R.id.etEndDate);
            etEndDate.setText(date);
        }
    }
}
