package com.example.gamefest.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamefest.data.LoginRequest
import com.example.gamefest.data.RetrofitInstance
import com.example.gamefest.data.SessionManager
import com.example.gamefest.data.User
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _state = mutableStateOf<LoginState>(LoginState.Idle)
    val state: State<LoginState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                // 1. Appel du login
                val loginResponse = RetrofitInstance.api.login(LoginRequest(email, password))
                
                // 2. Sauvegarde du token JWT dans le SessionManager
                SessionManager.authToken = loginResponse.token
                
                // 3. Appel du profil (qui utilisera le token automatiquement grâce à l'intercepteur)
                val userResponse = RetrofitInstance.api.getMe()
                _state.value = LoginState.Success(userResponse.user)
            } catch (e: Exception) {
                _state.value = LoginState.Error("Erreur d'authentification : ${e.message}")
            }
        }
    }
}
