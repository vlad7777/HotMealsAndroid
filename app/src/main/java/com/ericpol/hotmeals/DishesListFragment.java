package com.ericpol.hotmeals;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Order;
import com.ericpol.hotmeals.Entities.Supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DishesListFragment extends Fragment {

    private static final String LOG_TAG = "DishesListFragment";

    private Supplier supplier;

    private List<Dish> dishes = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<Dish> selected = new ArrayList<>();
    private double totalPrice = 0;

    public DishesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dishes_list, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("supplier")) {
            supplier = intent.getParcelableExtra("supplier");
            getActivity().setTitle(supplier.getName());
        }

        loadDishes();
        ListView listView = (ListView) rootView.findViewById(R.id.dishes_list);
        listView.setAdapter(new DishesListAdapter(getActivity().getApplicationContext()));

        return rootView;
    }

    public void loadDishes()
    {
        // TODO: 12.8.15 replace the dummy data
        dishes.add(new Dish(0, "Soup", "Troll's fat in vodka", 12000));
        dishes.add(new Dish(1, "Soup", "Żurek", 20000));
        dishes.add(new Dish(2, "Main Dish", "Chicken with fries", 25000));
        dishes.add(new Dish(3, "Main Dish", "VGA cable", 14000));
        dishes.add(new Dish(4, "Main Dish", "Burned curry wurst", 30000));
        dishes.add(new Dish(5, "Dessert", "Dragon's eye", 2300000));
        dishes.add(new Dish(6, "Dessert", "Salt", 10));

        Collections.sort(dishes);

        for (int i = 0; i < dishes.size(); i++) {
            if (i == 0 || !dishes.get(i).getCategoryName().equals(dishes.get(i - 1).getCategoryName()))
                items.add(new Item(dishes.get(i).getCategoryName(), null));
            items.add(new Item(null, dishes.get(i)));
        }
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
                } else if (items.get(position).dish != null){
                    View view = inflater.inflate(R.layout.dishes_list_item, parent, false);
                    TextView title = (TextView) view.findViewById(R.id.dishes_list_item_title);

                    title.setText(items.get(position).dish.getName());
                    TextView price = (TextView) view.findViewById(R.id.dishes_list_item_price);

                    price.setText(Integer.toString(items.get(position).dish.getPrice()));

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
                            TextView total = (TextView)getActivity().findViewById(R.id.total_price_value);
                            total.setText(Double.toString(totalPrice));
                        }
                    });
                    return view;
                }
                else {
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
        public boolean isSelected;
        Item(String title, Dish dish) {
            this.title = title;
            this.dish = dish;
            this.isSelected = false;
        }
    }

}
