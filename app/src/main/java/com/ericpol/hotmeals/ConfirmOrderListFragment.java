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
import android.widget.ArrayAdapter;
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

    private double totalPrice = 0;
    private Order order;

    public ConfirmOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_order_list, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("order")) {
            order = intent.getParcelableExtra("order");
            for (Dish dish : order.getDishes()) {
                totalPrice += dish.getPrice();
            }
        } else {
            Log.e(LOG_TAG, "no parameters in extra");
            return rootView;
        }

        ListView listView = (ListView) rootView.findViewById(R.id.dishes_list);
        listView.setAdapter(new OrderAdapter(getActivity(), order.getDishes()));
        Utility.setListViewHeightBasedOnChildren(listView);

        return rootView;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public class OrderAdapter extends ArrayAdapter<Dish> {
        public OrderAdapter(Context context, List<Dish> dishes) {
            super(context, 0, dishes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Dish dish = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dishes_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.dishes_list_item_title);
            TextView price = (TextView) convertView.findViewById(R.id.dishes_list_item_price);

            title.setText(dish.getName());
            price.setText(Double.toString(dish.getPrice()));

            return convertView;
        }
    }
}