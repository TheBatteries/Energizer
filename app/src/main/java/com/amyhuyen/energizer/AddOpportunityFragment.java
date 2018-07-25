package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOpportunityFragment extends Fragment{

    // the views
    @BindView (R.id.etOppName) EditText etOppname;
    @BindView (R.id.etOppDescription) EditText etOppDescriotion;
    @BindView (R.id.btnAddOpp) Button btnAddOpp;
    @BindView (R.id.etStartTime) EditText etStartTime;
    DatabaseReference firebaseDataOpp;


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
    public void onAddOppClick(){

        // get the contents of the edit texts
        final String name = etOppname.getText().toString().trim();
        final String description = etOppDescriotion.getText().toString().trim();


        // create an instance of the opportunity class based on this information
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");
        final String oppId = firebaseDataOpp.push().getKey();

        Opportunity newOpp = new Opportunity(name, description, oppId);
        firebaseDataOpp.child(oppId).setValue(newOpp);

    }

    // on click listener for start time edit text
    @OnClick (R.id.etStartTime)
    public void onStartTimeClick(){
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getActivity().getSupportFragmentManager(), "Start Time Picker");
    }
}
