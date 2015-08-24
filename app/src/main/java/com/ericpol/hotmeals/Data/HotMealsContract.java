package com.ericpol.hotmeals.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vlad on 20.8.15.
 */
public class HotMealsContract {

    public static final String CONTENT_AUTHORITY = "com.ericpol.hotmeals";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUPPLIERS = "suppliers";

    public static final String PATH_DISHES = "dishes";

    public static final String PATH_UPDATE = "update";

    public static final class SupplierEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUPPLIERS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        public static final String TABLE_NAME = "suppliers";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_ADDRESS = "address";

        public static final String COLUMN_COORD_LAT = "coord_lat";

        public static final String COLUMN_COORD_LONG = "coord_long";

        public static final Uri buildSupplierUriFromId(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static final String getSupplierIdFromUri(Uri uri) {
            try {
                return uri.getPathSegments().get(1);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static final class DishEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISHES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISHES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISHES;

        public static final String TABLE_NAME = "dishes";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_CATEGORY_NAME = "category_name";

        public static final String COLUMN_SUPPLIER_ID = "supplier_id";

        public static final String COLUMN_PRICE = "price";

        public static final String COLUMN_BEGIN_DATE = "begin_date";

        public static final String COLUMN_END_DATE = "end_date";

        public static final Uri buildDishUriFromId(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static final Uri buildDishUriFromSupplierId(long id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUPPLIERS).appendPath(Long.toString(id)).appendPath(PATH_DISHES).build();
        }

        public static final Uri buildDishUriFromSupplierIdAndDate(long id, String date) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUPPLIERS).appendPath(Long.toString(id)).appendPath(PATH_DISHES).appendPath(date).build();
        }

        public static final String getDateFromUri(Uri uri) {
            try {
                return uri.getPathSegments().get(3);
            } catch (Exception e) {
                return null;
            }
        }

        public static final String getDishIdFromUri(Uri uri) {
            try {
                return uri.getPathSegments().get(1);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static final class UpdateTimeEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_UPDATE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_UPDATE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_UPDATE;

        public static final String TABLE_NAME = "update_time";

        public static final String COLUMN_UPDATE_TIME = "update_time";

    }
}
