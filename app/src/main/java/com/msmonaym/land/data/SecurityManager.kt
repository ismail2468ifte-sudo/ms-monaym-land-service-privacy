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

    private fun convertToEnglishDigits(input: String): String {
        return input.replace('০', '0')
            .replace('১', '1')
            .replace('২', '2')
            .replace('৩', '3')
            .replace('৪', '4')
            .replace('৫', '5')
            .replace('৬', '6')
            .replace('৭', '7')
            .replace('৮', '8')
            .replace('৯', '9')
    }

    fun getSavedPin(): String {
        val saved = prefs.getString(KEY_PIN, DEFAULT_PIN) ?: DEFAULT_PIN
        return convertToEnglishDigits(saved)
    }

    fun savePin(newPin: String): Boolean {
        val converted = convertToEnglishDigits(newPin.trim())
        if (converted.length == 4 && converted.all { it.isDigit() }) {
            prefs.edit().putString(KEY_PIN, converted).apply()
            return true
        }
        return false
    }

    fun verifyPin(inputPin: String): Boolean {
        val convertedInput = convertToEnglishDigits(inputPin.trim())
        return convertedInput == getSavedPin()
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
