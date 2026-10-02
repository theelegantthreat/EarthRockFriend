package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.gemini.ChatMessage
import com.example.data.gemini.GeminiClient
import com.example.data.gemini.MessageSender
import com.example.data.local.MineralEntity
import com.example.ui.screens.camera.AnalysisResultView
import com.example.ui.screens.chat.ChatBubble
import com.example.ui.screens.chat.GemConsultScreen
import com.example.ui.screens.chat.GemConsultViewModel
import com.example.ui.screens.library.SpecimenDetailScreen
import com.example.util.ClipboardHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CopyButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCopyAllButtonCopiesToClipboard() {
        val client = GeminiClient()
        val viewModel = GemConsultViewModel(client)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

        composeTestRule.setContent {
            GemConsultScreen(
                viewModel = viewModel
            )
        }

        composeTestRule.waitForIdle()

        // Click the copy all button
        composeTestRule.onNodeWithTag("gem_consult_copy_all_btn").performClick()
        composeTestRule.waitForIdle()

        val clip = androidClipboard.primaryClip
        assertNotNull("Primary clip should not be null after copy all", clip)
        assertTrue("Clip should have items", clip!!.itemCount > 0)
        val text = clip.getItemAt(0).text.toString()
        assertTrue("Text should contain GemConsult", text.contains("GemConsult"))
    }

    @Test
    fun testChatBubbleCopyButtonCopiesToClipboard() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val testMessage = ChatMessage(
            id = "test-msg-1",
            sender = MessageSender.GEM_CONSULT,
            text = "Test message content to copy"
        )

        composeTestRule.setContent {
            ChatBubble(message = testMessage)
        }

        composeTestRule.waitForIdle()

        // Click the chat bubble copy button
        composeTestRule.onNodeWithTag("chat_bubble_copy_btn_test-msg-1").performClick()
        composeTestRule.waitForIdle()

        val clip = androidClipboard.primaryClip
        assertNotNull("Primary clip should not be null after chat bubble copy", clip)
        assertTrue("Clip should have items", clip!!.itemCount > 0)
        val text = clip.getItemAt(0).text.toString()
        assertEquals("Test message content to copy", text)
    }

    @Test
    fun testSpecimenDetailCopyDossierButton() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val testMineral = MineralEntity(
            id = 1,
            name = "Amethyst",
            chemicalFormula = "SiO₂",
            crystalSystem = "Hexagonal / Trigonal",
            hardnessDisplay = "7.0",
            hardnessMohsMin = 7.0,
            hardnessMohsMax = 7.0,
            luster = "Vitreous",
            color = "Purple, violet",
            colorCategory = "Purple",
            streak = "White",
            mysticalProperties = "Spiritual clarity and tranquility.",
            historicalLore = "Ancient Greek wine antidotes.",
            safetyProtocols = "Safe for general handling.",
            fieldNotes = "Locality: Thunder Bay, Ontario."
        )

        composeTestRule.setContent {
            SpecimenDetailScreen(
                mineral = testMineral,
                onBack = {},
                onToggleFavorite = { _, _ -> },
                onSaveNotes = { _, _ -> },
                onConsultAi = {}
            )
        }

        composeTestRule.waitForIdle()

        // Click the top app bar copy dossier button
        composeTestRule.onNodeWithTag("detail_btn_copy_dossier").performClick()
        composeTestRule.waitForIdle()

        val clip = androidClipboard.primaryClip
        assertNotNull("Primary clip should not be null after copy dossier", clip)
        val text = clip!!.getItemAt(0).text.toString()
        assertTrue("Dossier text must contain specimen name", text.contains("Amethyst"))
        assertTrue("Dossier text must contain chemical formula", text.contains("SiO₂"))
        assertTrue("Dossier text must contain field notes", text.contains("Thunder Bay"))
    }

    @Test
    fun testSpecimenDetailCopyNotesButton() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val testMineral = MineralEntity(
            id = 2,
            name = "Malachite",
            chemicalFormula = "Cu₂CO₃(OH)₂",
            crystalSystem = "Monoclinic",
            hardnessDisplay = "3.5 - 4.0",
            hardnessMohsMin = 3.5,
            hardnessMohsMax = 4.0,
            luster = "Silky, Adamantine",
            color = "Bright emerald green",
            colorCategory = "Green",
            streak = "Pale green",
            fieldNotes = "Found in copper mine tailings at 1200m elevation."
        )

        composeTestRule.setContent {
            SpecimenDetailScreen(
                mineral = testMineral,
                onBack = {},
                onToggleFavorite = { _, _ -> },
                onSaveNotes = { _, _ -> },
                onConsultAi = {}
            )
        }

        composeTestRule.waitForIdle()

        // Click copy notes button in My Field Notes
        composeTestRule.onNodeWithTag("detail_btn_copy_notes").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        val clip = androidClipboard.primaryClip
        assertNotNull("Primary clip should not be null after copy notes", clip)
        val text = clip!!.getItemAt(0).text.toString()
        assertEquals("Found in copper mine tailings at 1200m elevation.", text)
    }

    @Test
    fun testCameraScreenCopyAnalysisButton() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)

        composeTestRule.setContent {
            AnalysisResultView(
                title = "Pyrite",
                formula = "FeS₂",
                mohs = "6.0 - 6.5",
                system = "Isometric",
                luster = "Metallic",
                safety = "Contains sulfur. Wash hands after handling.",
                isHazard = false,
                details = "Brass-yellow metallic luster with distinct striations.",
                bitmap = bitmap,
                confidence = "92% On-Device Match",
                onReset = {},
                onConsultAi = {}
            )
        }

        composeTestRule.waitForIdle()

        // Click copy analysis button
        composeTestRule.onNodeWithTag("camera_btn_copy_analysis").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        val clip = androidClipboard.primaryClip
        assertNotNull("Primary clip should not be null after copy analysis", clip)
        val text = clip!!.getItemAt(0).text.toString()
        assertTrue("Analysis text should contain Pyrite", text.contains("Pyrite"))
        assertTrue("Analysis text should contain FeS₂", text.contains("FeS₂"))
        assertTrue("Analysis text should contain Mohs 6.0 - 6.5", text.contains("6.0 - 6.5"))
    }

    @Test
    fun testClipboardHelperEmptyTextDoesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = ClipboardHelper.copyToClipboard(context, "", showToast = false)
        assertEquals(false, result)
    }
}
