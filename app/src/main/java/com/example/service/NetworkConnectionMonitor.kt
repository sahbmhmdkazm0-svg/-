package com.example.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.data.model.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkConnectionMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _connectionState = MutableStateFlow(ConnectionState.ONLINE)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _disconnectDurationSeconds = MutableStateFlow(0L)
    val disconnectDurationSeconds: StateFlow<Long> = _disconnectDurationSeconds.asStateFlow()

    private var disconnectedStartTime: Long = 0L

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (disconnectedStartTime > 0L) {
                    val duration = (System.currentTimeMillis() - disconnectedStartTime) / 1000
                    _disconnectDurationSeconds.value = duration
                    // If restored within 60 seconds, mark as recent disconnect before returning to full green
                    _connectionState.value = ConnectionState.RECENT_DISCONNECT
                    CoroutineScope(Dispatchers.Default).launch {
                        kotlinx.coroutines.delay(8000)
                        _connectionState.value = ConnectionState.ONLINE
                    }
                } else {
                    _connectionState.value = ConnectionState.ONLINE
                }
            }

            override fun onLost(network: Network) {
                disconnectedStartTime = System.currentTimeMillis()
                _connectionState.value = ConnectionState.OFFLINE
            }
        })
    }
}
