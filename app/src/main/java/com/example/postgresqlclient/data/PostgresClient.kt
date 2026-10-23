package com.example.postgresqlclient.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

sealed interface TestResult{
    data class Success(val millis: Long): TestResult
    data class Failure(val message: String): TestResult
}

class PostgresClient {

    private var connection: Connection? = null

    suspend fun test(config: ConnectionConfig): TestResult = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            Class.forName("org.postgresql.Driver")
            DriverManager.setLoginTimeout(10)
            DriverManager
                .getConnection(config.jdbcUrl(), config.username, config.password)
                .use {connection ->
                    connection.createStatement().use { it.execute("SELECT 1") }
                }
            TestResult.Success(System.currentTimeMillis() - start)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TestResult.Failure(e.message?.lineSequence()?.firstOrNull() ?: "Connection failed")
        } catch (e: Throwable) {
            android.util.Log.e("PgClient", "Test failed", e)
            val reason = e.message?.lineSequence()?.firstOrNull() ?: "no message"
            TestResult.Failure("${e::class.java.simpleName}: $reason")
        }
    }

    suspend fun connect(config: ConnectionConfig): TestResult = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            connection?.close()
            connection = null
            Class.forName("org.postgresql.Driver")
            DriverManager.setLoginTimeout(10)
            connection = DriverManager.getConnection(config.jdbcUrl(), config.username, config.password)
            TestResult.Success(System.currentTimeMillis() - start)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            android.util.Log.e("PgClient", "Connection failed", e)
            TestResult.Failure(e.message?.lineSequence()?.firstOrNull() ?: "Connection Failed")
        }
    }

    suspend fun listTables(): TableResult = withContext(Dispatchers.IO) {
        val conn = connection ?: return@withContext TableResult.Failed("Not connected")
        try {
            val sql = """
                SELECT table_schema, table_name, table_type
                FROM information_schema.tables
                WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
                ORDER BY table_schema, table_name
            """.trimIndent()
            conn .createStatement().use {statement ->
                statement.executeQuery(sql).use { rows ->
                    val tables = mutableListOf<TableInfo>()
                    while (rows.next()) {
                        tables.add(TableInfo(rows.getString(1),  rows.getString(2),  rows.getString(3)))
                    }
                    TableResult.Loaded(tables)
                }
            }
        } catch (e: CancellationException) {
            throw e
        }  catch (e: Throwable) {
            TableResult.Failed(e.message?.lineSequence()?.firstOrNull()  ?: "Could not list tables")
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try { connection?.close() } catch (_: Exception) {}
        connection = null
    }
}