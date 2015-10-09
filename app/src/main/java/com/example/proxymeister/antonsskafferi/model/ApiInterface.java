package com.example.proxymeister.antonsskafferi.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ApiInterface {

    //// Menu Item Resource

    // Gets all items currently on a menu
    @GET("menu/{menu_id}/group/1/item")
    Call<List<Item>> getMenuItems(@Path("menu_id") int menuId);

    // Get 'expanded' (all groups with items) menu
    @GET("menu/{menu_id}?expand=true")
    Call<Menu> getMenu(@Path("menu_id") int menuId);

    // Removes an item from the menu group
    @DELETE("menu/{menu_id}/{group_id}/item/{item_id}")
    Call<Void> removeMenuItem(@Path("menu_id") int menuId, @Path("group_id") int groupId, @Path("item_id") int itemId);


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

    @GET("order")
    Call<List<Order>> getOrdersByStatus(@Query("status") String status);

    @POST("order")
    Call<Void> createOrder(@Body Order order);

    @PUT("order/{order_id}/group/{group_id}")
    Call<Void> changeStatus(@Body Group group, @Path("order_id") int orderId, @Path("group_id") int groupId);

    //// Article Resource

    @GET("storage")
    Call<List<Article>> getArticles();

    @GET("storage/{article_id}")
    Call<Article> getArticle (@Path("article_id") int articleId);

    @PUT("storage/{article_id}")
    Call<Void> changeArticle(@Path("article_id") int articleId);

    @DELETE("storage/{article_id}")
    Call<Item> removeArticle(@Path("article_id") int articleId);
}

