package com.example.animeinfoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class CheckNetworkConnection @Inject constructor(
    context: Context
) {

    private val _isConnected = MutableLiveData<Boolean>().apply { value = true }
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Log.e("CheckNetworkConnection", "on Losing")
            _isConnected.postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.e("CheckNetworkConnection", "onLost")
            _isConnected.postValue(false)
        }

        override fun onUnavailable() {
            Log.e("CheckNetworkConnection", "onUnavailable")
            super.onUnavailable()
            _isConnected.postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.e("CheckNetworkConnection", "onAvailable")
            _isConnected.postValue(true)
        }
    }

    // function to check network
    fun checkNetworkConnection() {
        Log.e("CheckNetworkConnection", "on checkNetworkConnection")
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork != null) {
            val networkBuilder = NetworkRequest.Builder().apply {
                addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    .addTransportType(TRANSPORT_WIFI)
                    .addTransportType(TRANSPORT_CELLULAR)
                    .addTransportType(TRANSPORT_ETHERNET)
            }.build()
            connectivityManager.registerNetworkCallback(networkBuilder, networkCallback)
        }
    }

    fun closeNetworkConnection() {
        Log.e("CheckNetworkConnection", "on closeNetworkConnection")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}