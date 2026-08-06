package com.example.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.R
import com.example.data.CheatSettingsManager
import com.example.data.KeySessionManager
import com.example.ui.components.KlogotMenuView
import com.example.ui.components.OverlayHudCanvas
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.InjectorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null

    private lateinit var sessionManager: KeySessionManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        sessionManager = KeySessionManager(this)
        CheatSettingsManager.setOverlayActive(true)

        lifecycleOwner = OverlayLifecycleOwner().apply { onCreate() }

        // Start session timer monitoring loop
        serviceScope.launch {
            while (true) {
                val isActive = sessionManager.updateSessionState()
                val remaining = sessionManager.remainingSeconds.value

                if (isActive && remaining > 0) {
                    SessionNotificationHelper.updateNotification(this@OverlayService, remaining)
                } else {
                    // 2 Hours Expired while playing Standoff 2 with Overlay!
                    SessionNotificationHelper.cancelNotification(this@OverlayService)
                    sessionManager.clearSession()
                    stopSelf() // Stop overlay automatically
                    break
                }
                delay(1000)
            }
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

            val owner = lifecycleOwner!!
            setViewTreeLifecycleOwner(owner)
            setViewTreeViewModelStoreOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)

            setContent {
                CompositionLocalProvider(
                    LocalLifecycleOwner provides owner
                ) {
                    InjectorTheme {
                        val cheatState by CheatSettingsManager.state.collectAsState()

                        Box(modifier = Modifier.fillMaxSize()) {
                            // 1. ESP & Aim Visual Overlay Canvas
                            OverlayHudCanvas(cheatState = cheatState)

                            // 2. Floating Draggable Avatar Button & Klogot Menu Window
                            FloatingControlView(
                                cheatState = cheatState,
                                onToggleMenu = {
                                    CheatSettingsManager.setKlogotMenuOpen(!cheatState.isKlogotMenuOpen)
                                }
                            )
                        }
                    }
                }
            }
        }

        overlayView = composeView
        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        SessionNotificationHelper.cancelNotification(this)
        CheatSettingsManager.setOverlayActive(false)
        overlayView?.let {
            windowManager.removeView(it)
        }
        lifecycleOwner?.onDestroy()
    }
}

@Composable
fun FloatingControlView(
    cheatState: com.example.data.CheatState,
    onToggleMenu: () -> Unit
) {
    var isMenuVisible by remember { mutableStateOf(cheatState.isKlogotMenuOpen) }

    // Position coordinates for draggable floating icon
    var offsetX by remember { mutableFloatStateOf(40f) }
    var offsetY by remember { mutableFloatStateOf(160f) }

    LaunchedEffect(cheatState.isKlogotMenuOpen) {
        isMenuVisible = cheatState.isKlogotMenuOpen
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // Draggable Floating Avatar Icon Button (Shiny 'I' icon)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, CyberAccent, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .clickable { onToggleMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_injector_avatar),
                        contentDescription = "Floating Injector Menu",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Expandable Klogot Menu Window
                AnimatedVisibility(
                    visible = isMenuVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    KlogotMenuView(
                        cheatState = cheatState,
                        onCloseClick = onToggleMenu
                    )
                }
            }
        }
    }
}

