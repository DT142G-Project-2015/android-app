package com.example.proxymeister.antonsskafferi.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ApiInterface {
    @GET("menu/{id}/item")
    Call<List<Item>> getItems(@Path("id") int id);

    @GET("item/{id}")
    Call<List<Item>> getSingleItem(@Path("id") int id);

    //This order call currently does not work correctly. Suspected to be the cause of the problem!
    @GET("order/{id}")
    Call<List<Order>> getOrders(@Path("id") int id);
}

