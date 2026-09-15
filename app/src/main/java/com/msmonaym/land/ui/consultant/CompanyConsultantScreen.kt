package com.msmonaym.land.ui.consultant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.msmonaym.land.ui.theme.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Data Model for Private Corporate / Consultant Website
 */
data class ConsultantWebsite(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val url: String,
    val description: String,
    val badge: String = "বেসরকারি",
    val iconEmoji: String = "🏢",
    val dateAdded: Long = System.currentTimeMillis()
)

/**
 * Local SharedPreferences Storage for Admin-managed Private Websites
 */
object CompanyConsultantRepository {
    private const val PREFS_NAME = "company_consultant_prefs"
    private const val KEY_WEBSITES = "custom_websites_json"

    val DEFAULT_WEBSITES = listOf(
        ConsultantWebsite(
            id = "rehab_bd",
            name = "রিহ্যাব (REHAB)",
            category = "রিয়েল এস্টেট ও আবাসন",
            url = "https://rehab-bd.org/",
            description = "রিয়েল এস্টেট অ্যান্ড হাউজিং অ্যাসোসিয়েশন অব বাংলাদেশ। দেশের শীর্ষ আবাসন ডেভেলপার ও ফ্ল্যাট প্রকল্পের কেন্দ্রীয় বেসরকারি পরামর্শক নেটওয়ার্ক।",
            badge = "বেসরকারি আবাসন সমিতি",
            iconEmoji = "🏢"
        ),
        ConsultantWebsite(
            id = "dcci_bd",
            name = "ঢাকা চেম্বার অব কমার্স (DCCI)",
            category = "ট্রেড ও কর্পোরেট",
            url = "https://www.dccibd.com/",
            description = "ঢাকা চেম্বার অব কমার্স অ্যান্ড ইন্ডাস্ট্রি। বেসরকারি ব্যবসা-বাণিজ্য, কোম্পানি রেজিস্ট্রেশন গাইড ও কর্পোরেট পরামর্শ সেবা।",
            badge = "ব্যবসায়ী সংগঠন",
            iconEmoji = "💼"
        ),
        ConsultantWebsite(
            id = "fbcci_bd",
            name = "এফবিসিসিআই (FBCCI)",
            category = "ব্যবসা ও বাণিজ্য",
            url = "https://www.fbcci.org/",
            description = "ফেডারেশন অব বাংলাদেশ চেম্বারস অব কমার্স অ্যান্ড ইন্ডাস্ট্রি। দেশের শীর্ষ বেসরকারি শিল্প ও বাণিজ্য সহায়তাকারী নেটওয়ার্ক।",
            badge = "শীর্ষ বাণিজ্য পরিষদ",
            iconEmoji = "🌐"
        ),
        ConsultantWebsite(
            id = "bd_legal",
            name = "বিডি লিগ্যাল কনসালটেন্সি",
            category = "আইনি ও অডিট",
            url = "https://bdlegalcounsel.com/",
            description = "কর্পোরেট কোম্পানি লিগ্যাল অডিট, ভূমির স্বত্ব যাচাই (Due Diligence) ও বাণিজ্যিক আইনি সহায়তা কেন্দ্র।",
            badge = "কর্পোরেট লিগ্যাল",
            iconEmoji = "⚖️"
        ),
        ConsultantWebsite(
            id = "bida_oss",
            name = "বিডা ওয়ান স্টপ সার্ভিস (BIDA)",
            category = "কোম্পানি ও লাইসেন্স",
            url = "https://bidaquickserv.org/",
            description = "বাংলাদেশ বিনিয়োগ উন্নয়ন কর্তৃপক্ষ ওয়ান-স্টপ সার্ভিস। বেসরকারি শিল্প প্রতিষ্ঠান স্থাপন, পরিবেশ ছাড়পত্র ও বিনিয়োগ পরামর্শ।",
            badge = "বিনিয়োগ সহায়তা",
            iconEmoji = "🏭"
        ),
        ConsultantWebsite(
            id = "idlc_finance",
            name = "আইডিএলসি হোম লোন ও কনসালটেন্সি",
            category = "ফাইন্যান্স ও লোন",
            url = "https://idlc.com/",
            description = "বেসরকারি আর্থিক প্রতিষ্ঠান। জমি ও ফ্ল্যাট ক্রয়ের গৃহঋণ সহায়তা, প্রজেক্ট ফাইন্যান্সিং ও কর্পোরেট পরামর্শ।",
            badge = "বেসরকারি ফাইন্যান্স",
            iconEmoji = "💰"
        ),
        ConsultantWebsite(
            id = "bgmea_bd",
            name = "বিজিএমইএ (BGMEA)",
            category = "ট্রেড ও কর্পোরেট",
            url = "https://www.bgmea.com.bd/",
            description = "বাংলাদেশ তৈরি পোশাক প্রস্তুত ও রপ্তানিকারক সমিতি। কারখানা লাইসেন্সিং ও বাণিজ্যিক পরামর্শ।",
            badge = "শিল্প পরিষদ",
            iconEmoji = "👔"
        ),
        ConsultantWebsite(
            id = "icab_audit",
            name = "আইসিএবি অডিট ও ট্যাক্স ফোরাম",
            category = "আইনি ও অডিট",
            url = "https://www.icab.org.bd/",
            description = "ইনস্টিটিউট অব চার্টার্ড অ্যাকাউন্ট্যান্টস অব বাংলাদেশ। কোম্পানি হিসাব নিরীক্ষা, ট্যাক্স কনসালটেন্সি ও আর্থিক অডিট।",
            badge = "অডিট ও ট্যাক্স",
            iconEmoji = "📊"
        )
    )

    fun getWebsites(context: Context): List<ConsultantWebsite> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_WEBSITES, null) ?: return DEFAULT_WEBSITES

        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<ConsultantWebsite>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ConsultantWebsite(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.optString("name", "অজ্ঞাত ওয়েবসাইট"),
                        category = obj.optString("category", "সাধারণ"),
                        url = obj.optString("url", "https://"),
                        description = obj.optString("description", ""),
                        badge = obj.optString("badge", "বেসরকারি"),
                        iconEmoji = obj.optString("iconEmoji", "🏢"),
                        dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())
                    )
                )
            }
            if (list.isEmpty()) DEFAULT_WEBSITES else list
        } catch (e: Exception) {
            DEFAULT_WEBSITES
        }
    }

    fun saveWebsites(context: Context, websites: List<ConsultantWebsite>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (item in websites) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("category", item.category)
            obj.put("url", item.url)
            obj.put("description", item.description)
            obj.put("badge", item.badge)
            obj.put("iconEmoji", item.iconEmoji)
            obj.put("dateAdded", item.dateAdded)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_WEBSITES, jsonArray.toString()).apply()
    }

    fun addWebsite(context: Context, website: ConsultantWebsite) {
        val current = getWebsites(context).toMutableList()
        current.add(0, website)
        saveWebsites(context, current)
    }

    fun deleteWebsite(context: Context, websiteId: String) {
        val current = getWebsites(context).filterNot { it.id == websiteId }
        saveWebsites(context, current)
    }

    fun resetToDefaults(context: Context) {
        saveWebsites(context, DEFAULT_WEBSITES)
    }
}

/**
 * Main Screen: কোম্পানি কনসালটেন্ট (Company Consultant)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyConsultantScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var websites by remember { mutableStateOf(CompanyConsultantRepository.getWebsites(context)) }

    // Admin & Dialog States
    var isAdminMode by remember { mutableStateOf(false) }
    var showStepByStepAddDialog by remember { mutableStateOf(false) }
    var websiteToDelete by remember { mutableStateOf<ConsultantWebsite?>(null) }

    // In-App Browser State
    var activeVisitingWebsite by remember { mutableStateOf<ConsultantWebsite?>(null) }

    // Search and Category Filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সব") }

    val categories = listOf("সব", "রিয়েল এস্টেট ও আবাসন", "ট্রেড ও কর্পোরেট", "আইনি ও অডিট", "কোম্পানি ও লাইসেন্স", "ফাইন্যান্স ও লোন", "ব্যবসা ও বাণিজ্য")

    val filteredWebsites = remember(websites, searchQuery, selectedCategory) {
        websites.filter { site ->
            val matchesCategory = selectedCategory == "সব" || site.category.contains(selectedCategory) || selectedCategory.contains(site.category)
            val matchesSearch = searchQuery.isBlank() ||
                    site.name.contains(searchQuery, ignoreCase = true) ||
                    site.description.contains(searchQuery, ignoreCase = true) ||
                    site.url.contains(searchQuery, ignoreCase = true) ||
                    site.badge.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // If viewing a website inside the app, show embedded in-app browser
    if (activeVisitingWebsite != null) {
        InAppConsultantBrowser(
            website = activeVisitingWebsite!!,
            onClose = { activeVisitingWebsite = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "কোম্পানি কনসালটেন্ট",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.5.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "বেসরকারি পোর্টাল",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "শীর্ষ বেসরকারি কর্পোরেট ও আইনি পরামর্শক ওয়েবসাইট তালিকা",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Admin Mode Toggle Button
                    IconButton(
                        onClick = {
                            isAdminMode = !isAdminMode
                            Toast.makeText(
                                context,
                                if (isAdminMode) "এডমিন মোড সক্রিয়: আপনি নতুন ওয়েবসাইট যুক্ত বা ডিলিট করতে পারেন" else "এডমিন মোড নিষ্ক্রিয়",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                            contentDescription = "Admin Toggle",
                            tint = if (isAdminMode) GoldAccent else Color.White
                        )
                    }

                    // Reset to defaults
                    if (isAdminMode) {
                        IconButton(
                            onClick = {
                                CompanyConsultantRepository.resetToDefaults(context)
                                websites = CompanyConsultantRepository.getWebsites(context)
                                Toast.makeText(context, "ডিফল্ট বেসরকারি ওয়েবসাইট তালিকা পুনরুদ্ধার করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenDark)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showStepByStepAddDialog = true },
                containerColor = LandGreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddLink, contentDescription = null) },
                text = {
                    Text(
                        text = "নতুন ওয়েবসাইট যুক্ত করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // 1. Top Header Banner with Stats & Disclaimer
            Surface(
                color = Color.White,
                border = BorderStroke(0.8.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LandGreenContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "💼", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "বেসরকারি কোম্পানি ও পরামর্শক নেটওয়ার্ক",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "অ্যাপের ভেতর থেকেই সরাসরি ওয়েবসাইটগুলোতে ঢুকে নিরাপদে পরিদর্শন ও ব্রাউজ করুন।",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Notice or Feature Highlight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isAdminMode) Color(0xFFFFFBEB) else LandGreenContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Default.Security else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isAdminMode) Color(0xFFB45309) else LandGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAdminMode) "এডমিন মোড চালিত: আপনি ধাপ অনুযায়ী যেকোনো বেসরকারি ওয়েবসাইট যুক্ত/অপসারণ করতে পারবেন।" else "সকল ওয়েবসাইট ইন-অ্যাপ ফাস্ট ব্রাউজারে সরাসরি ওপেন হয়।",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isAdminMode) Color(0xFF92400E) else LandGreenDark
                        )
                    }
                }
            }

            // 2. Search Field with Solid Black Text
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ওয়েবসাইটের নাম, সেবা বা ঠিকানা খুঁজুন...", fontSize = 12.5.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LandGreenPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // 3. Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LandGreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) LandGreenDark else BorderColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Website List
            if (filteredWebsites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🌐", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো বেসরকারি ওয়েবসাইট পাওয়া যায়নি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "নতুন ওয়েবসাইট যুক্ত করতে নিচের বাটনে চাপ দিন",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredWebsites, key = { it.id }) { site ->
                        ConsultantWebsiteCard(
                            website = site,
                            isAdminMode = isAdminMode,
                            onVisit = { activeVisitingWebsite = site },
                            onDelete = { websiteToDelete = site }
                        )
                    }
                }
            }
        }
    }

    // Step-by-Step Add Website Wizard Dialog
    if (showStepByStepAddDialog) {
        StepByStepAddWebsiteDialog(
            onDismiss = { showStepByStepAddDialog = false },
            onSave = { newSite ->
                CompanyConsultantRepository.addWebsite(context, newSite)
                websites = CompanyConsultantRepository.getWebsites(context)
                showStepByStepAddDialog = false
                Toast.makeText(context, "${newSite.name} সফলভাবে যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    if (websiteToDelete != null) {
        AlertDialog(
            onDismissRequest = { websiteToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.Red) },
            title = { Text("ওয়েবসাইট ডিলিট নিশ্চিত করুন") },
            text = { Text("\"${websiteToDelete?.name}\" ওয়েবসাইটটি তালিকা থেকে স্থায়ীভাবে মুছে ফেলতে চান?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        websiteToDelete?.let { site ->
                            CompanyConsultantRepository.deleteWebsite(context, site.id)
                            websites = CompanyConsultantRepository.getWebsites(context)
                            Toast.makeText(context, "ওয়েবসাইট মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                        websiteToDelete = null
                    }
                ) {
                    Text("ডিলিট করুন", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { websiteToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

/**
 * Individual Website Card
 */
@Composable
private fun ConsultantWebsiteCard(
    website: ConsultantWebsite,
    isAdminMode: Boolean,
    onVisit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVisit() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LandGreenContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = website.iconEmoji, fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = website.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = website.badge,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            )
                        }
                    }

                    Text(
                        text = website.category,
                        fontSize = 11.sp,
                        color = LandGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isAdminMode) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = website.description,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secure URL display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = website.url.removePrefix("https://").removePrefix("http://"),
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Direct Visit Button
                Button(
                    onClick = onVisit,
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "সরাসরি পরিদর্শন",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

/**
 * Step-by-Step Multi-Stage Add Website Dialog (ধাপে ধাপে বেসরকারি ওয়েবসাইট যুক্ত করার ফর্ম)
 */
@Composable
private fun StepByStepAddWebsiteDialog(
    onDismiss: () -> Unit,
    onSave: (ConsultantWebsite) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Form inputs with guaranteed black text
    var websiteName by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("রিয়েল এস্টেট ও আবাসন") }
    var selectedEmoji by remember { mutableStateOf("🏢") }
    var websiteDescription by remember { mutableStateOf("") }
    var websiteBadge by remember { mutableStateOf("ভেরিফায়েড বেসরকারি") }

    val categoryOptions = listOf(
        "রিয়েল এস্টেট ও আবাসন" to "🏢",
        "ট্রেড ও কর্পোরেট" to "💼",
        "আইনি ও অডিট" to "⚖️",
        "কোম্পানি ও লাইসেন্স" to "🏭",
        "ফাইন্যান্স ও লোন" to "💰",
        "ব্যবসা ও বাণিজ্য" to "🌐"
    )

    val badgeOptions = listOf("ভেরিফায়েড বেসরকারি", "কর্পোরেট পার্টনার", "আবাসন নেটওয়ার্ক", "ট্যাক্স ও অডিট", "পরামর্শক কেন্দ্র")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "বেসরকারি ওয়েবসাইট যুক্ত করুন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LandGreenDark
                        )
                        Text(
                            text = "ধাপ $currentStep এর ৪: ${
                                when (currentStep) {
                                    1 -> "প্রাথমিক নাম ও পরিচিতি"
                                    2 -> "ক্যাটাগরি ও আইকন নির্বাচন"
                                    3 -> "ওয়েবসাইটের URL লিংক"
                                    else -> "সেবা বিবরণ ও চূড়ান্ত অনুমোদন"
                                }
                            }",
                            fontSize = 11.sp,
                            color = LandGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Step Indicator Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i <= currentStep) LandGreenPrimary else Color(0xFFE2E8F0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content for Current Step
                when (currentStep) {
                    1 -> {
                        // Step 1: Website Name
                        Text(
                            text = "১. বেসরকারি ওয়েবসাইটের নাম লিখুন:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = websiteName,
                            onValueChange = { websiteName = it },
                            label = { Text("প্রতিষ্ঠানের নাম") },
                            placeholder = { Text("যেমন: রিহ্যাব বাংলাদেশ বা আইডিএলসি ফাইন্যান্স") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 পরামর্শ: ব্যবহারকারীরা সহজে চেনার মতো বেসরকারি কোম্পানি বা পরামর্শক সমিতির নাম দিন।",
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                    }
                    2 -> {
                        // Step 2: Category & Emoji
                        Text(
                            text = "২. উপযুক্ত সেবা ক্যাটাগরি বেছে নিন:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            categoryOptions.forEach { (cat, emoji) ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) LandGreenContainer else SurfaceLight,
                                    border = BorderStroke(1.dp, if (isSelected) LandGreenPrimary else BorderColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCategory = cat
                                            selectedEmoji = emoji
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) LandGreenDark else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Step 3: Website URL
                        Text(
                            text = "৩. বেসরকারি ওয়েবসাইটের সম্পূর্ণ URL লিংক দিন:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = websiteUrl,
                            onValueChange = { websiteUrl = it },
                            label = { Text("Website URL Link") },
                            placeholder = { Text("https://example.com") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = LandGreenPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🔒 অ্যাপের ভিতর সরাসরি ওপেন করার জন্য URL অবশ্যই 'https://' দিয়ে শুরু হতে হবে। (যেমন: https://rehab-bd.org/)",
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                    }
                    4 -> {
                        // Step 4: Description & Badge
                        Text(
                            text = "৪. সংক্ষিপ্ত বিবরণ ও পরিচিতি ব্যাজ:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = websiteDescription,
                            onValueChange = { websiteDescription = it },
                            label = { Text("সেবার সংক্ষিপ্ত বিবরণ") },
                            placeholder = { Text("যেমন: জমি ও ফ্ল্যাট ক্রয়ের আবাসন সহায়তা ও কর্পোরেট পরামর্শ") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 3,
                            textStyle = TextStyle(color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            colors = appTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "পরিচিতি ব্যাজ নির্বাচন করুন:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(badgeOptions) { b ->
                                val isSel = websiteBadge == b
                                FilterChip(
                                    selected = isSel,
                                    onClick = { websiteBadge = b },
                                    label = { Text(b, fontSize = 10.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LandGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Buttons (Next / Back / Finish)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("পূর্ববর্তী ধাপ")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < 4) {
                                if (currentStep == 1 && websiteName.isBlank()) {
                                    // Validation
                                    return@Button
                                }
                                if (currentStep == 3 && websiteUrl.isBlank()) {
                                    return@Button
                                }
                                currentStep++
                            } else {
                                // Validate and Save
                                val formattedUrl = if (!websiteUrl.startsWith("http://") && !websiteUrl.startsWith("https://")) {
                                    "https://${websiteUrl.trim()}"
                                } else {
                                    websiteUrl.trim()
                                }

                                val finalName = if (websiteName.isBlank()) "বেসরকারি ওয়েবসাইট" else websiteName.trim()
                                val finalDesc = if (websiteDescription.isBlank()) "বেসরকারি কর্পোরেট ও আইনি সহায়তা পোর্টাল।" else websiteDescription.trim()

                                val newWebsite = ConsultantWebsite(
                                    name = finalName,
                                    category = selectedCategory,
                                    url = formattedUrl,
                                    description = finalDesc,
                                    badge = websiteBadge,
                                    iconEmoji = selectedEmoji
                                )
                                onSave(newWebsite)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (currentStep == 4) "সংরক্ষণ করুন" else "পরবর্তী ধাপ")
                        if (currentStep < 4) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * In-App Direct Website Browser (অ্যাপের ভেতর থেকে সরাসরি website গুলোতে ঢুকে পরিদর্শন)
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InAppConsultantBrowser(
    website: ConsultantWebsite,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf(website.name) }
    var currentUrl by remember { mutableStateOf(website.url) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    // Intercept back button to navigate back in web history if possible
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onClose()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Browser Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = pageTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "SSL Secure",
                            tint = GoldAccent,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentUrl,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Browser", tint = Color.White)
                }
            },
            actions = {
                // Back page
                IconButton(
                    onClick = { webViewInstance?.goBack() },
                    enabled = canGoBack
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canGoBack) Color.White else Color.White.copy(alpha = 0.35f)
                    )
                }

                // Forward page
                IconButton(
                    onClick = { webViewInstance?.goForward() },
                    enabled = canGoForward
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (canGoForward) Color.White else Color.White.copy(alpha = 0.35f)
                    )
                }

                // Reload
                IconButton(onClick = { webViewInstance?.reload() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color.White)
                }

                // Open in External Browser
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "ব্রাউজারে ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open in External", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenDark)
        )

        // Loading Progress Indicator
        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadProgress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = GoldAccent,
                trackColor = LandGreenContainer
            )
        }

        // Live WebView Component
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewInstance = this
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            url?.let { currentUrl = it }
                            canGoBack = canGoBack()
                            canGoForward = canGoForward()
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            url?.let { currentUrl = it }
                            view?.title?.let { if (it.isNotBlank()) pageTitle = it }
                            canGoBack = canGoBack()
                            canGoForward = canGoForward()
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            loadProgress = newProgress
                            if (newProgress >= 100) {
                                isLoading = false
                            }
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            title?.let { if (it.isNotBlank()) pageTitle = it }
                        }
                    }

                    loadUrl(website.url)
                }
            },
            update = { wv ->
                canGoBack = wv.canGoBack()
                canGoForward = wv.canGoForward()
            }
        )
    }
}
