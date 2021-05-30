package com.example.coviddi.DataModels;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("history")
    Call<post1> getPost(@Query("country") String country, @Query("status") String status);
}
