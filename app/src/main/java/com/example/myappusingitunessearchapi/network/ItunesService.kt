package com.example.myappusingitunessearchapi.network

import com.example.myappusingitunessearchapi.models.ItunesResponse
import retrofit.Call
import retrofit.http.Query
import retrofit.http.GET

interface ItunesService {
    @GET("search")
    fun getItunes(
            @Query("term") term: String,
    ): Call<ItunesResponse>
}