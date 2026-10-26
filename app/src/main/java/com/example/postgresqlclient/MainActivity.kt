package com.example.postgresqlclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.postgresqlclient.data.ConnectionConfig
import com.example.postgresqlclient.data.ConnectionProfile
import com.example.postgresqlclient.data.TableInfo
import com.example.postgresqlclient.data.TestResult
import com.example.postgresqlclient.page.DataScreen
import com.example.postgresqlclient.page.TableScreen
import com.example.postgresqlclient.ui.theme.PostgreSQLClientTheme
import com.example.postgresqlclient.viewmodel.ConnectionsViewModel
import com.example.postgresqlclient.viewmodel.SessionViewmodel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PostgreSQLClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppRoot(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    //@Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        PostgreSQLClientTheme {
            Greeting("Android")
        }
    }

    @Composable
    fun HostField(
        modifier: Modifier = Modifier,
        vm: ConnectionsViewModel = viewModel(),
        session: SessionViewmodel = viewModel(),
    ) {
        val form = session.form

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1.3f),
                    value = form.host,
                    onValueChange = { form.host = it },
                    label = { Text("Host") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(0.7f),
                    value = form.port,
                    onValueChange = { input -> form.port = input.filter { it.isDigit() }.take(5) },
                    label = { Text("Port") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = form.username,
                    onValueChange = { form.username = it },
                    label = { Text("Username") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = form.password,
                    onValueChange = { form.password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = form.defaultDatabase,
                onValueChange = { form.defaultDatabase = it },
                label = { Text("Default database") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = form.saveConnection,
                        onCheckedChange = {
                            form.saveConnection = it
                            form.name = ""
                        }
                    )
                    Text("Save DB")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !session.isTesting && form.host.isNotBlank() && form.username.isNotBlank(),
                        onClick = { session.test(form.toConfig()) },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (session.isTesting) "Testing..." else "Test", fontSize = 13.sp)
                    }

                    Button(
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !session.isConnecting && form.host.isNotBlank() && form.username.isNotBlank(),
                        onClick = {
                            session.connect(form.toConfig())
                            if (form.saveConnection) {
                                vm.save(
                                    name = form.name,
                                    host = form.host,
                                    port = form.port,
                                    username = form.username
                                )
                            }
                        }
                    ) {
                        Text(
                            if (session.isConnecting) "Connecting..." else "Connect",
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (form.saveConnection) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = form.name,
                    onValueChange = { form.name = it },
                    label = { Text("Connection name") },
                    singleLine = true
                )
            }

            if (vm.profiles.isNotEmpty()) {
                Text("Saved connections", style = MaterialTheme.typography.titleMedium)
                vm.profiles.forEach { profile ->
                    SavedConnectionCard(
                        profile = profile,
                        onDelete = { vm.delete(profile) }
                    )
                }
            }

            when (val r = session.result) {
                is TestResult.Success -> ResultCard("Connected in ${r.millis} ms", success = true)
                is TestResult.Failure -> ResultCard(r.message, success = false)
                null -> Unit
            }

            session.connectError?.let { ResultCard(it, success = false) }
        }
    }

    @Composable
    fun SavedConnectionCard(
        profile: ConnectionProfile,
        onDelete: () -> Unit,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(profile.dbName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${profile.host}:${profile.port} · ${profile.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }

    @Composable
    fun ResultCard(text: String, success: Boolean) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (success) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer,
                contentColor = if (success) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(text = text, modifier = Modifier.padding(10.dp))
        }
    }

    @Composable
    fun AppRoot(
        modifier: Modifier = Modifier,
        session: SessionViewmodel = viewModel(),
    ) {
        val table = session.selectedTable

        BackHandler(enabled = session.isConnected) {
            if (session.selectedTable != null) session.closeTable() else session.disconnect()
        }

        when {
            !session.isConnected -> HostField(modifier = modifier)
            table == null -> TableScreen(
                tables = session.tables,
                onOpenTable = { session.openTable(it) },
                onDisconnect = { session.disconnect() },
                modifier = modifier
            )

            else -> DataScreen(
                table = table,
                onBack = { session.closeTable() },
                modifier = modifier
            )
        }
    }
}