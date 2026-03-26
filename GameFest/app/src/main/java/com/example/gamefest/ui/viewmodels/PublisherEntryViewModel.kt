package com.example.gamefest.ui.viewmodels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.remote.dto.PublisherDto
import com.example.gamefest.data.repository.PublisherRepository

class PublisherEntryViewModel(private val repository: PublisherRepository) : ViewModel() {

    var publisherUiState by mutableStateOf(PublisherUiState())
        private set

    // Mise à jour de l'état à chaque fois que l'utilisateur tape au clavier
    fun updateUiState(publisherDetails: PublisherDetails) {
        publisherUiState = PublisherUiState(
            publisherDetails = publisherDetails,
            isEntryValid = validateInput(publisherDetails)
        )
    }

    // Sauvegarde dans la base de données et sur le serveur
    suspend fun savePublisher() {
        if (validateInput()) {
            // Note : Assurez-vous d'avoir une fonction addPublisher dans votre Repository !
            repository.addPublisher(publisherUiState.publisherDetails.toDto())
        }
    }

    // La validation avec Le nom de l'éditeur qui est obligatoire
    private fun validateInput(uiState: PublisherDetails = publisherUiState.publisherDetails): Boolean {
        return uiState.name.isNotBlank()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                PublisherEntryViewModel(application.container.publisherRepository)
            }
        }
    }
}
data class PublisherUiState(
    val publisherDetails: PublisherDetails = PublisherDetails(),
    val isEntryValid: Boolean = false
)

data class PublisherDetails(
    val id: Int = 0,
    val name: String = "",
    val logoUrl: String = "",
    val exposant: Boolean = false,
    val distributeur: Boolean = false
)

fun PublisherDetails.toDto(): PublisherDto = PublisherDto(
    id = id,
    name = name,
    logoUrl = logoUrl.ifBlank { null }, // Si c'est vide, on envoie null
    exposant = exposant,
    distributeur = distributeur
)