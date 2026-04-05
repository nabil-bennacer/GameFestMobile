package com.example.gamefest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamefest.R
import com.example.gamefest.data.local.entity.GameEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(game: GameEntity, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = "Image du jeu ${game.name}",
                placeholder = painterResource(R.drawable.ic_launcher_background), // Image grise par défaut
                error = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
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
}