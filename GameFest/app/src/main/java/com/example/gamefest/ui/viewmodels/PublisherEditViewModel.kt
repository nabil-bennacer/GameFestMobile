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
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.repository.PublisherRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PublisherEditViewModel(private val repository: PublisherRepository) : ViewModel() {

    var publisherUiState by mutableStateOf(PublisherUiState())
        private set

    // 1. On charge les données existantes pour pré-remplir le formulaire
    fun loadPublisher(publisherId: Int) {
        viewModelScope.launch {
            // On lit la base de données (Assurez-vous d'avoir une fonction getPublisherById dans le DAO/Repository !)
            val publisher = repository.getPublisherById(publisherId).filterNotNull().first()

            publisherUiState = PublisherUiState(
                publisherDetails = publisher.toDetails(),
                isEntryValid = true // Le formulaire est valide par défaut puisqu'il vient de la BDD
            )
        }
    }

    // 2. On met à jour l'état quand l'utilisateur modifie le texte
    fun updateUiState(publisherDetails: PublisherDetails) {
        publisherUiState = PublisherUiState(
            publisherDetails = publisherDetails,
            isEntryValid = publisherDetails.name.isNotBlank()
        )
    }

    // 3. On sauvegarde la modification
    suspend fun updatePublisher() {
        if (publisherUiState.isEntryValid) {
            // Note : Assurez-vous d'avoir une fonction updatePublisher dans votre Repository
            repository.updatePublisher(publisherUiState.publisherDetails.toDto())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                PublisherEditViewModel(application.container.publisherRepository)
            }
        }
    }
}

// Petite fonction d'extension pour transformer l'Entité de la BDD en données de formulaire
fun PublisherEntity.toDetails() = PublisherDetails(
    id = id,
    name = name,
    logoUrl = logoUrl ?: "",
    exposant = exposant ?: false,
    distributeur = distributeur ?: false
)