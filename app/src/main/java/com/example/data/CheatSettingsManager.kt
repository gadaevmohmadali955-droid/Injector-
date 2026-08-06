package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CheatState(
    // Attach status
    val isAttached: Boolean = false,
    val isAttaching: Boolean = false,
    val standoffVersion: String = "v0.32.1",
    val accountId: String = "ID: 184920412",
    val attachErrorMessage: String? = null,

    // ESP Options
    val espBoxEnabled: Boolean = false,
    val espLinesEnabled: Boolean = false,
    val espHealthArmorEnabled: Boolean = false,
    val espEnemiesOnly: Boolean = true,
    val espSpeEnabled: Boolean = false, // SPE: Near ESP (< 60m) through walls
    val espPseEnabled: Boolean = false, // PSE: Far ESP (> 60m) white outlines through walls
    val espRadarDistanceListEnabled: Boolean = false, // Enemy distance list radar in bottom corner

    // AIM BOT Options
    val aimBotFovCircleEnabled: Boolean = false,
    val aimBotFovSize: Float = 120f, // pixels radius
    val aimBotSilentAim: Boolean = false,
    val aimBotAutoSnapHeadshot: Boolean = false,
    val aimBotTargetBone: String = "Голова", // Голова, Тело, Руки, Ноги

    // MISC Options
    val infiniteAmmo: Boolean = false,
    val fastGrenadeThrow: Boolean = false,
    val fastKnifeAttack: Boolean = false,
    val noReload: Boolean = false,
    val matchStatusMessage: String? = null,

    // Overlay visibility
    val isOverlayActive: Boolean = false,
    val isKlogotMenuOpen: Boolean = false
)

object CheatSettingsManager {
    private val _state = MutableStateFlow(CheatState())
    val state: StateFlow<CheatState> = _state.asStateFlow()

    fun setOverlayActive(active: Boolean) {
        _state.value = _state.value.copy(isOverlayActive = active)
    }

    fun setKlogotMenuOpen(open: Boolean) {
        _state.value = _state.value.copy(isKlogotMenuOpen = open)
    }

    fun setDetectedStandoffVersion(version: String) {
        _state.value = _state.value.copy(standoffVersion = version)
    }

    fun setAccountId(id: String) {
        _state.value = _state.value.copy(accountId = id)
    }

    // Attach Action
    fun startAttachProcess(isInGameOrLobby: Boolean = true, onNotAttachedError: (String) -> Unit) {
        if (!isInGameOrLobby) {
            val error = "Ошибка! Проверьте, находитесь ли вы в игре Standoff 2"
            _state.value = _state.value.copy(attachErrorMessage = error)
            onNotAttachedError(error)
            return
        }

        _state.value = _state.value.copy(isAttaching = true, attachErrorMessage = null)
    }

    fun completeAttachSuccess() {
        _state.value = _state.value.copy(
            isAttached = true,
            isAttaching = false,
            attachErrorMessage = null
        )
    }

    fun detach() {
        _state.value = _state.value.copy(
            isAttached = false,
            isAttaching = false,
            // Turn off features on detach
            espBoxEnabled = false,
            espLinesEnabled = false,
            espHealthArmorEnabled = false,
            espSpeEnabled = false,
            espPseEnabled = false,
            espRadarDistanceListEnabled = false,
            aimBotFovCircleEnabled = false,
            aimBotSilentAim = false,
            aimBotAutoSnapHeadshot = false,
            infiniteAmmo = false,
            fastGrenadeThrow = false,
            fastKnifeAttack = false,
            noReload = false
        )
    }

    // Toggle helper - Returns true if action succeeded, false if rejected due to not attached
    private fun checkAttachedOrWarn(onNotAttached: () -> Unit): Boolean {
        if (!_state.value.isAttached) {
            onNotAttached()
            return false
        }
        return true
    }

    fun toggleEspBox(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espBoxEnabled = !_state.value.espBoxEnabled)
        }
    }

    fun toggleEspLines(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espLinesEnabled = !_state.value.espLinesEnabled)
        }
    }

    fun toggleEspHealthArmor(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espHealthArmorEnabled = !_state.value.espHealthArmorEnabled)
        }
    }

    fun toggleEspEnemiesOnly(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espEnemiesOnly = !_state.value.espEnemiesOnly)
        }
    }

    fun toggleEspSpe(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espSpeEnabled = !_state.value.espSpeEnabled)
        }
    }

    fun toggleEspPse(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espPseEnabled = !_state.value.espPseEnabled)
        }
    }

    fun toggleEspRadarDistanceList(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(espRadarDistanceListEnabled = !_state.value.espRadarDistanceListEnabled)
        }
    }

    fun toggleAimBotFovCircle(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(aimBotFovCircleEnabled = !_state.value.aimBotFovCircleEnabled)
        }
    }

    fun setAimBotFovSize(size: Float, onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(aimBotFovSize = size)
        }
    }

    fun toggleAimBotSilentAim(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(
                aimBotSilentAim = !_state.value.aimBotSilentAim,
                // Disable conflicting auto snap if silent aim enabled
                aimBotAutoSnapHeadshot = if (!_state.value.aimBotSilentAim) false else _state.value.aimBotAutoSnapHeadshot
            )
        }
    }

    fun toggleAimBotAutoSnapHeadshot(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(
                aimBotAutoSnapHeadshot = !_state.value.aimBotAutoSnapHeadshot,
                aimBotSilentAim = if (!_state.value.aimBotAutoSnapHeadshot) false else _state.value.aimBotSilentAim
            )
        }
    }

    fun setTargetBone(bone: String, onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(aimBotTargetBone = bone)
        }
    }

    fun toggleInfiniteAmmo(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(infiniteAmmo = !_state.value.infiniteAmmo)
        }
    }

    fun toggleFastGrenadeThrow(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(fastGrenadeThrow = !_state.value.fastGrenadeThrow)
        }
    }

    fun toggleFastKnifeAttack(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(fastKnifeAttack = !_state.value.fastKnifeAttack)
        }
    }

    fun toggleNoReload(onNotAttached: () -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            _state.value = _state.value.copy(noReload = !_state.value.noReload)
        }
    }

    fun triggerCancelMatch(onNotAttached: () -> Unit, onResult: (String) -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            val msg = "Матч успешно отменён!"
            _state.value = _state.value.copy(matchStatusMessage = msg)
            onResult(msg)
        }
    }

    fun triggerWinMatch(onNotAttached: () -> Unit, onResult: (String) -> Unit) {
        if (checkAttachedOrWarn(onNotAttached)) {
            val msg = "Все противники уничтожены! Победа в матче!"
            _state.value = _state.value.copy(matchStatusMessage = msg)
            onResult(msg)
        }
    }
}
