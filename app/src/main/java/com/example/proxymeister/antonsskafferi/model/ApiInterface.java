package com.example.proxymeister.antonsskafferi.model;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ApiInterface {
    @GET("menu/{id}/item")
    Call<List<Item>> getItems(@Path("id") int id);
}

