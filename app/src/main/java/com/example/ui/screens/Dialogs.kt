package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.RedeemReward
import com.example.data.models.RewardCategory
import com.example.data.models.TaskOffer
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet

@Composable
fun TaskDetailDialog(
    task: TaskOffer,
    userUid: String,
    onDismiss: () -> Unit,
    onCompleteTask: (TaskOffer) -> Unit
) {
    val context = LocalContext.current
    val offerwallUrl = "https://offerwall.ai/APP_9821?subId=$userUid&taskId=${task.id}"

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberBackground, shape = RoundedCornerShape(20.dp))
                .border(width = 1.dp, brush = Brush.linearGradient(listOf(NeonCyan, NeonPink)), shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = task.iconEmoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                    Text(text = "Provider: ${task.provider} • +${task.coinReward} Coins", fontSize = 12.sp, color = NeonLime)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cyberpunk Demo Warning Box (Shown only in Demo Mode when OFFERWALL_API_KEY is empty)
            if (com.example.data.OFFERWALL_API_KEY.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x22FFB800), shape = RoundedCornerShape(12.dp))
                        .border(width = 1.dp, color = Color(0x99FFB800), shape = RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "⚠️ DEMO MODE NOTICE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Abhi Offerwall API connect nahi hai. Is task se abhi aapki earning/coins nahi badhenge. Jab Offerwall ki API key lagegi tabhi se real earning start hogi aur demo balance reset ho jayega.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            lineHeight = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(text = "Task Instructions:", fontWeight = FontWeight.SemiBold, color = NeonCyan, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, fontSize = 13.sp, color = CyberTextPrimary)

            Spacer(modifier = Modifier.height(20.dp))
            Row {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel", color = CyberTextSecondary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        launchCustomTab(context, offerwallUrl)
                        onCompleteTask(task)
                    },
                    modifier = Modifier.weight(1.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to Download", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

fun launchCustomTab(context: Context, url: String) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(false)
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        try {
            val directIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(directIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Opening Redirect Link...", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun RedeemConfirmDialog(
    reward: RedeemReward,
    userCoins: Int,
    onDismiss: () -> Unit,
    onConfirm: (RedeemReward, String) -> Unit
) {
    var playerInput by remember { mutableStateOf("") }
    val labelText = when (reward.category) {
        RewardCategory.GOOGLE_PLAY -> "Email / Notification ID (Optional)"
        RewardCategory.FREE_FIRE -> "Free Fire Player ID (e.g., 98214710)"
        RewardCategory.BGMI -> "BGMI Character ID (e.g., 51928401)"
        RewardCategory.PAYTM -> "Paytm Mobile Number / UPI ID"
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberBackground, shape = RoundedCornerShape(20.dp))
                .border(width = 1.dp, color = NeonPink, shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Text(text = "Confirm Redemption", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonPink)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = reward.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CyberTextPrimary)
            Text(text = "Cost: ${reward.coinsRequired} Coins • Delivery: ${reward.estimatedDelivery}", fontSize = 13.sp, color = CyberTextSecondary)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = playerInput,
                onValueChange = { playerInput = it },
                label = { Text(labelText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberTextSecondary,
                    focusedLabelColor = NeonCyan,
                    unfocusedLabelColor = CyberTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel", color = CyberTextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onConfirm(reward, playerInput) },
                    enabled = userCoins >= reward.coinsRequired,
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (userCoins >= reward.coinsRequired) "Confirm Redeem" else "Insufficient Coins", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TermsAndConditionsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberBackground, shape = RoundedCornerShape(20.dp))
                .border(width = 1.dp, color = NeonCyan, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📜", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TERMS & CONDITIONS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                    Text(
                        text = "GameReward Hub App Usage & Policy",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .height(320.dp)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0x1100D1FF), shape = RoundedCornerShape(12.dp))
                    .border(width = 1.dp, color = Color(0x2200D1FF), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. MONTHLY WITHDRAWAL POLICY 🗓️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonLime
                )
                Text(
                    text = "• Coins earned from active gaming & app tasks accumulate in your Available Coins balance.\n• Coins are automatically moved to your Withdrawal Balance and unlocked for cashout on the 20th of every month.\n• Once unlocked on the 20th, instant withdrawals for Paytm, Google Play, Free Fire, and BGMI are enabled.",
                    fontSize = 11.sp,
                    color = CyberTextPrimary,
                    lineHeight = 16.sp
                )

                Text(
                    text = "2. FAIR PLAY & ANTI-CHEAT RULES 🛡️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonPink
                )
                Text(
                    text = "• Only one account per physical mobile device is allowed.\n• Using VPNs, proxies, automated bots, or Android emulators to manipulate offerwall postbacks is strictly prohibited.\n• Fraudulent activity will lead to immediate account ban and forfeiture of accumulated coin balance.",
                    fontSize = 11.sp,
                    color = CyberTextPrimary,
                    lineHeight = 16.sp
                )

                Text(
                    text = "3. S2S POSTBACK VERIFICATION ⚡",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan
                )
                Text(
                    text = "• Tasks completed on Offerwall, OfferToro, and AdGate are verified using server-to-server (S2S) postback webhooks.\n• Coins are credited automatically once the advertiser confirms genuine app installation or target achievement.",
                    fontSize = 11.sp,
                    color = CyberTextPrimary,
                    lineHeight = 16.sp
                )

                Text(
                    text = "4. PRIVACY & DATA SECURITY 🔒",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "• We use Google OAuth and Firebase UID for account authentication.\n• Your personal data is never sold to third parties and is solely used for reward processing and leaderboard rankings.",
                    fontSize = 11.sp,
                    color = CyberTextPrimary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "I UNDERSTAND & ACCEPT",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}
