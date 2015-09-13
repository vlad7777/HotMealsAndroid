package com.ericpol.hotmeals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.Data.HotMealsProvider;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.Presenter.SuppliersListPresenter;
import com.ericpol.hotmeals.RetrofitTools.HotmealsApi;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class SuppliersListFragment extends Fragment {

    private static final String LOG_TAG = SuppliersListFragment.class.getName();

    protected CursorAdapter adapter;
    protected SuppliersListPresenter presenter;

    public SuppliersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        String[] columns = new String[]{HotMealsContract.SupplierEntry.COLUMN_NAME};
        int[] to = new int[]{R.id.suppliers_list_item_title};
        Cursor cursor = null;
        adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.suppliers_list_item, cursor, columns, to, 0);

        View rootView = inflater.inflate(R.layout.fragment_suppliers_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_suppliers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DishesActivity.class);
                intent.putExtra("supplier_name", ((TextView) view.findViewById(R.id.suppliers_list_item_title)).getText());
                intent.putExtra("supplier_id", i);
                intent.putExtra("date", getDateSetting());
                startActivity(intent);
            }
        });
        presenter = new SuppliersListPresenter(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected String getDateSetting() {
        DateChooserFragment dateChooser = (DateChooserFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_date_chooser);
        return dateChooser.getDateString();
    }

    public void onDateChange() {

    }

    public CursorAdapter getAdapter() {
        return adapter;
    }
}
