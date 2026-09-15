package com.example.ui.screens.chat

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.ChatMessage
import com.example.data.gemini.GeminiClient
import com.example.data.gemini.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GemConsultUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val selectedImage: Bitmap? = null,
    val errorMessage: String? = null
)

class GemConsultViewModel(
    private val geminiClient: GeminiClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GemConsultUiState(
            messages = listOf(
                ChatMessage(
                    sender = MessageSender.GEM_CONSULT,
                    text = "Greetings, field explorer! I am GemConsult, your resident mineralogist and lapidary lore scholar.\n\nAsk me anything regarding crystal systems, Mohs hardness tests, field safety & chemical toxicity, or ancient historical traditions and metaphysical therapy."
                )
            )
        )
    )
    val uiState: StateFlow<GemConsultUiState> = _uiState.asStateFlow()

    val isApiKeyAvailable: Boolean = geminiClient.isApiKeyAvailable

    fun attachImage(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(selectedImage = bitmap)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() && _uiState.value.selectedImage == null) return

        val attached = _uiState.value.selectedImage
        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = userText,
            attachedBitmap = attached
        )

        val updatedMessages = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            isGenerating = true,
            selectedImage = null,
            errorMessage = null
        )

        viewModelScope.launch {
            if (!geminiClient.isApiKeyAvailable) {
                // Friendly offline simulation if API key is not yet set in AI Studio Secrets
                val simulatedResponse = getOfflineKnowledgeFallback(userText)
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    messages = _uiState.value.messages + ChatMessage(
                        sender = MessageSender.GEM_CONSULT,
                        text = simulatedResponse
                    )
                )
                return@launch
            }

            val result = geminiClient.sendChatMessage(
                conversationHistory = updatedMessages,
                userMessage = userText,
                attachedBitmap = attached
            )

            result.onSuccess { reply ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    messages = _uiState.value.messages + ChatMessage(
                        sender = MessageSender.GEM_CONSULT,
                        text = reply
                    )
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = error.message,
                    messages = _uiState.value.messages + ChatMessage(
                        sender = MessageSender.SYSTEM,
                        text = "Unable to contact GemConsult AI: ${error.message}\n(Tip: Ensure your GEMINI_API_KEY is active in AI Studio Secrets)."
                    )
                )
            }
        }
    }

    fun clearChat() {
        _uiState.value = GemConsultUiState(
            messages = listOf(
                ChatMessage(
                    sender = MessageSender.GEM_CONSULT,
                    text = "Conversation refreshed. How may I guide your field mineralogy study today?"
                )
            )
        )
    }

    private fun getOfflineKnowledgeFallback(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("malachite") || q.contains("copper") ->
                "⚠️ Malachite Field Protocol: Malachite is Cu₂CO₃(OH)₂ (basic copper carbonate). It is safe to handle in polished form, but NEVER inhale cutting/grinding dust or make direct ingestible elixirs due to toxic copper poisoning. In folklore, ancient Egyptians ground it for kohl to ward off eye infections."
            q.contains("pyrite") || q.contains("gold") ->
                "🔍 Pyrite vs. Gold Diagnostic: Real gold has a Mohs hardness of 2.5-3.0 and produces a rich yellow streak. Pyrite (FeS₂) has a Mohs hardness of 6.0-6.5, a greenish-black streak, and smells of sulfur when struck."
            q.contains("selenite") || q.contains("dissolve") || q.contains("water") ->
                "💧 Water Sensitivity: Selenite (CaSO₄·2H₂O) has a Mohs hardness of 2.0 and dissolves slowly in water, losing all pearly luster. Other water-sensitive stones include Halite, Angelite, and Azurite. Cleanse them via dry sound or sage."
            q.contains("cinnabar") || q.contains("mercury") ->
                "☠️ Cinnabar Extreme Alert: Cinnabar is HgS (mercury sulfide). It is the primary ore of mercury. Never heat, grind, or taste. Taoist alchemists who ingested cinnabar elixirs suffered fatal mercury poisoning."
            else ->
                "GemConsult Field Assistant (Offline Companion):\nI have noted your inquiry about '$query'. In field mineralogy, always confirm identification using Streak, Mohs Hardness, and Cleavage before testing chemical solubility. For full generative AI insights with search grounding, configure your GEMINI_API_KEY in the AI Studio Secrets panel."
        }
    }

    class Factory(private val geminiClient: GeminiClient) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GemConsultViewModel(geminiClient) as T
        }
    }
}
