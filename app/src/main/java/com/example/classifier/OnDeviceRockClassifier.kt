package com.example.classifier

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.local.MineralEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class OnDevicePrediction(
    val specimen: MineralEntity,
    val confidencePercent: Int,
    val detectedColorCategory: String,
    val estimatedLuster: String,
    val matchRationale: String,
    val isSafetyHazard: Boolean
)

data class OfflineClassificationResult(
    val topPrediction: OnDevicePrediction?,
    val alternativeMatches: List<OnDevicePrediction>,
    val dominantHexColor: String,
    val averageBrightness: Float
)

/**
 * On-Device Physical Rock Classifier.
 * Analyzes optical properties (chromaticity, HSV hue clustering, saturation, specularity)
 * against local mineral signatures to provide 100% offline classification in field expeditions.
 */
class OnDeviceRockClassifier {

    suspend fun classifyImage(
        bitmap: Bitmap,
        catalog: List<MineralEntity>
    ): OfflineClassificationResult = withContext(Dispatchers.Default) {
        if (catalog.isEmpty()) {
            return@withContext OfflineClassificationResult(null, emptyList(), "#888888", 0.5f)
        }

        // Subsample bitmap for fast on-device analysis
        val sampleSize = 64
        val scaled = Bitmap.createScaledBitmap(bitmap, sampleSize, sampleSize, true)
        
        var totalR = 0L
        var totalG = 0L
        var totalB = 0L
        val hsv = FloatArray(3)
        var totalSaturation = 0f
        var totalValue = 0f

        val hueBuckets = mutableMapOf<String, Int>()

        for (x in 8 until sampleSize - 8 step 2) {
            for (y in 8 until sampleSize - 8 step 2) {
                val pixel = scaled.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                totalR += r
                totalG += g
                totalB += b

                Color.RGBToHSV(r, g, b, hsv)
                val hue = hsv[0]
                val sat = hsv[1]
                val value = hsv[2]

                totalSaturation += sat
                totalValue += value

                val category = classifyHueToColor(hue, sat, value)
                hueBuckets[category] = (hueBuckets[category] ?: 0) + 1
            }
        }

        val sampleCount = ((sampleSize - 16) / 2) * ((sampleSize - 16) / 2)
        val avgR = (totalR / sampleCount).toInt().coerceIn(0, 255)
        val avgG = (totalG / sampleCount).toInt().coerceIn(0, 255)
        val avgB = (totalB / sampleCount).toInt().coerceIn(0, 255)
        val avgSat = totalSaturation / sampleCount
        val avgVal = totalValue / sampleCount

        val hexColor = String.format("#%02X%02X%02X", avgR, avgG, avgB)

        // Dominant detected color category
        val dominantCategory = hueBuckets.maxByOrNull { it.value }?.key ?: "Brown"

        // Estimate luster by specular highlights and saturation
        val estimatedLuster = when {
            avgVal > 0.8f && avgSat < 0.2f -> "Vitreous"
            avgVal > 0.6f && avgSat > 0.6f -> "Metallic / Splendent"
            avgVal < 0.35f -> "Dull / Earthy"
            else -> "Resinous / Silky"
        }

        // Rank specimens by chromatic and luster matching
        val scoredSpecimens = catalog.map { specimen ->
            var score = 30 // baseline match probability

            if (specimen.colorCategory.equals(dominantCategory, ignoreCase = true)) {
                score += 45
            } else if (specimen.color.contains(dominantCategory, ignoreCase = true)) {
                score += 25
            }

            if (specimen.luster.contains(estimatedLuster, ignoreCase = true)) {
                score += 15
            }

            // High density or dark color bonus for heavy minerals
            if (dominantCategory == "Black" && specimen.name in listOf("Obsidian", "Black Tourmaline", "Galena")) {
                score += 10
            } else if (dominantCategory == "Green" && specimen.name in listOf("Malachite", "Amazonite", "Fluorite")) {
                score += 10
            } else if (dominantCategory == "Blue" && specimen.name in listOf("Lapis Lazuli", "Turquoise", "Aquamarine", "Chrysocolla")) {
                score += 10
            } else if (dominantCategory == "Red" && specimen.name in listOf("Cinnabar", "Carnelian", "Realgar")) {
                score += 10
            }

            val confidence = score.coerceIn(40, 91)
            val rationale = "Matched ${dominantCategory.lowercase()} chromatic signature with ${estimatedLuster.lowercase()} surface reflectance profile."

            OnDevicePrediction(
                specimen = specimen,
                confidencePercent = confidence,
                detectedColorCategory = dominantCategory,
                estimatedLuster = estimatedLuster,
                matchRationale = rationale,
                isSafetyHazard = specimen.isToxic || specimen.isWaterSensitive
            )
        }.sortedByDescending { it.confidencePercent }

        val top = scoredSpecimens.firstOrNull()
        val alts = scoredSpecimens.drop(1).take(3)

        OfflineClassificationResult(
            topPrediction = top,
            alternativeMatches = alts,
            dominantHexColor = hexColor,
            averageBrightness = avgVal
        )
    }

    private fun classifyHueToColor(hue: Float, sat: Float, valBrightness: Float): String {
        if (valBrightness < 0.2f) return "Black"
        if (sat < 0.15f && valBrightness > 0.75f) return "White/Clear"
        if (sat < 0.2f) return "Brown" // greyish earth

        return when (hue) {
            in 0f..20f -> "Red"
            in 21f..50f -> "Brown" // orange-brown / copper
            in 51f..70f -> "Yellow/Gold"
            in 71f..165f -> "Green"
            in 166f..260f -> "Blue"
            in 261f..320f -> "Purple"
            else -> "Red"
        }
    }
}
