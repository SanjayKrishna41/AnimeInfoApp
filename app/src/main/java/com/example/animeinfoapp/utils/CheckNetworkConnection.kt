package com.example.animeinfoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class CheckNetworkConnection(context: Context) : LiveData<Boolean>() {
    override fun onActive() {
        super.onActive()
        checkNetworkConnection()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)

        }
    }

    // function to check network
    private fun checkNetworkConnection() {
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
        } else {
            postValue(false)
        }
    }
}