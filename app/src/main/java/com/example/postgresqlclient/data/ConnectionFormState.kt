package com.example.postgresqlclient.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConnectionFormState {
    var host by mutableStateOf("")
    var port by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var defaultDatabase by mutableStateOf("")
    var name by mutableStateOf("")
    var saveConnection by mutableStateOf(false)

    fun toConfig() = ConnectionConfig(
        host = host,
        port = port.toIntOrNull() ?: 5432,
        database = defaultDatabase,
        username = username,
        password = password,
    )
}