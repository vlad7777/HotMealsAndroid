package com.ericpol.hotmeals.Tests;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.test.AndroidTestCase;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsDbHelper;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Supplier;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.ericpol.hotmeals.Data.HotMealsContract.SupplierEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.UpdateTimeEntry;

/**
 * Created by vlad on 24.8.15.
 */
public class ContentProviderTest extends AndroidTestCase {

    private static final String LOG_TAG = ContentProviderTest.class.getName();

    public void setUp() throws Exception {
        super.setUp();
        clearDB();
    }

    public void clearDB() {
        mContext.getContentResolver().delete(SupplierEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(DishEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(UpdateTimeEntry.CONTENT_URI, null, null);
        Cursor cursor;
        cursor = mContext.getContentResolver().query(SupplierEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(DishEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 0);
        cursor.close();
        cursor = mContext.getContentResolver().query(UpdateTimeEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 0);
        cursor.close();
    }

    public void testContentProvider() {

        clearDB();

        List<Supplier> suppliers = new ArrayList<Supplier>();
        List<Dish> dishes = new ArrayList<Dish>();

        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        Date yesterday = new Date(today.getTime() - (1000 * 60 * 60 * 24));

        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        String sToday = df.format(today);
        String sTomorrow = df.format(tomorrow);
        String sYesterday = df.format(yesterday);
        suppliers.add(new Supplier(1, "KFC", "a", 0, 0));
        suppliers.add(new Supplier(2, "McDonald's", "a", 0, 0));
        suppliers.add(new Supplier(3, "za", "a", 0, 0));

        dishes.add(new Dish(1, "Capricciosa CN", "Pizza", 79000, sToday, sTomorrow, 1));
        dishes.add(new Dish(2, "Margarita C", "Pizza", 78000, sToday, sToday, 1));
        dishes.add(new Dish(3, "Gatta N", "Pizza", 80000, sTomorrow, sTomorrow, 1));

        dishes.add(new Dish(4, "Chicken with fries CN", "Dessert", 79000, sToday, sTomorrow, 2));
        dishes.add(new Dish(5, "Curry wurst C", "Dessert", 78000, sToday, sToday, 2));
        dishes.add(new Dish(6, "Bulls Intestines N", "Dessert", 80000, sTomorrow, sTomorrow, 2));

        ContentResolver mContentResolver = mContext.getContentResolver();

        for (Supplier supplier : suppliers) {
            ContentValues cv = new ContentValues();
            cv.put(SupplierEntry._ID, supplier.getId());
            cv.put(SupplierEntry.COLUMN_NAME, supplier.getName());
            cv.put(SupplierEntry.COLUMN_ADDRESS, supplier.getAddress());
            cv.put(SupplierEntry.COLUMN_COORD_LAT, supplier.getLat());
            cv.put(SupplierEntry.COLUMN_COORD_LONG, supplier.getLng());
            Uri uri = mContentResolver.insert(SupplierEntry.CONTENT_URI, cv);
            Log.i(LOG_TAG, uri.toString());
            Cursor cursor = mContentResolver.query(uri, null, null, null, null);
            assertTrue(cursor.getCount() == 1);
            cursor.close();
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

            Uri uri = mContentResolver.insert(DishEntry.CONTENT_URI, cv);
            Cursor cursor = mContentResolver.query(uri, null, null, null, null);
            assertTrue(cursor.getCount() == 1);
            cursor.close();

        }

        Cursor cursor;

        cursor = mContentResolver.query(SupplierEntry.buildSupplierUriFromId(1), null, null, null, null);
        assertTrue("Insertion failed", cursor.getCount() == 1);
        Log.i(LOG_TAG, "" + cursor.getCount());
        cursor.close();

        mContentResolver.delete(SupplierEntry.buildSupplierUriFromId(1), null, null);

        cursor = mContentResolver.query(SupplierEntry.buildSupplierUriFromId(1), null, null, null, null);
        assertFalse("Deletion failed", cursor.getCount() > 0);
        cursor.close();

        cursor = mContentResolver.query(DishEntry.buildDishUriFromId(1), null, null, null, null);
        assertTrue("Insertion failed", cursor.getCount() == 1);
        cursor.close();

        mContentResolver.delete(DishEntry.buildDishUriFromId(1), null, null);

        cursor = mContentResolver.query(DishEntry.buildDishUriFromId(1), null, null, null, null);
        assertTrue("Deletion failed", cursor.getCount() == 0);
        cursor.close();

        cursor = mContentResolver.query(UpdateTimeEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 0);
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put(UpdateTimeEntry.COLUMN_UPDATE_TIME, "2312312");
        mContentResolver.insert(UpdateTimeEntry.CONTENT_URI, cv);

        cursor = mContentResolver.query(UpdateTimeEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 1);
        cursor.moveToFirst();
        assertEquals(cursor.getString(0), "2312312");
        cursor.close();

        mContentResolver.delete(UpdateTimeEntry.CONTENT_URI, null, null);

        cursor = mContentResolver.query(UpdateTimeEntry.CONTENT_URI, null, null, null, null);
        assertTrue(cursor.getCount() == 0);
        cursor.close();

    }
}
