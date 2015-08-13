package com.ericpol.hotmeals.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vlad on 10.8.15.
 */
public class Order implements Parcelable{

    private int id;

    private String date;

    private User user;

    private List<Dish> dishes = new ArrayList<>();

    public Order(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeInt(dishes.size());
        for (Dish d : dishes) {
            out.writeParcelable(d, flags);
        }
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel in) {
            int dishNo = in.readInt();
            List<Dish> dishes = new ArrayList<>();
            for (int i = 0; i < dishNo; i++) {
                Dish d = in.readParcelable(Dish.class.getClassLoader());
                dishes.add(d);
            }
            return new Order(dishes);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
