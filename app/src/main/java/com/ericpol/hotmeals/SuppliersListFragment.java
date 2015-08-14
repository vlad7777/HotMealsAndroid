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
    private List<Supplier> suppliers;

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
                // TODO: 11.8.15 add click listener
                Supplier supplier = suppliers.get(i);
                Intent intent = new Intent(getActivity(), DishesActivity.class);
                intent.putExtra("supplier", supplier);
                startActivity(intent);
            }
        });
        loadSuppliers();

        return rootView;
    }

    private static final String API = "http://3f80b12b.ngrok.com";

    private void loadSuppliers()
    {
        suppliers = new ArrayList<Supplier>();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setConverter(new GsonConverter(gson)).setEndpoint(API).build();
        HotmealsApi api = restAdapter.create(HotmealsApi.class);
        api.fetchSuppliers(new Callback<List<Supplier>>() {
            @Override
            public void success(List<Supplier> s, Response response) {
                suppliers = s;
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), Integer.toString(s.size()), Toast.LENGTH_LONG);
                toast.show();
                if (adapter != null){
                    adapter.clear();
                    for (Supplier e : suppliers){
                        adapter.add(e.getName());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                suppliers.add(new Supplier(8, "ERROR"));
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "error", Toast.LENGTH_LONG);
                toast.show();
                if (adapter != null) {
                    adapter.clear();
                    for (Supplier e : suppliers) {
                        adapter.add(e.getName());
                    }
                }
            }
        });

    }
}
