package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.R
import com.example.data.models.RedeemReward
import com.example.data.models.RewardCategory
import com.example.data.models.TaskOffer
import com.example.data.models.TaskType
import com.example.ui.components.GlassmorphismCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink

@Composable
fun TasksScreen(
    tasks: List<TaskOffer>,
    rewards: List<RedeemReward>,
    userCoins: Int = 1250,
    userWithdrawalCoins: Int = 850,
    onTaskClick: (TaskOffer) -> Unit,
    onRedeemClick: (RedeemReward) -> Unit,
    onCustomRedeem: ((Int, Int) -> Unit)? = null,
    onUnlockSalary: (() -> Unit)? = null,
    onRefreshTasks: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    // NEW: controlled by ViewModel via uiState.showAllTasks
    showAll: Boolean = false,
    // NEW: callback to toggle showAll (wire up to viewModel.toggleShowAllTasks())
    onToggleShowAll: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<TaskType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTasks = remember(tasks, selectedFilter, searchQuery) {
        tasks.filter { task ->
            val matchesCategory = selectedFilter == null || task.category == selectedFilter
            val matchesSearch = searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true) ||
                    task.provider.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val displayedTasks = if (showAll || searchQuery.isNotBlank() || selectedFilter != null) {
        filteredTasks
    } else {
        filteredTasks.take(5)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Hero Cyberpunk Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(width = 1.dp, color = Color(0x3300D1FF), shape = RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cyber_hero_1785941260183),
                    contentDescription = "Cyber Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD06060B))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "⚡ OFFERWALL BOOST ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonLime,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Earn 2x Coins on Top Missions",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search 20+ games & apps...", color = CyberTextSecondary, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { searchQuery = "" }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else null,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x1100D1FF),
                    unfocusedContainerColor = Color(0x0DFFFFFF),
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0x3300D1FF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Category filter tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        title = "All Tasks (${tasks.size})",
                        isSelected = selectedFilter == null,
                        onClick = { selectedFilter = null }
                    )
                }
                item {
                    FilterChip(
                        title = "🎮 Games",
                        isSelected = selectedFilter == TaskType.GAME,
                        onClick = { selectedFilter = TaskType.GAME }
                    )
                }
                item {
                    FilterChip(
                        title = "📱 Apps",
                        isSelected = selectedFilter == TaskType.APP,
                        onClick = { selectedFilter = TaskType.APP }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showAll || searchQuery.isNotBlank() || selectedFilter != null) {
                            "ALL MISSIONS (${filteredTasks.size})"
                        } else {
                            "FEATURED MISSIONS (${displayedTasks.size}/${filteredTasks.size})"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xB3FFFFFF),
                        letterSpacing = 1.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onRefreshTasks != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2200D1FF))
                                .border(width = 1.dp, color = NeonCyan.copy(alpha = 0.5f), shape = RoundedCornerShape(6.dp))
                                .clickable { onRefreshTasks() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔄 Shuffle",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (showAll || searchQuery.isNotBlank() || selectedFilter != null) "Show Featured (5)" else "View All (${filteredTasks.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onToggleShowAll() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        if (displayedTasks.isEmpty()) {
            item {
                GlassmorphismCard(borderColor = Color(0x33FF007A)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔍 NO MISSIONS FOUND", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "No games or apps match your search or filter criteria.", fontSize = 11.sp, color = CyberTextSecondary)
                    }
                }
            }
        } else {
            items(displayedTasks, key = { it.id }) { task ->
                TaskCard(task = task, onClick = { onTaskClick(task) })
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NeonPink)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FLASH REDEMPTION & CUSTOM VAULT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xB3FFFFFF),
                    letterSpacing = 1.5.sp
                )
            }
        }

        item {
            var customRupeesText by remember { mutableStateOf("10") }
            val customRupees = customRupeesText.toIntOrNull() ?: 0
            val requiredCoins = customRupees * 100

            GlassmorphismCard(
                borderColor = NeonCyan.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CUSTOM VAULT REDEEM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Custom Voucher / Paytm Cash",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(text = "⚡", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customRupeesText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 5) {
                                customRupeesText = input
                            }
                        },
                        label = { Text("Enter Amount (₹)", color = CyberTextSecondary, fontSize = 12.sp) },
                        placeholder = { Text("e.g. 10, 20, 50", color = CyberTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0x1100D1FF),
                            unfocusedContainerColor = Color(0x0DFFFFFF),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0x3300D1FF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val hasEnoughBalance = customRupees > 0 && userWithdrawalCoins >= requiredCoins

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "REQUIRED COINS (1 ₹ = 100 COINS)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextSecondary
                            )
                            Text(
                                text = "🪙 $requiredCoins Withdrawal Coins",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (hasEnoughBalance || customRupees == 0) NeonLime else NeonPink
                            )
                            if (customRupees > 0 && userWithdrawalCoins < requiredCoins) {
                                Text(
                                    text = "⚠️ Short by ${requiredCoins - userWithdrawalCoins} Withdrawal Coins (Payout on 20th)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (hasEnoughBalance) {
                                    if (onCustomRedeem != null) {
                                        onCustomRedeem(customRupees, requiredCoins)
                                    } else {
                                        val customReward = RedeemReward(
                                            id = "custom_${System.currentTimeMillis()}",
                                            title = "Custom ₹$customRupees Reward",
                                            category = RewardCategory.PAYTM,
                                            subtitle = "Custom Amount Voucher",
                                            coinsRequired = requiredCoins,
                                            iconEmoji = "⚡"
                                        )
                                        onRedeemClick(customReward)
                                    }
                                }
                            },
                            enabled = hasEnoughBalance,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                disabledContainerColor = Color(0x33FF2D95),
                                disabledContentColor = Color(0x66FFFFFF)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "REDEEM NOW",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        items(rewards, key = { it.id }) { reward ->
            WithdrawRewardCard(reward = reward, onRedeem = { onRedeemClick(reward) })
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
