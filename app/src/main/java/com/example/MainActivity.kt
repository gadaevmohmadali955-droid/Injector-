package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.CheatSettingsManager
import com.example.data.KeySessionManager
import com.example.data.UpdateManager
import com.example.service.OverlayService
import com.example.service.SessionNotificationHelper
import com.example.ui.components.*
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.InjectorTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: KeySessionManager
    private val updateManager = UpdateManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sessionManager = KeySessionManager(this)

        // Check for app updates on startup
        updateManager.checkForUpdates(forceShowAvailable = false)

        setContent {
            InjectorTheme {
                MainAppScreen(
                    sessionManager = sessionManager,
                    updateManager = updateManager,
                    onLaunchStandoff = { launchStandoff2() }
                )
            }
        }
    }

    private fun launchStandoff2() {
        val packageName = "com.axlebolt.standoff2"
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, "Запуск Standoff 2...", Toast.LENGTH_SHORT).show()
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                startActivity(intent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    sessionManager: KeySessionManager,
    updateManager: UpdateManager,
    onLaunchStandoff: () -> Unit
) {
    val context = LocalContext.current
    val isSessionActive by sessionManager.isSessionActive.collectAsState()
    val remainingSeconds by sessionManager.remainingSeconds.collectAsState()
    val updateStatus by updateManager.status.collectAsState()
    val cheatState by CheatSettingsManager.state.collectAsState()

    var isConnected by remember { mutableStateOf(true) }
    var showNoInternetDialog by remember { mutableStateOf(false) }

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Уведомления таймера разрешены", Toast.LENGTH_SHORT).show()
        }
    }

    // Request notification permission automatically if needed
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Listen to real network connectivity changes
    DisposableEffect(context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
            }

            override fun onLost(network: Network) {
                isConnected = false
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm?.registerNetworkCallback(request, callback)

        onDispose {
            try {
                cm?.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // Real-time 1 second session ticker loop & expiration handler
    LaunchedEffect(isSessionActive) {
        while (true) {
            val active = sessionManager.updateSessionState()
            val remaining = sessionManager.remainingSeconds.value

            if (active && remaining > 0) {
                // Update live 2-hour countdown notification
                SessionNotificationHelper.updateNotification(context, remaining)
            } else if (isSessionActive && remaining <= 0) {
                // 2 HOURS EXPIRED!
                // 1. Cancel notification
                SessionNotificationHelper.cancelNotification(context)
                // 2. Stop Overlay Service automatically
                context.stopService(Intent(context, OverlayService::class.java))
                // 3. Clear session
                sessionManager.clearSession()
                // 4. Remove app from tabs and close task
                (context as? Activity)?.finishAndRemoveTask()
                break
            }
            delay(1000)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "INJECTOR",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E1E2A))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v2.5.0",
                                fontSize = 10.sp,
                                color = CyberAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Wifi toggle button to test offline/reconnecting state
                    IconButton(
                        onClick = {
                            isConnected = !isConnected
                            if (!isConnected) {
                                showNoInternetDialog = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Toggle Network Status",
                            tint = if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }

                    // Check Updates icon button
                    IconButton(
                        onClick = {
                            if (!isConnected) {
                                showNoInternetDialog = true
                            } else {
                                updateManager.checkForUpdates(forceShowAvailable = true)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Check Updates",
                            tint = CyberAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // White Floral Accents Background Pattern ("черный фон с белыми цветами")
            WhiteFloralBackgroundPattern()

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Network Status Banner ("Reconnecting..." / "Connected" banner)
                NetworkStatusBanner(isConnected = isConnected)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!isSessionActive) {
                        // KEY ACTIVATION SECTION (No access yet)
                        KeyActivationSection(
                            sessionManager = sessionManager,
                            onSessionActivated = {
                                if (!isConnected) {
                                    showNoInternetDialog = true
                                } else {
                                    sessionManager.updateSessionState()
                                }
                            }
                        )
                    } else {
                        // ACTIVE SESSION SCREEN (Access granted)
                        ActiveSessionHeader(
                            remainingSeconds = remainingSeconds,
                            sessionManager = sessionManager
                        )

                        CheatControlButtons(
                            onLaunchStandoffClicked = {
                                if (!isConnected) {
                                    showNoInternetDialog = true
                                } else {
                                    onLaunchStandoff()
                                }
                            }
                        )
                    }
                }
            }

            // No Internet Alert Dialog
            NoInternetDialog(
                showDialog = showNoInternetDialog,
                onDismiss = { showNoInternetDialog = false }
            )

            // Update Dialog Modal Overlay
            UpdateDialog(
                updateStatus = updateStatus,
                onStartDownload = {
                    if (!isConnected) {
                        showNoInternetDialog = true
                    } else {
                        updateManager.startDownloadingUpdate()
                    }
                },
                onExecuteInstall = { updateManager.executeInstallation() },
                onDismiss = { updateManager.dismissUpdateDialog() }
            )
        }
    }
}

@Composable
fun WhiteFloralBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Subtle white glowing floral petals / starry background dots
        val flowerCenters = listOf(
            Offset(w * 0.15f, h * 0.22f),
            Offset(w * 0.85f, h * 0.18f),
            Offset(w * 0.20f, h * 0.75f),
            Offset(w * 0.80f, h * 0.82f),
            Offset(w * 0.50f, h * 0.92f)
        )

        flowerCenters.forEach { center ->
            // Soft white glowing center
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 40f,
                center = center
            )

            // Petal lines (white flower effect)
            for (i in 0 until 6) {
                val angle = (i * 60) * (Math.PI / 180).toFloat()
                val petalEnd = Offset(
                    x = center.x + kotlin.math.cos(angle) * 30f,
                    y = center.y + kotlin.math.sin(angle) * 30f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = center,
                    end = petalEnd,
                    strokeWidth = 2f
                )
            }
        }
    }
}
