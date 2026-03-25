package com.example.gamefest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamefest.data.local.entity.FestivalEntity

@Composable
fun FestivalCard(
    festival: FestivalEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = festival.name,
                    style = MaterialTheme.typography.titleLarge
                )
                festival.location?.let {
                    Text(
                        text = "Lieu : $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (festival.startDate != null || festival.endDate != null) {
                    Text(
                        text = "Du ${festival.startDate ?: "?"} au ${festival.endDate ?: "?"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
