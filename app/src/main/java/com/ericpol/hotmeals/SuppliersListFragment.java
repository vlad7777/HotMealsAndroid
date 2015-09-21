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
        adapter = new SuppliersAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_suppliers_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_suppliers);
        listView.setAdapter(adapter);
        presenter = new SuppliersListPresenter(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.updateData();
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

    public class SuppliersAdapter extends CursorAdapter {

        public SuppliersAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.suppliers_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String name = cursor.getString(SuppliersListPresenter.COLUMN_SUPPLIERS_NAME);
            int id = cursor.getInt(SuppliersListPresenter.COLUMN_SUPPLIERS_ID);
            TextView title = (TextView) view.findViewById(R.id.suppliers_list_item_title);
            title.setText(name);
            view.setTag(id);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DishesActivity.class);
                    intent.putExtra("supplier_name", ((TextView) v.findViewById(R.id.suppliers_list_item_title)).getText());
                    intent.putExtra("supplier_id", (Integer) v.getTag());
                    intent.putExtra("date", getDateSetting());
                    startActivity(intent);
                }
            });
        }
    }
}
