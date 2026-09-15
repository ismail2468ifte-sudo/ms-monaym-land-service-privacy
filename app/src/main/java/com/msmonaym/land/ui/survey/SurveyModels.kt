package com.msmonaym.land.ui.survey

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Model for a land plot measurement segment (Excel Spreadsheet Row)
 */
data class SurveyPlotEntry(
    val id: String = UUID.randomUUID().toString(),
    var plotName: String = "প্লট-১ (প্রধান জমি)",
    var northFt: Double = 66.0,
    var southFt: Double = 66.0,
    var eastFt: Double = 66.0,
    var westFt: Double = 66.0,
    var diagonalFt: Double = 0.0, // Optional diagonal for Heron's formula
    var notes: String = "সীমানা সঠিক রয়েছে"
) {
    val avgLength: Double
        get() = (northFt + southFt) / 2.0

    val avgWidth: Double
        get() = (eastFt + westFt) / 2.0

    val squareFeet: Double
        get() {
            if (diagonalFt > 0) {
                // Heron's formula for two triangles
                // Triangle 1: north, east, diagonal
                // Triangle 2: south, west, diagonal
                val s1 = (northFt + eastFt + diagonalFt) / 2.0
                val area1 = if (s1 > northFt && s1 > eastFt && s1 > diagonalFt) {
                    kotlin.math.sqrt(s1 * (s1 - northFt) * (s1 - eastFt) * (s1 - diagonalFt))
                } else 0.0

                val s2 = (southFt + westFt + diagonalFt) / 2.0
                val area2 = if (s2 > southFt && s2 > westFt && s2 > diagonalFt) {
                    kotlin.math.sqrt(s2 * (s2 - southFt) * (s2 - westFt) * (s2 - diagonalFt))
                } else 0.0

                val total = area1 + area2
                if (total > 0) return total
            }
            return avgLength * avgWidth
        }

    val decimal: Double
        get() = squareFeet / 435.6

    val katha: Double
        get() = squareFeet / 720.0

    val bigha: Double
        get() = decimal / 33.06 // Standard 33 decimal per bigha in BD (20 katha)

    val acre: Double
        get() = decimal / 100.0
}

/**
 * Model for shareholder or heir portion
 */
data class SurveyShareholder(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var relation: String = "অংশীদার",
    var sharePercentage: Double = 100.0, // Percentage of total land
    var notes: String = ""
)

/**
 * Complete Land Survey Official Report Data (Word + Excel state)
 */
data class SurveyReportData(
    var reportId: String = "SRV-" + SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault()).format(Date()),
    var date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    
    // Organization & Surveyor Details
    var organizationName: String = "স্মার্ট ভূমি সার্ভে ও কনসালটেন্সি",
    var surveyorName: String = "এম.এস মোনায়েম (সনদপ্রাপ্ত ডিজিটাল সার্ভেয়ার)",
    var surveyorRegNo: String = "GOVT-SRV-2026/894",
    var surveyorPhone: String = "০১৭০০-০০০০০০",
    
    // Client & Land Details
    var clientName: String = "মো: আব্দুল করিম",
    var clientFather: String = "মরহুম আলহাজ্ব রহিম উদ্দিন",
    var clientPhone: String = "০১৭১১-২২৩৩৪৪",
    var clientAddress: String = "গ্রাম: মধ্যপাড়া, ডাকঘর: সদর",
    var district: String = "ঢাকা",
    var upazila: String = "সাভার",
    var mouza: String = "ধামরাই",
    var jlNo: String = "১২৩",
    var khatianType: String = "বি.এস (BS)",
    var khatianNo: String = "৪৫৬",
    var dagNo: String = "৭৮৯ (সাবেক: ৪৫)",
    var landType: String = "নাল / বসতভিটা",
    
    // Boundaries (চতুর্সীমা)
    var northBoundary: String = "১০ ফুট প্রশস্ত পাকা রাস্তা",
    var southBoundary: String = "মো: রফিক মিয়ার আবাদি জমি",
    var eastBoundary: String = "সরকারি খাস খাল ও ড্রেন",
    var westBoundary: String = "মো: জমির হোসেনের বসতভিটা",
    
    // Plots & Calculations (Excel Grid)
    var plots: MutableList<SurveyPlotEntry> = mutableListOf(
        SurveyPlotEntry(
            plotName = "প্রধান অংশ (প্লট-ক)",
            northFt = 70.0,
            southFt = 70.0,
            eastFt = 62.0,
            westFt = 62.0,
            notes = "আবাসিক সীমানা চিহ্নিত"
        )
    ),
    
    // Shareholders
    var shareholders: MutableList<SurveyShareholder> = mutableListOf(
        SurveyShareholder(name = "মো: আব্দুল করিম", relation = "প্রধান মালিক", sharePercentage = 100.0)
    ),
    
    // Surveyor Remarks & Legal Opinion (Word Document content)
    var findingsRemarks: String = "উভয় পক্ষের উপস্থিতিতে আধুনিক ডিজিটাল ফিতা ও স্যাটেলাইট মেজারমেন্টের মাধ্যমে উক্ত দাগের সীমানা চিহ্নিত ও পরিমাপ সম্পন্ন করা হয়েছে। দাগের সীমানায় কোনো প্রকার বেদখল পাওয়া যায়নি।",
    var legalAdvice: String = "উক্ত পরিমাপকৃত পরিমাণের ভিত্তিতে সহকারী কমিশনার (ভূমি) কার্যালয়ে ই-নামজারি ও জমাভাগ খতিয়ান সৃজন করার জন্য সুপারিশ করা হলো।"
) {
    val totalSquareFeet: Double
        get() = plots.sumOf { it.squareFeet }

    val totalDecimal: Double
        get() = plots.sumOf { it.decimal }

    val totalKatha: Double
        get() = plots.sumOf { it.katha }

    val totalBigha: Double
        get() = plots.sumOf { it.bigha }

    val totalAcre: Double
        get() = plots.sumOf { it.acre }

    fun formattedTotalSummary(): String {
        val df = DecimalFormat("#,##0.00")
        return "${df.format(totalDecimal)} শতাংশ (${df.format(totalKatha)} কাঠা / ${df.format(totalSquareFeet)} বর্গফুট)"
    }
}
