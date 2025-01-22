package com.example.animeinfoapp.repositories

import com.example.animeinfoapp.api.RetrofitInstance

class AnimeRepository {

    suspend fun getTopAnime() = RetrofitInstance.api.getTopAnime()

    suspend fun getAnimeById(id:Int) = RetrofitInstance.api.getAnimeById(id)
}