package com.ericpol.hotmeals.Presenter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.DishesListFragment;
import com.ericpol.hotmeals.Entities.Category;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.DishesListFragment;

import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.CategoryEntry;

/**
 * Created by vlad on 24.8.15.
 */

public class DishesListPresenter implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DishesListPresenter.class.getName();

    public static class DishQueryParameters {

        public static final String[] DISH_PROJECTION = new String[]{
                DishEntry.TABLE_NAME + "." + DishEntry._ID,
                DishEntry.TABLE_NAME + "." + DishEntry.COLUMN_NAME,
                CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID,
                DishEntry.COLUMN_PRICE
        };

        public static final int COLUMN_DISH_ID = 0;
        public static final int COLUMN_DISH_NAME = 1;
        public static final int COLUMN_CATEGORY_ID = 2;
        public static final int COLUMN_DISH_PRICE = 3;

        public static final String SELECTION = CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " = ?";

    }

    public static class CategoryQueryParameters {

        public static final String[] CATEGORY_PROJECTION = new String[]{
                CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID,
                CategoryEntry.TABLE_NAME + "." + CategoryEntry.COLUMN_NAME
        };

        public static final int COLUMN_CATEGORY_ID = 0;
        public static final int COLUMN_CATEGORY_NAME = 1;
    }

    private static final int CATEGORIES_LOADER = -1;
    private static final int DISHES_LOADER = 1;

    DishesListFragment mFragment;
    DBPopulator dbPopulator;

    public void initCategoriesLoader() {
        mFragment.getActivity().getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
    }

    public void initDishesLoader(int id) {
        Log.i(LOG_TAG, "init dishes loader for id " + id);
        mFragment.getActivity().getSupportLoaderManager().restartLoader(id, null, this);
    }

    public DishesListPresenter(DishesListFragment fragment) {
        mFragment = fragment;
        dbPopulator = new DBPopulator(fragment.getActivity().getContentResolver(), mFragment.getActivity());
        dbPopulator.checkUpdateForSupplier(mFragment.getSupplierId());
        initCategoriesLoader();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CATEGORIES_LOADER) {
            return new CursorLoader(mFragment.getActivity(), HotMealsContract.CategoryEntry.buldUriFromSupplierAndDate(mFragment.getSupplierId(), mFragment.getDateString()),
                    CategoryQueryParameters.CATEGORY_PROJECTION, null, null, null);
        } else {
            String selectionsArgs[] = new String[]{Integer.toString(id)};
            Log.i(LOG_TAG, "starting dishes cursor for id " + id);
            return new CursorLoader(mFragment.getActivity(), HotMealsContract.DishEntry.buildDishUriFromSupplierIdAndDate(mFragment.getSupplierId(), mFragment.getDateString()),
                    DishQueryParameters.DISH_PROJECTION, DishQueryParameters.SELECTION, selectionsArgs, null);
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "received cursor " + loader.getId());
        if (!data.isClosed()) {
            data.moveToFirst();
            while (!data.isAfterLast()) {
                String s = "";
                for (int i = 0; i < data.getColumnCount(); i++)
                    s += data.getColumnName(i) + ":" + data.getString(i) + " ";
                Log.i(LOG_TAG, s);
                data.moveToNext();
            }
            data.moveToFirst();
            if (loader.getId() == CATEGORIES_LOADER) {
                mFragment.getAdapter().setGroupCursor(data);
            } else {
                mFragment.getAdapter().setChildrenCursorById(loader.getId(), data);
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CATEGORIES_LOADER) {
            mFragment.getAdapter().setGroupCursor(null);
        } else {
            mFragment.getAdapter().setChildrenCursorById(loader.getId(), null);
        }
    }
}
