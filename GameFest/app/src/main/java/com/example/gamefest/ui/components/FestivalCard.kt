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

fun formatDateString(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "?"
    if (dateString.contains("/")) return dateString // Déjà formatée
    return try {
        // Parse le format "bizarre" ISO de Node.js
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val date = parser.parse(dateString)
        if (date != null) formatter.format(date) else dateString
    } catch (e: Exception) {
        try {
            // Parse le format court YYYY-MM-DD
            val parser2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter2 = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val date2 = parser2.parse(dateString)
            if (date2 != null) formatter2.format(date2) else dateString
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
