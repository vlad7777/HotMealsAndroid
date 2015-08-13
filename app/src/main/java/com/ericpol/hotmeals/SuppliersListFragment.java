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

import com.ericpol.hotmeals.Entities.Supplier;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private void loadSuppliers()
    {
        // TODO: 11.8.15 replace the dummy data with something real
        suppliers = new ArrayList<Supplier>();
        suppliers.add(new Supplier(0, "Drug dealer"));
        suppliers.add(new Supplier(1, "KFC"));
        suppliers.add(new Supplier(2, "Macdonald's"));
        suppliers.add(new Supplier(3, "Beltelecom"));

        if (adapter != null){
            adapter.clear();
            for (Supplier e : suppliers){
                adapter.add(e.getName());
            }
        }
    }
}
