package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.data.remote.dto.UserDto
import com.example.gamefest.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    onBackClick: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory)
) {
    val users = viewModel.users
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val roles = listOf("ADMIN", "VISITOR", "VOLUNTEER", "ORGANISATOR", "SUPER_ORGANISATOR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des Utilisateurs") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading && users.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (errorMessage != null) {
                        item {
                            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    items(users) { user ->
                        UserAdminCard(
                            user = user,
                            roles = roles,
                            onRoleChange = { newRole ->
                                viewModel.updateUserRole(user.id, newRole)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdminCard(user: UserDto, roles: List<String>, onRoleChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = user.role,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rôle") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().width(150.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                expanded = false
                                if (role != user.role) {
                                    onRoleChange(role)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}