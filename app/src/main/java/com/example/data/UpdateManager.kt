package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    data class UpdateAvailable(
        val newVersion: String,
        val changelog: String,
        val fileSizeMb: Double
    ) : UpdateStatus()

    data class Downloading(
        val progressPercent: Int,
        val downloadedMb: Double,
        val totalMb: Double,
        val speedMbPerSec: Double
    ) : UpdateStatus()

    object DownloadCompleteSpinner : UpdateStatus()
    object InstallerManager : UpdateStatus()
    object InstallingUpdate : UpdateStatus()
    object UpdatedAndReady : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

class UpdateManager {
    private val _status = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val status: StateFlow<UpdateStatus> = _status.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun checkForUpdates(forceShowAvailable: Boolean = false) {
        scope.launch {
            _status.value = UpdateStatus.Checking
            delay(1000)

            if (forceShowAvailable) {
                _status.value = UpdateStatus.UpdateAvailable(
                    newVersion = "v2.5.0",
                    changelog = "• Обновленные визуалы ESP и Chams обводки противников\n• Улучшена защита и поддержка Standoff 2\n• Добавлена новая система онлайн-синхронизации",
                    fileSizeMb = 32.4
                )
            } else {
                _status.value = UpdateStatus.Idle
            }
        }
    }

    fun startDownloadingUpdate() {
        scope.launch {
            val totalMb = 32.4
            var downloadedMb = 0.0
            val speedMbPerSec = 4.5

            while (downloadedMb < totalMb) {
                delay(150)
                downloadedMb += 1.8
                if (downloadedMb > totalMb) downloadedMb = totalMb
                val progress = ((downloadedMb / totalMb) * 100).toInt()

                _status.value = UpdateStatus.Downloading(
                    progressPercent = progress,
                    downloadedMb = String.format("%.1f", downloadedMb).toDouble(),
                    totalMb = totalMb,
                    speedMbPerSec = speedMbPerSec
                )
            }

            // 100% reached -> hide download menu and show central infinite loading spinner
            _status.value = UpdateStatus.DownloadCompleteSpinner
            delay(2000)

            // Show installer manager pop-up
            _status.value = UpdateStatus.InstallerManager
        }
    }

    fun executeInstallation() {
        scope.launch {
            _status.value = UpdateStatus.InstallingUpdate
            delay(2500) // Spinner disappears after installing
            _status.value = UpdateStatus.UpdatedAndReady
        }
    }

    fun dismissUpdateDialog() {
        _status.value = UpdateStatus.Idle
    }
}
