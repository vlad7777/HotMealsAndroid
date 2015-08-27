package com.ericpol.hotmeals.Presenter;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.DishesListFragment;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.DishesListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 24.8.15.
 */
public class DishesListPresenter {
    private static final String LOG_TAG = DishesListPresenter.class.getName();

    DishesListFragment mFragment;
    DBPopulator dbPopulator;

    public DishesListPresenter(DishesListFragment fragment) {
        mFragment = fragment;
        dbPopulator = new DBPopulator(fragment.getActivity().getContentResolver());
        if (mFragment.getSupplier() != null)
            dbPopulator.checkUpdateForSupplier(mFragment.getSupplier().getId());
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
            //String[] projection = new String[]{HotMealsContract.DishEntry._ID, HotMealsContract.DishEntry.COLUMN_NAME};
            Cursor cursor = mFragment.getActivity().getContentResolver().query(HotMealsContract.DishEntry.buildDishUriFromSupplierIdAndDate(mFragment.getSupplier().getId(),
                            mFragment.getDateString()),
                   null, null, null, null);

            List<Dish> dishes = new ArrayList<>();
            cursor.moveToFirst();
            Log.i(LOG_TAG, "Loading dishes:");
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String categoryName = cursor.getString(2);
                double price = cursor.getDouble(3);
                String beginDate = cursor.getString(4);
                String endDate = cursor.getString(5);
                long supplier_id = cursor.getLong(6);
                Dish dish = new Dish(id, name, categoryName, price, beginDate, endDate, supplier_id);
                dishes.add(dish);
                Log.i(LOG_TAG, dish.getName());
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
