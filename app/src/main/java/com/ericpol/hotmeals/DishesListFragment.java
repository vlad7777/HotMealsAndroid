package com.ericpol.hotmeals;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Order;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.Presenter.DishesListPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DishesListFragment extends Fragment {

    private static final String LOG_TAG = "DishesListFragment";

    private DishesListPresenter presenter;

    private String dateString;
    private List<Dish> selected = new ArrayList<>();
    private double totalPrice = 0;
    private int supplierId = 0;

    ExpandableListView mListView;
    DishesAdapter mAdapter;

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

        mListView = (ExpandableListView) rootView.findViewById(R.id.dishes_list);
        presenter = new DishesListPresenter(this);
        mAdapter = new DishesAdapter(null, this.getActivity());
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    public void updateSupplier(int supplierId, String dateString) {
        this.supplierId = supplierId;
        this.dateString = dateString;
    }

    public Order formOrder() {
        return new Order(selected);
    }

    public String getDateString() {
        return dateString;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public class DishesAdapter extends CursorTreeAdapter {

        private final int VIEW_TAG_DISH = 0;

        private final String LOG_TAG = DishesAdapter.class.getName();
        protected final HashMap<Integer, Integer> mGroupMap;
        protected final HashMap<Integer, Cursor> mCursorMap;
        private LayoutInflater mInflater;
        boolean allowCollapse = false;

        public DishesAdapter(Cursor cursor, Context context) {
            super(cursor, context);
            mInflater = LayoutInflater.from(context);
            mGroupMap = new HashMap<Integer, Integer>();
            mCursorMap = new HashMap<>();
        }

        public void setChildrenCursorById(int id, Cursor cursor) {
            int position = mGroupMap.get(id);
            super.setChildrenCursor(position, cursor);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            String s = "";
            for (int i = 0; i < groupCursor.getColumnCount(); i++)
                s += groupCursor.getColumnName(i) + ":" + groupCursor.getString(i) + " ";

            int position = groupCursor.getPosition();
            int id = groupCursor.getInt(DishesListPresenter.CategoryQueryParameters.COLUMN_CATEGORY_ID);

            if (!mGroupMap.containsKey(id)) {
                mGroupMap.put(id, position);
                presenter.initDishesLoader(id);
            }

            return null;
        }

        //don't want to realease the cursors that belong to the collapsed group
        @Override
        public void onGroupCollapsed(int groupPosition) {
            allowCollapse = true;
        }

        @Override
        public View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.dishes_list_separator, parent, false);
            return view;
        }

        @Override
        public void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            TextView title = (TextView) view.findViewById(R.id.dishes_list_separator);
            String titleString = cursor.getString(1);
            title.setText(titleString);
            int position = cursor.getPosition();
            if (!allowCollapse)
                mListView.expandGroup(position);
        }

        @Override
        public View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.dishes_list_item, parent, false);
            return view;
        }

        @Override
        public void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            TextView title = (TextView) view.findViewById(R.id.dishes_list_item_title);
            TextView price = (TextView) view.findViewById(R.id.dishes_list_item_price);

            String sTitle = cursor.getString(DishesListPresenter.DishQueryParameters.COLUMN_DISH_NAME);
            String sPrice = Double.toString(cursor.getDouble(DishesListPresenter.DishQueryParameters.COLUMN_DISH_PRICE));
            int dishId = cursor.getInt(DishesListPresenter.DishQueryParameters.COLUMN_DISH_ID);

            title.setText(sTitle);
            price.setText(sPrice);

            Dish dish = new Dish(dishId, sTitle, Double.parseDouble(sPrice));
            view.setTag(dish);
            if (selected.contains(dish)) {
                view.setBackgroundColor(Color.parseColor("#dadada"));
            } else {
                view.setBackgroundColor(Color.parseColor("#f0f0f0"));
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dish dish = (Dish)v.getTag();
                    boolean isSelected = selected.contains(dish);
                    if (isSelected) {
                        selected.remove(dish);
                        totalPrice -= dish.getPrice();
                        v.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    } else {
                        selected.add(dish);
                        totalPrice += dish.getPrice();
                        v.setBackgroundColor(Color.parseColor("#dadada"));
                    }
                    TextView total = (TextView) getActivity().findViewById(R.id.total_price_value);
                    total.setText(Double.toString(totalPrice));
                }
            });
        }
    }

    public DishesAdapter getAdapter() {
        return mAdapter;
    }
}
