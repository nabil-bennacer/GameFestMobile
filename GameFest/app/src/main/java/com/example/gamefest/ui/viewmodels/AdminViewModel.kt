package com.example.gamefest.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.remote.dto.UserDto
import com.example.gamefest.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val userRepository: UserRepository) : ViewModel() {

    var users by mutableStateOf<List<UserDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            userRepository.getAllUsers()
                .onSuccess { result ->
                    users = result
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "Erreur de chargement"
                }
            isLoading = false
        }
    }

    fun updateUserRole(userId: Int, newRole: String) {
        viewModelScope.launch {
            userRepository.updateUserRole(userId, newRole)
                .onSuccess { updatedUser ->
                    // Mettre à jour la liste locale
                    users = users.map { if (it.id == userId) updatedUser else it }
                }
                .onFailure { error ->
                    errorMessage = "Erreur lors de la mise à jour du rôle"
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val repository = application.container.userRepository // Assurez-vous que l'AppContainer fournit bien userRepository
                AdminViewModel(repository)
            }
        }
    }
}