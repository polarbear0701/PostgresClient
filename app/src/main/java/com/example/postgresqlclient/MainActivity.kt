package com.example.postgresqlclient

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.MaterialTheme
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
import com.example.postgresqlclient.data.ConnectionProfile
import com.example.postgresqlclient.ui.theme.PostgreSQLClientTheme
import com.example.postgresqlclient.viewmodel.ConnectionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PostgreSQLClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HostField()
                }
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

@Preview(showBackground = true)
@Composable
fun HostField() {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var defaultDatabase by remember { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }

    val vm: ConnectionsViewModel = viewModel()
    var saveConnection by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1.3f),
                value = host,
                onValueChange = { host = it },
                label = { Text( text = "Host" ) },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.weight(.7f),
                value = port,
                onValueChange = { input ->
                    port = input.filter { it.isDigit() }.take(5)
                },
                label = { Text( text = "Port" ) },
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
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = defaultDatabase,
            onValueChange = { defaultDatabase = it },
            label = { Text(text = "Default database") },
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
                    checked = saveConnection,
                    onCheckedChange = {
                        saveConnection = it
                        name = ""
                    }
                )
                Text("Save DB")
            }

            Row () {
                Button(
                    modifier = Modifier.padding(2.dp).width(100.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {},
                    border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary))
                {
                    Text(
                        text = "Test",
                        fontSize = 13.sp
                    )
                }
                Button(
                    modifier = Modifier.padding(2.dp).width(100.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        if (saveConnection) {
                            vm.save(name = name, host = host, port = port, username = username)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = androidx.compose.ui.graphics.Color.White))
                {
                    Text(
                        text = "Connect",
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (saveConnection) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Connection name") },
                singleLine = true
            )
        }

        if (vm.profiles.isNotEmpty()) {
            Text("Saved connections", style = MaterialTheme.typography.titleMedium)
            vm.profiles.forEach {profile ->
                SavedConnectionCard(
                    profile = profile,
                    onDelete = { vm.delete(profile) }
                )
            }
        }
    }
}

@Composable
fun SavedConnectionCard (
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