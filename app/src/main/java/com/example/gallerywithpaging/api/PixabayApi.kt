package com.example.pagergallerypaging.api

import com.example.pagergallerypaging.model.Hit
import com.example.pagergallerypaging.model.PixabayResponse
import com.example.pagergallerypaging.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    @GET("/api")
    suspend fun getPixabayResponse(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") query: String,
        @Query("page") page: Int = 1, // 預設Pixabay最多給500項搜索值
        @Query("per_page") perPage: Int = 100 // 3~200, default 20
    ): Response<PixabayResponse>

    @GET("/api")
    suspend fun testPixabayResponse(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") query: String = "dog", // fix query
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<PixabayResponse>






}