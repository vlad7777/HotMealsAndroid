package com.ericpol.hotmeals.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ericpol.hotmeals.Data.HotMealsContract.SupplierEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.DishEntry;
import com.ericpol.hotmeals.Data.HotMealsContract.UpdateTimeEntry;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Supplier;
import com.ericpol.hotmeals.RetrofitTools.HotmealsApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by vlad on 20.8.15.
 */

public class HotMealsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    public static final String DATABASE_NAME = "hotmeals.db";

    private static final String LOG_TAG = HotMealsDbHelper.class.getName();

    public HotMealsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(LOG_TAG, "Creating database");
        final String SQL_CREATE_SUPPLIERS_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " (" +
                SupplierEntry._ID + " INTEGER PRIMARY KEY, " +
                SupplierEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                SupplierEntry.COLUMN_ADDRESS + " TEXT, " +
                SupplierEntry.COLUMN_COORD_LAT + " REAL, " +
                SupplierEntry.COLUMN_COORD_LONG + " REAL " +
                " );";

        final String SQL_CREATE_DISHES_TABLE = "CREATE TABLE " + DishEntry.TABLE_NAME + " (" +
                DishEntry._ID + " INTEGER PRIIMARY KEY, " +
                DishEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DishEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                DishEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                DishEntry.COLUMN_BEGIN_DATE + " TEXT NOT NULL, " +
                DishEntry.COLUMN_END_DATE + " TEXT NOT NULL, " +
                DishEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY(" + DishEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + "(" + SupplierEntry._ID + " ) " +
                " );";

        final String SQL_CREATE_UPDATE_TABLE = "CREATE TABLE " + UpdateTimeEntry.TABLE_NAME + " (" +
                UpdateTimeEntry.COLUMN_SUPPLIER_ID + " INTEGER PRIMARY KEY, " +
                UpdateTimeEntry.COLUMN_UPDATE_TIME + " TEXT NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DISHES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_UPDATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "updating database");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DishEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UpdateTimeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
