package com.ericpol.hotmeals.Presenter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.DishesListFragment;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.DishesListFragment;

import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.CategoryEntry;
import com.ericpol.hotmeals.LoginActivity;
import com.ericpol.hotmeals.R;
import com.ericpol.hotmeals.RetrofitTools.SecuredRestException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 24.8.15.
 */

public class DishesListPresenter {

    private static final String LOG_TAG = DishesListPresenter.class.getName();

    private static final String[] DISH_PROJECTION = new String[]{
            DishEntry.TABLE_NAME + "." + DishEntry._ID,
            DishEntry.TABLE_NAME + "." + DishEntry.COLUMN_NAME,
            CategoryEntry.TABLE_NAME + "." + CategoryEntry.COLUMN_NAME,
            DishEntry.COLUMN_PRICE
    };

    private static final int COLUMN_DISH_ID = 0;
    private static final int COLUMN_DISH_NAME = 1;
    private static final int COLUMN_CATEGORY_NAME = 2;
    private static final int COLUMN_DISH_PRICE = 3;

    DishesListFragment mFragment;
    DBPopulator dbPopulator;

    public DishesListPresenter(DishesListFragment fragment) {
        mFragment = fragment;
        String token = fragment.getActivity().getPreferences(Context.MODE_PRIVATE).getString(fragment.getActivity().getString(R.string.token), "");
        dbPopulator = new DBPopulator(fragment.getActivity().getContentResolver(), mFragment.getActivity());
        if (mFragment.getSupplier() != null) {
            dbPopulator.checkUpdateForSupplier(mFragment.getSupplier().getId());
        }
        //mFragment.getActivity().getLoaderManager().initLoader(1, null, this);
    }

    public void populate() {
        if (mFragment.getSupplier() == null)
            return;
        FetchDishesTask task = new FetchDishesTask();
        task.execute();
    }

    private class FetchDishesTask extends AsyncTask<List<Dish>, Void, List<Dish>> {

        private static final String LOG_TAG = "FetchDishesTask";

        protected List<Dish> doInBackground(List<Dish>... params) {
            // TODO: 31/08/15 add projection 
            Cursor cursor = mFragment.getActivity().getContentResolver().query(HotMealsContract.DishEntry.buildDishUriFromSupplierIdAndDate(mFragment.getSupplier().getId(),
                            mFragment.getDateString()),
                   DISH_PROJECTION, null, null, null);

            List<Dish> dishes = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(COLUMN_DISH_ID);
                String name = cursor.getString(COLUMN_DISH_NAME);
                String categoryName = cursor.getString(COLUMN_CATEGORY_NAME);
                double price = cursor.getDouble(COLUMN_DISH_PRICE);
                Dish dish = new Dish(id, name, categoryName, price);
                dishes.add(dish);
                cursor.moveToNext();
            }
            cursor.close();

            return dishes;
        }

        protected void onPostExecute(List<Dish> dishes) {
            mFragment.updateAdapter(dishes);
        }
    }

}
