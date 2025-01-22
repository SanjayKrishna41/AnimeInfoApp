package com.example.animeinfoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.animeinfoapp.models.AnimeResponse
import com.example.animeinfoapp.repositories.AnimeRepository
import com.example.animeinfoapp.utils.CheckNetworkConnection
import com.example.animeinfoapp.utils.Constants.Companion.NO_INTERNET
import com.example.animeinfoapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AnimeViewModel(
    app: Application,
    private val animeRepository: AnimeRepository
) : AndroidViewModel(app) {

    // variable to access internet connection for safe network calls
    private val isConnected = CheckNetworkConnection(app)

    private val topAnime: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()

    private val animeDataById: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()

    init {
        getTopAnime()
    }

    private fun getTopAnime() = viewModelScope.launch {
        topAnime.postValue(Resource.Loading())
        try {
            if (isConnected.value == true) {
                val response = animeRepository.getTopAnime()
                topAnime.postValue(handleAnimeResponse(response))
            } else {
                animeDataById.postValue(Resource.Error(NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topAnime.postValue(Resource.Error("Network failure"))
                else -> topAnime.postValue(Resource.Error("Json conversion error"))
            }
        }
    }

    private fun getAnimeById(id: Int) = viewModelScope.launch {
        animeDataById.postValue(Resource.Loading())
        try {
            if (isConnected.value == true) {
                val response = animeRepository.getAnimeById(id)
                animeDataById.postValue(handleAnimeResponse(response))
            } else {
                animeDataById.postValue(Resource.Error(NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topAnime.postValue(Resource.Error("Network failure"))
                else -> topAnime.postValue(Resource.Error("Json conversion error"))
            }
        }
    }


    private fun handleAnimeResponse(response: Response<AnimeResponse>): Resource<AnimeResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}