package com.example.gamefest.ui.components

import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDisplayDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Date inconnue"
    if (dateString.contains("/")) return dateString // Déjà formaté

    return try {
        // Gère le format ISO de Prisma : 2025-12-25T00:00:00.000Z
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE)
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        // Repli sur le format court au cas où : 2025-12-25
        try {
            val parser2 = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val formatter2 = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE)
            formatter2.format(parser2.parse(dateString)!!)
        } catch (e2: Exception) {
            dateString
        }
    }
}

@Composable
fun FestivalCard(
    festival: FestivalEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                        text = "Du ${formatDisplayDate(festival.startDate ) ?: "?"} au ${formatDisplayDate(festival.endDate) ?: "?"}",
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
