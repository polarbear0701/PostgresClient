package com.example.postgresqlclient.data

data class TableInfo(
    val schema: String,
    val  name: String,
    val type: String,
)

sealed interface TableResult {
    data class Loaded(val tables: List <TableInfo>): TableResult
    data class Failed(val message: String): TableResult
}
