package com.example.newschatbot;

import com.example.newschatbot.Model.Headlines;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("top-headlines")
    Call<Headlines> getHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<Headlines> getSpecificData(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
    // Add the new method for category-based news
    @GET("top-headlines")
    Call<Headlines> getCategoryNews(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );




}
