package com.msmonaym.land.data

import android.content.Context
import android.content.SharedPreferences

class UserProfileManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("land_user_profile_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_PROFILE_SET = "is_profile_set"
        const val DEFAULT_NAME = "ইসমাঈল"
        const val DEFAULT_PHONE = "01976444504"
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

    fun getUserName(): String {
        val savedName = prefs.getString(KEY_USER_NAME, DEFAULT_NAME)
        return if (savedName.isNullOrBlank()) DEFAULT_NAME else savedName
    }

    fun getUserPhone(): String {
        val savedPhone = prefs.getString(KEY_USER_PHONE, DEFAULT_PHONE)
        val phoneVal = if (savedPhone.isNullOrBlank()) DEFAULT_PHONE else savedPhone
        return convertToEnglishDigits(phoneVal)
    }

    fun saveProfile(name: String, phone: String): Boolean {
        val trimmedName = name.trim()
        val trimmedPhone = convertToEnglishDigits(phone.trim())
        if (trimmedName.isNotEmpty() && trimmedPhone.isNotEmpty()) {
            prefs.edit()
                .putString(KEY_USER_NAME, trimmedName)
                .putString(KEY_USER_PHONE, trimmedPhone)
                .putBoolean(KEY_PROFILE_SET, true)
                .apply()
            return true
        }
        return false
    }

    fun isProfileSet(): Boolean {
        return prefs.getBoolean(KEY_PROFILE_SET, false)
    }
}
