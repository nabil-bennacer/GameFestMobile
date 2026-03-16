package com.example.gamefest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.ui.LoginState
import com.example.gamefest.ui.LoginViewModel
import com.example.gamefest.ui.theme.GameFestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameFestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val loginViewModel: LoginViewModel = viewModel()
                    LoginScreen(
                        viewModel = loginViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Connexion GameFest", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is LoginState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is LoginState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Error -> {
                Text(
                    text = (state as LoginState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            is LoginState.Success -> {
                val user = (state as LoginState.Success).user
                Text(
                    text = "Bienvenue ${user.name ?: user.email} !",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is LoginState.Loading && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Se connecter")
        }
    }
}
