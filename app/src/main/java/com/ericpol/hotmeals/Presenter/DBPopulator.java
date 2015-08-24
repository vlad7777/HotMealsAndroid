package com.ericpol.hotmeals.Presenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.RetrofitTools.HotmealsApi;

import com.ericpol.hotmeals.Data.HotMealsContract.SupplierEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.UpdateTimeEntry;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit.RestAdapter;

/**
 * Created by vlad on 23.8.15.
 */
public class DBPopulator {

    private ContentResolver mContentResolver;

    private final String LOG_TAG = DBPopulator.class.getName();

    private final String API = "http://2dad28c6.ngrok.com/";

    public DBPopulator(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public void checkUpdate() {
        Cursor c = mContentResolver.query(HotMealsContract.UpdateTimeEntry.CONTENT_URI, null, null, null, null);
        String stringDate = "0000000000";
        if (c.moveToFirst())
            stringDate = c.getString(0);
        else
            Log.e(LOG_TAG, "error reading from db");
        c.close();
        DateFormat df = new SimpleDateFormat("yyyyMMddkk");
        Date date;
        try {
            date = df.parse(stringDate);
        } catch (ParseException e) {
            date = new Date();
        }

        Date current = new Date();
        Log.i(LOG_TAG, "update was " + stringDate);
        if (current.getTime() - date.getTime() >= 1000 * 60 * 60 * 12) {
            Log.w(LOG_TAG, "updating db");
            UpdateDatabaseTask task = new UpdateDatabaseTask();
            task.execute();
        }
    }

    private class UpdateDatabaseTask extends AsyncTask<Void, Void, Integer> {

        public Integer doInBackground(Void... params) {
            RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();
            HotmealsApi api = restAdapter.create(HotmealsApi.class);

            List<Supplier> suppliers = null;
            List<Dish> dishes = null;
            try {
                suppliers = api.fetchSuppliers();
                dishes = api.fetchDishes();
            } catch (Exception e) {
                Log.e(LOG_TAG, "couldn't fetch data");
            }
            if (suppliers == null || dishes == null)
                return 0;

            mContentResolver.delete(SupplierEntry.CONTENT_URI, null, null);
            mContentResolver.delete(DishEntry.CONTENT_URI, null, null);
            mContentResolver.delete(UpdateTimeEntry.CONTENT_URI, null, null);

            for (Supplier supplier : suppliers) {
                ContentValues cv = new ContentValues();
                cv.put(SupplierEntry._ID, supplier.getId());
                cv.put(SupplierEntry.COLUMN_NAME, supplier.getName());
                cv.put(SupplierEntry.COLUMN_ADDRESS, supplier.getAddress());
                cv.put(SupplierEntry.COLUMN_COORD_LAT, supplier.getLat());
                cv.put(SupplierEntry.COLUMN_COORD_LONG, supplier.getLng());
                mContentResolver.insert(SupplierEntry.CONTENT_URI, cv);
            }

            for (Dish dish : dishes) {
                ContentValues cv = new ContentValues();
                cv.put(DishEntry._ID, dish.getId());
                cv.put(DishEntry.COLUMN_NAME, dish.getName());
                cv.put(DishEntry.COLUMN_CATEGORY_NAME, dish.getCategoryName());
                cv.put(DishEntry.COLUMN_PRICE, dish.getPrice());
                cv.put(DishEntry.COLUMN_BEGIN_DATE, dish.getDateBegin());
                cv.put(DishEntry.COLUMN_END_DATE, dish.getDateEnd());
                cv.put(DishEntry.COLUMN_SUPPLIER_ID, dish.getSupplier_id());
                mContentResolver.insert(DishEntry.CONTENT_URI, cv);
            }

            return 1;
        }

        public void onPostExecute(Integer success) {
            if (success.equals(1)) {
                DateFormat df = new SimpleDateFormat("yyyyMMddkk");
                String nDate = df.format(new Date());
                ContentValues cv = new ContentValues();
                cv.put(UpdateTimeEntry.COLUMN_UPDATE_TIME, nDate);
                mContentResolver.delete(UpdateTimeEntry.CONTENT_URI, null, null);
                mContentResolver.insert(UpdateTimeEntry.CONTENT_URI, cv);
            }
        }
    }
}
