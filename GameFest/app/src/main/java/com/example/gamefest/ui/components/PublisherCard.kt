package com.example.gamefest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamefest.data.local.entity.PublisherEntity

@Composable
fun PublisherCard(publisher: PublisherEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick=onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = publisher.name,
                style = MaterialTheme.typography.titleLarge
            )

            val role = if (publisher.exposant == true) "Exposant" else "Distributeur"
            Text(
                text = "Rôle : $role",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}