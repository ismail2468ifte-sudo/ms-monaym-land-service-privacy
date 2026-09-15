package com.msmonaym.land.ui.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msmonaym.land.R
import com.msmonaym.land.data.ai.AiCategory
import com.msmonaym.land.data.ai.AiChatMessage
import com.msmonaym.land.data.ai.LandAiService
import com.msmonaym.land.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiLandAssistantModal(
    initialCategory: AiCategory = AiCategory.GENERAL,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            AiChatMessage(
                sender = "ai",
                message = "স্বাগতম! আমি আপনার ডিজিটাল **স্মার্ট ভূমি এআই সহকারী** (Smart Land AI Assistant)।\n\n💬 নিচে এসএমএস (SMS) লিখে জমি আইন, খতিয়ান যাচাই, ই-নামজারি, ভূমি উন্নয়ন কর বা জমি পরিমাপ সংক্রান্ত যেকোনো প্রশ্ন করুন।"
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF4F7F5),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(44.dp)
                    .height(5.dp),
                shape = RoundedCornerShape(3.dp),
                color = LandGreenPrimary.copy(alpha = 0.4f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
        ) {
            // Digital Header Bar with Futuristic AI styling
            Surface(
                color = LandGreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF042F1A),
                                    Color(0xFF0B6623),
                                    Color(0xFF0F766E)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Digital AI Glowing Avatar
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                border = BorderStroke(1.5.dp, GoldAccent),
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(6.dp, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_ai_digital_bot),
                                        contentDescription = "Digital AI Avatar",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ডিজিটাল ভূমি এআই সহকারী",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = GoldAccent,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFF22C55E),
                                        shape = CircleShape,
                                        modifier = Modifier.size(6.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "এসএমএস ও প্রশ্নোত্তরে তাৎক্ষণিক সহায়তা",
                                        fontSize = 11.sp,
                                        color = Color(0xFFD1FAE5)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Category Selector Chips
            Surface(
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AiCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = "${category.iconEmoji} ${category.title}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LandGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = LandGreenContainer,
                                labelColor = LandGreenDark
                            ),
                            border = null
                        )
                    }
                }
            }

            // Suggested Quick Prompt Chips (SMS Templates)
            Surface(
                color = Color(0xFFE8F5E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ai_chat_digital),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "রেডিমেড এসএমএস প্রশ্ন টেমপ্লেট (${selectedCategory.title}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LandGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(selectedCategory.suggestedQuestions) { question ->
                            SuggestionChip(
                                onClick = {
                                    coroutineScope.launch {
                                        inputText = ""
                                        focusManager.clearFocus()
                                        messages.add(AiChatMessage(sender = "user", message = question))
                                        isThinking = true
                                        listState.animateScrollToItem(messages.size - 1)

                                        val reply = LandAiService.askGemini(question, selectedCategory)
                                        messages.add(AiChatMessage(sender = "ai", message = reply))
                                        isThinking = false
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                },
                                label = {
                                    Text(
                                        text = question,
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = LandGreenPrimary.copy(alpha = 0.35f)
                                )
                            )
                        }
                    }
                }
            }

            // Chat SMS Messages Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { chat ->
                    val isUser = chat.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isUser) {
                            Surface(
                                shape = CircleShape,
                                color = LandGreenDark,
                                border = BorderStroke(1.dp, GoldAccent),
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(top = 2.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_ai_digital_bot),
                                        contentDescription = "AI",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            color = if (isUser) LandGreenPrimary else Color.White,
                            shadowElevation = if (isUser) 2.dp else 2.dp,
                            border = if (!isUser) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                            modifier = Modifier.widthIn(max = 310.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (isUser) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Email,
                                            contentDescription = "User SMS",
                                            tint = GoldAccent,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "আপনার এসএমএস বার্তা",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = "🤖 এআই সমাধান",
                                            fontSize = 10.sp,
                                            color = LandGreenDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = chat.message,
                                    fontSize = 13.sp,
                                    color = if (isUser) Color.White else TextPrimary,
                                    lineHeight = 20.sp
                                )

                                if (!isUser) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("AI Land Response", chat.message)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "উত্তর কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        IconButton(
                                            onClick = {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, "ভূমি এআই উত্তর:\n\n${chat.message}")
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }

                        if (isUser) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = LandGreenContainer,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(top = 2.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "User",
                                        tint = LandGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 38.dp, top = 6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = LandGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ডিজিটাল এআই উত্তর লিখছে...",
                                fontSize = 12.sp,
                                color = LandGreenDark,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // SMS Messaging Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = "এসএমএস লিখে প্রশ্ন করুন (যেমন: নামজারি ফি কত?)...",
                                    fontSize = 12.5.sp,
                                    color = Color.Gray
                                )
                            },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ai_chat_digital),
                                    contentDescription = "SMS Input",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = false,
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (inputText.isNotBlank() && !isThinking) {
                                        val query = inputText.trim()
                                        inputText = ""
                                        focusManager.clearFocus()
                                        coroutineScope.launch {
                                            messages.add(AiChatMessage(sender = "user", message = query))
                                            isThinking = true
                                            listState.animateScrollToItem(messages.size - 1)

                                            val reply = LandAiService.askGemini(query, selectedCategory)
                                            messages.add(AiChatMessage(sender = "ai", message = reply))
                                            isThinking = false
                                            listState.animateScrollToItem(messages.size - 1)
                                        }
                                    }
                                }
                            ),
                            textStyle = TextStyle(color = Color.Black, fontSize = 13.5.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedBorderColor = LandGreenPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Digital Send SMS Button
                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isThinking) {
                                    val query = inputText.trim()
                                    inputText = ""
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        messages.add(AiChatMessage(sender = "user", message = query))
                                        isThinking = true
                                        listState.animateScrollToItem(messages.size - 1)

                                        val reply = LandAiService.askGemini(query, selectedCategory)
                                        messages.add(AiChatMessage(sender = "ai", message = reply))
                                        isThinking = false
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            },
                            containerColor = LandGreenPrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send SMS",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable Digital Floating AI Button with sleek circular styling
 */
@Composable
fun AiFloatingActionButton(
    category: AiCategory = AiCategory.GENERAL,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = LandGreenDark,
        border = BorderStroke(2.dp, GoldAccent),
        shadowElevation = 8.dp,
        modifier = Modifier.size(52.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ai_digital_bot),
                contentDescription = "Digital AI Assistant",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * TopBar AI Action Icon with glowing digital vector badge
 */
@Composable
fun AiTopBarAction(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, GoldAccent),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ai_digital_bot),
                    contentDescription = "Digital AI Assistant",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
