package com.ericpol.hotmeals.Presenter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.LoginActivity;
import com.ericpol.hotmeals.R;
import com.ericpol.hotmeals.RetrofitTools.SecuredRestException;
import com.ericpol.hotmeals.SuppliersListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 21.8.15.
 */

public class SuppliersListPresenter /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

    private static final String LOG_TAG = SuppliersListPresenter.class.getName();

    SuppliersListFragment mFragment;
    DBPopulator dbPopulator;

    public SuppliersListPresenter(SuppliersListFragment fragment) {
        mFragment = fragment;
        dbPopulator = new DBPopulator(fragment.getActivity().getContentResolver(), mFragment.getActivity());
    }

    public void populate() {
        dbPopulator.checkSuppliers();
        FetchSuppliersTask task = new FetchSuppliersTask();
        task.execute();
    }

    private class FetchSuppliersTask extends AsyncTask<List<Supplier>, Void, List<Supplier>> {

        private static final String LOG_TAG = "FetchSuppliersTask";
        private List<Supplier> target;

        protected List<Supplier> doInBackground(List<Supplier>... params) {
            String[] projection = new String[]{HotMealsContract.SupplierEntry._ID, HotMealsContract.SupplierEntry.COLUMN_NAME};
            Cursor cursor = mFragment.getActivity().getContentResolver().query(HotMealsContract.SupplierEntry.CONTENT_URI, projection, null, null, null);

            List<Supplier> suppliers = new ArrayList<>();
            cursor.moveToFirst();
            Log.i(LOG_TAG, "Loading suppliers:");
            while (!cursor.isAfterLast()) {
                Supplier supplier = new Supplier(cursor.getLong(0), cursor.getString(1));
                suppliers.add(supplier);
                Log.i(LOG_TAG, supplier.getName());
                cursor.moveToNext();
            }
            cursor.close();

            return suppliers;
        }

        protected void onPostExecute(List<Supplier> suppliers) {
            mFragment.updateAdapter(suppliers);
        }
    }
}