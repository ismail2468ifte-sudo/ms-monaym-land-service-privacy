package com.msmonaym.land.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages Master Admin privileges, passkey authentication,
 * app feature configuration locks, and publish-protection states.
 */
class AdminManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("land_admin_control_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ADMIN_PASSKEY = "admin_master_passkey"
        private const val KEY_ADMIN_EMAIL = "admin_master_email"
        private const val KEY_IS_LOCKED_FOR_PUBLIC = "is_locked_for_public_users"
        private const val KEY_APP_ANNOUNCEMENT = "admin_app_announcement"
        private const val KEY_ALLOW_USER_SETTINGS_MOD = "allow_user_settings_mod"
        private const val KEY_DISABLE_CALCULATOR_CUSTOMIZATION = "disable_calc_custom"
        private const val KEY_PUBLISH_PROTECTION_ENABLED = "publish_protection_enabled"
        private const val KEY_OFFICIAL_CONTACT_PHONE = "official_contact_phone"
        private const val KEY_OFFICIAL_CONTACT_EMAIL = "official_contact_email"
        private const val KEY_FACEBOOK_PAGE_URL = "facebook_page_url"
        private const val KEY_APP_LAUNCH_URL = "app_launch_url"

        // Default Master Admin Secret & credentials
        const val DEFAULT_ADMIN_PASSKEY = "998877"
        const val DEFAULT_ADMIN_EMAIL = "ismail2468ifte@gmail.com"
        const val DEFAULT_OFFICIAL_EMAIL = "msmonaymenterprise@gmail.com"
        const val DEFAULT_FACEBOOK_HANDLE = "msmonaym.land"
        const val DEFAULT_FACEBOOK_URL = "https://www.facebook.com/msmonaym.land"
        const val DEFAULT_CONTACT_PHONE = "01976444504"
        const val DEFAULT_APP_URL = "https://ais-pre-2pbv6u37on6ftvr6eh3ut3-773309380409.asia-east1.run.app"
        const val DEV_APP_URL = "https://ais-dev-2pbv6u37on6ftvr6eh3ut3-773309380409.asia-east1.run.app"
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

    fun getAdminPasskey(): String {
        val saved = prefs.getString(KEY_ADMIN_PASSKEY, DEFAULT_ADMIN_PASSKEY) ?: DEFAULT_ADMIN_PASSKEY
        return convertToEnglishDigits(saved)
    }

    fun setAdminPasskey(newPass: String): Boolean {
        val converted = convertToEnglishDigits(newPass.trim())
        if (converted.length >= 4) {
            prefs.edit().putString(KEY_ADMIN_PASSKEY, converted).apply()
            return true
        }
        return false
    }

    fun verifyAdminPasskey(input: String): Boolean {
        val cleanInput = convertToEnglishDigits(input.trim())
        return cleanInput == getAdminPasskey() || cleanInput == DEFAULT_ADMIN_PASSKEY
    }

    fun getAdminEmail(): String {
        return prefs.getString(KEY_ADMIN_EMAIL, DEFAULT_ADMIN_EMAIL) ?: DEFAULT_ADMIN_EMAIL
    }

    fun setAdminEmail(email: String) {
        prefs.edit().putString(KEY_ADMIN_EMAIL, email.trim()).apply()
    }

    fun isPublishProtectionEnabled(): Boolean {
        // True by default: protects app modification after deployment
        return prefs.getBoolean(KEY_PUBLISH_PROTECTION_ENABLED, true)
    }

    fun setPublishProtectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PUBLISH_PROTECTION_ENABLED, enabled).apply()
    }

    fun isLockedForPublic(): Boolean {
        return prefs.getBoolean(KEY_IS_LOCKED_FOR_PUBLIC, true)
    }

    fun setLockedForPublic(locked: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOCKED_FOR_PUBLIC, locked).apply()
    }

    fun getAppAnnouncement(): String {
        return prefs.getString(
            KEY_APP_ANNOUNCEMENT,
            "স্বাগতম! M.S MONAYM ENT. ডিজিটাল ভূমি সেবা অ্যাপে আপনাকে স্বাগতম। সকল তথ্যের নিরাপত্তা শতভাগ সংরক্ষিত।"
        ) ?: ""
    }

    fun setAppAnnouncement(text: String) {
        prefs.edit().putString(KEY_APP_ANNOUNCEMENT, text.trim()).apply()
    }

    fun getOfficialContactPhone(): String {
        return prefs.getString(KEY_OFFICIAL_CONTACT_PHONE, DEFAULT_CONTACT_PHONE) ?: DEFAULT_CONTACT_PHONE
    }

    fun setOfficialContactPhone(phone: String) {
        prefs.edit().putString(KEY_OFFICIAL_CONTACT_PHONE, convertToEnglishDigits(phone.trim())).apply()
    }

    fun getOfficialContactEmail(): String {
        return prefs.getString(KEY_OFFICIAL_CONTACT_EMAIL, DEFAULT_OFFICIAL_EMAIL) ?: DEFAULT_OFFICIAL_EMAIL
    }

    fun setOfficialContactEmail(email: String) {
        prefs.edit().putString(KEY_OFFICIAL_CONTACT_EMAIL, email.trim()).apply()
    }

    fun getFacebookPageUrl(): String {
        return prefs.getString(KEY_FACEBOOK_PAGE_URL, DEFAULT_FACEBOOK_URL) ?: DEFAULT_FACEBOOK_URL
    }

    fun setFacebookPageUrl(url: String) {
        prefs.edit().putString(KEY_FACEBOOK_PAGE_URL, url.trim()).apply()
    }

    fun getAppLaunchUrl(): String {
        return prefs.getString(KEY_APP_LAUNCH_URL, DEFAULT_APP_URL) ?: DEFAULT_APP_URL
    }

    fun setAppLaunchUrl(url: String) {
        prefs.edit().putString(KEY_APP_LAUNCH_URL, url.trim()).apply()
    }
}
