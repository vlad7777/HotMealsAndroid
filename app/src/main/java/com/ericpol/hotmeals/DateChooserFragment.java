package com.ericpol.hotmeals;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateChooserFragment extends Fragment {


    public DateChooserFragment() {
        // Required empty public constructor
    }

    ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_date_chooser, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.date_spinner);

        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());

        spinner.setAdapter(mAdapter);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        populateSpinner();

        return rootView;
    }

    public void populateSpinner() {
        mAdapter.add("today");
        mAdapter.add("tomorrow");
    }


}