package com.amyhuyen.energizer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MakeOpp4Fragment extends Fragment {
    @BindView(R.id.etStartDate) EditText etStartDate;
    @BindView (R.id.etStartTime) EditText etStartTime;
    @BindView (R.id.etEndDate) EditText etEndDate;
    @BindView (R.id.etEndTime) EditText etEndTime;
    @BindView (R.id.btnFinishUpdating) Button btnFinishUpdating;

    Date dateStart;
    Date dateEnd;
    Date timeStart;
    Date timeEnd;
    String skill;
    String cause;
    String name;
    String description;
    String address;
    String latLong;
    String numVolNeeded;


    private OnFragmentInteractionListener mListener;

    public MakeOpp4Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_opp4, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("Create an Opportunity");
    }

    @Override
    public void onStop() {
        super.onStop();
        ((LandingActivity) getActivity()).getSupportActionBar().show();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        name = bundle.getString(DBKeys.KEY_NAME);
        description = bundle.getString(DBKeys.KEY_DESCRIPTION);
        skill = bundle.getString(DBKeys.KEY_SKILL_INNER);
        cause = bundle.getString(DBKeys.KEY_CAUSE_NAME);
        address = bundle.getString(DBKeys.KEY_ADDRESS);
        numVolNeeded = bundle.getString(DBKeys.KEY_NUM_VOL_NEEDED);
        latLong = bundle.getString(DBKeys.KEY_LAT_LONG);

        etStartDate.addTextChangedListener(mTextWatcher);
        etEndDate.addTextChangedListener(mTextWatcher);
        etStartTime.addTextChangedListener(mTextWatcher);
        etEndTime.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };


    void checkFieldsForEmptyValues() {
        // get the contents of the edit texts
        String startDate = etStartDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endDate) || TextUtils.isEmpty(endTime)){
            btnFinishUpdating.setEnabled(false);
            btnFinishUpdating.setClickable(false);
        } else {
            btnFinishUpdating.setEnabled(true);
            btnFinishUpdating.setClickable(true);
        }

    }

    public void checkTimeValidity() {
        // get the contents of the edit texts
        String startDate = etStartDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();


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
        } else if (timeStart.after(timeEnd) && dateStart.equals(dateEnd)){
            Toast.makeText(getActivity(), "Please enter a valid end time", Toast.LENGTH_SHORT).show();
        } else {

            Bundle bundle = new Bundle();
            bundle.putString(DBKeys.KEY_NAME, name);
            bundle.putString(DBKeys.KEY_DESCRIPTION, description);
            bundle.putString(DBKeys.KEY_SKILL_INNER, skill);
            bundle.putString(DBKeys.KEY_CAUSE_NAME, cause);
            bundle.putString(DBKeys.KEY_ADDRESS, address);
            bundle.putString(DBKeys.KEY_NUM_VOL_NEEDED, numVolNeeded);
            bundle.putString(DBKeys.KEY_START_DATE, startDate);
            bundle.putString(DBKeys.KEY_START_TIME, startTime);
            bundle.putString(DBKeys.KEY_END_DATE, endDate);
            bundle.putString(DBKeys.KEY_END_TIME, endTime);
            bundle.putString(DBKeys.KEY_LAT_LONG, latLong);
            etEndDate.setText(null);
            etEndTime.setText(null);
            etStartDate.setText(null);
            etStartTime.setText(null);


            // switch the fragments
            FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            AddOpportunityFragment addOpportunityFragment = new AddOpportunityFragment();
            addOpportunityFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.flContainer, addOpportunityFragment);
            fragmentTransaction.addToBackStack(null).commit();
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

    @OnClick(R.id.btnFinishUpdating)
    public void onContinueClick() {
        checkTimeValidity();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
