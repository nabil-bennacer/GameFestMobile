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
import com.example.gamefest.data.local.entity.UserEntity
import com.example.gamefest.data.remote.dto.LoginRequest
import com.example.gamefest.data.remote.dto.RegisterRequest
import com.example.gamefest.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var authUiState: AuthUiState by mutableStateOf(AuthUiState.Idle)
        private set

    // True tant que la vérification initiale du profil (via cookie) est en cours
    private val _isCheckingAuth = androidx.compose.runtime.mutableStateOf(true)
    val isCheckingAuth: androidx.compose.runtime.State<Boolean> = _isCheckingAuth

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            userRepository.getProfile()
            _isCheckingAuth.value = false
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            authUiState = AuthUiState.Loading
            userRepository.login(request)
                .onSuccess { user ->
                    authUiState = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    authUiState = AuthUiState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            authUiState = AuthUiState.Loading
            userRepository.register(request)
                .onSuccess { user ->
                    authUiState = AuthUiState.Success(user)
                }
                .onFailure { error ->
                    authUiState = AuthUiState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            authUiState = AuthUiState.Idle
        }
    }

    fun resetState() {
        authUiState = AuthUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val repository = application.container.userRepository
                AuthViewModel(repository)
            }
        }
    }
}
