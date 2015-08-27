package com.ericpol.hotmeals;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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

public class SuppliersListTabletFragment extends SuppliersListFragment {

    private static final String LOG_TAG = SuppliersListTabletFragment.class.getName();

    public SuppliersListTabletFragment() {
        // Required empty public constructor
    }

    View prevSelected = null;

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
                view.setBackgroundColor(Color.parseColor("#dadada"));
                prevSelected.setBackgroundColor(Color.parseColor("#f0f0f0"));
                prevSelected = view;
                Supplier supplier = suppliers.get(i);
                DishesListFragment fragment = (DishesListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.dishes_list_fragment);
            }
        });
        presenter = new SuppliersListPresenter(this);
        presenter.populate();
        return rootView;
    }
}

