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
import android.widget.Button;
import android.widget.EditText;

import com.amyhuyen.energizer.utils.AutocompleteUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MakeOpp3Fragment extends Fragment {
    @BindView(R.id.etOppLocation) EditText etOppLocation;
    @BindView (R.id.etNumVolNeeded) EditText etNumVolNeeded;
    @BindView (R.id.btnFinishUpdating) Button btnFinishUpdating;

    String skill;
    String cause;
    String name;
    String description;
    String address;
    String numVolNeeded;
    UserDataProvider userDataProvider;

    private OnFragmentInteractionListener mListener;

    public MakeOpp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_opp3, container, false);

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

        userDataProvider = UserDataProvider.getInstance();
        etOppLocation.setText(userDataProvider.getCurrentUserAddress());

        Bundle bundle = getArguments();
        name = bundle.getString(DBKeys.KEY_NAME);
        description = bundle.getString(DBKeys.KEY_DESCRIPTION);
        skill = bundle.getString(DBKeys.KEY_SKILL_INNER);
        cause = bundle.getString(DBKeys.KEY_CAUSE_NAME);

        etNumVolNeeded.addTextChangedListener(mTextWatcher);
        etOppLocation.addTextChangedListener(mTextWatcher);

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
        address = etOppLocation.getText().toString().trim();
        numVolNeeded = etNumVolNeeded.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(numVolNeeded)){
            btnFinishUpdating.setEnabled(false);
            btnFinishUpdating.setClickable(false);
        } else {
            btnFinishUpdating.setEnabled(true);
            btnFinishUpdating.setClickable(true);
        }

    }
    @OnClick(R.id.btnFinishUpdating)
    public void onContinueClick() {

        Bundle bundle = new Bundle();
        bundle.putString(DBKeys.KEY_NAME, name);
        bundle.putString(DBKeys.KEY_DESCRIPTION, description);
        bundle.putString(DBKeys.KEY_SKILL_INNER, skill);
        bundle.putString(DBKeys.KEY_CAUSE_NAME, cause);
        bundle.putString(DBKeys.KEY_ADDRESS, address);
        bundle.putString(DBKeys.KEY_NUM_VOL_NEEDED, numVolNeeded);
        bundle.putString(DBKeys.KEY_LAT_LONG, userDataProvider.getCurrentUserLatLong());
        etNumVolNeeded.setText(null);
        etOppLocation.setText(null);


        // switch the fragments
        FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MakeOpp4Fragment makeOpp4Frag = new MakeOpp4Fragment();
        makeOpp4Frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.flContainer, makeOpp4Frag);
        fragmentTransaction.addToBackStack(null).commit();
    }

    // on click listener for opportunity location edit text
    @OnClick (R.id.etOppLocation)
    public void onOppLocationClick(){
        AutocompleteUtils.callPlaceAutocompleteActivityIntent(getActivity());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
