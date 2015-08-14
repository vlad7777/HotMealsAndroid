package com.ericpol.hotmeals.RetrofitTools;

import java.util.List;

import com.ericpol.hotmeals.Entities.Supplier;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by vlad on 14.8.15.
 */
public interface HotmealsApi {

    @GET("/hotmeals/suppliers")
    public void fetchSuppliers(Callback<List<Supplier>> response);
}
