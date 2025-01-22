package com.example.animeinfoapp.api

import com.example.animeinfoapp.models.AnimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeApi {

    @GET("v4/top/anime")
    suspend fun getTopAnime(): Response<AnimeResponse>

    @GET("v4/anime/{id}")
    suspend fun getAnimeById(
        @Path("id") id: Int
    ): Response<AnimeResponse>
}