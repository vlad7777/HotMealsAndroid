package com.ericpol.hotmeals;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ericpol.hotmeals.Data.HotMealsContract;
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

    public ArrayAdapter<String> adapter;
    public List<Supplier> suppliers = new ArrayList<>();

    private SuppliersListPresenter presenter;

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
        adapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.suppliers_list_item, // The name of the layout ID.
                        R.id.suppliers_list_item_title, // The ID of the textview to populate.
                        new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_suppliers_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_suppliers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Supplier supplier = suppliers.get(i);
                Intent intent = new Intent(getActivity(), DishesActivity.class);
                intent.putExtra("supplier", supplier);
                intent.putExtra("date", getDateSetting());
                startActivity(intent);
            }
        });
        presenter = new SuppliersListPresenter(this);
        presenter.populate();
        return rootView;
    }

    // TODO: 19.8.15 make this asynchronous and cache it

    private String getDateSetting() {
        DateChooserFragment dateChooser = (DateChooserFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_date_chooser);
        return dateChooser.getDateString();
    }

    public void updateAdapter(List<Supplier> suppliers) {
        this.suppliers = suppliers;
        adapter.clear();
        for (Supplier supplier : suppliers) {
            adapter.add(supplier.getName());
        }
    }
}
