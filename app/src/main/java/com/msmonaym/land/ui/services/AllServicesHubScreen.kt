package com.msmonaym.land.ui.services

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.data.AdminManager
import com.msmonaym.land.data.UserProfileManager
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.ui.ai.AiLandAssistantModal
import com.msmonaym.land.ui.dashboard.SummaryDashboardView
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.*

data class HubServiceItem(
    val title: String,
    val subtitle: String,
    val emojiIcon: String,
    val category: String,
    val screen: Screen,
    val badge: String? = null,
    val tintColor: Color = LandGreenContainer,
    val aiCategory: AiCategory = AiCategory.GENERAL,
    val customAction: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesHubScreen(
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profileManager = remember { UserProfileManager(context) }
    val adminManager = remember { AdminManager(context) }

    val officialEmail = remember { adminManager.getOfficialContactEmail() }
    val officialPhone = remember { adminManager.getOfficialContactPhone() }
    val facebookUrl = remember { adminManager.getFacebookPageUrl() }
    val appLiveUrl = remember { adminManager.getAppLaunchUrl() }

    fun copyText(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ওয়েবসাইট বা পেজ ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "M.S MONAYM ENT. ভূমি সেবা ও পরামর্শ সংক্রান্ত")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ইমেইল অ্যাপ খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareAppUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "M.S MONAYM ENT. স্মার্ট ভূমি সেবা অ্যাপ")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🏛️ M.S MONAYM ENT. - স্মার্ট ডিজিটাল ভূমি সেবা ও দলিল ক্যালকুলেটর অ্যাপ।\n\n🔗 অ্যাপটির সঠিক সাইট এড্রেস:\n$url\n\n📧 অফিসিয়াল ইমেইল: $officialEmail\n🌐 ফেসবুক পেজ: $facebookUrl\n📞 হটলাইন: $officialPhone"
                )
            }
            context.startActivity(Intent.createChooser(intent, "অ্যাপ লিঙ্ক শেয়ার করুন"))
        } catch (e: Exception) {
            Toast.makeText(context, "শেয়ার করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    var userName by remember { mutableStateOf(profileManager.getUserName()) }
    var userPhone by remember { mutableStateOf(profileManager.getUserPhone()) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val adminNotice = remember { adminManager.getAppAnnouncement() }

    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সকল সেবা") }

    var showAiAssistant by remember { mutableStateOf(false) }
    var currentAiCategory by remember { mutableStateOf(AiCategory.GENERAL) }

    val categories = remember {
        listOf("সকল সেবা", "📋 ভূমি নির্দেশিকা", "📐 পরিমাপ ও সার্ভে", "⚖️ আইন ও দলিল", "💎 প্রিমিয়াম ও অর্থ", "⚙️ হেল্প ও প্রশাসন")
    }

    val allServices = remember {
        listOf(
            // 💎 প্রিমিয়াম ও অর্থ
            HubServiceItem(
                title = "প্রিমিয়াম ভূমি সেবা ও আমিন বুকিং",
                subtitle = "ডিজিটাল সার্ভেয়ার ফিল্ড ভিজিট, বায়নানামা চুক্তি ও আইনি সমাধান সরাসরি নিন",
                emojiIcon = "💎",
                category = "💎 প্রিমিয়াম ও অর্থ",
                screen = Screen.PremiumServices,
                badge = "VIP",
                tintColor = AmberTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "সার্ভে রিপোর্ট ও ক্যালকুলেশন টুলবক্স",
                subtitle = "Microsoft Word ও Excel ব্যবহার করে জমির ডিজিটাল সার্ভে রিপোর্ট প্রস্তুত ও A4 PDF ডাউনলোড",
                emojiIcon = "📊",
                category = "📐 পরিমাপ ও সার্ভে",
                screen = Screen.SurveyReport,
                badge = "PRO",
                tintColor = TealTint,
                aiCategory = AiCategory.CALCULATOR
            ),
            HubServiceItem(
                title = "বিকাশ ও নগদ সেন্ড মানি গেটওয়ে",
                subtitle = "এডমিন নম্বরে (01976444504) ফি পরিশোধ করুন, অ্যাপ ওপেন করুন ও মানি রসিদ ডাউনলোড করুন",
                emojiIcon = "💳",
                category = "💎 প্রিমিয়াম ও অর্থ",
                screen = Screen.Payment,
                badge = "পেমেন্ট",
                tintColor = RoseTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "ডিজিটাল জমি মাপজোক ক্যালকুলেটর",
                subtitle = "ফুট, লিঙ্ক, গজ, মিটার থেকে শতাংশ, কাঠা, বিঘা ও একরে নিখুঁত পরিমাপ",
                emojiIcon = "📐",
                category = "📐 পরিমাপ ও সার্ভে",
                screen = Screen.Calculator,
                badge = "জনপ্রিয়",
                tintColor = SkyTint,
                aiCategory = AiCategory.CALCULATOR
            ),

            // 📋 ভূমি নির্দেশিকা
            HubServiceItem(
                title = "সার্ভিস সামারি ড্যাশবোর্ড ও বিশ্লেষণ",
                subtitle = "খতিয়ান, ভূমি কর, নামজারি সহায়িকা ও একনজরে সকল সেবার সার্বিক অবস্থা",
                emojiIcon = "📊",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.SummaryDashboard,
                badge = "নতুন",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "খতিয়ান ও পর্চা নির্দেশিকা (CS, SA, RS, BS)",
                subtitle = "সিএস, এসএ, আরএস, বিএস ও সিটি জরিপের খতিয়ান চেনার উপায় ও পরিচিতি",
                emojiIcon = "📜",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.Khatian,
                badge = "ডিজিটাল",
                tintColor = AmberTint,
                aiCategory = AiCategory.KHATIAN
            ),
            HubServiceItem(
                title = "পর্চা ও খতিয়ান নকল নির্দেশিকা",
                subtitle = "জমির সার্টিফাইড নকল পর্চা উত্তোলন ও রেকর্ড যাচাইয়ের নিয়মাবলী",
                emojiIcon = "📄",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.Khatian,
                badge = "অফলাইন",
                tintColor = SkyTint,
                aiCategory = AiCategory.PORCHA
            ),
            HubServiceItem(
                title = "ই-নামজারি সহায়িকা ও সরকারি ফি হিসাব",
                subtitle = "জমি নামজারি ও খারিজের নিয়মাবলী, সরকারি ফি ১,১৭০/- ও কাগজপত্র চেকলিস্ট",
                emojiIcon = "🏛️",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.Mutation,
                badge = "সহায়িকা",
                tintColor = IndigoTint,
                aiCategory = AiCategory.MUTATION
            ),
            HubServiceItem(
                title = "ভূমি উন্নয়ন কর ও খাজনা ক্যালকুলেটর",
                subtitle = "জমির শ্রেণী অনুযায়ী বাৎসরিক কর হিসাব, ২৫ বিঘা মওকুফ নিয়ম ও দাখিলা নির্দেশিকা",
                emojiIcon = "💰",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.Tax,
                badge = "ক্যালকুলেটর",
                tintColor = RoseTint,
                aiCategory = AiCategory.TAX
            ),
            HubServiceItem(
                title = "ডিজিটাল ভূমি সেবা ও নির্দেশিকা ডিরেক্টরি",
                subtitle = "খতিয়ান, নামজারি, কর ও সকল ভূমিসেবার সম্পূর্ণ নির্দেশিকা হাব",
                emojiIcon = "🌐",
                category = "📋 ভূমি নির্দেশিকা",
                screen = Screen.Services,
                badge = "ডিরেক্টরি",
                tintColor = TealTint,
                aiCategory = AiCategory.GENERAL
            ),

            // ⚖️ আইন ও দলিল
            HubServiceItem(
                title = "বাংলা ডিজিটাল দলিল রাইটার ও ওয়ার্ড এডিটর",
                subtitle = "সাফ-কবলা, হেবা, দানপত্র, বায়না, বন্টন ও সকল দলিলের আদর্শ বয়ান, ফর্ম ও Word (.doc) এক্সপোর্ট",
                emojiIcon = "🖋️",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.DeedWriter,
                badge = "নতুন",
                tintColor = PurpleTint,
                aiCategory = AiCategory.REGISTRATION
            ),
            HubServiceItem(
                title = "কোম্পানি কনসালটেন্ট ও বেসরকারি পোর্টাল",
                subtitle = "শীর্ষ বেসরকারি কর্পোরেট, আবাসন ও আইনি ওয়েবসাইট তালিকা, অ্যাডমিন লিংক ম্যানেজার ও ইন-অ্যাপ ব্রাউজার",
                emojiIcon = "💼",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.CompanyConsultant,
                badge = "বেসরকারি",
                tintColor = BlueTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "এআই ক্যামেরা ও দলিল স্ক্যানার (Gemini 2.5)",
                subtitle = "ক্যামেরা ছবি বা ইমেজ দিয়ে দলিলের দাগ, খতিয়ান, মালিকানা যাচাই ও A4 PDF লিগ্যাল অডিট রিপোর্ট",
                emojiIcon = "📷",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.AiDocScanner,
                badge = "AI স্ক্যান",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.SCANNER
            ),
            HubServiceItem(
                title = "উত্তরাধিকার বন্টন ক্যালকুলেটর (১০০% নির্ভুল)",
                subtitle = "বাংলাদেশ মুসলিম পারিবারিক আইন অধ্যাদেশ ১৯৬১ ও কোরআনিক ফারায়েজ অনুযায়ী জমির সঠিক অংশ ও হিস্যা বণ্টন",
                emojiIcon = "⚖️",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.WarishCalculator,
                badge = "নতুন",
                tintColor = AmberTint,
                aiCategory = AiCategory.LAW
            ),
            HubServiceItem(
                title = "বাংলাদেশ ভূমি আইন ও অধিকার গাইড",
                subtitle = "মালিকানা স্বত্ব, বেদখল উদ্ধার, ওয়ারিশ আইন, বাটোয়ারা ও তামাদি আইনের পূর্ণাঙ্গ সংকলন",
                emojiIcon = "⚖️",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.Law,
                badge = "আইন",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.LAW
            ),
            HubServiceItem(
                title = "জমি রেজিস্ট্রেশন ও দলিল ফি ক্যালকুলেটর",
                subtitle = "সাব-রেজিস্ট্রি ফি, স্ট্যাম্প ডিউটি, স্থানীয় সরকার কর ও এআইটি নিখুঁত হিসাব",
                emojiIcon = "📝",
                category = "⚖️ আইন ও দলিল",
                screen = Screen.Registration,
                badge = "রেজিস্ট্রি",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.REGISTRATION
            ),

            // ⚙️ হেল্প ও প্রশাসন
            HubServiceItem(
                title = "অ্যাপ কিউআর কোড (Direct Scan QR)",
                subtitle = "ক্যামেরা দিয়ে স্ক্যান করে সরাসরি অ্যাপে প্রবেশ, ছবি সেভ বা প্রিন্ট করার সুবিধা",
                emojiIcon = "📲",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.AppQrCode,
                badge = "QR",
                tintColor = IndigoTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "মাস্টার এডমিন কন্ট্রোল ও রেভিনিউ ম্যানেজমেন্ট",
                subtitle = "এডমিনের প্রতিদিনের আয় লগ, সেবা ফি রেট চার্ট ও ফুল কন্ট্রোল প্যানেল",
                emojiIcon = "🛡️",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.AdminGuide,
                badge = "এডমিন",
                tintColor = AmberTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "২৪/৭ কাস্টমার সাপোর্ট ও হটলাইন",
                subtitle = "ভূমি সেবা সংক্রান্ত হেল্পলাইন ও সাপোর্ট টিমের সাথে সরাসরি যোগাযোগের মাধ্যম",
                emojiIcon = "📞",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.Contact,
                badge = "হেল্প",
                tintColor = TealTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "অ্যাপ পরিচিতি ও ব্যবহারের নিয়মাবলী",
                subtitle = "M.S MONAYM ENT. স্মার্ট ভূমি সেবা অ্যাপের ফিচার ও ব্যবহারের নির্দেশিকা",
                emojiIcon = "ℹ️",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.About,
                badge = "তথ্য",
                tintColor = SkyTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "সিস্টেম সেটিংস ও ইউজার প্রোফাইল",
                subtitle = "ব্যবহারকারীর নাম ও ফোন নম্বর পরিবর্তন, অ্যাপ ব্যাকআপ ও কনফিগারেশন",
                emojiIcon = "⚙️",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.Settings,
                badge = "সেটিংস",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.GENERAL
            ),
            HubServiceItem(
                title = "অফিশিয়াল ফেসবুক পেজ (msmonaym.land)",
                subtitle = "https://facebook.com/msmonaym.land - ফেসবুক পেজে সরাসরি ভিজিট করুন ও মেসেজ দিন",
                emojiIcon = "🌐",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.Contact,
                badge = "ফেসবুক",
                tintColor = IndigoTint,
                aiCategory = AiCategory.GENERAL,
                customAction = { openUrl(facebookUrl) }
            ),
            HubServiceItem(
                title = "অফিশিয়াল ইমেইল (msmonaymenterprise@gmail.com)",
                subtitle = "msmonaymenterprise@gmail.com - যেকোনো দলিল বা সেবা অনুসন্ধান পাঠাতে ইমেইল করুন",
                emojiIcon = "📧",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.Contact,
                badge = "ইমেইল",
                tintColor = RoseTint,
                aiCategory = AiCategory.GENERAL,
                customAction = { sendEmail(officialEmail) }
            ),
            HubServiceItem(
                title = "অ্যাপটির সঠিক লাইভ ওয়েব সাইট এড্রেস (Live URL)",
                subtitle = "https://ais-pre-2pbv6u37on6ftvr6eh3ut3-773309380409.asia-east1.run.app - সরাসরি ব্রাউজারে ব্যবহার করুন",
                emojiIcon = "🔗",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.AppQrCode,
                badge = "সাইট লিংক",
                tintColor = TealTint,
                aiCategory = AiCategory.GENERAL,
                customAction = { openUrl(appLiveUrl) }
            ),
            HubServiceItem(
                title = "গোপনীয়তা নীতি ও সরকারি ডিসক্লেইমার (Privacy Policy)",
                subtitle = "গুগল প্লে-স্টোর মানসম্মত প্রাইভেসি পলিসি, ডাটা নিরাপত্তা ও সরকারি তথ্যের অফিশিয়াল পাবলিক উৎসসমূহ",
                emojiIcon = "🛡️",
                category = "⚙️ হেল্প ও প্রশাসন",
                screen = Screen.About,
                badge = "প্লে-স্টোর",
                tintColor = EmeraldTint,
                aiCategory = AiCategory.GENERAL,
                customAction = { showPrivacyDialog = true }
            )
        )
    }

    val filteredServices = remember(searchQuery, selectedCategory) {
        allServices.filter { item ->
            val matchesCategory = if (selectedCategory == "সকল সেবা") true else item.category == selectedCategory
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.title.contains(searchQuery, ignoreCase = true) ||
                        item.subtitle.contains(searchQuery, ignoreCase = true) ||
                        item.category.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "সকল ভূমি সেবা ও টুলবক্স",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "অ্যাপের সকল ফিচার ও অনলাইন পোর্টাল ডিরেক্টরি",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
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
                    IconButton(onClick = {
                        currentAiCategory = AiCategory.GENERAL
                        showAiAssistant = true
                    }) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ai_chat_digital),
                                    contentDescription = "AI",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LandGreenPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight)
        ) {
            // Search Input Box
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("যেকোনো সেবা বা টুলস খুঁজুন...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = LandGreenPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = LandGreenPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                color = if (isSelected) LandGreenPrimary else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) LandGreenPrimary else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Results count and info bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট প্রাপ্ত সেবা: ${filteredServices.size} টি",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "ট্যাপ করে সরাসরি চালু করুন",
                    fontSize = 11.sp,
                    color = LandGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Services List
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // When on Default view ("সকল সেবা" and no active search query), display all hub modules & files
                if (searchQuery.isBlank() && selectedCategory == "সকল সেবা") {
                    // 1. Hero Welcome Banner & User Profile
                    item(key = "hub_hero_banner") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(LandGreenDark, LandGreenPrimary)
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = GoldAccent.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.8f))
                                    ) {
                                        Text(
                                            text = "🏢 M.S MONAYM ENT. হাব",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight,
                                            letterSpacing = 0.4.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        modifier = Modifier.clickable {
                                            inputName = userName
                                            inputPhone = userPhone
                                            profileError = ""
                                            showProfileDialog = true
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit Profile",
                                                tint = GoldLight,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "প্রোফাইল",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "স্বাগতম, $userName",
                                    fontSize = 17.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "আপনার ভূমি সংক্রান্ত যাবতীয় তথ্য, ফাইল, ড্যাশবোর্ড ও স্মার্ট এআই সেবা এক ক্লিকে পান।",
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // AI Quick Query Launcher Bar
                                Surface(
                                    color = Color.White.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentAiCategory = AiCategory.GENERAL
                                            showAiAssistant = true
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, GoldAccent),
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_ai_digital_bot),
                                                    contentDescription = "Digital AI",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "স্মার্ট এআই ভূমি সহকারী",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "জমির যেকোনো প্রশ্ন লিখুন বা এআই সমাধান নিন...",
                                                fontSize = 10.sp,
                                                color = GoldLight
                                            )
                                        }
                                        Surface(
                                            color = GoldAccent,
                                            shape = CircleShape
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "প্রশ্ন করুন",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Icon(
                                                    Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Live Admin Notice / Announcement Bar
                    if (adminNotice.isNotBlank()) {
                        item(key = "hub_admin_notice") {
                            Surface(
                                color = Color(0xFFFEFCE8),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📢", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = adminNotice,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF713F12),
                                        lineHeight = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Live Summary Dashboard View (Interactive Material 3 Live Status & Quick Action Cards)
                    item(key = "hub_summary_dashboard_section") {
                        SummaryDashboardView(
                            onNavigate = onNavigate,
                            onLaunchAi = { category ->
                                currentAiCategory = category
                                showAiAssistant = true
                            },
                            isEmbedded = true
                        )
                    }

                    // 4. Featured Special Action Cards Header
                    item(key = "hub_featured_header") {
                        Text(
                            text = "🚀 বিশেষায়িত সেবা ও হটলাইন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = LandGreenDark,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Featured 1: AI Camera & Doc Scanner
                    item(key = "hub_card_ai_scanner") {
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.2.dp, LandGreenLight.copy(alpha = 0.8f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.AiDocScanner) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = LandGreenPrimary.copy(alpha = 0.15f),
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, LandGreenPrimary),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("📷", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "এআই ক্যামেরা ও দলিল স্ক্যানার",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LandGreenDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = LandGreenPrimary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "Gemini 2.5",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        "দলিলের ছবি তুলে তাৎক্ষণিক সত্যতা, দাগ ও A4 PDF লিগ্যাল অডিট",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = LandGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Featured 2: 100% Warish Calculator
                    item(key = "hub_card_warish_calc") {
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.2.dp, GoldAccent),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.WarishCalculator) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = GoldAccent.copy(alpha = 0.22f),
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, GoldAccent),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚖️", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "উত্তরাধিকার বন্টন ক্যালকুলেটর",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LandGreenDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFB45309),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "১০০% নির্ভুল",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        "১৯৬১ সালের পারিবারিক আইন ও কোরআনিক ফারায়েজ অনুযায়ী হিস্যা",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF78350F),
                                        lineHeight = 14.sp
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Featured 3: Digital Deed Writer
                    item(key = "hub_card_deed_writer") {
                        Surface(
                            color = Color(0xFFFAF5FF),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFA855F7).copy(alpha = 0.6f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.DeedWriter) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFFA855F7).copy(alpha = 0.15f),
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color(0xFFA855F7)),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🖋️", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "বাংলা ডিজিটাল দলিল রাইটার ও ওয়ার্ড এডিটর",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LandGreenDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFA855F7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "নতুন",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        "সাফ-কবলা, হেবা, দানপত্র, বায়না ও বন্টন দলিলের আদর্শ বয়ান ও Word (.doc) ডাউনলোড",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Featured 4: Quick Support & Hotlines Bar
                    item(key = "hub_quick_support_bar") {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:16122"))
                                            context.startActivity(intent)
                                        }
                                        .padding(4.dp)
                                ) {
                                    Text("📞", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("ভূমি সেবা হটলাইন", fontSize = 9.5.sp, color = TextMuted)
                                        Text("১৬১২২ (কল করুন)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LandGreenDark)
                                    }
                                }

                                VerticalDivider(modifier = Modifier.height(28.dp), color = DividerColor)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onNavigate(Screen.Payment) }
                                        .padding(4.dp)
                                ) {
                                    Text("💳", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("বিকাশ ও নগদ পেমেন্ট", fontSize = 9.5.sp, color = TextMuted)
                                        Text("01976444504", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                                    }
                                }
                            }
                        }
                    }

                    // Featured 5: Official Websites, Social & App Live URL Hub Card
                    item(key = "hub_official_websites_and_social") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.2.dp, LandGreenPrimary.copy(alpha = 0.35f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = LandGreenPrimary.copy(alpha = 0.12f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("🌐", fontSize = 18.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "অফিশিয়াল ওয়েবসাইট ও যোগাযোগ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.5.sp,
                                                color = LandGreenDark
                                            )
                                            Text(
                                                text = "M.S MONAYM ENT. অফিসিয়াল ডিজিটাল চ্যানেল",
                                                fontSize = 10.5.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        color = GoldAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, GoldAccent)
                                    ) {
                                        Text(
                                            text = "অফিশিয়াল",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Channel 1: Facebook Page (msmonaym.land)
                                Surface(
                                    color = Color(0xFFF0F5FF),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFF1877F2),
                                            shape = CircleShape,
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("f", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "ফেসবুক পেজ",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF1E3A8A)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "msmonaym.land",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF2563EB)
                                                )
                                            }
                                            Text(
                                                text = "https://facebook.com/msmonaym.land",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF64748B),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                copyText("ফেসবুক পেজ লিঙ্ক", facebookUrl)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy Facebook Link",
                                                tint = Color(0xFF1E3A8A),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Button(
                                            onClick = { openUrl(facebookUrl) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("ভিজিট করুন", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Channel 2: Official Email (msmonaymenterprise@gmail.com)
                                Surface(
                                    color = Color(0xFFFFF1F2),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFE11D48),
                                            shape = CircleShape,
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Email,
                                                    contentDescription = "Email",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "অফিশিয়াল ইমেইল",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF881337)
                                            )
                                            Text(
                                                text = officialEmail,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF4C0519),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                copyText("অফিশিয়াল ইমেইল", officialEmail)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy Email",
                                                tint = Color(0xFF881337),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Button(
                                            onClick = { sendEmail(officialEmail) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("ইমেইল পাঠান", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Channel 3: App's Official Live Web Site Address
                                Surface(
                                    color = Color(0xFFF0FDF4),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, LandGreenLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = LandGreenPrimary,
                                                shape = CircleShape,
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Link,
                                                        contentDescription = "App URL",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "অ্যাপের সঠিক লাইভ ওয়েব সাইট",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = LandGreenDark
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = Color(0xFF15803D),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            "অনলাইন সাইট",
                                                            fontSize = 8.5.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "মোবাইল ও কম্পিউটারের যেকোনো ব্রাউজার থেকে সরাসরি ব্যবহারযোগ্য",
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // URL Container Box with quick copy & open
                                        Surface(
                                            color = Color.White,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Language,
                                                    contentDescription = null,
                                                    tint = LandGreenPrimary,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = appLiveUrl,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF1E293B),
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { copyText("অ্যাপ ওয়েবসাইট লিঙ্ক", appLiveUrl) },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.ContentCopy,
                                                        contentDescription = "Copy URL",
                                                        tint = LandGreenPrimary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { openUrl(appLiveUrl) },
                                                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                                            ) {
                                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("ব্রাউজারে যান", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = { shareAppUrl(appLiveUrl) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LandGreenDark),
                                                border = BorderStroke(1.dp, LandGreenPrimary),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("শেয়ার লিঙ্ক", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = { onNavigate(Screen.AppQrCode) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF92400E)),
                                                border = BorderStroke(1.dp, GoldAccent),
                                                modifier = Modifier.weight(0.9f),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                            ) {
                                                Text("📲 কিউআর", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section Header: Full Services Directory
                    item(key = "hub_directory_header") {
                        Text(
                            text = "📁 সকল সেবার পূর্ণ তালিকা ও ফাইল ডিরেক্টরি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = LandGreenDark,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                items(filteredServices) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (item.customAction != null) {
                                    item.customAction.invoke()
                                } else {
                                    onNavigate(item.screen)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(item.tintColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.emojiIcon, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = LandGreenDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                    if (item.badge != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = when (item.badge) {
                                                "VIP", "PRO" -> GoldAccent
                                                "পেমেন্ট" -> Color(0xFFF43F5E)
                                                else -> LandGreenContainer
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = item.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (item.badge) {
                                                    "VIP", "PRO" -> Color.Black
                                                    "পেমেন্ট" -> Color.White
                                                    else -> LandGreenDark
                                                },
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = LandGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // AI Land Assistant Sheet
    if (showAiAssistant) {
        AiLandAssistantModal(
            initialCategory = currentAiCategory,
            onDismiss = { showAiAssistant = false }
        )
    }

    // User Profile Setup / Edit Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = LandGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ব্যবহারকারীর প্রোফাইল সেটিংস",
                        fontWeight = FontWeight.Bold,
                        color = LandGreenDark,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনার নাম এবং মোবাইল নম্বর দিন। এটি অ্যাপের ড্যাশবোর্ডে প্রদর্শিত হবে:",
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = {
                            inputName = it
                            profileError = ""
                        },
                        label = { Text("আপনার নাম (User Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputPhone,
                        onValueChange = {
                            inputPhone = it.filter { char -> char.isDigit() || char == '+' }
                            profileError = ""
                        },
                        label = { Text("মোবাইল নম্বর (Phone Number)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        colors = appTextFieldColors()
                    )

                    if (profileError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profileError,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = inputName.trim()
                        val trimmedPhone = inputPhone.trim()

                        if (trimmedName.isEmpty()) {
                            profileError = "অনুগ্রহ করে আপনার নাম লিখুন"
                            return@Button
                        }
                        if (trimmedPhone.isEmpty() || trimmedPhone.length < 11) {
                            profileError = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                            return@Button
                        }

                        profileManager.saveProfile(trimmedName, trimmedPhone)
                        userName = trimmedName
                        userPhone = trimmedPhone
                        showProfileDialog = false
                        Toast.makeText(context, "প্রোফাইল তথ্য সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("বাতিল", color = Color.Gray)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        com.msmonaym.land.data.PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false }
        )
    }
}
