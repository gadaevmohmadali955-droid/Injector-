package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAccentPrimary
import com.example.ui.theme.DarkTextMuted
import kotlinx.coroutines.delay

@Composable
fun NetworkStatusBanner(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    var showConnectedNotification by remember { mutableStateOf(false) }

    // When connection restores (isConnected turns true), show "Connected" with green circle for 3 seconds then hide
    LaunchedEffect(isConnected) {
        if (isConnected) {
            showConnectedNotification = true
            delay(3000)
            showConnectedNotification = false
        }
    }

    val shouldShowBanner = !isConnected || showConnectedNotification

    AnimatedVisibility(
        visible = shouldShowBanner,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (isConnected) Color(0xFF0F2218) else Color(0xFF281018),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444)
            ),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Circle indicator: Red dot when offline, Green dot when online
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444),
                                shape = CircleShape
                            )
                    )

                    Text(
                        text = if (isConnected) "Connected" else "Reconnecting...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = if (isConnected) "Интернет подключен" else "Нет подключения к интернету",
                    fontSize = 11.sp,
                    color = if (isConnected) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                )
            }
        }
    }
}

@Composable
fun NoInternetDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161224),
        titleContentColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF3B1824), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Wi-Fi Unavailable",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Нет подключения",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Проверьте подключение к интернету на вашем устройстве и повторите попытку.",
                fontSize = 13.sp,
                color = DarkTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberAccentPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Понятно", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
