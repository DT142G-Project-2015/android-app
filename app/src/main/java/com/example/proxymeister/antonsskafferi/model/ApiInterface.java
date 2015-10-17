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

    // Get all menus
    @GET("menu")
    Call<List<Menu>> getMenus();

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

    @POST("order/{order_id}/group/{group_id}/item")
    Call<IdHolder> addItem(@Body Item item, @Path("order_id") int orderId, @Path("group_id") int groupId);

    @POST("order/{order_id}/group/{group_id}/item/{item_id}/subitem")
    Call<IdHolder> addSubItem(@Body Item subitem, @Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId);

    @POST("order/{order_id}/group")
    Call<Group> createOrderGroup(@Body Group group, @Path("order_id") int orderId);

    @PUT("order/{order_id}")
    Call<Void> updateOrderStatus(@Body Order order, @Path("order_id") int orderId);

    //// Article Resource

    @GET("storage")
    Call<List<Article>> getArticles();

    @GET("storage/{article_id}")
    Call<Article> getArticle (@Path("article_id") int articleId);

    @PUT("storage/{article_id}")
    Call<Void> changeArticle(@Path("article_id") int articleId, @Body Article article);

    @POST("order/{order_id}/group/{group_id}/item/{item_id}/note")
    Call<Void> addNote(@Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId, @Body Note note);

    @POST("order/{order_id}/group/{group_id}/item/{item_id}/subitem/{subitem_id}/note")
    Call<Void> addSubItemNote(@Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId, @Path("subitem_id") int subitemId, @Body Note note);


    @DELETE("order/{order_id}/group/{group_id}/item/{item_id}/note/{note_id}")
    Call<Void> deleteNote(@Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId, @Path("note_id") int noteId);

    @DELETE("order/{order_id}/group/{group_id}/item/{item_id}/subitem/{subitem_id}/note/{note_id}")
    Call<Void> deleteSubItemNote(@Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId, @Path("subitem_id") int subitemId, @Path("note_id") int noteId);

    @DELETE("storage/{article_id}")
    Call<Void> deleteArticle(@Path("article_id") int articleId);

    @DELETE("order/{order_id}/group/{group_id}/item/{item_id}")
    Call<Void> deleteItem(@Path("order_id") int orderId, @Path("group_id") int groupId, @Path("item_id") int itemId);

    @POST("storage")
    Call<Void> createArticle(@Body Article article);


    // Staff
    // Get all items
    @GET("staff")
    Call<List<Staff>> getStaffMembers();
}

