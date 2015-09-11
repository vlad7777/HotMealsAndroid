package com.ericpol.hotmeals.RetrofitTools;

import java.util.List;

import com.ericpol.hotmeals.Entities.Category;
import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Supplier;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by vlad on 14.8.15.
 */
public interface HotmealsApi {

    @GET("/hotmeals/suppliers")
    public List<Supplier> fetchSuppliers();

    @GET("/hotmeals/suppliers/{userId}/dishes")
    public List<Dish> fetchDishes(@Path("userId") String userId);

    @GET("/hotmeals/suppliers/{supplierId}/dishes/{date}")
    public List<Dish> fetchDishes(@Path("supplierId") String supplierId, @Path("date") String date);

    @GET("/hotmeals/dishes")
    public List<Dish> fetchDishes();

    @GET("/hotmeals/category")
    public List<Category> fetchCategories();

    @GET("/hotmeals/suppliers/{supplierId}/categories")
    public List<Category> fetchCategories(@Path("supplierId") String supplierId);

}
