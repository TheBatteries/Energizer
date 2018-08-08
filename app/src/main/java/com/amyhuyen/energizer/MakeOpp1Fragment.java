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

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MakeOpp1Fragment extends Fragment {
    @BindView(R.id.etOppName) EditText etOppName;
    @BindView (R.id.etOppDescription) EditText etOppDescription;
    @BindView (R.id.btnFinishUpdating) Button btnFinishUpdating;

    String name;
    String description;

    private OnFragmentInteractionListener mListener;

    public MakeOpp1Fragment() {
        // Required empty public constructor
    }


    public static MakeOpp1Fragment newInstance(String param1, String param2) {
        MakeOpp1Fragment fragment = new MakeOpp1Fragment();

        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_opp1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);


        // set text change listeners for all fields
        etOppName.addTextChangedListener(mTextWatcher);
        etOppDescription.addTextChangedListener(mTextWatcher);

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
        name = etOppName.getText().toString().trim();
        description = etOppDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) ){
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
        bundle.putParcelable(DBKeys.KEY_NAME, Parcels.wrap(name));
        bundle.putParcelable(DBKeys.KEY_DESCRIPTION, Parcels.wrap(description));
        etOppName.setText(null);
        etOppDescription.setText(null);
        // switch the fragments
        FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MakeOpp2Fragment makeOpp2Frag = new MakeOpp2Fragment();
        makeOpp2Frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.flContainer, makeOpp2Frag);
        fragmentTransaction.addToBackStack(null).commit();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
