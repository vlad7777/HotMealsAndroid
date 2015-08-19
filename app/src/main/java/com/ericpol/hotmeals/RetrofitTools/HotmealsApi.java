package com.ericpol.hotmeals.RetrofitTools;

import java.util.List;

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
    public void fetchSuppliers(Callback<List<Supplier>> response);

    @GET("/hotmeals/suppliers/{userId}/dishes")
    public void fetchDishes(@Path("userId") String userId, Callback<List<Dish>> response);

    @GET("/hotmeals/suppliers/{supplierId}/dishes/{date}")
    public void fetchDishes(@Path("supplierId") String supplierId, @Path("date") String date, Callback<List<Dish>> response);
}
