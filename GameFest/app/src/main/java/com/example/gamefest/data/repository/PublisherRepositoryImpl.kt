package com.example.gamefest.data.repository

import com.example.gamefest.data.local.dao.PublisherDao
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.mapper.toEntityList
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.PublisherDto
import kotlinx.coroutines.flow.Flow
import android.util.Log

class PublisherRepositoryImpl(
    private val dao: PublisherDao,
    private val api: GameFestApiService
) : PublisherRepository {


    override fun getAllPublishers(): Flow<List<PublisherEntity>> = dao.getAllPublishers()

    override fun getPublisherById(id: Int): Flow<PublisherEntity?> = dao.getPublisherById(id)

    // 2. Synchronisation (Web -> Local)
    override suspend fun refreshPublishers() {
        try {
            val response = api.getAllPublishers()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    // On convertit les DTOs en Entities et on les sauvegarde.
                    // Grâce au Flow du DAO, l'UI se mettra à jour toute seule
                    dao.insertPublishers(dtos.toEntityList())
                }
            }
        } catch (e: Exception) {
            // Pas d'internet ? Le serveur a crashé ?
            // Ce n'est pas grave, l'UI affichera les données locales grâce au Flow.
            e.printStackTrace()
            Log.e("PublisherRepository", "Impossible de rafraichir les éditeurs (pas d'internet ?)", e)
        }
    }

    // Ajout (Local puis Web)
    override suspend fun addPublisher(publisher: PublisherDto) {
        dao.insertPublisher(publisher.toEntity())

        try {
            api.createPublisher(publisher)
        } catch (e: Exception) {
            Log.e("PublisherRepository", "Erreur lors de l'ajout sur la base de données distante", e)
        }
    }

    override suspend fun updatePublisher(publisher: PublisherDto) {
        dao.insertPublisher(publisher.toEntity())

        try {
            api.updatePublisher(publisher.id, publisher)
        } catch (e: Exception) {
            Log.e("PublisherRepository", "Erreur lors de la mise à jour sur la base de données distante", e)
        }
    }

    // 4. Suppression
    override suspend fun deletePublisher(id: Int) {
        dao.deletePublisherById(id) // Supprime localement
        try {
            api.deletePublisher(id)
        } catch (e: Exception) {
            // Gérer l'erreur hors-ligne

        }
    }
}