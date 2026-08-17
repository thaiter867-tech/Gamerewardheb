package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.components.GlassmorphismCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ProfileScreen(
    user: UserProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTermsDialog by remember { mutableStateOf(false) }

    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
    }

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
                text = "👤 ACCOUNT & AUTHENTICATION",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonCyan
            )
        }

        item {
            GlassmorphismCard(glowGradient = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 32.sp,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x3300D1FF))
                            .border(width = 2.dp, color = NeonCyan, shape = CircleShape)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = user.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                        Text(
                            text = user.email,
                            fontSize = 13.sp,
                            color = CyberTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                color = NeonLime,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0x337EFFA3), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Authenticated",
                                fontSize = 11.sp,
                                color = NeonLime,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            // SubId & Integration parameters
            GlassmorphismCard(borderColor = CyberCardBorder) {
                Column {
                    Text(text = "🔥 OFFERWALL SUBID (FIREBASE UID)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = user.uid, fontSize = 13.sp, color = CyberTextPrimary, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SubId", user.uid)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied Firebase subId!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300D1FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "📋 COPY SUBID LINK FOR OFFERWALL", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Referral program
            GlassmorphismCard(borderColor = Color(0x407EFFA3)) {
                Column {
                    Text(text = "🎁 REFER & EARN 500 COINS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonLime)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Share your referral code with gamers. When they sign up, both receive 500 coins!",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x227EFFA3))
                            .border(width = 1.dp, color = NeonLime, shape = RoundedCornerShape(10.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Referral", user.referralCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied referral code!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "CODE: ${user.referralCode}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "TAP TO COPY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = NeonLime)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🎮 Join GameReward Hub and earn free Google Play, Free Fire, and BGMI rewards! Use my referral code: ${user.referralCode} to get 500 bonus coins!\nDownload now: https://gamerewardhub.app/invite?ref=${user.referralCode}"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Referral Code via")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📲 SHARE CODE VIA WHATSAPP / APPS",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            // Terms & Conditions and Policy Section
            GlassmorphismCard(borderColor = Color(0x3300D1FF)) {
                Column {
                    Text(text = "📜 TERMS, PRIVACY & WITHDRAWAL POLICY", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Review app usage rules, fair play guidelines, privacy terms, and the monthly 20th withdrawal payout schedule.",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showTermsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300D1FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📋 VIEW TERMS & CONDITIONS",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
