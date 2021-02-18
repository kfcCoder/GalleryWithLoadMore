package com.example.pagergallerypaging.repository

import com.example.pagergallerypaging.api.RetrofitInstance

class Repository {
    /**
     * q as searching input
     */
    suspend fun getPixabayResponse(q: String) = RetrofitInstance.api.getPixabayResponse(query = q)

    /**
     * DEBUG
     */
    suspend fun testPixabayResponse() = RetrofitInstance.api.testPixabayResponse()




}