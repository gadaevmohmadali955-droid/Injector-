package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.CheatState
import kotlin.math.sin

data class SimulatedTarget(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val widthRatio: Float,
    val heightRatio: Float,
    val hp: Int,
    val armor: Int,
    val name: String,
    val baseDistance: Int
)

@Composable
fun OverlayHudCanvas(cheatState: CheatState) {
    if (!cheatState.isAttached) return

    val textMeasurer = rememberTextMeasurer()

    // Infinite transition to simulate active live game player movement and dynamic distance tracking
    val infiniteTransition = rememberInfiniteTransition(label = "hud_live_motion")
    val motionPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "motion_phase"
    )

    // Base enemy player targets in the round
    val baseTargets = remember {
        listOf(
            SimulatedTarget(1, 0.28f, 0.32f, 0.12f, 0.30f, 90, 100, "Соперник 1", 14),
            SimulatedTarget(2, 0.46f, 0.26f, 0.10f, 0.26f, 65, 80, "Соперник 2", 28),
            SimulatedTarget(3, 0.65f, 0.38f, 0.08f, 0.22f, 100, 100, "Соперник 3", 52),
            SimulatedTarget(4, 0.82f, 0.20f, 0.06f, 0.18f, 40, 50, "Соперник 4", 72),
            SimulatedTarget(5, 0.14f, 0.18f, 0.05f, 0.16f, 100, 100, "Соперник 5", 88)
        )
    }

    // Dynamic targets with real-time movement offsets & distance shifts
    val dynamicTargets = baseTargets.map { target ->
        val xShift = (sin((motionPhase + target.id).toDouble()) * 0.02).toFloat()
        val yShift = (sin((motionPhase * 1.5 + target.id).toDouble()) * 0.015).toFloat()
        val distShift = (sin((motionPhase + target.id * 2).toDouble()) * 3).toInt()

        val dynamicDist = (target.baseDistance + distShift).coerceAtLeast(1)

        target.copy(
            xRatio = (target.xRatio + xShift).coerceIn(0.05f, 0.85f),
            yRatio = (target.yRatio + yShift).coerceIn(0.05f, 0.70f),
            baseDistance = dynamicDist
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val screenWidth = size.width
        val screenHeight = size.height

        // 1. AIM BOT FOV Circle
        if (cheatState.aimBotFovCircleEnabled) {
            val center = Offset(screenWidth / 2f, screenHeight / 2f)
            val radius = cheatState.aimBotFovSize

            drawCircle(
                color = Color(0x8000E5FF),
                radius = radius,
                center = center,
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
            )

            // Crosshair dot
            drawCircle(
                color = Color.Cyan,
                radius = 3f,
                center = center
            )
        }

        // 2. ESP Targets Rendering (Standard ESP, SPE <60m, PSE >60m)
        dynamicTargets.forEach { target ->
            val left = target.xRatio * screenWidth
            val top = target.yRatio * screenHeight
            val width = target.widthRatio * screenWidth
            val height = target.heightRatio * screenHeight
            val bottom = top + height
            val centerX = left + (width / 2f)

            val currentDist = target.baseDistance
            val isNearTarget = currentDist < 60

            // SPE Feature: Near targets (< 60m) rendered in neon red/orange through walls
            val isSpeActive = cheatState.espSpeEnabled && isNearTarget

            // PSE Feature: Far targets (>= 60m) rendered in white outlines through walls
            val isPseActive = cheatState.espPseEnabled && !isNearTarget

            // Standard ESP Box
            if (cheatState.espBoxEnabled || isSpeActive || isPseActive) {
                val boxColor = when {
                    isSpeActive -> Color(0xFFFF3333) // Neon Red for near SPE
                    isPseActive -> Color.White       // Pure White for far PSE
                    else -> Color.Red
                }

                val strokeWidth = if (isSpeActive || isPseActive) 4f else 3f

                drawRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    style = Stroke(width = strokeWidth)
                )

                // Head circle
                val headRadius = width * 0.22f
                drawCircle(
                    color = if (isPseActive) Color.White else Color.Yellow,
                    radius = headRadius,
                    center = Offset(centerX, top + headRadius + 4f),
                    style = Stroke(width = 2f)
                )

                // Label above box
                val prefixText = when {
                    isSpeActive -> "[SPE <60m] "
                    isPseActive -> "[PSE >60m] "
                    else -> ""
                }

                val labelText = "$prefixText${target.name}: ${currentDist}м"
                val textLayout = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(
                        color = if (isPseActive) Color.White else if (isSpeActive) Color(0xFFFFD700) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(left, top - 20f)
                )
            }

            // ESP Snaplines (Line from bottom center to target)
            if (cheatState.espLinesEnabled || (isPseActive && cheatState.espPseEnabled)) {
                val lineColor = if (isPseActive) Color.White else Color(0xFFFF2A5F)
                drawLine(
                    color = lineColor,
                    start = Offset(screenWidth / 2f, screenHeight),
                    end = Offset(centerX, bottom),
                    strokeWidth = 2f
                )
            }

            // ESP Health & Armor
            if (cheatState.espHealthArmorEnabled) {
                // Health bar on left side of box
                val hpBarWidth = 5f
                val hpHeight = height * (target.hp / 100f)
                drawRect(
                    color = Color.DarkGray,
                    topLeft = Offset(left - hpBarWidth - 4f, top),
                    size = Size(hpBarWidth, height)
                )
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(left - hpBarWidth - 4f, top + (height - hpHeight)),
                    size = Size(hpBarWidth, hpHeight)
                )

                val textLayout = textMeasurer.measure(
                    text = "HP:${target.hp}% AP:${target.armor}%",
                    style = TextStyle(color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                )

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(left, bottom + 4f)
                )
            }
        }

        // 3. Bottom Corner Distance Radar List Overlay ("соперник 1 находится от вас X м ... столько сколько соперников в раунде")
        if (cheatState.espRadarDistanceListEnabled) {
            val radarPadding = 20f
            val cardWidth = 320f
            val rowHeight = 22f
            val headerHeight = 30f
            val cardHeight = headerHeight + (dynamicTargets.size * rowHeight) + 12f

            val radarLeft = radarPadding
            val radarTop = screenHeight - cardHeight - radarPadding

            // Semi-transparent dark background
            drawRoundRect(
                color = Color(0xDC0A0A14),
                topLeft = Offset(radarLeft, radarTop),
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Neon border
            drawRoundRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(radarLeft, radarTop),
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(12f, 12f),
                style = Stroke(width = 2f)
            )

            // Header title
            val titleLayout = textMeasurer.measure(
                text = "🎯 РАДАР СОПЕРНИКОВ В РАУНДЕ",
                style = TextStyle(
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = titleLayout,
                topLeft = Offset(radarLeft + 12f, radarTop + 8f)
            )

            // Enemy Distance Rows
            dynamicTargets.forEachIndexed { index, target ->
                val currentY = radarTop + headerHeight + (index * rowHeight)

                val distColor = when {
                    target.baseDistance < 30 -> Color(0xFFFF4444) // Very close (Red)
                    target.baseDistance < 60 -> Color(0xFFFFBB00) // Medium SPE (Yellow)
                    else -> Color(0xFF00FFCC)                 // Far PSE (Cyan)
                }

                val rowText = "• ${target.name} находится от вас: ${target.baseDistance}м"
                val rowLayout = textMeasurer.measure(
                    text = rowText,
                    style = TextStyle(
                        color = distColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                drawText(
                    textLayoutResult = rowLayout,
                    topLeft = Offset(radarLeft + 12f, currentY)
                )
            }
        }
    }
}
