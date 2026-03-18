package com.example.gamefest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamefest.data.local.entity.FestivalEntity

@Composable
fun FestivalCard(festival: FestivalEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = festival.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Année : ${festival.year}",
                style = MaterialTheme.typography.bodyMedium
            )
            festival.location?.let {
                Text(
                    text = "Lieu : $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
