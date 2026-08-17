package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import androidx.compose.foundation.layout.height
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

@Composable
fun HeaderSection(
    user: UserProfile,
    onUnlockSalary: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
    val isSalaryUnlockAvailable = currentDay >= 20 && user.coins > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xDA06060B))
            .border(width = 1.dp, color = Color(0x14FFFFFF))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CENTRAL HUB",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 2.sp
                )
                Row {
                    Text(
                        text = "GAMEREWARD ",
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "HUB",
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        color = NeonPink
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0x0DFFFFFF))
                    .border(width = 1.dp, color = Color(0x1AFFFFFF), shape = RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonViolet, NeonCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "AV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = user.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DUAL COIN BALANCE CARDS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: Available Coins (Active Tasks)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1A00D1FF))
                    .border(width = 1.dp, color = NeonCyan.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AVAILABLE COINS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "%,d".format(user.coins),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonLime
                        )
                    }
                }
            }

            // Card 2: Withdrawal Balance (Unlocked)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1AFF2D95))
                    .border(width = 1.dp, color = NeonPink.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WITHDRAWAL BAL",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonPink,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "%,d".format(user.withdrawalCoins),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isSalaryUnlockAvailable && onUnlockSalary != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonLime)
                                .padding(vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡ UNLOCK SALARY COINS",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    } else {
                        Text(
                            text = "🗓️ Next Salary Payout: 20th of this month",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xB3FFFFFF)
                        )
                    }
                }
            }
        }
    }
}

