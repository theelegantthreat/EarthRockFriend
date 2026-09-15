package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Comprehensive Mineral and Rock Specimen Room Entity.
 * Designed for 100% offline field expeditions, safety alerts, gemological data,
 * and historical/mystical lore.
 */
@Entity(tableName = "minerals")
data class MineralEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val scientificName: String = "",
    val chemicalFormula: String,
    val crystalSystem: String, // Isometric, Hexagonal, Tetragonal, Orthorhombic, Monoclinic, Triclinic, Amorphous
    val hardnessMohsMin: Double,
    val hardnessMohsMax: Double,
    val hardnessDisplay: String, // e.g., "3.5 - 4.0"
    val luster: String, // Vitreous, Metallic, Submetallic, Silky, Pearly, Resinous, Dull/Earthy, Adamantine
    val color: String, // Description of colors, e.g. "Emerald to deep banded green"
    val colorCategory: String, // Primary color tag: Green, Blue, Red, Yellow/Gold, Purple, Black, White/Clear, Brown, Multicolored
    val streak: String, // Powder color on unglazed porcelain
    val rockClassification: String = "Mineral", // Mineral, Igneous, Sedimentary, Metamorphic, Gemstone
    val transparency: String = "Opaque", // Transparent, Translucent, Opaque
    val cleavage: String = "Perfect", // Perfect, Good, Imperfect, None, Conchoidal
    val specificGravity: String = "3.5 - 4.0",
    val geographicLocations: String = "Worldwide", // Mining locations worldwide
    val diagnosticTests: String = "", // Field scratch test, acid reaction, etc.
    
    // Safety & Toxicity Protocols
    val toxicityWarnings: String = "Standard field handling.", // Detailed health & chemical warnings (copper, mercury, arsenic, etc.)
    val safetyProtocols: String = "Wash hands after handling rough specimens.", // Practical precautions: "Do not use in elixirs", "Wash hands", "Do not inhale dust"
    val isWaterSensitive: Boolean = false, // Dissolves or damaged by water (e.g., Selenite)
    val isToxic: Boolean = false, // Contains hazardous elements (e.g., Malachite, Cinnabar, Galena)
    val isSunlightSensitive: Boolean = false, // Fades under UV (e.g., Amethyst, Turquoise, Realgar)
    
    // Mystical & Historical Lore
    val mysticalProperties: String = "None recorded", // Metaphysical attributes, chakras, energy work, grounding
    val historicalLore: String = "None recorded", // Historical anecdotes, archaeological lore, ancient texts (Pliny, Egyptians, Medieval)
    
    // User Field Data
    val isFavorite: Boolean = false,
    val fieldNotes: String = ""
)
