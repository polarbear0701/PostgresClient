package com.example.postgresqlclient.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postgresqlclient.data.ConnectionConfig
import com.example.postgresqlclient.data.ConnectionFormState
import com.example.postgresqlclient.data.PostgresClient
import com.example.postgresqlclient.data.TableInfo
import com.example.postgresqlclient.data.TableResult
import com.example.postgresqlclient.data.TestResult
import kotlinx.coroutines.launch

class SessionViewmodel: ViewModel() {
    private val client = PostgresClient()
    val form = ConnectionFormState()
    var isTesting by mutableStateOf(false)
        private set

    var result by mutableStateOf<TestResult?>(null)
        private set

    var isConnecting by mutableStateOf(false)
        private set

    var isConnected by mutableStateOf(false)
        private set

    var tables by mutableStateOf<List<TableInfo>>(emptyList())
        private set

    var selectedTable by mutableStateOf<TableInfo?>(null)
        private set

    var connectError by mutableStateOf<String?>(null)
        private set

    fun openTable(table: TableInfo) {
        selectedTable = table
    }

    fun closeTable() {
        selectedTable = null
    }

    fun test(config: ConnectionConfig) {
        if (isTesting) return
        viewModelScope.launch {
            isTesting = true
            result = null
            try {
                result = client.test(config)

            } finally {
                isTesting = false
            }
        }
    }

    fun connect(config: ConnectionConfig) {
        if (isConnecting) return
        viewModelScope.launch {
            isConnecting = true
            connectError = null
            try {
                when (val r = client.connect(config)) {
                    is TestResult.Failure -> connectError = r.message
                    is TestResult.Success -> when (val t = client.listTables()) {
                        is TableResult.Loaded -> {
                            tables = t.tables
                            selectedTable = null
                            isConnected = true
                        }
                        is TableResult.Failed -> {
                            connectError = t.message
                            client.disconnect()
                        }
                    }
                }
            } finally {
                isConnecting = false
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            client.disconnect()
            isConnected = false
            selectedTable = null
            tables = emptyList()
        }
    }
}