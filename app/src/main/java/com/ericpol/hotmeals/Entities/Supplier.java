package com.ericpol.hotmeals.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Supplier implements Parcelable{

    private int id;

    private String name;

    private String address;

    private double lat;

    private double lng;

    public Supplier(int id, String name)
    {
        this.name = name;
        this.id = id;
    }

    public Supplier(int id, String name, String address, double lat, double lng)
    {
        this.name = name;
        this.id = id;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //parcelable
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeInt(id);
        out.writeString(name);
        out.writeString(address);
        out.writeDouble(lat);
        out.writeDouble(lng);
    }

    public static final Parcelable.Creator<Supplier> CREATOR = new Parcelable.Creator<Supplier>() {
        public Supplier createFromParcel(Parcel in) {
            int id = in.readInt();
            String name = in.readString();
            String address = in.readString();
            double lat = in.readDouble();
            double lng = in.readDouble();
            return new Supplier(id, name, address, lat, lng);
        }

        public Supplier[] newArray(int size) {
            return new Supplier[size];
        }
    };
}
