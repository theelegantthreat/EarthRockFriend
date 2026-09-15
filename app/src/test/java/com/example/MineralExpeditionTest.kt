package com.example

import android.graphics.Bitmap
import android.graphics.Color
import com.example.classifier.OnDeviceRockClassifier
import com.example.data.local.MineralEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MineralExpeditionTest {

    @Test
    fun testOfflineClassifierDetectsGreenMineral() = runBlocking {
        val classifier = OnDeviceRockClassifier()

        // Create a solid emerald green bitmap
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        for (x in 0 until 64) {
            for (y in 0 until 64) {
                bitmap.setPixel(x, y, Color.rgb(20, 140, 50))
            }
        }

        val testCatalog = listOf(
            MineralEntity(
                name = "Malachite",
                chemicalFormula = "Cu₂CO₃(OH)₂",
                crystalSystem = "Monoclinic",
                hardnessMohsMin = 3.5,
                hardnessMohsMax = 4.0,
                hardnessDisplay = "3.5 - 4.0",
                luster = "Silky, Dull, Vitreous",
                color = "Vibrant bright emerald green",
                colorCategory = "Green",
                streak = "Light green",
                toxicityWarnings = "Copper toxicity",
                safetyProtocols = "Wear respirator when cutting",
                isToxic = true
            ),
            MineralEntity(
                name = "Obsidian",
                chemicalFormula = "SiO₂",
                crystalSystem = "Amorphous",
                hardnessMohsMin = 5.0,
                hardnessMohsMax = 5.5,
                hardnessDisplay = "5.0 - 5.5",
                luster = "Vitreous",
                color = "Jet black",
                colorCategory = "Black",
                streak = "White"
            )
        )

        val result = classifier.classifyImage(bitmap, testCatalog)
        assertNotNull(result.topPrediction)
        assertEquals("Malachite", result.topPrediction?.specimen?.name)
        assertTrue(result.topPrediction!!.isSafetyHazard)
    }

    @Test
    fun testMineralEntitySafetyFlags() {
        val cinnabar = MineralEntity(
            name = "Cinnabar",
            chemicalFormula = "HgS",
            crystalSystem = "Trigonal",
            hardnessMohsMin = 2.0,
            hardnessMohsMax = 2.5,
            hardnessDisplay = "2.0 - 2.5",
            luster = "Adamantine",
            color = "Scarlet red",
            colorCategory = "Red",
            streak = "Scarlet red",
            toxicityWarnings = "Mercury ore. Highly toxic.",
            safetyProtocols = "Sealed display only.",
            isToxic = true
        )

        assertTrue(cinnabar.isToxic)
        assertEquals("HgS", cinnabar.chemicalFormula)
    }
}
