package com.ericpol.hotmeals;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateChooserFragment extends Fragment {


    public DateChooserFragment() {
        // Required empty public constructor
    }

    private static final String LOG_TAG = DateChooserFragment.class.getName();

    ArrayAdapter<String> mAdapter;
    Spinner mSpinner;
    Date date = new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_date_chooser, container, false);

        mSpinner = (Spinner) rootView.findViewById(R.id.date_spinner);

        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.date_spinner_item, new ArrayList<String>());

        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SuppliersListFragment fragment = (SuppliersListFragment) getFragmentManager().findFragmentById(R.id.suppliers_list_fragment);
                fragment.onDateChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAdapter.setDropDownViewResource(R.layout.date_spinner_dropdown_item);

        populateSpinner();

        return rootView;
    }

    public void populateSpinner() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        mAdapter.add(df.format(today));
        mAdapter.add(df.format(tomorrow));
    }

    public String getDateString() {
        try {
            DateFormat dfFrom = new SimpleDateFormat("dd/MM/yyyy");
            String value = mSpinner.getSelectedItem().toString();
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df.format(dfFrom.parse(value));
        } catch (ParseException pe) {
            Log.e(LOG_TAG, "Parse error");
            return "";
        }
    }


}