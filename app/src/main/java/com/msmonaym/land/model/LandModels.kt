package com.msmonaym.land.model

data class LandLaw(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val fullContent: String,
    val keyPoints: List<String> = emptyList()
)

data class KhatianInfo(
    val type: String,
    val fullName: String,
    val period: String,
    val description: String,
    val keyFeatures: List<String>,
    val identificationTips: String,
    val colorHex: String
)

data class LandService(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val url: String,
    val description: String,
    val instructions: List<String>
)

data class ImportantContact(
    val title: String,
    val category: String,
    val phone: String,
    val description: String,
    val isHotline: Boolean = false,
    val websiteUrl: String? = null
)

data class OfficialPortal(
    val id: String,
    val title: String,
    val subTitle: String,
    val url: String,
    val category: String,
    val iconEmoji: String,
    val description: String
)

data class RegistrationFeeResult(
    val landValue: Double,
    val registrationFee: Double,
    val stampDuty: Double,
    val localGovtTax: Double,
    val sourceTax: Double,
    val totalFee: Double
)
