package com.ericpol.hotmeals.Entities;

/**
 * Created by vlad on 10.8.15.
 */
public class Category {

    private int id;

    private int supplierId;

    private String name;

    public Category(int id, String name, int supplierId) {
        this.id = id;
        this.name = name;
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
