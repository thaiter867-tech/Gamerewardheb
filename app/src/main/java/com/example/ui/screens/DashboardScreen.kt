package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.components.GlassmorphismCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

@Composable
fun DashboardScreen(
    user: UserProfile,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📊 GAMER DASHBOARD & STATS",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonCyan
            )
        }

        item {
            // Level & XP Card
            GlassmorphismCard(glowGradient = true) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "CURRENT LEVEL", fontSize = 10.sp, color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                            Text(text = "Level 4 • Cyber Earner", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        }
                        Text(text = "⚡ 1,250 / 2,000 XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonLime)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { 1250f / 2000f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NeonCyan,
                        trackColor = Color(0x3300D1FF)
                    )
                }
            }
        }

        item {
            // Serverless Postback S2S Integration Status
            GlassmorphismCard(borderColor = NeonPink) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌐 S2S POSTBACK ENDPOINT", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vercel Serverless Function (/api/postback.js) active & secured.",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SubId (Firebase UID): ${user.uid}\nSecurity Status: Protected (Secret Key strictly stored in Server Environment)",
                        fontSize = 11.sp,
                        color = NeonLime,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x227EFFA3), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }
        }

        item {
            // Leaderboard section
            Text(text = "🏆 TOP EARNER LEADERBOARD", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonGold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LeaderboardRow(rank = "1", name = "CyberKnight_99", coins = "24,500", badge = "🥇")
                LeaderboardRow(rank = "2", name = "NeonGamer_Pro", coins = "19,800", badge = "🥈")
                LeaderboardRow(rank = "3", name = "Alex Vance (You)", coins = "%,d".format(user.coins), badge = "🥉", isUser = true)
                LeaderboardRow(rank = "4", name = "Vortex_Hunter", coins = "11,200", badge = "⭐")
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: String,
    name: String,
    coins: String,
    badge: String,
    isUser: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUser) Color(0x3300D1FF) else Color(0x22121020))
            .border(
                width = 1.dp,
                color = if (isUser) NeonCyan else CyberCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = badge, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
                    color = CyberTextPrimary
                )
            }

            Text(
                text = "🪙 $coins",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonGold
            )
        }
    }
}
