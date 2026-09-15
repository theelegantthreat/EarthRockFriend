package com.example.ui.screens.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MineralEntity
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardRedBg
import com.example.ui.theme.MysticalPurple
import com.example.ui.theme.MysticalPurpleBg
import com.example.ui.theme.OchreGold
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecimenDetailScreen(
    mineral: MineralEntity?,
    onBack: () -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    onSaveNotes: (Int, String) -> Unit,
    onConsultAi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (mineral == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Specimen not found in database")
        }
        return
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var userNotes by remember(mineral.fieldNotes) { mutableStateOf(mineral.fieldNotes) }
    var notesSavedMessage by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val tabs = listOf(
        Pair("Physical", Icons.Default.Science),
        Pair("Safety", Icons.Default.Warning),
        Pair("Mystical", Icons.Default.AutoAwesome),
        Pair("History", Icons.Default.MenuBook)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mineral.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(mineral.id, mineral.isFavorite) }) {
                        Icon(
                            imageVector = if (mineral.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (mineral.isFavorite) OchreGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mineral.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (mineral.scientificName.isNotBlank()) {
                                Text(
                                    text = mineral.scientificName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = mineral.chemicalFormula,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Mohs ${mineral.hardnessDisplay}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailBadge(label = "System", value = mineral.crystalSystem)
                        DetailBadge(label = "Class", value = mineral.rockClassification)
                        DetailBadge(label = "Luster", value = mineral.luster)
                    }
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, (label, icon) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = label, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                // 1. Physical Gemological Specs
                0 -> {
                    DossierCard(title = "Physical & Crystallographic Characteristics") {
                        SpecRow(title = "Chemical Composition", value = mineral.chemicalFormula)
                        SpecRow(title = "Crystal System", value = mineral.crystalSystem)
                        SpecRow(title = "Hardness (Mohs)", value = "${mineral.hardnessDisplay} / 10.0")
                        SpecRow(title = "Streak (Porcelain)", value = mineral.streak)
                        SpecRow(title = "Luster", value = mineral.luster)
                        SpecRow(title = "Color Variations", value = mineral.color)
                        SpecRow(title = "Cleavage & Fracture", value = mineral.cleavage)
                        SpecRow(title = "Specific Gravity", value = mineral.specificGravity)
                        SpecRow(title = "Transparency", value = mineral.transparency)
                        SpecRow(title = "Geographic Deposits", value = mineral.geographicLocations)
                        if (mineral.diagnosticTests.isNotBlank()) {
                            SpecRow(title = "Field Diagnostic Test", value = mineral.diagnosticTests)
                        }
                    }
                }

                // 2. Safety & Handling Precautions
                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (mineral.isToxic) HazardRedBg else WarningAmberBg
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Hazard Warning",
                                        tint = if (mineral.isToxic) HazardRed else WarningAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (mineral.isToxic) "CRITICAL TOXICITY WARNING" else "FIELD CAUTION",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (mineral.isToxic) HazardRed else WarningAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = mineral.toxicityWarnings,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF421515)
                                )
                            }
                        }

                        DossierCard(title = "Expedition Handling & Storage Protocols") {
                            Text(
                                text = mineral.safetyProtocols,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (mineral.isWaterSensitive) {
                                ProtocolAlertItem(
                                    title = "Water Vulnerability",
                                    desc = "Do not submerge, cleanse in water, or prepare water infusions. Can dissolve, pit, or leach toxic elements."
                                )
                            }

                            if (mineral.isSunlightSensitive) {
                                ProtocolAlertItem(
                                    title = "Sunlight Sensitivity",
                                    desc = "Prolonged exposure to ultraviolet rays or direct sunlight will cause severe color fading or decomposition."
                                )
                            }

                            ProtocolAlertItem(
                                title = "Ingestion / Elixir Contraindication",
                                desc = "Never place rough specimens in drinking water. Use the indirect method (specimen placed in dry glass vial outside water) for safe holistic gem waters."
                            )
                        }
                    }
                }

                // 3. Mystical & Metaphysical Properties
                2 -> {
                    DossierCard(
                        title = "Metaphysical Resonance & Gem Therapy",
                        headerColor = MysticalPurple
                    ) {
                        Text(
                            text = mineral.mysticalProperties,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MysticalPurpleBg)
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Holistic Integration Guidance",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MysticalPurple
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Resonant in meditative presence, field grounding, and sacred altar spaces. Always balance holistic lore with empirical scientific safety.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF3B1E48)
                                )
                            }
                        }
                    }
                }

                // 4. Historical Lore & Ancient Anecdotes
                3 -> {
                    DossierCard(title = "Ancient Lore & Lapidary Chronicles") {
                        Text(
                            text = mineral.historicalLore,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Historical Reference Tradition",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Recorded in classical Mediterranean lapidaries (Theophrastus, Pliny the Elder's Natural History), medieval monastic texts (St. Hildegard of Bingen), and traditional indigenous geologies worldwide.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Ask GemConsult Button
            Button(
                onClick = { onConsultAi("Tell me all about ${mineral.name}, its mineralogy, field safety warnings, and historical lore.") },
                colors = ButtonDefaults.buttonColors(containerColor = MysticalPurple),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("detail_btn_consult_ai")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Consult AI")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask GemConsult AI about ${mineral.name}")
            }

            // User Field Notes
            DossierCard(title = "My Field Notes") {
                OutlinedTextField(
                    value = userNotes,
                    onValueChange = {
                        userNotes = it
                        notesSavedMessage = false
                    },
                    placeholder = { Text("Record field locality, GPS coordinates, matrix associations, or personal observations...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notesSavedMessage) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "Saved", tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Saved to offline DB", style = MaterialTheme.typography.labelSmall, color = ForestGreenPrimary)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            onSaveNotes(mineral.id, userNotes)
                            notesSavedMessage = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Notes")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DossierCard(
    title: String,
    headerColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = headerColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SpecRow(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

@Composable
private fun DetailBadge(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProtocolAlertItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(WarningAmber)
                .padding(top = 6.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
