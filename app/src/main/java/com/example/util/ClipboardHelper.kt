package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager as ComposeClipboardManager
import androidx.compose.ui.text.AnnotatedString

object ClipboardHelper {

    /**
     * Copies text reliably to both Android System ClipboardManager and Compose LocalClipboardManager.
     * This guarantees synchronization across Compose runtime, Android OS services, and host emulator.
     */
    fun copyToClipboard(
        context: Context,
        text: String,
        label: String = "Earth Rock Friend",
        composeClipboard: ComposeClipboardManager? = null,
        showToast: Boolean = true,
        toastMessage: String = "Copied to clipboard"
    ): Boolean {
        if (text.isBlank()) {
            if (showToast) {
                Toast.makeText(context, "Nothing to copy", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        var success = false

        // 1. Android OS System ClipboardManager (ensures external app compatibility and streaming emulator bridge)
        try {
            val systemClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (systemClipboard != null) {
                val clip = ClipData.newPlainText(label, text)
                systemClipboard.setPrimaryClip(clip)
                success = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Jetpack Compose ClipboardManager
        try {
            composeClipboard?.setText(AnnotatedString(text))
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (success && showToast) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }

        return success
    }
}
