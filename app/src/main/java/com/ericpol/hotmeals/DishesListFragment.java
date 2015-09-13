package com.ericpol.hotmeals;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.ericpol.hotmeals.Presenter.DishesListPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DishesListFragment extends Fragment {

    private static final String LOG_TAG = "DishesListFragment";

    private DishesListPresenter presenter;

    private String dateString;
    private List<Dish> dishes = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<Dish> selected = new ArrayList<>();
    private double totalPrice = 0;
    private int supplierId = 0;

    ListView mListView;

    public DishesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dishes_list, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("supplier_name") && intent.hasExtra("supplier_id") && intent.hasExtra("date")) {
            String supplierName = intent.getStringExtra("supplier_name");
            supplierId = intent.getIntExtra("supplier_id", -1);
            dateString = intent.getStringExtra("date");
            getActivity().setTitle(supplierName);
        } else { //we are on a tablet here
            dateString = null;
        }

        mListView = (ListView) rootView.findViewById(R.id.dishes_list);
        mListView.setAdapter(new DishesListAdapter(getActivity().getApplicationContext()));
        presenter = new DishesListPresenter(this);
        presenter.populate();

        return rootView;
    }

    public void updateSupplier(int supplierId, String dateString) {
        this.supplierId = supplierId;
        this.dateString = dateString;
        presenter.populate();
        Log.i(LOG_TAG, "supplier click or date change detected, updating");
    }

    public void updateAdapter(List<Dish> dishes) {
        this.dishes = dishes;
        Collections.sort(dishes);
        items.clear();

        for (int i = 0; i < dishes.size(); i++) {
            if (i == 0 || !dishes.get(i).getCategoryName().equals(dishes.get(i - 1).getCategoryName()))
                items.add(new Item(dishes.get(i).getCategoryName(), null));
            items.add(new Item(null, dishes.get(i)));
        }

        mListView.setAdapter(new DishesListAdapter(getActivity().getApplicationContext()));
    }

    public Order formOrder() {
        return new Order(selected);
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

                    if (items.get(position).isSelected)
                        view.setBackgroundColor(Color.parseColor("#dadada"));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Item item = items.get(position);
                            if (item.isSelected) {
                                selected.remove(item.dish);
                                totalPrice -= item.dish.getPrice();
                                v.setBackgroundColor(Color.parseColor("#f0f0f0"));
                            } else {
                                selected.add(item.dish);
                                totalPrice += item.dish.getPrice();
                                v.setBackgroundColor(Color.parseColor("#dadada"));
                            }
                            item.isSelected = !item.isSelected;
                            TextView total = (TextView) getActivity().findViewById(R.id.total_price_value);
                            total.setText(Double.toString(totalPrice));
                        }
                    });
                    return view;
                }
            } catch (Resources.NotFoundException e) {
                View view = inflater.inflate(R.layout.dishes_list_separator, parent, false);
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
        public boolean isSelected;
        Item(String title, Dish dish) {
            this.title = title;
            this.dish = dish;
            this.isSelected = false;
        }
    }

    public String getDateString() {
        return dateString;
    }

    public int getSupplierId() {
        return supplierId;
    }
}
