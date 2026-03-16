package com.example.gamefest.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamefest.data.Post
import com.example.gamefest.data.PostRepository
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading: UiState()
    data class Success(val posts: List<Post>): UiState()
    data class Error(val message: String): UiState()
}

class PostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private var internalState : MutableState<UiState> = mutableStateOf(UiState.Loading)
    val state : State<UiState> = internalState

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            internalState.value = UiState.Loading
            try {
                val posts = postRepository.getPosts()
                internalState.value = UiState.Success(posts)
            } catch (e: Exception) {
                internalState.value = UiState.Error("Failed to load posts: " + e.message)
            }
        }
    }
}
