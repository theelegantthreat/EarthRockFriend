package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MineralEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.EarthBrownDark
import com.example.ui.theme.EarthBrownPrimary
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardRedBg
import com.example.ui.theme.MysticalPurple
import com.example.ui.theme.OchreGold
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg

@Composable
fun HomeScreen(
    featuredMineral: MineralEntity?,
    onNavigate: (Screen) -> Unit,
    onSelectMineral: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Field Companion Welcome Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ForestGreenDark,
                                ForestGreenPrimary,
                                EarthBrownDark
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OchreGold.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "FIELD EXPEDITION ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OchreGold
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "100% OFFLINE READY",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreenLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Identify, Lore & Field Safety",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your digital mineralogist with physical attributes database, camera recognition, and sacred lapidary lore.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Quick Action Grid (2x2)
        item {
            Text(
                text = "Field Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Rock Scanner",
                    subtitle = "Dual-mode Camera",
                    icon = Icons.Default.CameraAlt,
                    accentColor = ForestGreenPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_btn_scanner"),
                    onClick = { onNavigate(Screen.CameraScan) }
                )
                QuickActionCard(
                    title = "Physical Search",
                    subtitle = "Mohs, Luster, Color",
                    icon = Icons.Default.Search,
                    accentColor = EarthBrownPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_btn_search"),
                    onClick = { onNavigate(Screen.Search) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Lore & Safety",
                    subtitle = "Toxicity & Lore",
                    icon = Icons.Default.CollectionsBookmark,
                    accentColor = HazardRed,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_btn_lore"),
                    onClick = { onNavigate(Screen.LoreLibrary) }
                )
                QuickActionCard(
                    title = "GemConsult AI",
                    subtitle = "Chatbot Guide",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = MysticalPurple,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_btn_gemconsult"),
                    onClick = { onNavigate(Screen.GemConsult) }
                )
            }
        }

        // Field Safety Urgent Protocol Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = HazardRedBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HazardRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safety Caution",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Expedition Field Safety Notice",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = HazardRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Never consume direct mineral elixirs with copper (Malachite), lead (Galena), arsenic (Realgar), or mercury (Cinnabar). Keep Selenite dry—it dissolves in water!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5A1A1A)
                        )
                    }
                }
            }
        }

        // Featured Specimen Spotlight
        if (featuredMineral != null) {
            item {
                Text(
                    text = "Field Specimen Spotlight",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                ElevatedCard(
                    onClick = { onSelectMineral(featuredMineral.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_specimen_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = featuredMineral.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = featuredMineral.chemicalFormula,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Mohs ${featuredMineral.hardnessDisplay}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = featuredMineral.mysticalProperties,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpecimenTag(text = featuredMineral.crystalSystem)
                            SpecimenTag(text = featuredMineral.luster)
                            if (featuredMineral.isToxic) {
                                SpecimenTag(text = "⚠️ Toxic Warning", isWarning = true)
                            }
                        }
                    }
                }
            }
        }

        // Mohs Hardness Field Quick Scale
        item {
            Text(
                text = "Mohs Hardness Field Scale",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            val fieldScales = listOf(
                Pair("1.0", "Talc (Softest, greasy)"),
                Pair("2.0 - 2.5", "Fingernail (Scratches Gypsum/Selenite)"),
                Pair("3.0 - 3.5", "Copper Penny (Scratches Calcite)"),
                Pair("4.0 - 4.5", "Fluorite / Iron Nail"),
                Pair("5.0 - 5.5", "Pocket Knife / Window Glass"),
                Pair("6.0 - 6.5", "Steel File / Orthoclase"),
                Pair("7.0", "Quartz (Scratches all common glass)"),
                Pair("8.0+", "Topaz, Corundum, Diamond (Toughest)")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(fieldScales) { item ->
                    OutlinedCard(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(180.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Mohs ${item.first}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.height(115.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SpecimenTag(
    text: String,
    isWarning: Boolean = false
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isWarning) HazardRed.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isWarning) HazardRed else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
