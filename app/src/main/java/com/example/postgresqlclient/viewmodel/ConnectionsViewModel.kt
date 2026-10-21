package com.example.postgresqlclient.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.postgresqlclient.data.ConnectionProfile

class ConnectionsViewModel: ViewModel() {
    private val _profiles = mutableStateListOf<ConnectionProfile>()
    val profiles: List<ConnectionProfile> get() = _profiles

    fun save (name: String, host: String, port: String, username: String) {
        _profiles.add(
            ConnectionProfile(
                id = System.currentTimeMillis(),
                dbName = name.ifBlank { host },
                host = host.trim(),
                port = port.toIntOrNull() ?: 5432,
                username = username.trim(),
            )
        )
    }

    fun delete(profile: ConnectionProfile) {
        _profiles.remove(profile)
    }
}