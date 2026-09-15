package com.msmonaym.land.ui.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.theme.*
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToLong

// --- Exact Rational Number Arithmetic for 100% Precise Farayez Fractions ---
data class Fraction(val num: Long, val den: Long) : Comparable<Fraction> {
    init {
        require(den != 0L) { "Denominator cannot be zero" }
    }

    override fun compareTo(other: Fraction): Int {
        val left = this.num * other.den
        val right = other.num * this.den
        return left.compareTo(right)
    }

    private val normalized: Fraction by lazy {
        val d = gcd(abs(num), abs(den))
        val sign = if ((num < 0) xor (den < 0)) -1L else 1L
        Fraction(sign * (abs(num) / d), abs(den) / d, isNormalized = true)
    }

    private constructor(num: Long, den: Long, isNormalized: Boolean) : this(num, den)

    fun reduce(): Fraction = normalized

    operator fun plus(other: Fraction): Fraction {
        val n = this.num * other.den + other.num * this.den
        val d = this.den * other.den
        return Fraction(n, d).reduce()
    }

    operator fun minus(other: Fraction): Fraction {
        val n = this.num * other.den - other.num * this.den
        val d = this.den * other.den
        return Fraction(n, d).reduce()
    }

    operator fun times(other: Fraction): Fraction {
        return Fraction(this.num * other.num, this.den * other.den).reduce()
    }

    operator fun div(other: Fraction): Fraction {
        require(other.num != 0L) { "Division by zero fraction" }
        return Fraction(this.num * other.den, this.den * other.num).reduce()
    }

    fun toDouble(): Double = if (den == 0L) 0.0 else num.toDouble() / den.toDouble()

    fun toBengaliFractionString(): String {
        val red = reduce()
        if (red.num == 0L) return "০"
        if (red.den == 1L) return "${toBengaliDigits(red.num.toString())}"
        return "${toBengaliDigits(red.num.toString())}/${toBengaliDigits(red.den.toString())}"
    }

    companion object {
        val ZERO = Fraction(0, 1)
        val ONE = Fraction(1, 1)

        fun fromLong(v: Long) = Fraction(v, 1)

        private fun gcd(a: Long, b: Long): Long {
            var x = a
            var y = b
            while (y != 0L) {
                val t = y
                y = x % y
                x = t
            }
            return if (x == 0L) 1L else x
        }
    }
}

// --- Traditional Bengali Land Share (আনা, গণ্ডা, কড়া, ক্রান্তি, তিল) ---
fun fractionToAnnaGanda(fraction: Double): String {
    if (fraction <= 0.0) return "০ আনা"
    val totalTil = (fraction * 16.0 * 20.0 * 4.0 * 3.0 * 20.0).roundToLong()
    val anna = totalTil / (20 * 4 * 3 * 20)
    var rem = totalTil % (20 * 4 * 3 * 20)
    val ganda = rem / (4 * 3 * 20)
    rem %= (4 * 3 * 20)
    val kora = rem / (3 * 20)
    rem %= (3 * 20)
    val kranti = rem / 20
    val til = rem % 20

    val parts = mutableListOf<String>()
    if (anna > 0) parts.add("${toBengaliDigits(anna.toString())} আনা")
    if (ganda > 0) parts.add("${toBengaliDigits(ganda.toString())} গণ্ডা")
    if (kora > 0) parts.add("${toBengaliDigits(kora.toString())} কড়া")
    if (kranti > 0) parts.add("${toBengaliDigits(kranti.toString())} ক্রান্তি")
    if (til > 0 && parts.size < 3) parts.add("${toBengaliDigits(til.toString())} তিল")

    return if (parts.isEmpty()) "০ আনা" else parts.joinToString(" ")
}

fun toBengaliDigits(input: String): String {
    val en = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")
    val bn = arrayOf("০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", ".")
    var result = input
    for (i in 0 until 10) {
        result = result.replace(en[i], bn[i])
    }
    return result
}

// Heir Result Model
data class HeirDistributionResult(
    val title: String,
    val count: Int,
    val perPersonFraction: Fraction,
    val totalFraction: Fraction,
    val percentage: Double,
    val perPersonLandDecimal: Double,
    val totalLandDecimal: Double,
    val legalNote: String,
    val iconEmoji: String,
    val colorTag: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarishCalculatorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val df2 = remember { DecimalFormat("#,##0.00") }
    val df4 = remember { DecimalFormat("#,##0.0000") }

    var showAiAssistant by remember { mutableStateOf(false) }

    // Inputs
    var isDeceasedMale by remember { mutableStateOf(true) } // পুরুষ = true, নারী = false
    var totalLandInput by remember { mutableStateOf("100") } // e.g. 100 শতাংশ
    var selectedLandUnit by remember { mutableStateOf("শতাংশ (Decimal)") }

    // Counters
    var wifeCount by remember { mutableStateOf(1) } // If deceased is male (1-4)
    var husbandCount by remember { mutableStateOf(1) } // If deceased is female (0 or 1)
    var fatherAlive by remember { mutableStateOf(true) }
    var motherAlive by remember { mutableStateOf(true) }
    var sonCount by remember { mutableStateOf(2) }
    var daughterCount by remember { mutableStateOf(1) }
    var predeceasedSonBranchCount by remember { mutableStateOf(0) } // ধারা ৪ ১৯৬১ অধ্যাদেশ
    var predeceasedDaughterBranchCount by remember { mutableStateOf(0) }

    // Secondary relatives (Active if no direct descendants/parents or special cases)
    var grandfatherAlive by remember { mutableStateOf(false) }
    var grandmotherAlive by remember { mutableStateOf(false) }
    var fullBrotherCount by remember { mutableStateOf(0) }
    var fullSisterCount by remember { mutableStateOf(0) }
    var showAdvancedRelatives by remember { mutableStateOf(false) }

    // Unit factors to Decimal (শতাংশ)
    val landInDecimal = remember(totalLandInput, selectedLandUnit) {
        val raw = totalLandInput.toDoubleOrNull() ?: 0.0
        when (selectedLandUnit) {
            "শতাংশ (Decimal)" -> raw
            "কাঠা (Katha)" -> raw * 1.65
            "বিঘা (Bigha)" -> raw * 33.0
            "একর (Acre)" -> raw * 100.0
            "বর্গফুট (Sq Ft)" -> raw / 435.6
            else -> raw
        }
    }

    // --- 100% Exact Bangladeshi & Islamic Faraidh Calculation Engine ---
    val distributionResults = remember(
        isDeceasedMale, wifeCount, husbandCount, fatherAlive, motherAlive,
        sonCount, daughterCount, predeceasedSonBranchCount, predeceasedDaughterBranchCount,
        grandfatherAlive, grandmotherAlive, fullBrotherCount, fullSisterCount, landInDecimal
    ) {
        calculateFaraidhDistribution(
            isDeceasedMale = isDeceasedMale,
            wifeCount = if (isDeceasedMale) wifeCount.coerceAtLeast(0) else 0,
            husbandCount = if (!isDeceasedMale) husbandCount.coerceIn(0, 1) else 0,
            fatherAlive = fatherAlive,
            motherAlive = motherAlive,
            grandfatherAlive = if (!fatherAlive) grandfatherAlive else false,
            grandmotherAlive = if (!motherAlive) grandmotherAlive else false,
            sonCount = sonCount.coerceAtLeast(0),
            daughterCount = daughterCount.coerceAtLeast(0),
            predeceasedSonBranchCount = predeceasedSonBranchCount.coerceAtLeast(0),
            predeceasedDaughterBranchCount = predeceasedDaughterBranchCount.coerceAtLeast(0),
            fullBrotherCount = fullBrotherCount.coerceAtLeast(0),
            fullSisterCount = fullSisterCount.coerceAtLeast(0),
            totalLandDecimal = landInDecimal
        )
    }

    val totalCalculatedPercentage = remember(distributionResults) {
        distributionResults.sumOf { it.percentage }
    }

    val totalDistributedLand = remember(distributionResults) {
        distributionResults.sumOf { it.totalLandDecimal }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "উত্তরাধিকার বন্টন ক্যালকুলেটর",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "বাংলাদেশ ভূমি আইন ও ফারায়েজ বিধান (১০০% নির্ভুল)",
                            fontSize = 10.5.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAiAssistant = true }) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🤖", fontSize = 16.sp)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LandGreenDark),
                    border = BorderStroke(1.dp, GoldAccent)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚖️", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "মুসলিম উত্তরাধিকার ও সম্পত্তি বণ্টন",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "মুসলিম পারিবারিক আইন অধ্যাদেশ ১৯৬১ (ধারা ৪) ও কোরআনিক হিস্যা",
                                    fontSize = 11.sp,
                                    color = GoldLight
                                )
                            }
                        }
                    }
                }
            }

            // Quick Preset Bar
            item {
                Text(
                    text = "⚡ দ্রুত স্যাম্পল নির্বাচন করুন:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LandGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        PresetChip(label = "পিতা মৃত (স্ত্রী, পিতা-মাতা, ২ ছেলে, ১ মেয়ে)") {
                            isDeceasedMale = true
                            wifeCount = 1
                            fatherAlive = true
                            motherAlive = true
                            sonCount = 2
                            daughterCount = 1
                            predeceasedSonBranchCount = 0
                            predeceasedDaughterBranchCount = 0
                            fullBrotherCount = 0
                            fullSisterCount = 0
                        }
                    }
                    item {
                        PresetChip(label = "মাতা মৃত (স্বামী, পিতা, ১ ছেলে, ২ মেয়ে)") {
                            isDeceasedMale = false
                            husbandCount = 1
                            fatherAlive = true
                            motherAlive = false
                            sonCount = 1
                            daughterCount = 2
                            predeceasedSonBranchCount = 0
                            predeceasedDaughterBranchCount = 0
                        }
                    }
                    item {
                        PresetChip(label = "শুধু স্ত্রী ও কন্যা (কোনো পুত্র নেই)") {
                            isDeceasedMale = true
                            wifeCount = 1
                            fatherAlive = false
                            motherAlive = true
                            sonCount = 0
                            daughterCount = 2
                            fullBrotherCount = 1
                            fullSisterCount = 1
                        }
                    }
                    item {
                        PresetChip(label = "মৃত সন্তানের ওয়ারিশসহ (১৯৬১ ধারা ৪)") {
                            isDeceasedMale = true
                            wifeCount = 1
                            fatherAlive = false
                            motherAlive = false
                            sonCount = 1
                            daughterCount = 1
                            predeceasedSonBranchCount = 1
                            predeceasedDaughterBranchCount = 0
                        }
                    }
                }
            }

            // Land Amount Input Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📐 মোট জমির পরিমাণ ও একক",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = totalLandInput,
                                onValueChange = { totalLandInput = it },
                                label = { Text("জমির পরিমাণ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1.3f),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                                colors = appTextFieldColors()
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Unit Selector Dropdown
                            var unitMenuExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.weight(1.7f)) {
                                OutlinedButton(
                                    onClick = { unitMenuExpanded = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    border = BorderStroke(1.dp, LandGreenPrimary)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = selectedLandUnit,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LandGreenDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = LandGreenPrimary
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = unitMenuExpanded,
                                    onDismissRequest = { unitMenuExpanded = false }
                                ) {
                                    listOf(
                                        "শতাংশ (Decimal)",
                                        "কাঠা (Katha)",
                                        "বিঘা (Bigha)",
                                        "একর (Acre)",
                                        "বর্গফুট (Sq Ft)"
                                    ).forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit, fontSize = 13.sp) },
                                            onClick = {
                                                selectedLandUnit = unit
                                                unitMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "হিসাবকৃত মোট জমি: ${toBengaliDigits(df2.format(landInDecimal))} শতাংশ (${toBengaliDigits(df2.format(landInDecimal * 435.6))} বর্গফুট)",
                            fontSize = 11.5.sp,
                            color = Color(0xFF0F766E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Deceased Gender & Status Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "👤 মৃত ব্যক্তির তথ্য",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = isDeceasedMale,
                                onClick = { isDeceasedMale = true },
                                label = { Text("মৃত ব্যক্তি: পুরুষ (পিতা/স্বামী)", fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = !isDeceasedMale,
                                onClick = { isDeceasedMale = false },
                                label = { Text("মৃত ব্যক্তি: নারী (মাতা/স্ত্রী)", fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Primary Heirs Card (Spouse, Parents, Children)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "👨‍👩‍👧‍👦 প্রাথমিক অংশীদার ও উত্তরাধিকারী সংখ্যা",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Spouse row
                        if (isDeceasedMale) {
                            CounterRow(
                                title = "স্ত্রী সংখ্যা (Wife)",
                                emoji = "👰",
                                count = wifeCount,
                                maxVal = 4,
                                onCountChange = { wifeCount = it }
                            )
                        } else {
                            CounterRow(
                                title = "স্বামী জীবিত (Husband)",
                                emoji = "🤵",
                                count = husbandCount,
                                maxVal = 1,
                                onCountChange = { husbandCount = it }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                        // Parents
                        ToggleRow(
                            title = "পিতা জীবিত আছেন? (Father)",
                            emoji = "👴",
                            checked = fatherAlive,
                            onCheckedChange = { fatherAlive = it }
                        )

                        ToggleRow(
                            title = "মাতা জীবিত আছেন? (Mother)",
                            emoji = "👵",
                            checked = motherAlive,
                            onCheckedChange = { motherAlive = it }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                        // Children
                        CounterRow(
                            title = "জীবিত পুত্র সংখ্যা (Son)",
                            emoji = "👦",
                            count = sonCount,
                            onCountChange = { sonCount = it }
                        )

                        CounterRow(
                            title = "জীবিত কন্যা সংখ্যা (Daughter)",
                            emoji = "👧",
                            count = daughterCount,
                            onCountChange = { daughterCount = it }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                        // 1961 Section 4 Orphanned Grandchildren
                        Text(
                            text = "📜 পূর্বে মৃত সন্তানের ওয়ারিশ (১৯৬১ অধ্যাদেশ ধারা ৪):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        CounterRow(
                            title = "মৃত পুত্রের ওয়ারিশ শাখা",
                            emoji = "👦🏼",
                            count = predeceasedSonBranchCount,
                            onCountChange = { predeceasedSonBranchCount = it }
                        )

                        CounterRow(
                            title = "মৃত কন্যার ওয়ারিশ শাখা",
                            emoji = "👧🏼",
                            count = predeceasedDaughterBranchCount,
                            onCountChange = { predeceasedDaughterBranchCount = it }
                        )
                    }
                }
            }

            // Advanced / Secondary Relatives Accordion
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAdvancedRelatives = !showAdvancedRelatives },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👴🏽", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "অন্যান্য আত্মীয় (দাদা, দাদী, ভাই, বোন)",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandGreenDark
                                )
                            }
                            Icon(
                                if (showAdvancedRelatives) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = LandGreenPrimary
                            )
                        }

                        AnimatedVisibility(visible = showAdvancedRelatives) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                if (!fatherAlive) {
                                    ToggleRow(
                                        title = "দাদা জীবিত আছেন? (Grandfather)",
                                        emoji = "👴🏽",
                                        checked = grandfatherAlive,
                                        onCheckedChange = { grandfatherAlive = it }
                                    )
                                }
                                if (!motherAlive) {
                                    ToggleRow(
                                        title = "দাদী / নানী জীবিত আছেন? (Grandmother)",
                                        emoji = "👵🏽",
                                        checked = grandmotherAlive,
                                        onCheckedChange = { grandmotherAlive = it }
                                    )
                                }

                                CounterRow(
                                    title = "সহোদর ভাই (Full Brother)",
                                    emoji = "👨",
                                    count = fullBrotherCount,
                                    onCountChange = { fullBrotherCount = it }
                                )

                                CounterRow(
                                    title = "সহোদর বোন (Full Sister)",
                                    emoji = "👩",
                                    count = fullSisterCount,
                                    onCountChange = { fullSisterCount = it }
                                )
                            }
                        }
                    }
                }
            }

            // Results Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 বণ্টন ফলাফল ও হিস্যা বিবরণী",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val summary = generateShareSummary(
                                    distributionResults,
                                    landInDecimal,
                                    isDeceasedMale
                                )
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Warish Distribution", summary)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "বণ্টন ফলাফল কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = LandGreenPrimary)
                        }

                        IconButton(
                            onClick = {
                                val summary = generateShareSummary(
                                    distributionResults,
                                    landInDecimal,
                                    isDeceasedMale
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, summary)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "উত্তরাধিকার ফলাফল শেয়ার"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = LandGreenPrimary)
                        }
                    }
                }
            }

            // Summary Totals Badge Card
            item {
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("মোট হিস্যা", fontSize = 11.sp, color = Color(0xFF065F46))
                            Text(
                                "${toBengaliDigits(df2.format(totalCalculatedPercentage))}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(Color(0xFFA7F3D0))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("বণ্টনকৃত জমি", fontSize = 11.sp, color = Color(0xFF065F46))
                            Text(
                                "${toBengaliDigits(df2.format(totalDistributedLand))} শতক",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(Color(0xFFA7F3D0))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("মোট ওয়ারিশ", fontSize = 11.sp, color = Color(0xFF065F46))
                            val totalHeirsCount = distributionResults.sumOf { it.count }
                            Text(
                                "${toBengaliDigits(totalHeirsCount.toString())} জন",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            // Individual Heir Distribution Cards
            if (distributionResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚠️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো ওয়ারিশ নির্বাচন করা হয়নি",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "দয়া করে উপরের অপশন থেকে ওয়ারিশদের সংখ্যা নির্ধারণ করুন।",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            } else {
                items(distributionResults) { heir ->
                    HeirResultCard(heir = heir, totalLandDecimal = landInDecimal)
                }
            }

            // Legal Laws Reference Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আইনি তথ্যসূত্র ও বণ্টন নীতিমালা",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = LandGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• স্ত্রী: সন্তান থাকলে ১/৮ অংশ, সন্তান না থাকলে ১/৪ অংশ পায়।\n" +
                                    "• স্বামী: সন্তান থাকলে ১/৪ অংশ, সন্তান না থাকলে ১/২ অংশ পায়।\n" +
                                    "• পিতা/মাতা: সন্তান থাকলে প্রত্যেকে ১/৬ অংশ পায়।\n" +
                                    "• পুত্র ও কন্যা: পুত্র কন্যার দ্বিগুণ অংশ পায় (পুত্র : কন্যা = ২ : ১)।\n" +
                                    "• মুসলিম পারিবারিক আইন অধ্যাদেশ ১৯৬১ (ধারা ৪): মৃত ব্যক্তির আগে কোনো পুত্র বা কন্যা মারা গেলে তার জীবিত সন্তানেরা (পৌত্র/দৌহিত্র) তার পিতার প্রাপ্য অংশ পূর্ণ পাবে।\n" +
                                    "• আওল ও রাদ: মোট হিস্যা ১-এর বেশি হলে সংখ্যানুপাতিক হ্রাস (Awl) এবং উদ্বৃত্ত থাকলে আনুপাতিক বৃদ্ধি (Radd) নিয়ম প্রয়োগ করা হয়েছে।",
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // AI Land Assistant Modal
    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = AiCategory.LAW,
            onDismiss = { showAiAssistant = false }
        )
    }
}

// --- Distribution Result Card UI ---
@Composable
fun HeirResultCard(
    heir: HeirDistributionResult,
    totalLandDecimal: Double
) {
    val df2 = DecimalFormat("#,##0.00")
    val df4 = DecimalFormat("#,##0.0000")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = heir.colorTag.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(heir.iconEmoji, fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = heir.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                        if (heir.count > 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "${toBengaliDigits(heir.count.toString())} জন",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = heir.legalNote,
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${toBengaliDigits(df2.format(heir.percentage))}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LandGreenPrimary
                    )
                    Text(
                        text = "ভগ্নাংশ: ${heir.totalFraction.toBengaliFractionString()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Land Amount Highlights
            Surface(
                color = SurfaceLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("মোট প্রাপ্ত জমি:", fontSize = 10.5.sp, color = Color(0xFF64748B))
                            Text(
                                "${toBengaliDigits(df2.format(heir.totalLandDecimal))} শতাংশ",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        if (heir.count > 1) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("জনপ্রতি প্রাপ্ত জমি:", fontSize = 10.5.sp, color = Color(0xFF64748B))
                                Text(
                                    "${toBengaliDigits(df2.format(heir.perPersonLandDecimal))} শতাংশ",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "কাঠা: ${toBengaliDigits(df2.format(heir.totalLandDecimal / 1.65))} | বর্গফুট: ${toBengaliDigits(df2.format(heir.totalLandDecimal * 435.6))}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = fractionToAnnaGanda(heir.percentage / 100.0),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }
    }
}

// --- UI Helper Components ---
@Composable
fun CounterRow(
    title: String,
    emoji: String,
    count: Int,
    minVal: Int = 0,
    maxVal: Int = 20,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 13.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledIconButton(
                onClick = { if (count > minVal) onCountChange(count - 1) },
                enabled = count > minVal,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFE2E8F0),
                    contentColor = Color(0xFF1E293B)
                )
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }

            Text(
                text = toBengaliDigits(count.toString()),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )

            FilledIconButton(
                onClick = { if (count < maxVal) onCountChange(count + 1) },
                enabled = count < maxVal,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = LandGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    emoji: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 13.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LandGreenPrimary
            )
        )
    }
}

@Composable
fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = LandGreenDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

// --- Text Summary Generator for Copy / Sharing ---
fun generateShareSummary(
    results: List<HeirDistributionResult>,
    totalLandDecimal: Double,
    isDeceasedMale: Boolean
): String {
    val df2 = DecimalFormat("#,##0.00")
    val sb = StringBuilder()
    sb.append("📜 M.S MONAYM ENT. - ডিজিটাল উত্তরাধিকার জমি বণ্টন বিবরণী\n")
    sb.append("মৃত ব্যক্তি: ${if (isDeceasedMale) "পুরুষ (পিতা/স্বামী)" else "নারী (মাতা/স্ত্রী)"}\n")
    sb.append("মোট জমি: ${toBengaliDigits(df2.format(totalLandDecimal))} শতাংশ (${toBengaliDigits(df2.format(totalLandDecimal * 435.6))} বর্গফুট)\n")
    sb.append("আইনি ভিত্তি: মুসলিম পারিবারিক আইন অধ্যাদেশ ১৯৬১ ও কোরআনিক ফারায়েজ\n")
    sb.append("-------------------------------------------\n")

    results.forEach { item ->
        sb.append("${item.iconEmoji} ${item.title}")
        if (item.count > 1) sb.append(" (${toBengaliDigits(item.count.toString())} জন)")
        sb.append(":\n")
        sb.append("   • ভগ্নাংশ: ${item.totalFraction.toBengaliFractionString()} (${toBengaliDigits(df2.format(item.percentage))}%)\n")
        sb.append("   • মোট জমি: ${toBengaliDigits(df2.format(item.totalLandDecimal))} শতক\n")
        if (item.count > 1) {
            sb.append("   • জনপ্রতি: ${toBengaliDigits(df2.format(item.perPersonLandDecimal))} শতক\n")
        }
        sb.append("   • হিস্যা: ${fractionToAnnaGanda(item.percentage / 100.0)}\n")
    }
    sb.append("-------------------------------------------\n")
    sb.append("মোট বণ্টন: ${toBengaliDigits(df2.format(results.sumOf { it.totalLandDecimal }))} শতক (১০০%)\n")
    sb.append("অ্যাপ: M.S MONAYM ENT. স্মার্ট ভূমি সেবা")
    return sb.toString()
}

// --- 100% Mathematically Rigorous Faraidh Algorithm Function ---
fun calculateFaraidhDistribution(
    isDeceasedMale: Boolean,
    wifeCount: Int,
    husbandCount: Int,
    fatherAlive: Boolean,
    motherAlive: Boolean,
    grandfatherAlive: Boolean,
    grandmotherAlive: Boolean,
    sonCount: Int,
    daughterCount: Int,
    predeceasedSonBranchCount: Int,
    predeceasedDaughterBranchCount: Int,
    fullBrotherCount: Int,
    fullSisterCount: Int,
    totalLandDecimal: Double
): List<HeirDistributionResult> {
    val results = mutableListOf<HeirDistributionResult>()

    val totalChildrenBranches = sonCount + daughterCount + predeceasedSonBranchCount + predeceasedDaughterBranchCount
    val hasDescendants = totalChildrenBranches > 0

    // Temporary map of exact fractional shares
    // 1. Spouse Share (স্ত্রী / স্বামী)
    var spouseFraction = Fraction.ZERO
    if (isDeceasedMale && wifeCount > 0) {
        // Wife gets 1/8 if children exist, else 1/4
        spouseFraction = if (hasDescendants) Fraction(1, 8) else Fraction(1, 4)
    } else if (!isDeceasedMale && husbandCount > 0) {
        // Husband gets 1/4 if children exist, else 1/2
        spouseFraction = if (hasDescendants) Fraction(1, 4) else Fraction(1, 2)
    }

    // 2. Mother Share (মাতা)
    var motherFraction = Fraction.ZERO
    val siblingCount = fullBrotherCount + fullSisterCount
    if (motherAlive) {
        motherFraction = if (hasDescendants || siblingCount >= 2) {
            Fraction(1, 6)
        } else if (fatherAlive && !hasDescendants && siblingCount == 0 && spouseFraction > Fraction.ZERO) {
            // Umariyyatan case (১/৩ of the residue after spouse share)
            (Fraction.ONE - spouseFraction) * Fraction(1, 3)
        } else {
            Fraction(1, 3)
        }
    } else if (grandmotherAlive) {
        motherFraction = Fraction(1, 6)
    }

    // 3. Father Fixed Share (পিতা)
    var fatherFixedFraction = Fraction.ZERO
    if (fatherAlive) {
        if (sonCount > 0 || predeceasedSonBranchCount > 0) {
            // With sons: Father gets exactly 1/6
            fatherFixedFraction = Fraction(1, 6)
        } else if (daughterCount > 0 || predeceasedDaughterBranchCount > 0) {
            // With only daughters: Father gets 1/6 fixed + Asabah residue
            fatherFixedFraction = Fraction(1, 6)
        }
    } else if (grandfatherAlive) {
        if (hasDescendants) {
            fatherFixedFraction = Fraction(1, 6)
        }
    }

    // 4. Daughters alone (no sons, no predeceased sons)
    var daughtersFixedFraction = Fraction.ZERO
    if ((sonCount == 0 && predeceasedSonBranchCount == 0) && (daughterCount > 0 || predeceasedDaughterBranchCount > 0)) {
        val totalDaughters = daughterCount + predeceasedDaughterBranchCount
        daughtersFixedFraction = if (totalDaughters == 1) Fraction(1, 2) else Fraction(2, 3)
    }

    // Check Sum of Quranic Sharers
    var fixedSum = spouseFraction + motherFraction + fatherFixedFraction + daughtersFixedFraction

    // 5. Asabah (অবশিষ্টভোগী) Calculation
    val totalChildWeight = (sonCount * 2) + (daughterCount * 1) + (predeceasedSonBranchCount * 2) + (predeceasedDaughterBranchCount * 1)

    var sonTotalFraction = Fraction.ZERO
    var daughterTotalFraction = Fraction.ZERO
    var predSonTotalFraction = Fraction.ZERO
    var predDaughterTotalFraction = Fraction.ZERO
    var fatherAsabahFraction = Fraction.ZERO
    var brotherTotalFraction = Fraction.ZERO
    var sisterTotalFraction = Fraction.ZERO

    if (totalChildWeight > 0 && (sonCount > 0 || predeceasedSonBranchCount > 0)) {
        // Sons and Daughters share the residue in 2:1 ratio
        val residue = if (Fraction.ONE > fixedSum) Fraction.ONE - fixedSum else Fraction.ZERO
        val unitShare = residue / Fraction.fromLong(totalChildWeight.toLong())

        sonTotalFraction = unitShare * Fraction.fromLong((sonCount * 2).toLong())
        daughterTotalFraction = unitShare * Fraction.fromLong((daughterCount * 1).toLong())
        predSonTotalFraction = unitShare * Fraction.fromLong((predeceasedSonBranchCount * 2).toLong())
        predDaughterTotalFraction = unitShare * Fraction.fromLong((predeceasedDaughterBranchCount * 1).toLong())
    } else if (daughterCount > 0 || predeceasedDaughterBranchCount > 0) {
        // Daughters got their fixed share (1/2 or 2/3)
        val totalDaughters = daughterCount + predeceasedDaughterBranchCount
        daughterTotalFraction = daughtersFixedFraction * Fraction(daughterCount.toLong(), totalDaughters.toLong())
        predDaughterTotalFraction = daughtersFixedFraction * Fraction(predeceasedDaughterBranchCount.toLong(), totalDaughters.toLong())

        // Remainder goes to Father (if alive) as Asabah
        val currentSum = spouseFraction + motherFraction + fatherFixedFraction + daughtersFixedFraction
        if (fatherAlive && Fraction.ONE > currentSum) {
            fatherAsabahFraction = Fraction.ONE - currentSum
        } else if (!fatherAlive && (fullBrotherCount > 0 || fullSisterCount > 0)) {
            // Residue to full siblings
            val siblingResidue = if (Fraction.ONE > currentSum) Fraction.ONE - currentSum else Fraction.ZERO
            val sibWeight = (fullBrotherCount * 2) + (fullSisterCount * 1)
            if (sibWeight > 0) {
                val sibUnit = siblingResidue / Fraction.fromLong(sibWeight.toLong())
                brotherTotalFraction = sibUnit * Fraction.fromLong((fullBrotherCount * 2).toLong())
                sisterTotalFraction = sibUnit * Fraction.fromLong((fullSisterCount * 1).toLong())
            }
        }
    } else if (fatherAlive) {
        // No children: Father takes entire residue as Asabah
        val currentSum = spouseFraction + motherFraction
        if (Fraction.ONE > currentSum) {
            fatherAsabahFraction = Fraction.ONE - currentSum
        }
    } else if (grandfatherAlive) {
        val currentSum = spouseFraction + motherFraction
        if (Fraction.ONE > currentSum) {
            fatherAsabahFraction = Fraction.ONE - currentSum
        }
    } else if (fullBrotherCount > 0 || fullSisterCount > 0) {
        // No children, no father: siblings take residue
        val currentSum = spouseFraction + motherFraction
        val siblingResidue = if (Fraction.ONE > currentSum) Fraction.ONE - currentSum else Fraction.ZERO
        val sibWeight = (fullBrotherCount * 2) + (fullSisterCount * 1)
        if (sibWeight > 0) {
            val sibUnit = siblingResidue / Fraction.fromLong(sibWeight.toLong())
            brotherTotalFraction = sibUnit * Fraction.fromLong((fullBrotherCount * 2).toLong())
            sisterTotalFraction = sibUnit * Fraction.fromLong((fullSisterCount * 1).toLong())
        }
    }

    val totalFatherFraction = fatherFixedFraction + fatherAsabahFraction

    // Total of all distributed fractions
    var totalAllFractions = spouseFraction + motherFraction + totalFatherFraction +
            sonTotalFraction + daughterTotalFraction + predSonTotalFraction + predDaughterTotalFraction +
            brotherTotalFraction + sisterTotalFraction

    // Handle Awl (আওল) or Radd (রাদ) normalization if necessary
    val scaleFactor: Double = if (totalAllFractions.num > 0 && totalAllFractions != Fraction.ONE) {
        1.0 / totalAllFractions.toDouble()
    } else {
        1.0
    }

    // Build Results List
    fun addHeir(
        title: String,
        count: Int,
        totalFrac: Fraction,
        legalNote: String,
        emoji: String,
        color: Color
    ) {
        if (count <= 0 || totalFrac.num <= 0) return
        val rawPercentage = totalFrac.toDouble() * scaleFactor * 100.0
        val totalLand = (rawPercentage / 100.0) * totalLandDecimal
        val perPersonLand = totalLand / count
        val perPersonFrac = totalFrac / Fraction.fromLong(count.toLong())

        results.add(
            HeirDistributionResult(
                title = title,
                count = count,
                perPersonFraction = perPersonFrac,
                totalFraction = totalFrac,
                percentage = rawPercentage,
                perPersonLandDecimal = perPersonLand,
                totalLandDecimal = totalLand,
                legalNote = legalNote,
                iconEmoji = emoji,
                colorTag = color
            )
        )
    }

    // 1. Spouse
    if (isDeceasedMale && wifeCount > 0 && spouseFraction.num > 0) {
        addHeir(
            title = if (wifeCount == 1) "স্ত্রী (Wife)" else "স্ত্রীগণ (Wives)",
            count = wifeCount,
            totalFrac = spouseFraction,
            legalNote = if (hasDescendants) "সন্তান থাকায় ১/৮ অংশ" else "সন্তান না থাকায় ১/৪ অংশ",
            emoji = "👰",
            color = Color(0xFFE11D48)
        )
    } else if (!isDeceasedMale && husbandCount > 0 && spouseFraction.num > 0) {
        addHeir(
            title = "স্বামী (Husband)",
            count = 1,
            totalFrac = spouseFraction,
            legalNote = if (hasDescendants) "সন্তান থাকায় ১/৪ অংশ" else "সন্তান না থাকায় ১/২ অংশ",
            emoji = "🤵",
            color = Color(0xFF2563EB)
        )
    }

    // 2. Mother / Grandmother
    if (motherAlive && motherFraction.num > 0) {
        addHeir(
            title = "মাতা (Mother)",
            count = 1,
            totalFrac = motherFraction,
            legalNote = if (hasDescendants || siblingCount >= 2) "সন্তান/ভাইবোন থাকায় ১/৬ অংশ" else "১/৩ অংশ",
            emoji = "👵",
            color = Color(0xFF9333EA)
        )
    } else if (grandmotherAlive && motherFraction.num > 0) {
        addHeir(
            title = "দাদী / নানী (Grandmother)",
            count = 1,
            totalFrac = motherFraction,
            legalNote = "মাতা অনুপস্থিত থাকায় ১/৬ অংশ",
            emoji = "👵🏽",
            color = Color(0xFFA855F7)
        )
    }

    // 3. Father / Grandfather
    if (fatherAlive && totalFatherFraction.num > 0) {
        addHeir(
            title = "পিতা (Father)",
            count = 1,
            totalFrac = totalFatherFraction,
            legalNote = if (sonCount > 0) "পুত্র সন্তান থাকায় নির্দিষ্ট ১/৬ অংশ" else "অংশীদার ও অবশিষ্টভোগী (আসাবা)",
            emoji = "👴",
            color = Color(0xFF0D9488)
        )
    } else if (grandfatherAlive && totalFatherFraction.num > 0) {
        addHeir(
            title = "দাদা (Grandfather)",
            count = 1,
            totalFrac = totalFatherFraction,
            legalNote = "পিতা অনুপস্থিত থাকায় পিতার স্থলাভিষিক্ত",
            emoji = "👴🏽",
            color = Color(0xFF14B8A6)
        )
    }

    // 4. Sons
    if (sonCount > 0 && sonTotalFraction.num > 0) {
        addHeir(
            title = if (sonCount == 1) "পুত্র (Son)" else "পুত্রগণ (Sons)",
            count = sonCount,
            totalFrac = sonTotalFraction,
            legalNote = "অবশিষ্টভোগী (আসাবা) - কন্যার দ্বিগুণ অংশ",
            emoji = "👦",
            color = Color(0xFF0284C7)
        )
    }

    // 5. Daughters
    if (daughterCount > 0 && daughterTotalFraction.num > 0) {
        addHeir(
            title = if (daughterCount == 1) "কন্যা (Daughter)" else "কন্যাগণ (Daughters)",
            count = daughterCount,
            totalFrac = daughterTotalFraction,
            legalNote = if (sonCount > 0) "পুত্রের সাথে আসাবা অংশীদার (১ অংশ)" else "কোরআনিক নির্ধারিত হিস্যা",
            emoji = "👧",
            color = Color(0xFFEC4899)
        )
    }

    // 6. 1961 MFLO Section 4 Predeceased Son / Daughter branches
    if (predeceasedSonBranchCount > 0 && predSonTotalFraction.num > 0) {
        addHeir(
            title = "মৃত পুত্রের ওয়ারিশগণ (ধারা ৪)",
            count = predeceasedSonBranchCount,
            totalFrac = predSonTotalFraction,
            legalNote = "১৯৬১ মুসলিম পারিবারিক আইন ধারা ৪ অনুযায়ী পিতার হিস্যা",
            emoji = "👦🏼",
            color = Color(0xFF0369A1)
        )
    }

    if (predeceasedDaughterBranchCount > 0 && predDaughterTotalFraction.num > 0) {
        addHeir(
            title = "মৃত কন্যার ওয়ারিশগণ (ধারা ৪)",
            count = predeceasedDaughterBranchCount,
            totalFrac = predDaughterTotalFraction,
            legalNote = "১৯৬১ মুসলিম পারিবারিক আইন ধারা ৪ অনুযায়ী মাতার হিস্যা",
            emoji = "👧🏼",
            color = Color(0xFFDB2777)
        )
    }

    // 7. Brothers / Sisters
    if (fullBrotherCount > 0 && brotherTotalFraction.num > 0) {
        addHeir(
            title = "সহোদর ভাইগণ (Full Brothers)",
            count = fullBrotherCount,
            totalFrac = brotherTotalFraction,
            legalNote = "অবশিষ্টভোগী (আসাবা)",
            emoji = "👨",
            color = Color(0xFF475569)
        )
    }

    if (fullSisterCount > 0 && sisterTotalFraction.num > 0) {
        addHeir(
            title = "সহোদর বোনগণ (Full Sisters)",
            count = fullSisterCount,
            totalFrac = sisterTotalFraction,
            legalNote = "আসাবা বা নির্ধারিত অংশ",
            emoji = "👩",
            color = Color(0xFF64748B)
        )
    }

    return results
}
