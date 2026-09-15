package com.msmonaym.land.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.*

data class SearchResult(
    val title: String,
    val category: String,
    val description: String,
    val targetScreen: Screen,
    val iconEmoji: String = "📄"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToScreen: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("সকল") }

    val categories = remember {
        listOf("সকল", "এআই ও স্ক্যানার", "খতিয়ান", "আইন ও ফারায়েজ", "ক্যালকুলেটর", "অনলাইন সেবা", "পেমেন্ট ও ভিআইপি")
    }

    val allSearchableItems = remember {
        val list = mutableListOf<SearchResult>()

        list.add(SearchResult("এআই ক্যামেরা ও দলিল স্ক্যানার (Gemini 2.5)", "এআই ও স্ক্যানার", "ক্যামেরা ছবি বা ইমেজ দিয়ে দলিলের সত্যতা, খতিয়ান, দাগ ও পূর্ণাঙ্গ A4 PDF লিগ্যাল অডিট রিপোর্ট", Screen.AiDocScanner, "📷"))
        list.add(SearchResult("উত্তরাধিকার বন্টন ক্যালকুলেটর (১০০% নির্ভুল)", "আইন ও ফারায়েজ", "বাংলাদেশ আইন ও ১৯৬১ অধ্যাদেশ অনুযায়ী পিতা, মাতা, স্ত্রী, স্বামী, পুত্র ও কন্যার জমির সঠিক হিস্যা বণ্টন", Screen.WarishCalculator, "⚖️"))
        list.add(SearchResult("প্রিমিয়াম ভূমি সেবা ও ডিজিটাল আমিন বুকিং", "পেমেন্ট ও ভিআইপি", "আমিন দ্বারা জমি পরিমাপ, বায়নানামা তৈরি, খতিয়ান যাচাই ও স্পেশাল লিগ্যাল পরামর্শ", Screen.PremiumServices, "💎"))
        list.add(SearchResult("বিকাশ ও নগদ সেন্ড মানি (01976444504)", "পেমেন্ট ও ভিআইপি", "এডমিনের বিকাশ ও নগদ নম্বরে সেন্ড মানি করুন, অ্যাপ ওপেন করুন ও ডিজিটাল রসিদ নিন", Screen.Payment, "💳"))
        list.add(SearchResult("সার্ভে রিপোর্ট টুলবক্স (Word ও Excel)", "ক্যালকুলেটর", "Microsoft Word ও Excel ব্যবহার করে জমির ডিজিটাল সার্ভে রিপোর্ট প্রস্তুত ও A4 PDF ডাউনলোড", Screen.SurveyReport, "📊"))
        list.add(SearchResult("দলিল রেজিস্ট্রি ফি ক্যালকুলেটর", "ক্যালকুলেটর", "জমির দাম দিয়ে স্ট্যাম্প শুল্ক, রেজিস্ট্রি ফি ও মোট সরকারি খরচ হিসাব করুন", Screen.Registration, "🧮"))
        list.add(SearchResult("জমি পরিমাপ একক রূপান্তর", "ক্যালকুলেটর", "শতক, কাঠা, বিঘা, একর, বর্গফুট ও আনা-গণ্ডা রূপান্তর", Screen.Calculator, "📐"))
        list.add(SearchResult("EasyArea অনলাইন স্যাটেলাইট পরিমাপ", "ক্যালকুলেটর", "ম্যাপ ও জিপিএস স্যাটেলাইট দ্বারা সরাসরি জমি পরিমাপ", Screen.Calculator, "🛰️"))
        list.add(SearchResult("সকল ভূমি সেবা ও টুলবক্স ডিরেক্টরি", "অনলাইন সেবা", "ই-নামজারি, ভূমি কর, খতিয়ান, পরিমাপ, আইন ও সকল অনলাইন পোর্টাল এক সাথে", Screen.AllServicesHub, "🗂️"))
        list.add(SearchResult("অ্যাপ কিউআর কোড (Direct Scan QR)", "পেমেন্ট ও ভিআইপি", "ক্যামেরা দিয়ে স্ক্যান করে সরাসরি অ্যাপে প্রবেশ, ছবি সেভ বা প্রিন্ট করার সুবিধা", Screen.AppQrCode, "📱"))
        list.add(SearchResult("দৈনিক আয় ও ফি কন্ট্রোল প্যানেল", "পেমেন্ট ও ভিআইপি", "এডমিনের প্রতিদিনের আয় লগ, সেবা ফি রেট চার্ট ও রেভিনিউ ম্যানেজমেন্ট", Screen.AdminEarnings, "💰"))
        list.add(SearchResult("মাস্টার এডমিন কন্ট্রোল ও সুরক্ষা গাইড", "পেমেন্ট ও ভিআইপি", "শুধুমাত্র মূল এডমিনের জন্য - অ্যাপ পাবলিশের পর সর্বময় নিয়ন্ত্রণ ও অপশন আপডেট গাইড", Screen.AdminGuide, "🔒"))

        LandDataRepository.landLaws.forEach { law ->
            list.add(SearchResult(law.title, "আইন ও ফারায়েজ", law.shortDescription, Screen.Law, "⚖️"))
        }

        LandDataRepository.khatianList.forEach { khatian ->
            list.add(SearchResult("${khatian.type} - ${khatian.fullName}", "খতিয়ান", khatian.description, Screen.Khatian, "📜"))
        }

        LandDataRepository.onlineServices.forEach { service ->
            list.add(SearchResult(service.title, "অনলাইন সেবা", service.description, Screen.Services, "🏛️"))
        }

        LandDataRepository.importantContacts.forEach { contact ->
            list.add(SearchResult(contact.title, "পেমেন্ট ও ভিআইপি", "${contact.phone} - ${contact.description}", Screen.Contact, "📞"))
        }

        list
    }

    val filteredResults = remember(searchQuery, selectedCategoryFilter) {
        val query = searchQuery.trim().lowercase()
        allSearchableItems.filter { item ->
            val matchesCategory = if (selectedCategoryFilter == "সকল") true else {
                when (selectedCategoryFilter) {
                    "এআই ও স্ক্যানার" -> item.category == "এআই ও স্ক্যানার"
                    "খতিয়ান" -> item.category == "খতিয়ান"
                    "আইন ও ফারায়েজ" -> item.category == "আইন ও ফারায়েজ"
                    "ক্যালকুলেটর" -> item.category == "ক্যালকুলেটর"
                    "অনলাইন সেবা" -> item.category == "অনলাইন সেবা"
                    "পেমেন্ট ও ভিআইপি" -> item.category == "পেমেন্ট ও ভিআইপি"
                    else -> true
                }
            }

            val matchesQuery = if (query.isBlank()) true else {
                item.title.lowercase().contains(query) ||
                        item.category.lowercase().contains(query) ||
                        item.description.lowercase().contains(query)
            }

            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("দ্রুত সার্চ ও ডিরেক্টরি", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("স্মার্ট ফিল্টার ও তাৎক্ষণিক ফলাফল", fontSize = 10.sp, color = GoldLight)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight)
        ) {
            // Search Input Field
            Surface(
                color = Color.White,
                shadowElevation = 1.5.dp,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("আইন, খতিয়ান, দলিল বা সেবার নাম লিখুন...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LandGreenPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = LandGreenPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = SurfaceLight
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Quick Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategoryFilter == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = category },
                                label = { Text(category, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LandGreenPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) LandGreenPrimary else BorderColor,
                                    borderWidth = 1.dp,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Results Count Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ফলাফল: ${filteredResults.size} টি পাওয়া গেছে",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LandGreenDark
                )
                if (selectedCategoryFilter != "সকল" || searchQuery.isNotEmpty()) {
                    Text(
                        text = "ফিল্টার সক্রিয়",
                        fontSize = 10.5.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Search Results List
            if (filteredResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোনো তথ্য পাওয়া যায়নি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LandGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ভিন্ন বানান বা অন্য ক্যাটাগরি নির্বাচন করে চেষ্টা করুন",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredResults, key = { it.title + it.category }) { result ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToScreen(result.targetScreen) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = LandGreenContainer,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(result.iconEmoji, fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = result.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            color = LandGreenPrimary.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = result.category,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LandGreenDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = result.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.5.sp,
                                        maxLines = 2
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
