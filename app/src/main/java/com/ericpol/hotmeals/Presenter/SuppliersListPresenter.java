package com.ericpol.hotmeals.Presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class SuppliersListPresenter implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = SuppliersListPresenter.class.getName();

    private static final int SUPPLIERS_LOADER = 0;
    public static final int COLUMN_SUPPLIERS_ID = 0;
    public static final int COLUMN_SUPPLIERS_NAME = 1;


    SuppliersListFragment mFragment;
    DBPopulator dbPopulator;

    public SuppliersListPresenter(SuppliersListFragment fragment) {
        mFragment = fragment;
        dbPopulator = new DBPopulator(fragment.getActivity().getContentResolver(), mFragment.getActivity());
        mFragment.getLoaderManager().initLoader(SUPPLIERS_LOADER, null, this);
    }

    public void updateData() {
        dbPopulator.checkSuppliers();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{HotMealsContract.SupplierEntry._ID, HotMealsContract.SupplierEntry.COLUMN_NAME};
        return new CursorLoader(mFragment.getActivity(), HotMealsContract.SupplierEntry.CONTENT_URI, projection, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "received suppliers cursor");
        String s = "";
        data.moveToFirst();
        while (!data.isAfterLast()) {
            for (int i = 0; i < data.getColumnCount(); i++)
                s += data.getColumnName(i) + ":" + data.getString(i) + " ";
            Log.i(LOG_TAG, s);
            data.moveToNext();
        }
        data.moveToFirst();
        mFragment.getAdapter().swapCursor(data);
        mFragment.getAdapter().notifyDataSetChanged();
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mFragment.getAdapter().swapCursor(null);
    }

}