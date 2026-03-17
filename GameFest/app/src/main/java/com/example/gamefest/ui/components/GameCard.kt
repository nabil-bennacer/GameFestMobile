package com.example.gamefest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamefest.data.local.entity.GameEntity

@Composable
fun GameCard(game: GameEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = game.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏳ ${game.type} ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "👥 ${game.maxPlayers} joueurs max",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "🎂 ${game.minAge}+ ans",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}