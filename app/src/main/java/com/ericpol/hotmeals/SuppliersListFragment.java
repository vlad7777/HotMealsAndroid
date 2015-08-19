package com.ericpol.hotmeals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ericpol.hotmeals.Entities.Supplier;
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

    private ArrayAdapter<String> adapter;
    private List<Supplier> suppliers = new ArrayList<>();

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
        API = getResources().getString(R.string.domain);
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
        loadSuppliers();

        return rootView;
    }

    private String API;

    // TODO: 19.8.15 make this asynchronous and cash it

    private void loadSuppliers()
    {
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();
        HotmealsApi api = restAdapter.create(HotmealsApi.class);
        api.fetchSuppliers(new Callback<List<Supplier>>() {
            @Override
            public void success(List<Supplier> s, Response response) {
                updateAdapter(s);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "couldn't fetch data", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private String getDateSetting() {
        DateChooserFragment dateChooser = (DateChooserFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_date_chooser);
        return dateChooser.getDateString();
    }

    private void updateAdapter(List<Supplier> suppliers) {
        this.suppliers = suppliers;
        if (adapter != null) {
            adapter.clear();
            for (Supplier e : suppliers) {
                adapter.add(e.getName());
            }
        }
    }
}
