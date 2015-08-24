package com.ericpol.hotmeals.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vlad on 10.8.15.
 */
public class Dish implements Comparable<Dish>, Parcelable{

    private long id;

    private String name;

    private String categoryName;

    private double price;

    private String dateBegin;

    private String dateEnd;

    private long supplier_id;

    public Dish(long id, String name, String categoryName, double price, String dateBegin, String dateEnd, long supplier_id)
    {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.price = price;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.supplier_id = supplier_id;
    }

    @Override
    public int compareTo(Dish dish)
    {
        int c1 = this.categoryName.compareTo(dish.getCategoryName());
        return c1 == 0 ? this.name.compareTo(dish.getName()) : c1;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(id);
        out.writeString(name);
        out.writeString(categoryName);
        out.writeDouble(price);
        out.writeString(dateBegin);
        out.writeString(dateEnd);
        out.writeLong(supplier_id);
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        public Dish createFromParcel(Parcel in) {
            long id = in.readLong();
            String name = in.readString();
            String categoryName = in.readString();
            double price = in.readDouble();
            String beginDate = in.readString();
            String endDate = in.readString();
            long supplier_id = in.readLong();
            return new Dish(id,name, categoryName, price, beginDate, endDate, supplier_id);
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

    public long getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(long supplier_id) {
        this.supplier_id = supplier_id;
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

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
