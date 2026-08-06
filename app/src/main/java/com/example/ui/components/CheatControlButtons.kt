package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CheatSettingsManager
import com.example.service.OverlayService
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.CyberAccentPrimary
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkTextMuted
import com.example.ui.theme.NeonPurpleBorder

@Composable
fun CheatControlButtons(
    onLaunchStandoffClicked: () -> Unit
) {
    val context = LocalContext.current
    val cheatState by CheatSettingsManager.state.collectAsState()

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showNotInstalledDialog by remember { mutableStateOf(false) }
    var showNewVersionDialog by remember { mutableStateOf(false) }
    var detectedVersionName by remember { mutableStateOf("0.39.2") }

    val checkAndStartCheat = {
        if (!Settings.canDrawOverlays(context)) {
            showPermissionDialog = true
        } else {
            // Check if Standoff 2 (com.axlebolt.standoff2) is installed
            val packageName = "com.axlebolt.standoff2"
            val packageManager = context.packageManager
            var isInstalled = false
            var installedVersion = "0.39.2"

            try {
                @Suppress("DEPRECATION")
                val pkgInfo = packageManager.getPackageInfo(packageName, 0)
                isInstalled = true
                if (!pkgInfo.versionName.isNullOrEmpty()) {
                    installedVersion = pkgInfo.versionName!!
                }
            } catch (e: Exception) {
                isInstalled = false
            }

            detectedVersionName = installedVersion

            // Start Overlay Service
            val overlayIntent = Intent(context, OverlayService::class.java)
            context.startService(overlayIntent)

            CheatSettingsManager.setDetectedStandoffVersion("StandOff 2 v$installedVersion")
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "18492041"
            val shortId = if (androidId.length >= 8) androidId.takeLast(8).uppercase() else "18492041"
            CheatSettingsManager.setAccountId("ID: $shortId")

            // Automatically ATTACH cheat and OPEN MENU
            CheatSettingsManager.startAttachProcess(isInGameOrLobby = true) {}
            CheatSettingsManager.completeAttachSuccess()
            CheatSettingsManager.setKlogotMenuOpen(true)

            if (!isInstalled) {
                Toast.makeText(context, "Чит запущен и меню открыто! (Standoff 2 не найден на телефоне)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Чит запущен! Переход в Standoff 2...", Toast.LENGTH_SHORT).show()
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    try {
                        context.startActivity(launchIntent)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    val stopCheatService = {
        val intent = Intent(context, OverlayService::class.java)
        context.stopService(intent)
        Toast.makeText(context, "Чит остановлен", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Cheat Action Control Card (Screenshot 2 style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(NeonPurpleBorder, Color(0xFF33205B))
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130D24).copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Button 1: ЗАПУСТИТЬ ЧИТ (Sleek dark purple box with glowing neon violet border)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1233))
                        .border(
                            width = 1.5.dp,
                            color = NeonPurpleBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { checkAndStartCheat() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = CyberAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ЗАПУСТИТЬ ЧИТ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = CyberAccent
                        )
                    }
                }

                // Button 2: ОСТАНОВИТЬ ЧИТ (Sleek dark purple box with neon pink/red border)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1C1024))
                        .border(
                            width = 1.5.dp,
                            color = CyberRed.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { stopCheatService() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = CyberRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ОСТАНОВИТЬ ЧИТ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = CyberRed
                        )
                    }
                }
            }
        }

        // Information Box (Screenshot 2 style: "Информация", "Версия игры", "Версия чита", "UNDETECT")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = NeonPurpleBorder.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF130D24).copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Информация",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberAccent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Версия игры: 0.39.2",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )

                    Text(
                        text = "Версия чита: 1.2",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )

                    Text(
                        text = "Тип: injector (no root)",
                        fontSize = 13.sp,
                        color = DarkTextMuted
                    )
                }

                // UNDETECT Tag (Bottom right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0C241B))
                        .border(1.dp, CyberGreen, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "UNDETECT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberGreen,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Button 3: Запустить Standoff 2
        OutlinedButton(
            onClick = { onLaunchStandoffClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurpleBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_standoff2),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Запустить Standoff 2",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // System Overlay Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            containerColor = Color(0xFF130D24),
            titleContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Требуется разрешение") },
            text = {
                Text(
                    text = "Для отображения плавающего оверлея поверх Standoff 2, необходимо предоставить разрешение 'Поверх других приложений'.",
                    fontSize = 13.sp,
                    color = DarkTextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPrimary)
                ) {
                    Text("Открыть настройки", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Отмена", color = DarkTextMuted)
                }
            }
        )
    }

    // Standoff 2 Not Installed Error Dialog
    if (showNotInstalledDialog) {
        AlertDialog(
            onDismissRequest = { showNotInstalledDialog = false },
            containerColor = Color(0xFF130D24),
            titleContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = CyberRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Игра не обнаружена", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "Ошибка: Игра Standoff 2 не обнаружена на вашем устройстве! Нажмите 'Скачать', чтобы перейти в Google Play Маркет и установить Standoff 2.",
                    fontSize = 13.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotInstalledDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.axlebolt.standoff2"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.axlebolt.standoff2"))
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPrimary)
                ) {
                    Text("Скачать Standoff 2", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotInstalledDialog = false }) {
                    Text("Закрыть", color = DarkTextMuted)
                }
            }
        )
    }

    // Standoff 2 New Version Update Available Dialog
    if (showNewVersionDialog) {
        AlertDialog(
            onDismissRequest = { showNewVersionDialog = false },
            containerColor = Color(0xFF130D24),
            titleContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = CyberAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Вышла новая версия!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "У вас установлена версия Standoff 2 v$detectedVersionName. В Google Play вышла новая актуальная версия Standoff 2! Обновите игру для полной поддержки всех чит-функций.",
                    fontSize = 13.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNewVersionDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.axlebolt.standoff2"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.axlebolt.standoff2"))
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPrimary)
                ) {
                    Text("Обновить в Google Play", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNewVersionDialog = false
                        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.axlebolt.standoff2")
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        }
                    }
                ) {
                    Text("Продолжить без обновления", color = DarkTextMuted)
                }
            }
        )
    }
}

