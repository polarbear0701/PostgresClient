package com.example.postgresqlclient.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.DriverManager

sealed interface TestResult{
    data class Success(val millis: Long): TestResult
    data class Failure(val message: String): TestResult
}

class PostgresClient {
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
}