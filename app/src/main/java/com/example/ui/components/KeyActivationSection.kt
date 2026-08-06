package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.KeyRepository
import com.example.data.KeySessionManager
import com.example.data.KeyVerificationResult
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun KeyActivationSection(
    sessionManager: KeySessionManager,
    onSessionActivated: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { KeyRepository(sessionManager) }

    var keyInputText by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val lootLabsUrl = remember { sessionManager.getLootLabsUrl() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonPurpleBorder.copy(alpha = 0.9f),
                            CyberAccentPrimary.copy(alpha = 0.4f),
                            Color(0xFF2C1E4A)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF130D24).copy(alpha = 0.92f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shiny Hero Avatar Badge (Screenshot 3 style)
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.radialGradient(
                                colors = listOf(CyberAccent, NeonPurpleBorder, Color.Black)
                            ),
                            shape = CircleShape
                        )
                        .background(Color(0xFF0D0819)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_injector_avatar),
                        contentDescription = "Avatar Badge",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                // Authorization Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "АВТОРИЗАЦИЯ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Введите ключ для доступа",
                        fontSize = 12.sp,
                        color = CyberAccent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Error Banner
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberRed, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = CyberRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage!!,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Key Input Box
                OutlinedTextField(
                    value = keyInputText,
                    onValueChange = { keyInputText = it },
                    placeholder = {
                        Text(
                            text = "Введите ключ доступа...",
                            fontSize = 13.sp,
                            color = DarkTextMuted
                        )
                    },
                    singleLine = true,
                    enabled = !isVerifying,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1B1233),
                        unfocusedContainerColor = Color(0xFF1B1233),
                        disabledContainerColor = Color(0xFF1B1233),
                        focusedBorderColor = CyberAccent,
                        unfocusedBorderColor = Color(0xFF35255E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Button 1: ВОЙТИ / АКТИВИРОВАТЬ
                Button(
                    onClick = {
                        if (keyInputText.isBlank()) {
                            errorMessage = "Пожалуйста, введите ключ"
                            return@Button
                        }
                        isVerifying = true
                        errorMessage = null

                        coroutineScope.launch {
                            val result = repository.verifyKey(keyInputText)
                            isVerifying = false

                            when (result) {
                                is KeyVerificationResult.Success -> {
                                    sessionManager.activateKeySession(result.key, result.durationMs)
                                    onSessionActivated()
                                    Toast.makeText(context, "Доступ активирован на 2 часа!", Toast.LENGTH_LONG).show()
                                }
                                is KeyVerificationResult.Failure -> {
                                    errorMessage = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isVerifying,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberAccentPrimary,
                        disabledContainerColor = CyberAccentPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("ПРОВЕРКА...", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text(
                            text = "ВОЙТИ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Button 2: ПОЛУЧИТЬ КЛЮЧ БЕСПЛАТНО
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lootLabsUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Не удалось открыть браузер", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurpleBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberAccent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ПОЛУЧИТЬ КЛЮЧ БЕСПЛАТНО",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // System Settings Link
                TextButton(
                    onClick = { showSettingsDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = DarkTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Настройки API ключей (LootLabs / Lovable)",
                        fontSize = 11.sp,
                        color = DarkTextMuted
                    )
                }
            }
        }
    }

    // Config Settings Dialog
    if (showSettingsDialog) {
        var tempLootLabs by remember { mutableStateOf(sessionManager.getLootLabsUrl()) }
        var tempLovableApi by remember { mutableStateOf(sessionManager.getLovableApiUrl()) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = Color(0xFF130D24),
            titleContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Настройки LootLabs & Lovable API") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ссылка LootLabs:", fontSize = 12.sp, color = DarkTextMuted)
                    OutlinedTextField(
                        value = tempLootLabs,
                        onValueChange = { tempLootLabs = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberAccent)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Lovable API Endpoint:", fontSize = 12.sp, color = DarkTextMuted)
                    OutlinedTextField(
                        value = tempLovableApi,
                        onValueChange = { tempLovableApi = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberAccent)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionManager.setLootLabsUrl(tempLootLabs)
                        sessionManager.setLovableApiUrl(tempLovableApi)
                        showSettingsDialog = false
                        Toast.makeText(context, "Настройки сохранены", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPrimary)
                ) {
                    Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Закрыть", color = DarkTextMuted)
                }
            }
        )
    }
}

