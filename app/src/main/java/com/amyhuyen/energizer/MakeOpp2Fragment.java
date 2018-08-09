package com.amyhuyen.energizer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.amyhuyen.energizer.models.Cause;
import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MakeOpp2Fragment extends Fragment {
    @BindView(R.id.actvOppSkill) AutoCompleteTextView actvOppSkill;
    @BindView (R.id.actvOppCause) AutoCompleteTextView actvOppCause;
    @BindView (R.id.btnFinishUpdating) Button btnFinishUpdating;

    String skill;
    String cause;
    String name;
    String description;

    private DatabaseReference skillsRef;
    private DatabaseReference causeRef;

    // text variables
    String npoId;
    String npoName;

    LandingActivity landing;

    private OnFragmentInteractionListener mListener;

    public MakeOpp2Fragment() {
        // Required empty public constructor
    }



    public static MakeOpp2Fragment newInstance(String param1, String param2) {
        MakeOpp2Fragment fragment = new MakeOpp2Fragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_opp2, container, false);
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

        Bundle bundle = getArguments();
        name = bundle.getString(DBKeys.KEY_NAME);
        description = bundle.getString(DBKeys.KEY_DESCRIPTION);

        skillsRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_SKILL_OUTER);
        causeRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_CAUSE);

        // bind the views
        ButterKnife.bind(this, view);

        // autofill for the TextView
        skillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the skills -- and add the skills to it through "collectSkillName"
                ArrayList<String> skills = collectSkillName((Map<String,Object>) dataSnapshot.getValue());

                // connect the TextView to ArrayAdapter that holds the list of skills
                actvOppSkill.setAdapter(newAdapter(skills));
                actvOppSkill.setThreshold(1);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        causeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the causes -- and add the causes to it through "collectCauseName"
                ArrayList<String> causes = collectCauseName((Map<String,Object>) dataSnapshot.getValue());

                // connect the TextView to ArrayAdapter that holds the list of skills
                actvOppCause.setAdapter(newAdapter(causes));
                actvOppCause.setThreshold(1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        actvOppSkill.addTextChangedListener(mTextWatcher);
        actvOppCause.addTextChangedListener(mTextWatcher);

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
        skill = actvOppSkill.getText().toString().trim();
        cause = actvOppCause.getText().toString().trim();

        if (TextUtils.isEmpty(skill) || TextUtils.isEmpty(cause)){
            btnFinishUpdating.setEnabled(false);
            btnFinishUpdating.setClickable(false);
        } else {
            btnFinishUpdating.setEnabled(true);
            btnFinishUpdating.setClickable(true);
        }
    }

    @OnClick (R.id.btnFinishUpdating)
    public void onContinueClick() {
        Bundle bundle = new Bundle();
        bundle.putString(DBKeys.KEY_NAME, name);
        bundle.putString(DBKeys.KEY_DESCRIPTION, description);
        bundle.putString(DBKeys.KEY_SKILL_INNER, skill);
        bundle.putString(DBKeys.KEY_CAUSE_NAME, cause);
        actvOppCause.setText(null);
        actvOppSkill.setText(null);
        // switch the fragments
        FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MakeOpp3Fragment makeOpp3Frag = new MakeOpp3Fragment();
        makeOpp3Frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.flContainer, makeOpp3Frag);
        fragmentTransaction.addToBackStack(null).commit();
    }



    // retrieve cause name when in a "Cause" DataSnapShot
    private ArrayList<String> collectCauseName(Map<String, Object> cause){
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> causes = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the cause
        for (Map.Entry<String, Object> entry : cause.entrySet()){
            // gets the name of the skill
            Map singleCause = (Map) entry.getValue();
            // adds that cause name to the ArrayList
            Cause userInputCause = new Cause((String) singleCause.get(DBKeys.KEY_CAUSE_NAME));
            causes.add(userInputCause.getCause());
        }
        return causes;
    }

    // retrieve skill name when in a "Skill" DataSnapShot
    private ArrayList<String> collectSkillName(Map<String, Object> skill){
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> skills = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the skill
        for (Map.Entry<String, Object> entry : skill.entrySet()){
            // gets the name of the skill
            Map singleSkill = (Map) entry.getValue();
            // adds that skill name to the ArrayList
            Skill userInputSkill = new Skill((String) singleSkill.get(DBKeys.KEY_SKILL_INNER));
            skills.add(userInputSkill.getSkill());
        }
        return skills;
    }

    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list){
        final ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        return autoFillAdapter;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
