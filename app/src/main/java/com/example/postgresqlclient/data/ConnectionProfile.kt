package com.example.postgresqlclient.data

data class ConnectionProfile(
    val id: Long,
    val dbName: String,
    val host: String,
    val port: Int,
    val username: String,
)