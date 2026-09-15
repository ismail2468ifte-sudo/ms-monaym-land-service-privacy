package com.msmonaym.land.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.data.LandDataRepository
import com.msmonaym.land.ui.navigation.Screen
import com.msmonaym.land.ui.theme.LandGreenContainer
import com.msmonaym.land.ui.theme.LandGreenDark
import com.msmonaym.land.ui.theme.LandGreenPrimary

data class SearchResult(
    val title: String,
    val category: String,
    val description: String,
    val targetScreen: Screen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToScreen: (Screen) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allSearchableItems = remember {
        val list = mutableListOf<SearchResult>()

        LandDataRepository.landLaws.forEach { law ->
            list.add(SearchResult(law.title, "আইন", law.shortDescription, Screen.Law))
        }

        LandDataRepository.khatianList.forEach { khatian ->
            list.add(SearchResult("${khatian.type} - ${khatian.fullName}", "খতিয়ান", khatian.description, Screen.Khatian))
        }

        LandDataRepository.onlineServices.forEach { service ->
            list.add(SearchResult(service.title, "অনলাইন সেবা", service.description, Screen.Services))
        }

        LandDataRepository.importantContacts.forEach { contact ->
            list.add(SearchResult(contact.title, "ফোন নম্বর", "${contact.phone} - ${contact.description}", Screen.Contact))
        }

        list.add(SearchResult("দলিল রেজিস্ট্রি ফি ক্যালকুলেটর", "ক্যালকুলেটর", "জমির দাম দিয়ে মোট সরকারি ফি হিসাব করুন", Screen.Registration))
        list.add(SearchResult("জমি পরিমাপ একক রূপান্তর", "ক্যালকুলেটর", "শতক, কাঠা, বিঘা, একর ও বর্গফুট রূপান্তর", Screen.Calculator))

        list
    }

    val filteredResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allSearchableItems
        } else {
            val query = searchQuery.trim().lowercase()
            allSearchableItems.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.category.lowercase().contains(query) ||
                        item.description.lowercase().contains(query)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সার্চ করুন", fontWeight = FontWeight.Bold, color = Color.White) },
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
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("আইন, খতিয়ান বা সেবার নাম দিয়ে খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LandGreenPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LandGreenPrimary,
                        focusedLabelColor = LandGreenPrimary
                    )
                )
            }

            Text(
                text = "ফলাফল: ${filteredResults.size} টি পাওয়া গেছে",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToScreen(result.targetScreen) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = result.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = LandGreenDark,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    color = LandGreenContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = result.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LandGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.description,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
