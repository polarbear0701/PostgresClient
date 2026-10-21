package com.example.postgresqlclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postgresqlclient.data.ConnectionProfile
import com.example.postgresqlclient.ui.theme.PostgreSQLClientTheme

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

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {

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
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier.padding(5.dp).width(100.dp).height(40.dp),
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
                modifier = Modifier.padding(5.dp).width(100.dp).height(40.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {},
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
}

@Composable
fun SavedConnectionCard (
    profile: ConnectionProfile,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

    }

}