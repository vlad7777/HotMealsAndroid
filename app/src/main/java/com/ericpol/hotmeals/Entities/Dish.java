package com.ericpol.hotmeals.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vlad on 10.8.15.
 */

public class Dish implements Comparable<Dish>, Parcelable{

    // TODO: 11/09/15 remove categoryName field 

    private long id;

    private String name;

    private long categoryId;

    private double price;

    private String dateBegin;

    private String dateEnd;

    private long supplierId;

    public Dish(long id, String name, long categoryId, double price, String dateBegin, String dateEnd, long supplierId)
    {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.supplierId = supplierId;
    }

    public Dish(long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Override
    public int compareTo(Dish dish)
    {
        if (id < dish.id)
            return -1;
        else if (id == dish.id)
            return 0;
        else
            return 1;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(id);
        out.writeString(name);
        out.writeLong(categoryId);
        out.writeDouble(price);
        out.writeString(dateBegin);
        out.writeString(dateEnd);
        out.writeLong(supplierId);
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        public Dish createFromParcel(Parcel in) {
            long id = in.readLong();
            String name = in.readString();
            long categoyId = in.readLong();
            double price = in.readDouble();
            String beginDate = in.readString();
            String endDate = in.readString();
            long supplier_id = in.readLong();
            return new Dish(id,name, categoyId, price, beginDate, endDate, supplier_id);
        }

        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
}
