package com.msmonaym.land.ui.services

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.model.LandService
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onSelectService: (LandService) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val services = remember { LandDataRepository.onlineServices }
    val portals = remember { LandDataRepository.officialGovernmentPortals }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ডিজিটাল ভূমি সেবা ও নির্দেশিকা", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandGreenPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7F5))
        ) {
            Surface(
                color = LandGreenContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = LandGreenPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "খতিয়ান, নামজারি ও ভূমি কর সংক্রান্ত ডিজিটাল নির্দেশিকা ও টুলবক্স",
                        fontSize = 13.sp,
                        color = LandGreenDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(services) { service ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectService(service) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LandGreenContainer,
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = service.iconEmoji, fontSize = 24.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = service.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LandGreenDark
                                        )
                                        Text(
                                            text = service.category,
                                            fontSize = 12.sp,
                                            color = LandGreenPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onSelectService(service) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = service.description,
                                fontSize = 12.5.sp,
                                color = Color.DarkGray,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Section 2: Mandatory Government Disclaimer Card
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Gavel,
                                    contentDescription = "Government Disclaimer",
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "সরকারি অস্বীকৃতি ও তথ্যের পাবলিক উৎস (Legal Disclaimer)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚠️ এই অ্যাপ্লিকেশনটি গণপ্রজাতন্ত্রী বাংলাদেশ সরকার বা কোনো সরকারি দপ্তরের অফিশিয়াল অ্যাপ নয় এবং সরকারের কোনো প্রতিনিধিত্ব করে না। এটি একটি স্বাধীন বেসরকারি প্রযুক্তিগত ও আইনি সহায়ক প্ল্যাটফর্ম। নাগরিকদের তথ্যের স্বচ্ছতার জন্য সরকারের অনুমোদিত পাবলিক পোর্টালের সরাসরি লিংকসমূহ নিচে প্রদান করা হলো।",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }

                // Section 3: Official Government Portals Header
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = LandGreenContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏛️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "অফিসিয়াল সরকারি ভূমি সেবা পোর্টালসমূহ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = LandGreenDark
                                )
                                Text(
                                    text = "সরাসরি ব্রাউজারে প্রবেশ করতে ভিজিট বাটনে ক্লিক করুন",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }

                items(portals) { portal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LandGreenContainer,
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = portal.iconEmoji, fontSize = 22.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = portal.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = LandGreenDark
                                        )
                                        Text(
                                            text = portal.subTitle,
                                            fontSize = 12.sp,
                                            color = LandGreenPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portal.url))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "ওয়েবসাইট ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LandGreenPrimary),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("ভিজিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = portal.description,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
