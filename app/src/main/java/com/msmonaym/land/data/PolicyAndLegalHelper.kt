package com.msmonaym.land.data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.ui.theme.GoldAccent
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

object PolicyAndLegalContent {
    const val GOVERNMENT_DISCLAIMER_TITLE = "সরকারি অস্বীকৃতি ও তথ্যের উৎস (Government Disclaimer)"
    
    const val GOVERNMENT_DISCLAIMER_BODY = """M.S MONAYM ENT. একটি সম্পূর্ণ স্বাধীন, স্বত্বাধিকারী ভিত্তিক বেসরকারি আইনি ও প্রযুক্তিগত সহায়ক ডিজিটাল প্ল্যাটফর্ম।

⚠️ গুরুত্বপূর্ণ ঘোষণা:
১. এই অ্যাপ্লিকেশনটি গণপ্রজাতন্ত্রী বাংলাদেশ সরকার বা ভূমি মন্ত্রণালয় কিংবা অন্য কোনো সরকারি দপ্তরের অফিশিয়াল বা প্রতিনিধিত্বকারী অ্যাপ নয়।
২. এই অ্যাপের মাধ্যমে কোনো সরকারি অফিসিয়াল সার্ভিস সরাসরি প্রদান করা হয় না; বরং নাগরিকদের ভূমি পরিমাপ, রেজিস্ট্রেশন খরচ হিসাব, দলিল খসড়া তৈরি এবং সরকারি তথ্যের সহজ সহায়িকা হিসেবে টুলটি কাজ করে।
৩. অ্যাপটিতে উল্লিখিত তথ্য, ফিস ও বিধিমালার মূল পাবলিক উৎসসমূহ:
   • জাতীয় ভূমি সেবা পোর্টাল (Ministry of Land): https://www.land.gov.bd
   • ভূমি রেকর্ড ও জরিপ অধিদপ্তর (DLR&S): https://dlrs.gov.bd
   • ভূমি মন্ত্রণালয় বাংলাদেশ: https://minland.gov.bd
   • জাতীয় ই-পর্চা পোর্টাল: https://eporcha.gov.bd
   • ডিজিটাল ভূমি উন্নয়ন কর: https://ldtax.gov.bd
   • ই-নামজারি সিস্টেম: https://mutation.land.gov.bd
   • বাংলাদেশ আইন ও সংসদ বিষয়ক বিভাগ: http://bdlaws.minlaw.gov.bd
   • জাতীয় তথ্য বাতায়ন: https://bangladesh.gov.bd"""

    const val PRIVACY_POLICY_TITLE = "গোপনীয়তা নীতি ও ডেটা সেফটি (Privacy Policy & Data Safety)"

    const val PRIVACY_POLICY_BODY = """M.S MONAYM ENT. ব্যবহারকারীদের ব্যক্তিগত গোপনীয়তা ও তথ্যের নিরাপত্তাকে সর্বোচ্চ অগ্রাধিকার দেয়। আমাদের ফাইনাল AAB-এর প্রকৃত ডেটা আচরণ অনুযায়ী:

১. ডেটা সেফটি ও সংগ্রহ বিবরণ:
• ব্যক্তিগত তথ্য (নাম, মোবাইল নম্বর): কেবলমাত্র অ্যাপের অভ্যন্তরীণ প্রোফাইল এবং দলিল প্রস্তুতের সুবিধার্থে ঐচ্ছিকভাবে গ্রহণ করা হয়। এটি ব্যবহারকারীর নিজস্ব ডিভাইসে Local Storage-এ সংরক্ষিত থাকে, কোনো ক্লাউড সার্ভারে আপলোড বা কারো সাথে শেয়ার করা হয় না।
• ছবি ও ডকুমেন্টস (Camera/Picker): ব্যবহারকারী যখন কোনো দলিল স্ক্যান বা অডিট করতে চান, কেবল তখনই সেই ছবিটি Gemini AI ভিশন প্রক্রিয়াকরণের জন্য এনক্রিপ্টেড চ্যানেলে ব্যবহৃত হয়। কোনো ছবি সংরক্ষণ বা বিজ্ঞাপনে ব্যবহার হয় না।
• লোকেশন (GPS): জমি ও প্লটের সঠিক দৈর্ঘ্য-প্রস্থ ও সীমানা নির্ণয় করার সময় ডিভাইস-লেভেলে রিয়েল-টাইম প্রসেস হয়।
• আর্থিক ট্রানজেকশন তথ্য: প্রিমিয়াম সেবার বুকিং ম্যানুয়াল TrxID হিসেবে প্রসেস হয়, কোনো ব্যাংক পাসওয়ার্ড বা গোপন পিন গ্রহণ করা হয় না।
• বিজ্ঞাপন বা ট্র্যাকিং: কোনো তৃতীয় পক্ষের ট্র্যাকিং কুকি, ট্র্যাকার বা অ্যাড নেটওয়ার্ক নেই (Zero Third-Party Sharing)।

২. ডিভাইস পারমিশন নীতি:
• ক্যামেরা (Camera): দলিল স্ক্যানিং ও ডিজিটাল এআই অডিটের জন্য।
• লোকেশন (GPS): স্যাটেলাইট জমি পরিমাপ ও দাগ নির্ণয়ের জন্য।
• ফাইল ও স্টোরেজ: প্রস্তুতকৃত দলিল বা সার্ভে রিপোর্ট PDF আকারে ডিভাইসে সংরক্ষণ ও শেয়ারের জন্য।
• বায়োমেট্রিক: অ্যাপের নিজস্ব সিকিউরিটি লক স্ক্রিনের জন্য।

৩. ডেটা নিরাপত্তা ও অপসারণ (Data Deletion):
• সকল নেটওয়ার্ক যোগাযোগ HTTPS (TLS 1.3) দ্বারা এনক্রিপ্টেড।
• ব্যবহারকারী অ্যাপ সেটিংস থেকে ডেটা ক্লিয়ার করলে তাৎক্ষণিকভাবে তার সমস্ত লোকাল তথ্য মুছে যায়।

৪. যোগাযোগ ও স্বত্বাধিকারী:
• প্রতিষ্ঠান: M.S MONAYM ENT.
• স্বত্বাধিকারী: ইসমাঈল (Ismail)
• হেল্পলাইন: ০১৯৭৬৪৪৪৪৫০৪
• অফিসিয়াল ইমেইল: ismail2468ifte@gmail.com / msmonaymenterprise@gmail.com"""
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("আমি সম্মত ও অবগত", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = LandGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "গোপনীয়তা নীতি ও ডেটা সেফটি",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LandGreenDark
                )
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Disclaimer Box
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "সরকারি অস্বীকৃতি (Legal Disclaimer)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PolicyAndLegalContent.GOVERNMENT_DISCLAIMER_BODY,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }

                // Privacy Policy & Data Safety Box
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LandGreenPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = LandGreenDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ডেটা সেফটি ও নিরাপত্তা (Data Safety)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = LandGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PolicyAndLegalContent.PRIVACY_POLICY_BODY,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White
    )
}
