package com.example.data.models

enum class AppTab {
    TASKS, DASHBOARD, TRANSACTIONS, PROFILE
}

data class UserProfile(
    val uid: String = "sub_user_8921a",
    val name: String = "Alex Vance",
    val email: String = "alex.vance@gamer.io",
    val coins: Int = 1250,
    val withdrawalCoins: Int = 0,
    val avatarUrl: String = "https://api.dicebear.com/7.x/bottts/svg?seed=CyberUser",
    val isLoggedInWithGoogle: Boolean = true,
    val referralCode: String = "CYBER2026"
)

enum class TaskType {
    GAME, APP, SURVEY
}

data class TaskOffer(
    val id: String,
    val title: String,
    val category: TaskType,
    val description: String,
    val coinReward: Int,
    val iconEmoji: String,
    val provider: String = "Offerwall",
    val isHot: Boolean = false,
    val isCompleted: Boolean = false
)

enum class RewardCategory {
    GOOGLE_PLAY, FREE_FIRE, BGMI, PAYTM
}

data class RedeemReward(
    val id: String,
    val title: String,
    val category: RewardCategory,
    val subtitle: String,
    val coinsRequired: Int,
    val iconEmoji: String,
    val estimatedDelivery: String = "Instant Delivery"
)

enum class TransactionType {
    EARN_TASK, REDEEM_CODE, S2S_POSTBACK
}

data class TransactionItem(
    val id: String,
    val title: String,
    val type: TransactionType,
    val coins: Int,
    val redeemCode: String? = null,
    val timestamp: String,
    val status: String = "SUCCESS"
)
