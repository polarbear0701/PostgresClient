package com.example.postgresqlclient.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postgresqlclient.data.ConnectionConfig
import com.example.postgresqlclient.data.PostgresClient
import com.example.postgresqlclient.data.TestResult
import kotlinx.coroutines.launch

class SessionViewmodel: ViewModel() {
    private val client = PostgresClient()
    var isTesting by mutableStateOf(false)
        private set

    var result by mutableStateOf<TestResult?>(null)
        private set

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
}