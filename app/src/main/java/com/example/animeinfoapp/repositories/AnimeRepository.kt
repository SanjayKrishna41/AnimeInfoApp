package com.example.animeinfoapp.repositories

import com.example.animeinfoapp.api.RetrofitInstance
import javax.inject.Inject

class AnimeRepository @Inject constructor() {

    suspend fun getTopAnime() = RetrofitInstance.api.getTopAnime()

    suspend fun getAnimeById(id:Int) = RetrofitInstance.api.getAnimeById(id)
}