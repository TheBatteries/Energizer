package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOpportunityFragment extends Fragment{

    // the views
    @BindView (R.id.etOppName) EditText etOppname;
    @BindView (R.id.etOppDescription) EditText etOppDescriotion;
    @BindView (R.id.btnAddOpp) Button btnAddOpp;
    @BindView (R.id.etStartDate) EditText etStartDate;
    @BindView (R.id.etStartTime) EditText etStartTime;
    @BindView (R.id.etEndDate) EditText etEndDate;
    @BindView (R.id.etEndTime) EditText etEndTime;
    DatabaseReference firebaseDataOpp;
    Date dateStart;
    Date dateEnd;
    Date timeStart;
    Date timeEnd;
    Date dateOne;
    Date dateTwo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_opportunity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);
    }

    // on click listener for add opportunity button
    @OnClick (R.id.btnAddOpp)
    public void onAddOppClick() {

        // get the contents of the edit texts
        String name = etOppname.getText().toString().trim();
        String description = etOppDescriotion.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        // check that all fields are populated
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endDate) || TextUtils.isEmpty(endTime)) {
            Toast.makeText(getActivity(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // remove prefixes on start and end times/dates
            startDate = startDate.replace("Start Date:  ", "");
            endDate = endDate.replace("End Date:  ", "");
            startTime = startTime.replace("Start Time:  ", "");
            endTime = endTime.replace("End Time:  ", "");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

            // convert strings to dates
            try {
                dateStart = dateFormat.parse(startDate);
                dateEnd = dateFormat.parse(endDate);
                timeStart = timeFormat.parse(startTime);
                timeEnd = timeFormat.parse(endTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            // check that dates and times are valid
            if (dateStart.after(dateEnd)) {
                // alert user if end date is before start date
                Toast.makeText(getActivity(), "Please enter a valid end date", Toast.LENGTH_SHORT).show();
            } else {
                // alert user if end time is before start time or equal to start time
                if (timeStart.after(timeEnd) || timeStart.equals(timeEnd)) {
                    Toast.makeText(getActivity(), "Please enter a valid end time", Toast.LENGTH_SHORT).show();
                } else {
                    // create an instance of the opportunity class based on this information
                    firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");
                    final String oppId = firebaseDataOpp.push().getKey();

                    Opportunity newOpp = new Opportunity(name, description, oppId, startDate, startTime, endDate, endTime);
                    firebaseDataOpp.child(oppId).setValue(newOpp);
                }
            }
        }
    }

    // on click listener for start time edit text
    @OnClick (R.id.etStartTime)
    public void onStartTimeClick(){
        DialogFragment timeStartPicker = new TimePickerFragment();
        timeStartPicker.show(getActivity().getSupportFragmentManager(), "Start Time Picker");
    }

    // on click listener for end time edit text
    @OnClick (R.id.etEndTime)
    public void onEndTimeClick(){
        DialogFragment timeEndPicker = new TimePickerFragment();
        timeEndPicker.show(getActivity().getSupportFragmentManager(), "End Time Picker");
    }

    // on click listener for start date edit text
    @OnClick (R.id.etStartDate)
    public void onStartDateClick(){
        DialogFragment dateStartPicker = new DatePickerFragment();
        dateStartPicker.show(getActivity().getSupportFragmentManager(), "Start Date Picker");

    }

    // on click listener for end date edit text
    @OnClick (R.id.etEndDate)
    public void onEndDateClick(){
        DialogFragment dateEndPicker = new DatePickerFragment();
        dateEndPicker.show(getActivity().getSupportFragmentManager(), "End Date Picker");
    }
}
