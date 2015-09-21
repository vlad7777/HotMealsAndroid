package com.ericpol.hotmeals.Presenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract;
import com.ericpol.hotmeals.Entities.Category;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.LoginActivity;
import com.ericpol.hotmeals.R;
import com.ericpol.hotmeals.RetrofitTools.HotmealsApi;

import com.ericpol.hotmeals.Data.HotMealsContract.SupplierEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.UpdateTimeEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.CategoryEntry;
import com.ericpol.hotmeals.RetrofitTools.SecuredRestBuilder;
import com.ericpol.hotmeals.RetrofitTools.SecuredRestException;
import com.ericpol.hotmeals.RetrofitTools.UnsafeHttpsClient;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.OkClient;

/**
 * Created by vlad on 23.8.15.
 */

public class DBPopulator {

    private static final class DummyData {

        public static final List<Dish> dishes;
        public static final List<Category> categories;
        public static final List<Supplier> suppliers;

        static {
            dishes = new ArrayList<>();
            dishes.add(new Dish(0, "Wine", 0, 34, "20150909", "20160909", 0));
            dishes.add(new Dish(1, "Coke", 0, 20, "20150909", "20160909", 0));
            dishes.add(new Dish(2, "Meat", 1, 30, "20150909", "20160909", 0));
            dishes.add(new Dish(3, "Zurek", 2, 30, "20150909", "20160909", 1));
            dishes.add(new Dish(4, "Ice cream", 3, 30, "20150909", "20160909", 1));
            dishes.add(new Dish(5, "Meat", 3, 30, "20150909", "20160909", 1));

            categories = new ArrayList<>();
            categories.add(new Category(0, "Drink", 0));
            categories.add(new Category(1, "Main course", 0));
            categories.add(new Category(2, "soup", 1));
            categories.add(new Category(3, "Dessert", 1));

            suppliers = new ArrayList<>();
            suppliers.add(new Supplier(0, "Vociferous phatasm"));
            suppliers.add(new Supplier(1, "Obstreperous ferrule"));
        }

        public static List<Dish> dishesById(long id) {
            if (id == -1)
                return dishes;
            List<Dish> result = new ArrayList<>();
            for (Dish dish : dishes) {
                if (dish.getSupplierId() == id)
                    result.add(dish);
            }
            return result;
        }

        public static List<Category> categoriesById(long id) {
            if (id == -1)
                return categories;
            List<Category> result = new ArrayList<>();
            for (Category category : categories) {
                if (category.getSupplierId() == id)
                    result.add(category);
            }
            return result;
        }

    };

    private ContentResolver mContentResolver;
    private Context mContext;

    private final String LOG_TAG = DBPopulator.class.getName();

    private final String SERVICE_URL = "https://10.0.2.2:8080/";
    private final String TOKEN_PATH = "oauth/token";
    private final String CLIENT_SECRET = "secret";
    private final String CLIENT_ID = "mobile";

    private boolean flagDishes, flagCategories;
    private boolean useDummy = false;

    public DBPopulator(ContentResolver contentResolver, Context context) {
        mContentResolver = contentResolver;
        mContext = context;
    }

    private String getToken() {
        String prefName = mContext.getResources().getString(R.string.pref_name);
        SharedPreferences pref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String tokenFieldName = mContext.getResources().getString(R.string.token);
        String defaultValue = "";
        return pref.getString(tokenFieldName, defaultValue);
    }

    private HotmealsApi getApi() {
        return new SecuredRestBuilder().
                setLoginEndpoint(SERVICE_URL + TOKEN_PATH).
                setClientId(CLIENT_ID).
                setClientSecret(CLIENT_SECRET).
                setClient(new OkClient(UnsafeHttpsClient.createUnsafeClient())).
                setEndpoint(SERVICE_URL).
                setLogLevel(RestAdapter.LogLevel.FULL).
                setToken(getToken()).build().create(HotmealsApi.class);
    }

    private void startLogInActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    public void checkUpdateForSupplier(long supplierId) {
        Cursor c = mContentResolver.query(HotMealsContract.UpdateTimeEntry.buildUriFromId(supplierId), new String[]{UpdateTimeEntry.COLUMN_UPDATE_TIME}, null, null, null);
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
            Log.w(LOG_TAG, "updating db dishes");
            flagCategories = false;
            flagDishes = false;
            new UpdateDishesTask().execute(supplierId);
            new UpdateCategoriesTask().execute(supplierId);
        }
    }

    private void updateSuccess(long supplierId) {
        mContentResolver.delete(UpdateTimeEntry.buildUriFromId(supplierId), null, null);

        Log.i(LOG_TAG, "Update successful for " + supplierId);
        DateFormat df = new SimpleDateFormat("yyyyMMddkk");
        String nDate = df.format(new Date());
        ContentValues cv = new ContentValues();
        cv.put(UpdateTimeEntry.COLUMN_UPDATE_TIME, nDate);
        cv.put(UpdateTimeEntry.COLUMN_SUPPLIER_ID, supplierId);

        mContentResolver.insert(UpdateTimeEntry.CONTENT_URI, cv);
    }

    public void checkSuppliers() {
        Cursor c = mContentResolver.query(HotMealsContract.UpdateTimeEntry.buildUriFromId(-1), new String[]{UpdateTimeEntry.COLUMN_UPDATE_TIME}, null, null, null);
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
            Log.w(LOG_TAG, "updating db suppliers");
            UpdateSuppliersTask task = new UpdateSuppliersTask();
            task.execute();
        }
    }

    private class UpdateDishesTask extends AsyncTask<Long, Void, Integer> {
        private Long supplierId;

        public Integer doInBackground(Long... params) {
            supplierId = params[0];

            List<Dish> dishes = null;
            try {
                dishes = getApi().fetchDishes(Long.toString(supplierId));
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "retrofitError");
                if (e.getResponse() == null)
                    Log.e(LOG_TAG, e.toString());
                else if (e.getResponse().getStatus() == 400 || e.getResponse().getStatus() == 401) {
                    Log.e(LOG_TAG, "tried to fetch categories without being authorized");
                }
                else
                {
                    Log.e(LOG_TAG, "" + e.getResponse().getStatus());
                    Log.e(LOG_TAG, e.toString());
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }

            if (dishes == null)
                if (useDummy)
                    dishes = DummyData.dishesById(supplierId);
                else
                    return 0;

            String selection = DishEntry.COLUMN_SUPPLIER_ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(supplierId)};
            mContentResolver.delete(DishEntry.CONTENT_URI, selection, selectionArgs);

            Vector<ContentValues> cvVector = new Vector<ContentValues>();

            Log.i(LOG_TAG, "received dishes");

            for (Dish dish : dishes) {
                Log.i(LOG_TAG, dish.getName() + ' ' + dish.getId() + ' ' + dish.getSupplierId());
                ContentValues cv = new ContentValues();
                cv.put(DishEntry._ID, dish.getId());
                cv.put(DishEntry.COLUMN_NAME, dish.getName());
                cv.put(DishEntry.COLUMN_CATEGORY_ID, dish.getCategoryId());
                cv.put(DishEntry.COLUMN_PRICE, dish.getPrice());
                cv.put(DishEntry.COLUMN_BEGIN_DATE, dish.getDateBegin());
                cv.put(DishEntry.COLUMN_END_DATE, dish.getDateEnd());
                cv.put(DishEntry.COLUMN_SUPPLIER_ID, dish.getSupplierId());
                cvVector.add(cv);
            }

            mContentResolver.bulkInsert(DishEntry.CONTENT_URI, cvVector.toArray(new ContentValues[]{}));

            return 1;
        }

        public void onPostExecute(Integer success) {
            if (success.equals(1)) {
                flagDishes = true;
                if (flagCategories && flagDishes)
                    updateSuccess(supplierId);
            }
        }
    }

    private class UpdateSuppliersTask extends AsyncTask<Long, Void, Integer> {
        private final long supplierId = -1;

        public Integer doInBackground(Long... params) {

            List<Supplier> suppliers = null;
            try {
                suppliers = getApi().fetchSuppliers();
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "retrofitError");
                if (e.getResponse() == null)
                    Log.e(LOG_TAG, e.toString());
                else if (e.getResponse().getStatus() == 400 || e.getResponse().getStatus() == 401) {
                    Log.e(LOG_TAG, "tried to fetch categories without being authorized");
                    return -1;
                }
                else
                {
                    Log.e(LOG_TAG, "" + e.getResponse().getStatus());
                    Log.e(LOG_TAG, e.toString());
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }


            if (suppliers == null)
                if (useDummy) {
                    suppliers = DummyData.suppliers;
                    Log.i(LOG_TAG, "used dummy data");
                }
                else
                    return 0;

            mContentResolver.delete(SupplierEntry.CONTENT_URI, null, null);
            Vector<ContentValues> cvVector = new Vector<ContentValues>();
            Log.i(LOG_TAG, "suppliers received:");
            for (Supplier supplier : suppliers) {
                Log.i(LOG_TAG, supplier.getName() + " " + supplier.getId());
                ContentValues cv = new ContentValues();
                cv.put(SupplierEntry._ID, supplier.getId());
                cv.put(SupplierEntry.COLUMN_NAME, supplier.getName());
                cv.put(SupplierEntry.COLUMN_ADDRESS, supplier.getAddress());
                cv.put(SupplierEntry.COLUMN_COORD_LAT, supplier.getLat());
                cv.put(SupplierEntry.COLUMN_COORD_LONG, supplier.getLng());
                cvVector.add(cv);
            }

            mContentResolver.bulkInsert(SupplierEntry.CONTENT_URI, cvVector.toArray(new ContentValues[]{}));

            return 1;
        }

        public void onPostExecute(Integer success) {
            if (success.equals(1)) {
                updateSuccess(supplierId);
            } else if (success.equals(-1)) {
                Log.i(LOG_TAG, "not logged in, starting login activity");
                startLogInActivity();
            }
        }
    }

    private class UpdateCategoriesTask extends AsyncTask<Long, Void, Integer> {
        private Long supplierId;

        public Integer doInBackground(Long... params) {
            supplierId = params[0];

            List<Category> categories = null;
            try {
                categories = getApi().fetchCategories(Long.toString(supplierId));
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "retrofitError");
                if (e.getResponse() == null)
                    Log.e(LOG_TAG, e.toString());
                else if (e.getResponse().getStatus() == 400 || e.getResponse().getStatus() == 401) {
                    Log.e(LOG_TAG, "tried to fetch categories without being authorized");
                }
                else
                {
                    Log.e(LOG_TAG, "" + e.getResponse().getStatus());
                    Log.e(LOG_TAG, e.toString());
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }

            if (categories == null)
                if (useDummy)
                       categories = DummyData.categoriesById(supplierId);
                else
                    return 0;

            String selection = CategoryEntry.COLUMN_SUPPLIER_ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(supplierId)};
            mContentResolver.delete(CategoryEntry.CONTENT_URI, selection, selectionArgs);

            Vector<ContentValues> cvVector = new Vector<ContentValues>();

            for (Category category : categories) {
                ContentValues cv = new ContentValues();
                cv.put(CategoryEntry._ID, category.getId());
                cv.put(CategoryEntry.COLUMN_NAME, category.getName());
                cv.put(CategoryEntry.COLUMN_SUPPLIER_ID, category.getSupplierId());
                cvVector.add(cv);
            }

            mContentResolver.bulkInsert(CategoryEntry.CONTENT_URI, cvVector.toArray(new ContentValues[]{}));

            return 1;
        }

        public void onPostExecute(Integer success) {
            if (success.equals(1)) {
                flagCategories = true;
                if (flagCategories && flagDishes)
                    updateSuccess(supplierId);
            }
        }
    }
}
