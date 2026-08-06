package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UpdateStatus
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.DarkTextMuted

@Composable
fun UpdateDialog(
    updateStatus: UpdateStatus,
    onStartDownload: () -> Unit,
    onExecuteInstall: () -> Unit,
    onDismiss: () -> Unit
) {
    if (updateStatus is UpdateStatus.Idle || updateStatus is UpdateStatus.Checking) return

    // Screen overlay container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (updateStatus is UpdateStatus.DownloadCompleteSpinner) Color.Black.copy(alpha = 0.75f)
                else Color.Black.copy(alpha = 0.88f)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (updateStatus) {
            // STEP 1: Update Available Dialog
            is UpdateStatus.UpdateAvailable -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberAccent.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101018)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(CyberAccent.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "Update Icon",
                                tint = CyberAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Доступно новое обновление",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Вышла новая версия приложения (${updateStatus.newVersion}). Нажмите кнопку ниже, чтобы скачать и установить обновление.",
                            fontSize = 13.sp,
                            color = DarkTextMuted,
                            textAlign = TextAlign.Center
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Что нового в ${updateStatus.newVersion}:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = updateStatus.changelog,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Позже", color = Color.Gray, fontSize = 13.sp)
                            }

                            Button(
                                onClick = onStartDownload,
                                modifier = Modifier
                                    .weight(2f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberAccent)
                            ) {
                                Text(
                                    text = "Скачать обновление",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // STEP 2: Downloading Progress (0% -> 100%)
            is UpdateStatus.Downloading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberAccent.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101018)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(CyberAccent.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadForOffline,
                                contentDescription = "Downloading",
                                tint = CyberAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Загрузка обновления...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { updateStatus.progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = CyberAccent,
                                trackColor = Color(0xFF222230)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${updateStatus.progressPercent}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberAccent
                                )
                                Text(
                                    text = "${updateStatus.downloadedMb} MB / ${updateStatus.totalMb} MB",
                                    fontSize = 12.sp,
                                    color = DarkTextMuted
                                )
                            }

                            Text(
                                text = "Скорость: ${updateStatus.speedMbPerSec} MB/s",
                                fontSize = 11.sp,
                                color = DarkTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // STEP 3: Infinite Central Loading Spinner (100% reached, download menu closes)
            is UpdateStatus.DownloadCompleteSpinner -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = CyberAccent,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Подготовка файлов обновления...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // STEP 4: Installer Manager Dialog ("Менеджер установщик")
            is UpdateStatus.InstallerManager -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF3F3F56), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14141E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = "Installer Manager",
                                tint = CyberGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Менеджер установки",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Файл обновления готов к установке. Выберите и нажмите на пакет ниже для запуск обновления:",
                            fontSize = 12.sp,
                            color = DarkTextMuted,
                            textAlign = TextAlign.Center
                        )

                        // App update item to click
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, CyberAccent.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .clickable { onExecuteInstall() },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(CyberAccent.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Android,
                                        contentDescription = "APK",
                                        tint = CyberAccent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Injector Standoff 2 v2.5.0.apk",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "32.4 MB • Готов к установке",
                                        fontSize = 11.sp,
                                        color = CyberGreen
                                    )
                                }

                                Button(
                                    onClick = onExecuteInstall,
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Обновить", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // STEP 5: Installing update progress
            is UpdateStatus.InstallingUpdate -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = CyberGreen,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(60.dp)
                    )
                    Text(
                        text = "Установка обновления v2.5.0...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // STEP 6: Update Completed / Test update ready
            is UpdateStatus.UpdatedAndReady -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberGreen, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF101018))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = CyberGreen,
                            modifier = Modifier.size(56.dp)
                        )

                        Text(
                            text = "Приложение успешно обновлено!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Вы обновились до версии v2.5.0! Теперь вы можете протестировать все новые визуалы и обновленные функции.",
                            fontSize = 12.sp,
                            color = DarkTextMuted,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Протестировать обновление",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
