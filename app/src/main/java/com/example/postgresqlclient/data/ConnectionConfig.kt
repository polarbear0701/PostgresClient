package com.example.postgresqlclient.data

data class ConnectionConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val ssl: String = "prefer"
) {
    fun jdbcUrl(): String {
        return "jdbc:postgresql://${host.trim()}:${port}/${database.trim().ifBlank { "postgres" }}?sslmode=${ssl.trim()}&connectTimeout=10"
    }
}