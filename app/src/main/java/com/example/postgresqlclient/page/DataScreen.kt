package com.example.postgresqlclient.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.postgresqlclient.data.TableInfo

@Composable
fun DataScreen(
    table: TableInfo,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Tables") }
        Text("${table.schema}.${table.name}", style = MaterialTheme.typography.titleLarge)
        Text(
            "Rows will appear here in the next step.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}