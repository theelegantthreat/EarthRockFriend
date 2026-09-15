package com.example.ui.screens.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.gemini.ChatMessage
import com.example.data.gemini.MessageSender
import com.example.ui.theme.EarthBrownDark
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.HazardRedBg
import com.example.ui.theme.MysticalPurple
import com.example.ui.theme.OchreGold
import com.example.ui.theme.WarningAmberBg
import kotlinx.coroutines.delay
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemConsultScreen(
    viewModel: GemConsultViewModel,
    initialPrompt: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf(initialPrompt ?: "") }
    val listState = rememberLazyListState()

    val quickQuestions = listOf(
        "Is Malachite safe to hold?",
        "How do I test Pyrite vs Gold?",
        "Which crystals dissolve in water?",
        "Lore of Aaron's breastplate",
        "Field Mohs hardness tests",
        "Explain Cinnabar toxicity"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    viewModel.attachImage(bitmap)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("gem_consult_screen_content")
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Toolbar with Copy and Clear options
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MysticalPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "GemConsult AI",
                        tint = MysticalPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "GemConsult AI Guide",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (viewModel.isApiKeyAvailable) "Powered by Gemini 3.5 Flash" else "Expedition Knowledge Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val clipboardManager = LocalClipboardManager.current
                var allCopied by remember { mutableStateOf(false) }

                LaunchedEffect(allCopied) {
                    if (allCopied) {
                        delay(2000)
                        allCopied = false
                    }
                }

                IconButton(
                    onClick = {
                        if (uiState.messages.isEmpty()) {
                            Toast.makeText(context, "No guide messages to copy yet", Toast.LENGTH_SHORT).show()
                        } else {
                            val notesExport = buildString {
                                appendLine("=== GemConsult AI Guide Field Notes ===")
                                uiState.messages.forEach { msg ->
                                    val senderTitle = when (msg.sender) {
                                        MessageSender.USER -> "Explorer"
                                        MessageSender.GEM_CONSULT -> "GemConsult AI Guide"
                                        MessageSender.SYSTEM -> "System"
                                    }
                                    appendLine("\n[$senderTitle]:")
                                    appendLine(msg.text)
                                }
                            }.trim()

                            clipboardManager.setText(AnnotatedString(notesExport))
                            allCopied = true
                            Toast.makeText(context, "Copied guide notes to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("gem_consult_copy_all_btn")
                ) {
                    Icon(
                        imageVector = if (allCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy Consultation to Notes",
                        tint = if (allCopied) ForestGreenPrimary else MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("chat_btn_clear")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestion Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickQuestions) { q ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.sendMessage(q) },
                    label = { Text(q, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Conversation Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (uiState.isGenerating) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MysticalPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MysticalPurple,
                                strokeWidth = 2.dp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GemConsult is evaluating crystallography & lore...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Image Attachment Preview
        if (uiState.selectedImage != null) {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            ) {
                Image(
                    bitmap = uiState.selectedImage!!.asImageBitmap(),
                    contentDescription = "Attached Specimen",
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { viewModel.attachImage(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.testTag("chat_btn_attach_photo")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Attach specimen photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about minerals, safety, lore...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text"),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty() || uiState.selectedImage != null) {
                        viewModel.sendMessage(text)
                        inputText = ""
                    }
                },
                enabled = !uiState.isGenerating && (inputText.isNotBlank() || uiState.selectedImage != null),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (!uiState.isGenerating && (inputText.isNotBlank() || uiState.selectedImage != null))
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("chat_btn_send")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (!uiState.isGenerating && (inputText.isNotBlank() || uiState.selectedImage != null))
                        Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isUser = message.sender == MessageSender.USER
    val isSystem = message.sender == MessageSender.SYSTEM
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSystem) WarningAmberBg else MysticalPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSystem) Icons.Default.Close else Icons.Default.AutoAwesome,
                    contentDescription = "GemConsult Avatar",
                    tint = if (isSystem) Color(0xFF8D4F00) else MysticalPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = when {
                isUser -> MaterialTheme.colorScheme.primary
                isSystem -> WarningAmberBg
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.88f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.attachedBitmap != null) {
                    Image(
                        bitmap = message.attachedBitmap.asImageBitmap(),
                        contentDescription = "User uploaded photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = when {
                        isUser -> Color.White
                        isSystem -> Color(0xFF4C2A00)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Copy button for easily pasting information into notes app
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.text))
                            isCopied = true
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isUser -> Color.White.copy(alpha = 0.22f)
                            isCopied -> ForestGreenPrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                        },
                        modifier = Modifier.testTag("chat_bubble_copy_btn_${message.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy message to notes",
                                tint = when {
                                    isUser -> Color.White
                                    isCopied -> ForestGreenPrimary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCopied) "Copied" else "Copy",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = when {
                                    isUser -> Color.White
                                    isCopied -> ForestGreenPrimary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
