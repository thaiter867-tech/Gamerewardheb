package com.example.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGold

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier
) {
    val animatedCoins by animateIntAsState(
        targetValue = coins,
        animationSpec = tween(durationMillis = 600),
        label = "coinCount"
    )

    Row(
        modifier = modifier
            .background(Color(0x22FFD700), shape = RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = Color(0x66FFD700), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🪙", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "%,d Coins".format(animatedCoins),
            color = NeonGold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
