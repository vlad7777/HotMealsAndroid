package com.ericpol.hotmeals;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Order;
import com.ericpol.hotmeals.Entities.Supplier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOrderListFragment extends Fragment {

    private static final String LOG_TAG = "ConfirmOrderListFragmen";

    private List<Item> items = new ArrayList<>();
    private double totalPrice = 0;
    private Supplier supplier;
    private Order order;

    public ConfirmOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dishes_list, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("supplier") && intent.hasExtra("order")) {
            supplier = intent.getParcelableExtra("supplier");
            order = intent.getParcelableExtra("order");
        } else {
            Log.e(LOG_TAG, "no parameters in extra");
            return rootView;
        }

        loadDishes();
        ListView listView = (ListView) rootView.findViewById(R.id.dishes_list);
        listView.setAdapter(new DishesListAdapter(getActivity().getApplicationContext()));

        return rootView;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void loadDishes() {

        List<Dish> dishes = order.getDishes();
        if (dishes == null) {
            Log.e(LOG_TAG, "dishes is null");
            return;
        }

        Collections.sort(dishes);

        for (int i = 0; i < dishes.size(); i++) {
            if (i == 0 || !dishes.get(i).getCategoryName().equals(dishes.get(i - 1).getCategoryName()))
                items.add(new Item(dishes.get(i).getCategoryName(), null));
            items.add(new Item(null, dishes.get(i)));
            totalPrice += dishes.get(i).getPrice();
        }

    }

    private class DishesListAdapter extends BaseAdapter {

        private static final String LOG_TAG = "DishesListAdapter";

        private final Context mContext;

        public DishesListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int pos) {
            return true;
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(mContext);

            try {
                if (items.get(position).title != null) {
                    View view = inflater.inflate(R.layout.dishes_list_separator, parent, false);
                    TextView title = (TextView) view.findViewById(R.id.dishes_list_separator);
                    title.setText(items.get(position).title);
                    view.setEnabled(true);
                    return view;
                } else if (items.get(position).dish != null) {
                    View view = inflater.inflate(R.layout.dishes_list_item, parent, false);
                    TextView title = (TextView) view.findViewById(R.id.dishes_list_item_title);

                    title.setText(items.get(position).dish.getName());
                    TextView price = (TextView) view.findViewById(R.id.dishes_list_item_price);

                    price.setText(Double.toString(items.get(position).dish.getPrice()));

                    return view;
                } else {
                    Log.e(LOG_TAG, "item is null");
                }
            } catch (Resources.NotFoundException e) {
                View view = inflater.inflate(R.layout.dishes_list_separator, parent, false);
                if (view == null)
                    Log.e(LOG_TAG, "view is null, not inflated");
                TextView title = (TextView) view.findViewById(R.id.dishes_list_separator);
                title.setText("didn't work here");
                return view;
            }
            return null;
        }
    }

    private class Item {
        public String title;
        public Dish dish;

        Item(String title, Dish dish) {
            this.title = title;
            this.dish = dish;
        }
    }
}