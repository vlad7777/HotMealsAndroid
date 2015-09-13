package com.ericpol.hotmeals.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract.SupplierEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.UpdateTimeEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.CategoryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vlad on 19.8.15.
 */
public class HotMealsProvider extends ContentProvider {

    private static final String LOG_TAG = HotMealsProvider.class.getName();

    private static final UriMatcher sUriMatcher;
    private static final String sAuthority = "com.ericpol.hotmeals";
    private HotMealsDbHelper mDbHelper;
    private static final SQLiteQueryBuilder sQueryBuilder;
    private static final Map<String, String> sProjectionMap;

    private static final int SUPPLIERS = 1;
    private static final int SUPPLIERS_BY_ID = 2;
    private static final int DISHES = 3;
    private static final int DISHES_BY_SUPPLIER = 4;
    private static final int DISHES_BY_SUPPLIER_AND_DATE = 5;
    private static final int DISHES_BY_ID = 6;
    private static final int UPDATE = 7;
    private static final int UPDATE_BY_SUPPLIER = 8;
    private static final int CATEGORIES = 9;
    private static final int CATEGORIES_BY_ID = 10;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(sAuthority, "suppliers", SUPPLIERS);
        sUriMatcher.addURI(sAuthority, "suppliers/#", SUPPLIERS_BY_ID);
        sUriMatcher.addURI(sAuthority, "suppliers/#/dishes", DISHES_BY_SUPPLIER);
        sUriMatcher.addURI(sAuthority, "suppliers/#/dishes/*", DISHES_BY_SUPPLIER_AND_DATE);
        sUriMatcher.addURI(sAuthority, "dishes", DISHES);
        sUriMatcher.addURI(sAuthority, "dishes/#", DISHES_BY_ID);
        sUriMatcher.addURI(sAuthority, "update", UPDATE);
        sUriMatcher.addURI(sAuthority, "update/*", UPDATE_BY_SUPPLIER);
        sUriMatcher.addURI(sAuthority, "categories", CATEGORIES);
        sUriMatcher.addURI(sAuthority, "categories/#", CATEGORIES_BY_ID);

        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(
                        DishEntry.TABLE_NAME +
                        " INNER JOIN " +
                        CategoryEntry.TABLE_NAME + " ON " +
                        DishEntry.TABLE_NAME + "." +
                        DishEntry.COLUMN_CATEGORY_ID + " = " +
                        CategoryEntry.TABLE_NAME + "." +
                        CategoryEntry._ID
        );

        sProjectionMap = new HashMap<String, String>();

    }

    Cursor getSuppliers(Uri uri, String[] projection, String sortOrder) {
        String supplierId = SupplierEntry.getSupplierIdFromUri(uri);
        String selection = null;
        String[] selectionArgs = null;

        if (supplierId != null) {
            selection = SupplierEntry._ID + " = ?";
            selectionArgs = new String[]{supplierId};
        }

        return mDbHelper.getReadableDatabase().query(SupplierEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    Cursor getDishes(Uri uri, String[] projection, String sortOrder) {
        String dishId = DishEntry.getDishIdFromUri(uri);
        String selection = null;
        String[] selectionArgs = null;

        if (dishId != null) {
            selection = SupplierEntry._ID + " = ?";
            selectionArgs = new String[]{dishId};
        }

        return sQueryBuilder.query(
                mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    Cursor getDishesBySupplier(Uri uri, String[] projection, String sortOrder) {
        String supplierId = SupplierEntry.getSupplierIdFromUri(uri);
        String date = DishEntry.getDateFromUri(uri);

        String selection;
        String[] selectionArgs;

        if (date == null) {
            selection = DishEntry.COLUMN_SUPPLIER_ID + " = ?";
            selectionArgs = new String[]{supplierId};
        } else {
            selection = DishEntry.TABLE_NAME + "." + DishEntry.COLUMN_SUPPLIER_ID + " = ? AND " + DishEntry.COLUMN_BEGIN_DATE + " <= ?  AND " + DishEntry.COLUMN_END_DATE + " >= ?";
            selectionArgs = new String[]{supplierId, date, date};
        }

        return sQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    Cursor getUpdate(Uri uri, String[] projection, String sortOrder) {
        String id = UpdateTimeEntry.getIdFromUri(uri);
        String selection = null;
        String[] selectionArgs = null;
        if (id != null) {
            selection = UpdateTimeEntry.COLUMN_SUPPLIER_ID + " = ?";
            selectionArgs = new String[]{id};
        }
        return mDbHelper.getReadableDatabase().query(UpdateTimeEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    Cursor getCategories(Uri uri, String[] projection, String sortOrder) {
        String id = CategoryEntry.getIdFromUri(uri);
        String selection = null;
        String[] selectionArgs = null;
        if (id != null) {
            selection = CategoryEntry.COLUMN_SUPPLIER_ID + " = ?";
            selectionArgs = new String[]{id};
        }
        return mDbHelper.getReadableDatabase().query(CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private int deleteSupplier(Uri uri, String selection, String selectionArgs[]) {

        String supplierId = SupplierEntry.getSupplierIdFromUri(uri);
        List<String> list = new ArrayList<>();
        if (selectionArgs != null)
            Collections.addAll(list, selectionArgs);

        if (supplierId != null) {
            if (selection == null)
                selection = "";
            selection = selection + " AND " + SupplierEntry._ID + " = ?";
            list.add(supplierId);
        }
        String[] test = new String[]{};
        return mDbHelper.getWritableDatabase().delete(SupplierEntry.TABLE_NAME, selection, list.toArray(test));
    }

    private int deleteDish(Uri uri, String selection, String selectionArgs[]) {

        String dishId = DishEntry.getDishIdFromUri(uri);
        List<String> list = new ArrayList<>();
        if (selectionArgs != null)
            Collections.addAll(list, selectionArgs);

        if (dishId != null) {
            if (selection == null)
                selection = "";
            selection = selection + " AND " + DishEntry._ID + " = ?";
            list.add(dishId);
        }
        String[] test = new String[]{};
        return mDbHelper.getWritableDatabase().delete(DishEntry.TABLE_NAME, selection, list.toArray(test));
    }

    private int deleteCategory(Uri uri, String selection, String selectionArgs[]) {

        String id = CategoryEntry.getIdFromUri(uri);
        List<String> list = new ArrayList<>();
        if (selectionArgs != null)
            Collections.addAll(list, selectionArgs);

        if (id != null) {
            if (selection == null)
                selection = "";
            selection = selection + " AND " + CategoryEntry.COLUMN_SUPPLIER_ID + " = ?";
            list.add(id);
        }
        String[] test = new String[]{};
        return mDbHelper.getWritableDatabase().delete(CategoryEntry.TABLE_NAME, selection, list.toArray(test));
    }

    private int deleteUpdate(Uri uri, String selection, String selectionArgs[]) {

        String id = UpdateTimeEntry.getIdFromUri(uri);
        List<String> list = new ArrayList<>();
        if (selectionArgs != null)
            Collections.addAll(list, selectionArgs);

        if (id != null) {
            if (selection == null)
                selection = "";
            selection = selection + " AND " + UpdateTimeEntry.COLUMN_SUPPLIER_ID + " = ?";
            list.add(id);
        }
        String[] test = new String[]{};
        return mDbHelper.getWritableDatabase().delete(UpdateTimeEntry.TABLE_NAME, selection, list.toArray(test));
    }
    @Override
    public boolean onCreate() {
        Log.i(LOG_TAG, "Currently on thread " + Thread.currentThread().getName());
        mDbHelper = new HotMealsDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SUPPLIERS:
                return SupplierEntry.CONTENT_TYPE;

            case SUPPLIERS_BY_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;

            case DISHES:
            case DISHES_BY_SUPPLIER:
            case DISHES_BY_SUPPLIER_AND_DATE:
                return DishEntry.CONTENT_TYPE;

            case DISHES_BY_ID:
                return DishEntry.CONTENT_ITEM_TYPE;
            case UPDATE:
                return UpdateTimeEntry.CONTENT_TYPE;
            case UPDATE_BY_SUPPLIER:
                return UpdateTimeEntry.CONTENT_ITEM_TYPE;

            case CATEGORIES:
                return CategoryEntry.CONTENT_TYPE;
            case CATEGORIES_BY_ID:
                return CategoryEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor result;

        switch (sUriMatcher.match(uri)) {
            case SUPPLIERS:
            case SUPPLIERS_BY_ID:
                result = getSuppliers(uri, projection, sortOrder);
                break;

            case DISHES:
            case DISHES_BY_ID:
                result = getDishes(uri, projection, sortOrder);
                break;

            case DISHES_BY_SUPPLIER:
            case DISHES_BY_SUPPLIER_AND_DATE:
                result = getDishesBySupplier(uri, projection, sortOrder);
                break;

            case UPDATE:
            case UPDATE_BY_SUPPLIER:
                result = getUpdate(uri, projection, sortOrder);
                break;

            case CATEGORIES:
            case CATEGORIES_BY_ID:
                result = getCategories(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Uri returnUri;

        long _id = -1;

        switch (sUriMatcher.match(uri)) {
            case SUPPLIERS:
                _id = db.insert(SupplierEntry.TABLE_NAME, null, values);
                if (_id >= 0) {
                    returnUri = SupplierEntry.buildSupplierUriFromId(_id);
                } else
                    throw new android.database.SQLException("Failed to insert into " + uri);
                break;

            case DISHES:
                _id = db.insert(DishEntry.TABLE_NAME, null, values);
                if (_id >= 0) {
                    returnUri = DishEntry.buildDishUriFromId(_id);
                } else
                    throw new android.database.SQLException("Failed to insert into " + uri);
                break;

            case UPDATE:
                _id = db.insert(UpdateTimeEntry.TABLE_NAME, null, values);
                if (_id >= -1) {
                    returnUri = UpdateTimeEntry.CONTENT_URI;
                } else
                    throw new android.database.SQLException("Failed to insert into " + uri);
                break;

            case CATEGORIES:
                _id = db.insert(CategoryEntry.TABLE_NAME, null, values);
                if (_id >= 0) {
                    returnUri = CategoryEntry.CONTENT_URI;
                } else
                    throw new android.database.SQLException("Failed to insert into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (_id != -1)
            getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case SUPPLIERS:
                rowsDeleted = db.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUPPLIERS_BY_ID:
                rowsDeleted = deleteSupplier(uri, selection, selectionArgs);
                break;
            case DISHES:
                rowsDeleted = db.delete(DishEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DISHES_BY_ID:
                rowsDeleted = deleteDish(uri, selection, selectionArgs);
                break;
            case UPDATE:
                rowsDeleted = db.delete(UpdateTimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case UPDATE_BY_SUPPLIER:
                rowsDeleted = deleteUpdate(uri, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsDeleted = db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORIES_BY_ID:
                rowsDeleted = deleteCategory(uri, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }
        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case SUPPLIERS:
                rowsUpdated = db.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case DISHES:
                rowsUpdated = db.update(DishEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case UPDATE:
                rowsUpdated = db.update(UpdateTimeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CATEGORIES:
                rowsUpdated = db.update(CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case SUPPLIERS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SupplierEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case DISHES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DishEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case UPDATE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CategoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case CATEGORIES:

                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CategoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
