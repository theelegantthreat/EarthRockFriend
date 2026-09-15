package com.example.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.MineralEntity
import com.example.ui.screens.search.SpecimenSearchCard
import com.example.ui.theme.OchreGold

@Composable
fun FavoritesScreen(
    favoriteMinerals: List<MineralEntity>,
    onSelectMineral: (Int) -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (favoriteMinerals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("favorites_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Empty Favorites",
                    tint = OchreGold.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Field Specimens Saved Yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the star icon on any mineral in the Physical Search or Lore Library to pin it to your expedition collection.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("favorites_screen_content")
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "My Field Specimen Collection (${favoriteMinerals.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(favoriteMinerals, key = { it.id }) { mineral ->
                SpecimenSearchCard(
                    mineral = mineral,
                    onClick = { onSelectMineral(mineral.id) },
                    onToggleFavorite = { onToggleFavorite(mineral.id, mineral.isFavorite) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
