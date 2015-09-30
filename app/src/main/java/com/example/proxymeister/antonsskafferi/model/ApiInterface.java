package com.example.proxymeister.antonsskafferi.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ApiInterface {

    //// Menu Item Resource

    // Gets all items currently on a menu
    @GET("menu/{menu_id}/item")
    Call<List<Item>> getMenuItems(@Path("menu_id") int menuId);

    // Removes an item from the menu
    @DELETE("menu/{menu_id}/item/{item_id}")
    Call<Item> removeMenuItem(@Path("menu_id") int menuId, @Path("item_id") int itemId);



    //// Item Resource

    // Get item by id
    @GET("item/{item_id}")
    Call<Item> getItem(@Path("item_id") int itemId);

    // Get all items
    @GET("item")
    Call<List<Item>> getItems();

    //// Order Resource

    // Get order by id
    @GET("order/{order_id}")
    Call<Order> getOrder(@Path("order_id") int orderId);

    // Get all items (this is kinda useless)
    // in the future we need to add a filtering to the api
    // for example to get all orders that have groups ready for the kitchen
    @GET("order")
    Call<List<Order>> getOrders();
}

