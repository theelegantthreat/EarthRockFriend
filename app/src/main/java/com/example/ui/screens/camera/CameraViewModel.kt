package com.example.ui.screens.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.classifier.OfflineClassificationResult
import com.example.classifier.OnDeviceRockClassifier
import com.example.data.gemini.GeminiAnalysisResult
import com.example.data.gemini.GeminiClient
import com.example.data.local.MineralEntity
import com.example.data.repository.MineralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class IdentificationMode {
    ONLINE_GEMINI,
    OFFLINE_FIELD_EXPEDITION
}

sealed class IdentificationUiState {
    object Idle : IdentificationUiState()
    object Analyzing : IdentificationUiState()
    data class SuccessCloud(val result: GeminiAnalysisResult, val capturedBitmap: Bitmap) : IdentificationUiState()
    data class SuccessOffline(val result: OfflineClassificationResult, val capturedBitmap: Bitmap) : IdentificationUiState()
    data class Error(val message: String, val canFallbackToOffline: Boolean = false, val bitmap: Bitmap? = null) : IdentificationUiState()
}

class CameraViewModel(
    private val repository: MineralRepository,
    private val geminiClient: GeminiClient,
    private val offlineClassifier: OnDeviceRockClassifier = OnDeviceRockClassifier()
) : ViewModel() {

    private val _uiState = MutableStateFlow<IdentificationUiState>(IdentificationUiState.Idle)
    val uiState: StateFlow<IdentificationUiState> = _uiState.asStateFlow()

    private val _mode = MutableStateFlow(
        if (geminiClient.isApiKeyAvailable) IdentificationMode.ONLINE_GEMINI
        else IdentificationMode.OFFLINE_FIELD_EXPEDITION
    )
    val mode: StateFlow<IdentificationMode> = _mode.asStateFlow()

    val isGeminiAvailable: Boolean = geminiClient.isApiKeyAvailable

    fun setMode(newMode: IdentificationMode) {
        _mode.value = newMode
    }

    fun analyzePhoto(bitmap: Bitmap) {
        _uiState.value = IdentificationUiState.Analyzing

        viewModelScope.launch {
            if (_mode.value == IdentificationMode.ONLINE_GEMINI) {
                val cloudResult = geminiClient.analyzeMineralImage(bitmap)
                cloudResult.onSuccess { result ->
                    _uiState.value = IdentificationUiState.SuccessCloud(result, bitmap)
                }.onFailure { error ->
                    // Offer seamless offline fallback
                    _uiState.value = IdentificationUiState.Error(
                        message = "Cloud analysis failed: ${error.message}. You can run local expedition analysis without internet.",
                        canFallbackToOffline = true,
                        bitmap = bitmap
                    )
                }
            } else {
                runOfflineAnalysis(bitmap)
            }
        }
    }

    fun runOfflineAnalysis(bitmap: Bitmap) {
        _uiState.value = IdentificationUiState.Analyzing
        viewModelScope.launch {
            val catalog = repository.allMinerals.first()
            val offlineResult = offlineClassifier.classifyImage(bitmap, catalog)
            _uiState.value = IdentificationUiState.SuccessOffline(offlineResult, bitmap)
        }
    }

    fun resetScanner() {
        _uiState.value = IdentificationUiState.Idle
    }

    class Factory(
        private val repository: MineralRepository,
        private val geminiClient: GeminiClient
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CameraViewModel(repository, geminiClient) as T
        }
    }
}
