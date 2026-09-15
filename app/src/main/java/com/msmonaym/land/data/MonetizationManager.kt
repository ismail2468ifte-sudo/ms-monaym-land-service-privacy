package com.msmonaym.land.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages Monetization strategies, premium service charges, consult bookings,
 * digital survey fee tiers, and daily earnings tracking for the Admin.
 */
class MonetizationManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("land_monetization_prefs", Context.MODE_PRIVATE)

    companion object {
        // Feature toggles
        private const val KEY_MONETIZATION_ENABLED = "monetization_enabled"
        private const val KEY_SHOW_VIP_BADGE = "show_vip_badge"
        private const val KEY_ENABLE_CONSULTATION_BOOKING = "enable_consultation_booking"
        private const val KEY_ENABLE_SURVEY_HIRE = "enable_survey_hire"

        // Service Pricing (in BDT)
        private const val KEY_FEE_SURVEY_FIELD = "fee_survey_field" // আমিন/সার্ভেয়ার ফিল্ড ভিজিট ফি
        private const val KEY_FEE_CONSULTATION = "fee_consultation" // ফোন/হোয়াটসঅ্যাপ আইনি পরামর্শ ফি
        private const val KEY_FEE_KHATIAN_CHECK = "fee_khatian_check" // খতিয়ান ও পর্চা অনুসন্ধান ফি
        private const val KEY_FEE_DOC_DRAFT = "fee_doc_draft" // বায়নানামা/চুক্তিপত্র দলিল ড্রাফট ফি
        private const val KEY_FEE_REPORT_DOWNLOAD = "fee_report_download" // প্রিমিয়াম সার্ভে রিপোর্ট ফি

        // Defaults
        const val DEFAULT_FEE_SURVEY_FIELD = "1500"
        const val DEFAULT_FEE_CONSULTATION = "200"
        const val DEFAULT_FEE_KHATIAN_CHECK = "100"
        const val DEFAULT_FEE_DOC_DRAFT = "500"
        const val DEFAULT_FEE_REPORT_DOWNLOAD = "50"

        // Daily Earnings Log Tracker (stored as JSON/CSV string or total sum)
        private const val KEY_TOTAL_EARNINGS_SUM = "total_earnings_sum"
        private const val KEY_EARNINGS_HISTORY = "earnings_history_list"
    }

    fun isMonetizationActive(): Boolean = prefs.getBoolean(KEY_MONETIZATION_ENABLED, true)
    fun setMonetizationActive(active: Boolean) = prefs.edit().putBoolean(KEY_MONETIZATION_ENABLED, active).apply()

    fun isConsultationBookingEnabled(): Boolean = prefs.getBoolean(KEY_ENABLE_CONSULTATION_BOOKING, true)
    fun setConsultationBookingEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_ENABLE_CONSULTATION_BOOKING, enabled).apply()

    fun isSurveyHireEnabled(): Boolean = prefs.getBoolean(KEY_ENABLE_SURVEY_HIRE, true)
    fun setSurveyHireEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_ENABLE_SURVEY_HIRE, enabled).apply()

    // Pricing Getters & Setters
    fun getSurveyFieldFee(): String = prefs.getString(KEY_FEE_SURVEY_FIELD, DEFAULT_FEE_SURVEY_FIELD) ?: DEFAULT_FEE_SURVEY_FIELD
    fun setSurveyFieldFee(fee: String) = prefs.edit().putString(KEY_FEE_SURVEY_FIELD, fee.trim()).apply()

    fun getConsultationFee(): String = prefs.getString(KEY_FEE_CONSULTATION, DEFAULT_FEE_CONSULTATION) ?: DEFAULT_FEE_CONSULTATION
    fun setConsultationFee(fee: String) = prefs.edit().putString(KEY_FEE_CONSULTATION, fee.trim()).apply()

    fun getKhatianCheckFee(): String = prefs.getString(KEY_FEE_KHATIAN_CHECK, DEFAULT_FEE_KHATIAN_CHECK) ?: DEFAULT_FEE_KHATIAN_CHECK
    fun setKhatianCheckFee(fee: String) = prefs.edit().putString(KEY_FEE_KHATIAN_CHECK, fee.trim()).apply()

    fun getDocDraftFee(): String = prefs.getString(KEY_FEE_DOC_DRAFT, DEFAULT_FEE_DOC_DRAFT) ?: DEFAULT_FEE_DOC_DRAFT
    fun setDocDraftFee(fee: String) = prefs.edit().putString(KEY_FEE_DOC_DRAFT, fee.trim()).apply()

    fun getReportDownloadFee(): String = prefs.getString(KEY_FEE_REPORT_DOWNLOAD, DEFAULT_FEE_REPORT_DOWNLOAD) ?: DEFAULT_FEE_REPORT_DOWNLOAD
    fun setReportDownloadFee(fee: String) = prefs.edit().putString(KEY_FEE_REPORT_DOWNLOAD, fee.trim()).apply()

    // Total Earnings Tracking
    fun getTotalEarnings(): Long = prefs.getLong(KEY_TOTAL_EARNINGS_SUM, 0L)

    fun recordNewEarning(amount: Long, customer: String, service: String) {
        val current = getTotalEarnings()
        prefs.edit().putLong(KEY_TOTAL_EARNINGS_SUM, current + amount).apply()

        // Append to history string
        val currentHistory = prefs.getString(KEY_EARNINGS_HISTORY, "") ?: ""
        val newEntry = "${System.currentTimeMillis()}::$amount::$customer::$service"
        val updatedHistory = if (currentHistory.isEmpty()) newEntry else "$newEntry;;$currentHistory"
        prefs.edit().putString(KEY_EARNINGS_HISTORY, updatedHistory).apply()
    }

    fun getEarningsHistory(): List<EarningRecord> {
        val raw = prefs.getString(KEY_EARNINGS_HISTORY, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(";;").filter { it.isNotBlank() }.mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size >= 4) {
                EarningRecord(
                    timestamp = parts[0].toLongOrNull() ?: 0L,
                    amount = parts[1].toLongOrNull() ?: 0L,
                    customer = parts[2],
                    service = parts[3]
                )
            } else null
        }
    }
}

data class EarningRecord(
    val timestamp: Long,
    val amount: Long,
    val customer: String,
    val service: String
)
