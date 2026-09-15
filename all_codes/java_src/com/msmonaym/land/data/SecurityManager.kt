package com.msmonaym.land.data

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager

class SecurityManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("land_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN = "user_security_pin"
        private const val KEY_LOCK_ENABLED = "security_lock_enabled"
        private const val KEY_FINGERPRINT_ENABLED = "fingerprint_lock_enabled"
        const val DEFAULT_PIN = "1234"
    }

    fun getSavedPin(): String {
        return prefs.getString(KEY_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
    }

    fun savePin(newPin: String): Boolean {
        if (newPin.length == 4 && newPin.all { it.isDigit() }) {
            prefs.edit().putString(KEY_PIN, newPin).apply()
            return true
        }
        return false
    }

    fun verifyPin(inputPin: String): Boolean {
        return inputPin == getSavedPin()
    }

    fun isLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_LOCK_ENABLED, true)
    }

    fun setLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }

    fun isFingerprintEnabled(): Boolean {
        return prefs.getBoolean(KEY_FINGERPRINT_ENABLED, true)
    }

    fun setFingerprintEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FINGERPRINT_ENABLED, enabled).apply()
    }

    fun canAuthenticateWithBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }
}
