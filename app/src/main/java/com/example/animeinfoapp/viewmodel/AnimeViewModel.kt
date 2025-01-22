package com.example.animeinfoapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeinfoapp.models.AnimeResponse
import com.example.animeinfoapp.repositories.AnimeRepository
import com.example.animeinfoapp.utils.CheckNetworkConnection
import com.example.animeinfoapp.utils.Constants.Companion.NO_INTERNET
import com.example.animeinfoapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val network: CheckNetworkConnection,
    private val animeRepository: AnimeRepository
) : ViewModel() {

    val topAnime: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()

    val animeDataById: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()

    val networkStatus: LiveData<Boolean> = network.isConnected

    init {
        networkStatus.observeForever { status ->
            if (status) {
                getTopAnime()
            } else {
                topAnime.postValue(Resource.Error(NO_INTERNET))
            }
        }
    }

    private fun getTopAnime() = viewModelScope.launch {

        topAnime.postValue(Resource.Loading())
        try {
            Log.e("View model", "inside try")
            networkStatus.value?.let { connected ->
                if (connected) {
                    val response = animeRepository.getTopAnime()
                    topAnime.postValue(handleAnimeResponse(response))
                } else {
                    Log.e("ViewModel", "inside else $connected")
                    topAnime.postValue(Resource.Error(NO_INTERNET))
                }
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> topAnime.postValue(Resource.Error("Network failure"))
                else -> topAnime.postValue(Resource.Error("Json conversion error - ${t.message}"))
            }
        }
    }

    private fun getAnimeById(id: Int) = viewModelScope.launch {
        animeDataById.postValue(Resource.Loading())
        try {
            if (network.isConnected.value == true) {
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
            Log.e("handleAnimeResponse", "$response.isSuccessful")
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun connectionRetry() {
        network.checkNetworkConnection()
    }

    override fun onCleared() {
        super.onCleared()
        network.closeNetworkConnection()
    }
}