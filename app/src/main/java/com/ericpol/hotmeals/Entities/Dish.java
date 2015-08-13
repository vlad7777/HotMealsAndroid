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
    private int id;

    private int category_id;

    private String categoryName;

    private int supplier_id;

    private String name;

    private int price;

    private Date date;

    public Dish(int id, String categoryName, String name, int price)
    {
        this.id = id;
        this.categoryName = categoryName;
        this.name = name;
        this.price = price;
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
        out.writeInt(id);
        out.writeString(categoryName);
        out.writeString(name);
        out.writeInt(price);
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        public Dish createFromParcel(Parcel in) {
            int id = in.readInt();
            String categoryName = in.readString();
            String name = in.readString();
            int price = in.readInt();
            return new Dish(id, categoryName, name, price);
        }

        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
