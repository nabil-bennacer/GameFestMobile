package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamefest.ui.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        currentUser?.let { user ->
            Text(text = "Profil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nom d'utilisateur : ${user.name}")
            Text(text = "Email : ${user.email}")
            Text(text = "Rôle : ${user.role}")

            Spacer(modifier = Modifier.height(24.dp))

            if (user.role == "ADMIN") {
                Button(
                    onClick = onNavigateToAdmin,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Administration des Utilisateurs")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Se déconnecter")
            }
        } ?: run {
            Text("Non connecté")
        }
    }
}