package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.CheatState
import com.example.ui.theme.*

@Composable
fun KlogotMenuView(
    cheatState: CheatState,
    onCloseClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: ESP, 1: AIM BOT, 2: MISC, 3: ATTACH

    val notAttachedWarning = {
        Toast.makeText(
            context,
            "Ошибка! Сначала нажмите ATTACH во вкладке Attach",
            Toast.LENGTH_SHORT
        ).show()
    }

    Card(
        modifier = Modifier
            .width(340.dp)
            .wrapContentHeight()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(CyberAccent.copy(alpha = 0.8f), Color.White.copy(alpha = 0.2f))
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F14).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row with App Avatar & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_injector_avatar),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, CyberAccent, CircleShape)
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Klogot",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (cheatState.isAttached) CyberGreen.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (cheatState.isAttached) "ATTACHED" else "NOT ATTACHED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cheatState.isAttached) CyberGreen else CyberRed
                                )
                            }
                        }
                        Text(
                            text = "Standoff 2 Cheat Visual Menu",
                            fontSize = 11.sp,
                            color = DarkTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = DarkTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF181822))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val tabs = listOf("ESP", "AIM", "MISC", "ПРОФИЛЬ", "ATTACH")
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) CyberAccent.copy(alpha = 0.25f) else Color.Transparent
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CyberAccent else DarkTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> EspTabContent(cheatState, notAttachedWarning)
                    1 -> AimBotTabContent(cheatState, notAttachedWarning)
                    2 -> MiscTabContent(cheatState, notAttachedWarning)
                    3 -> ProfileTabContent(cheatState)
                    4 -> AttachTabContent(cheatState)
                }
            }
        }
    }
}

@Composable
private fun EspTabContent(
    cheatState: CheatState,
    onNotAttached: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "НАСТРОЙКИ ESP (ВИЗУАЛЫ)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberAccent
        )

        CheatToggleRow(
            title = "ESP Box (Квадраты)",
            subtitle = "Показывать боксы вокруг игроков",
            checked = cheatState.espBoxEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspBox(onNotAttached) }
        )

        CheatToggleRow(
            title = "ESP Snaplines (Линии)",
            subtitle = "Линия снизу экрана до противников",
            checked = cheatState.espLinesEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspLines(onNotAttached) }
        )

        CheatToggleRow(
            title = "Показ HP & Брони",
            subtitle = "Отображать реальный уровень здоровья и брони",
            checked = cheatState.espHealthArmorEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspHealthArmor(onNotAttached) }
        )

        CheatToggleRow(
            title = "SPE (Ближний ESP < 60м)",
            subtitle = "Показывает противников через стены рядом (< 60м)",
            checked = cheatState.espSpeEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspSpe(onNotAttached) }
        )

        CheatToggleRow(
            title = "PSE (Дальний ESP > 60м)",
            subtitle = "Показывает дальних противников (> 60м) в белом обводе",
            checked = cheatState.espPseEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspPse(onNotAttached) }
        )

        CheatToggleRow(
            title = "Радар дистанции (Снизу в углу)",
            subtitle = "Список всех соперников и метры до них в углу экрана",
            checked = cheatState.espRadarDistanceListEnabled,
            onCheckedChange = { CheatSettingsManager.toggleEspRadarDistanceList(onNotAttached) }
        )

        CheatToggleRow(
            title = "Только на противников",
            subtitle = "Игнорировать союзников своей команды",
            checked = cheatState.espEnemiesOnly,
            onCheckedChange = { CheatSettingsManager.toggleEspEnemiesOnly(onNotAttached) }
        )
    }
}

@Composable
private fun AimBotTabContent(
    cheatState: CheatState,
    onNotAttached: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "НАСТРОЙКИ AIM BOT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberAccent
        )

        CheatToggleRow(
            title = "FOV Круг Прицела",
            subtitle = "Показывать радиус срабатывания аима",
            checked = cheatState.aimBotFovCircleEnabled,
            onCheckedChange = { CheatSettingsManager.toggleAimBotFovCircle(onNotAttached) }
        )

        if (cheatState.aimBotFovCircleEnabled) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14141E))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Размер FOV кружка: ${cheatState.aimBotFovSize.toInt()} px",
                    fontSize = 11.sp,
                    color = Color.White
                )
                Slider(
                    value = cheatState.aimBotFovSize,
                    onValueChange = { CheatSettingsManager.setAimBotFovSize(it, onNotAttached) },
                    valueRange = 50f..300f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberAccent,
                        activeTrackColor = CyberAccent
                    )
                )
            }
        }

        CheatToggleRow(
            title = "Silent Aim (Векторный)",
            subtitle = "Стрельба в зону FOV зачитывает попадание по направлению",
            checked = cheatState.aimBotSilentAim,
            onCheckedChange = { CheatSettingsManager.toggleAimBotSilentAim(onNotAttached) }
        )

        CheatToggleRow(
            title = "Auto Snap Headshot",
            subtitle = "При появлении в круге автоматически наводит в голову",
            checked = cheatState.aimBotAutoSnapHeadshot,
            onCheckedChange = { CheatSettingsManager.toggleAimBotAutoSnapHeadshot(onNotAttached) }
        )

        // Target bone selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF14141E))
                .padding(8.dp)
        ) {
            Text(
                text = "Целевая точка попадания:",
                fontSize = 11.sp,
                color = DarkTextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val bones = listOf("Голова", "Тело", "Руки", "Ноги")
                bones.forEach { bone ->
                    val isSel = cheatState.aimBotTargetBone == bone
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) CyberAccent else Color(0xFF222230))
                            .clickable { CheatSettingsManager.setTargetBone(bone, onNotAttached) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bone,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiscTabContent(
    cheatState: CheatState,
    onNotAttached: () -> Unit
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ДОПОЛНИТЕЛЬНЫЕ ФУНКЦИИ (MISC)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberAccent
        )

        CheatToggleRow(
            title = "Бесконечные патроны",
            subtitle = "Патроны не расходуются при стрельбе",
            checked = cheatState.infiniteAmmo,
            onCheckedChange = { CheatSettingsManager.toggleInfiniteAmmo(onNotAttached) }
        )

        CheatToggleRow(
            title = "Быстрое бросание гранат",
            subtitle = "Бросок гранаты без задержки анимации",
            checked = cheatState.fastGrenadeThrow,
            onCheckedChange = { CheatSettingsManager.toggleFastGrenadeThrow(onNotAttached) }
        )

        CheatToggleRow(
            title = "Быстрый удар ножом",
            subtitle = "Увеличивает скорость атаки ножом",
            checked = cheatState.fastKnifeAttack,
            onCheckedChange = { CheatSettingsManager.toggleFastKnifeAttack(onNotAttached) }
        )

        CheatToggleRow(
            title = "Без перезарядки",
            subtitle = "Мгновенная сменяемость магазинов",
            checked = cheatState.noReload,
            onCheckedChange = { CheatSettingsManager.toggleNoReload(onNotAttached) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    CheatSettingsManager.triggerCancelMatch(onNotAttached) { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C3A)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("Отменить матч", fontSize = 11.sp, color = Color.White)
            }

            Button(
                onClick = {
                    CheatSettingsManager.triggerWinMatch(onNotAttached) { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text("Выиграть матч", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileTabContent(
    cheatState: CheatState
) {
    val context = LocalContext.current
    var inputId by remember { mutableStateOf(cheatState.standoffPlayerId) }
    var inputNick by remember { mutableStateOf(cheatState.standoffNickname) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ПРИВЯЗКА АККАУНТА STANDOFF 2",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberAccent
        )

        // Connected Account Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141420))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Player Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF221A38))
                        .border(2.dp, CyberAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_injector_avatar),
                        contentDescription = "Player Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cheatState.standoffNickname,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = cheatState.standoffClan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberAccent
                            )
                        }
                    }

                    Text(
                        text = "ID: ${cheatState.standoffPlayerId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen
                    )

                    Text(
                        text = "Уровень: ${cheatState.standoffLevel} | Статус: Подключен",
                        fontSize = 10.sp,
                        color = DarkTextMuted
                    )
                }
            }
        }

        // Input Fields to Change Standoff ID or Connect
        Text(
            text = "Введите ваш ID в Standoff 2:",
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = inputId,
            onValueChange = { inputId = it },
            placeholder = { Text("Например: 18492041", color = DarkTextMuted, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccent,
                unfocusedBorderColor = Color(0xFF333348),
                focusedContainerColor = Color(0xFF161622),
                unfocusedContainerColor = Color(0xFF161622),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = inputNick,
            onValueChange = { inputNick = it },
            placeholder = { Text("Никнейм игрока", color = DarkTextMuted, fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberAccent,
                unfocusedBorderColor = Color(0xFF333348),
                focusedContainerColor = Color(0xFF161622),
                unfocusedContainerColor = Color(0xFF161622),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = {
                if (inputId.isBlank()) {
                    Toast.makeText(context, "Введите ваш ID Standoff 2!", Toast.LENGTH_SHORT).show()
                } else {
                    CheatSettingsManager.connectStandoffAccount(inputId, inputNick)
                    Toast.makeText(context, "Аккаунт Standoff 2 (ID: $inputId) успешно подключен!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberAccent),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("ПОДКЛЮЧИТЬ АККАУНТ СТАНДОФФ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
private fun AttachTabContent(
    cheatState: CheatState
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ПОДКЛЮЧЕНИЕ К ИГРЕ (STANDOFF 2)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberAccent
        )

        if (cheatState.attachErrorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberRed.copy(alpha = 0.15f))
                    .border(1.dp, CyberRed, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = cheatState.attachErrorMessage,
                    fontSize = 11.sp,
                    color = CyberRed,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (cheatState.isAttaching) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                CircularProgressIndicator(
                    color = CyberAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Поиск процесса Standoff 2...",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        } else if (!cheatState.isAttached) {
            Button(
                onClick = {
                    val packageName = "com.axlebolt.standoff2"
                    val packageManager = context.packageManager
                    var isInstalled = false
                    var installedVersion = "v0.39.2"

                    try {
                        @Suppress("DEPRECATION")
                        val pkgInfo = packageManager.getPackageInfo(packageName, 0)
                        isInstalled = true
                        if (!pkgInfo.versionName.isNullOrEmpty()) {
                            installedVersion = "v${pkgInfo.versionName}"
                        }
                    } catch (e: Exception) {
                        isInstalled = false
                    }

                    if (!isInstalled) {
                        Toast.makeText(
                            context,
                            "Ошибка: Standoff 2 не обнаружен! Установите игру.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        CheatSettingsManager.setDetectedStandoffVersion(installedVersion)
                        CheatSettingsManager.setAccountId("ID: 18492041")
                        CheatSettingsManager.startAttachProcess(
                            isInGameOrLobby = true,
                            onNotAttachedError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        )
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            CheatSettingsManager.completeAttachSuccess()
                            Toast.makeText(context, "Подключено к Standoff 2 ($installedVersion)", Toast.LENGTH_SHORT).show()
                        }, 800)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "ПОДКЛЮЧИТЬСЯ (ATTACH)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        } else {
            // ATTACHED STATE - Show rounded card with Standoff 2 icon, Version and Account ID
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121E18))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_standoff2),
                                contentDescription = "Standoff 2 Logo",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                            )
                            Column {
                                Text(
                                    text = "Standoff 2",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Версия: ${cheatState.standoffVersion}",
                                    fontSize = 11.sp,
                                    color = DarkTextMuted
                                )
                                Text(
                                    text = "Аккаунт: ${cheatState.accountId}",
                                    fontSize = 11.sp,
                                    color = CyberGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Attached",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "✓ Читы и функции Klogot активированы. Доступны в лобби и катках.",
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Stop Cheat & Close Overlay Service Button
            Button(
                onClick = {
                    CheatSettingsManager.detach()
                    val intent = android.content.Intent(context, com.example.service.OverlayService::class.java)
                    context.stopService(intent)
                    Toast.makeText(context, "Чит выключен, оверлей закрыт", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ВЫКЛЮЧИТЬ ЧИТ (ЗАКРЫТЬ OVERLAY)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun CheatToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF14141E))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = DarkTextMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyberAccent,
                uncheckedThumbColor = DarkTextMuted,
                uncheckedTrackColor = Color(0xFF22222E)
            )
        )
    }
}
