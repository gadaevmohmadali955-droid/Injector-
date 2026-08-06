package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeySessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("injector_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EXPIRATION_TIMESTAMP = "key_expiration_timestamp"
        private const val KEY_LOOTLABS_URL = "key_lootlabs_url"
        private const val KEY_LOVABLE_API_URL = "key_lovable_api_url"
        private const val KEY_STORED_KEY = "key_stored_key"
        
        const val DEFAULT_LOOTLABS_URL = "https://lootlabs.gg/get-key-injector"
        const val DEFAULT_LOVABLE_API_URL = "https://lovable-keys-api.web.app/v1/verify"
        const val ACCESS_DURATION_MS = 2 * 60 * 60 * 1000L // 2 hours
    }

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    init {
        updateSessionState()
    }

    fun getLootLabsUrl(): String {
        return prefs.getString(KEY_LOOTLABS_URL, DEFAULT_LOOTLABS_URL) ?: DEFAULT_LOOTLABS_URL
    }

    fun setLootLabsUrl(url: String) {
        prefs.edit().putString(KEY_LOOTLABS_URL, url).apply()
    }

    fun getLovableApiUrl(): String {
        return prefs.getString(KEY_LOVABLE_API_URL, DEFAULT_LOVABLE_API_URL) ?: DEFAULT_LOVABLE_API_URL
    }

    fun setLovableApiUrl(url: String) {
        prefs.edit().putString(KEY_LOVABLE_API_URL, url).apply()
    }

    fun activateKeySession(key: String, durationMs: Long = ACCESS_DURATION_MS) {
        val now = System.currentTimeMillis()
        val expirationTime = now + durationMs
        prefs.edit()
            .putLong(KEY_EXPIRATION_TIMESTAMP, expirationTime)
            .putString(KEY_STORED_KEY, key)
            .apply()
        updateSessionState()
    }

    fun updateSessionState(): Boolean {
        val expirationTime = prefs.getLong(KEY_EXPIRATION_TIMESTAMP, 0L)
        val now = System.currentTimeMillis()
        val diff = expirationTime - now

        if (diff > 0) {
            _remainingSeconds.value = diff / 1000L
            _isSessionActive.value = true
            return true
        } else {
            if (expirationTime != 0L) {
                // Session expired, clear stored key
                clearSession()
            } else {
                _remainingSeconds.value = 0L
                _isSessionActive.value = false
            }
            return false
        }
    }

    fun clearSession() {
        prefs.edit().remove(KEY_EXPIRATION_TIMESTAMP).remove(KEY_STORED_KEY).apply()
        _remainingSeconds.value = 0L
        _isSessionActive.value = false
    }

    fun formatTimeString(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}
