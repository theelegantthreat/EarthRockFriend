package com.example.ui.screens.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.gemini.GeminiAnalysisResult
import com.example.ui.navigation.Screen
import com.example.ui.screens.home.SpecimenTag
import com.example.ui.theme.EarthBrownDark
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.HazardRed
import com.example.ui.theme.HazardRedBg
import com.example.ui.theme.MysticalPurple
import com.example.ui.theme.OchreGold
import com.example.ui.theme.WarningAmber
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    viewModel.analyzePhoto(bitmap)
                }
            } catch (e: Exception) {
                Log.e("CameraScreen", "Failed to load image from gallery", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("camera_screen_content")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode Selector Tab (Online Gemini vs Offline Expedition)
        TabRow(
            selectedTabIndex = if (mode == IdentificationMode.ONLINE_GEMINI) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = mode == IdentificationMode.ONLINE_GEMINI,
                onClick = { viewModel.setMode(IdentificationMode.ONLINE_GEMINI) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Cloud AI",
                            modifier = Modifier.size(16.dp),
                            tint = if (mode == IdentificationMode.ONLINE_GEMINI) ForestGreenPrimary else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gemini Cloud AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = mode == IdentificationMode.OFFLINE_FIELD_EXPEDITION,
                onClick = { viewModel.setMode(IdentificationMode.OFFLINE_FIELD_EXPEDITION) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline Expedition",
                            modifier = Modifier.size(16.dp),
                            tint = if (mode == IdentificationMode.OFFLINE_FIELD_EXPEDITION) EarthBrownDark else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Expedition Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        when (val state = uiState) {
            is IdentificationUiState.Idle -> {
                if (hasCameraPermission) {
                    CameraCaptureView(
                        onCapture = { bitmap -> viewModel.analyzePhoto(bitmap) },
                        onPickGallery = { galleryLauncher.launch("image/*") }
                    )
                } else {
                    CameraPermissionFallback(
                        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        onPickGallery = { galleryLauncher.launch("image/*") }
                    )
                }
            }

            is IdentificationUiState.Analyzing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (mode == IdentificationMode.ONLINE_GEMINI) "Analyzing with Gemini Multi-Modal AI..."
                            else "Scanning On-Device Mineral Signatures...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Evaluating crystallography, hue clustering, and safety hazards...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            is IdentificationUiState.SuccessCloud -> {
                AnalysisResultView(
                    title = state.result.specimenName,
                    formula = state.result.chemicalFormula,
                    mohs = state.result.mohsHardness,
                    system = state.result.crystalSystem,
                    luster = state.result.luster,
                    safety = state.result.safetySummary,
                    isHazard = state.result.isHazardous,
                    details = state.result.fullAnalysis,
                    bitmap = state.capturedBitmap,
                    confidence = state.result.confidence,
                    onReset = { viewModel.resetScanner() },
                    onConsultAi = { onNavigate(Screen.GemConsult) }
                )
            }

            is IdentificationUiState.SuccessOffline -> {
                val top = state.result.topPrediction
                AnalysisResultView(
                    title = top?.specimen?.name ?: "Unknown Specimen",
                    formula = top?.specimen?.chemicalFormula ?: "Undetermined",
                    mohs = top?.specimen?.hardnessDisplay ?: "Field Test Required",
                    system = top?.specimen?.crystalSystem ?: "Amorphous/Unknown",
                    luster = top?.estimatedLuster ?: "Vitreous",
                    safety = top?.specimen?.safetyProtocols ?: "Follow standard field safety: wash hands after handling.",
                    isHazard = top?.isSafetyHazard == true,
                    details = "Field Match Rationale: ${top?.matchRationale}\n\n" +
                            "Color: ${top?.detectedColorCategory} (Hex ${state.result.dominantHexColor})\n" +
                            "Geographic Localities: ${top?.specimen?.geographicLocations}\n" +
                            "Diagnostic Field Test: ${top?.specimen?.diagnosticTests}",
                    bitmap = state.capturedBitmap,
                    confidence = "${top?.confidencePercent ?: 75}% (On-Device Expedition Match)",
                    onReset = { viewModel.resetScanner() },
                    onConsultAi = { onNavigate(Screen.GemConsult) }
                )
            }

            is IdentificationUiState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HazardRedBg),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Identification Notice",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = HazardRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5A1A1A)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (state.canFallbackToOffline && state.bitmap != null) {
                                Button(
                                    onClick = { viewModel.runOfflineAnalysis(state.bitmap) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EarthBrownDark)
                                ) {
                                    Text("Analyze Offline")
                                }
                            }
                            OutlinedButton(onClick = { viewModel.resetScanner() }) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraCaptureView(
    onCapture: (Bitmap) -> Unit,
    onPickGallery: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraScreen", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Viewfinder reticle overlay for crystal alignment
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.Center)
                .border(2.dp, ForestGreenLight.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
        ) {
            Text(
                text = "Center Specimen Inside Reticle",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // Bottom Controls Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Picker
                IconButton(
                    onClick = onPickGallery,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("camera_btn_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Pick from Gallery",
                        tint = Color.White
                    )
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, ForestGreenLight, CircleShape)
                        .background(Color.White)
                        .clickable {
                            val capture = imageCapture ?: return@clickable
                            capture.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        val bitmap = imageProxyToBitmap(image)
                                        image.close()
                                        onCapture(bitmap)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("CameraScreen", "Capture failed: ${exception.message}", exception)
                                    }
                                }
                            )
                        }
                        .testTag("camera_btn_shutter"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(ForestGreenPrimary)
                    )
                }

                // Lens switch
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("camera_btn_switch_lens")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPermissionFallback(
    onRequestPermission: () -> Unit,
    onPickGallery: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Camera Permission",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Camera Access Required",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Earth Rock Friend uses the camera to visually scan rock specimens in the field. You can also pick a photo from your gallery.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Grant Camera Permission")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onPickGallery) {
                Text("Pick Photo from Gallery")
            }
        }
    }
}

@Composable
fun AnalysisResultView(
    title: String,
    formula: String,
    mohs: String,
    system: String,
    luster: String,
    safety: String,
    isHazard: Boolean,
    details: String,
    bitmap: Bitmap,
    confidence: String,
    onReset: () -> Unit,
    onConsultAi: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Captured Image with Confidence Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = title,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = confidence,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ForestGreenLight
                )
            }
        }

        // Identification Title & Specs
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formula,
                            style = MaterialTheme.typography.bodyMedium,
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
                            text = "Mohs $mohs",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpecimenTag(text = system)
                    SpecimenTag(text = luster)
                }
            }
        }

        // Safety Caution Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isHazard) HazardRedBg else MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (isHazard) Icons.Default.Warning else Icons.Default.Shield,
                    contentDescription = "Safety",
                    tint = if (isHazard) HazardRed else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHazard) "⚠️ HAZARD / TOXICITY WARNING" else "Field Safety Protocol",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isHazard) HazardRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = safety,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isHazard) Color(0xFF5A1A1A) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Deep Analysis Text
        OutlinedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gemological & Lore Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Scan Another", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Again")
            }

            Button(
                onClick = onConsultAi,
                colors = ButtonDefaults.buttonColors(containerColor = MysticalPurple),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Consult GemConsult", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ask AI")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val plane = image.planes[0]
    val buffer: ByteBuffer = plane.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
