package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.TransactionEntity
import com.example.data.db.UserEntity
import com.example.data.models.AppTab
import com.example.data.models.RedeemReward
import com.example.data.models.RewardCategory
import com.example.data.models.TaskOffer
import com.example.data.models.TaskType
import com.example.data.models.TransactionItem
import com.example.data.models.TransactionType
import com.example.data.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// UI state for the app; includes showAllTasks to control the Tasks UI
data class RewardUiState(
    val currentTab: AppTab = AppTab.TASKS,
    val isLoading: Boolean = true,
    val user: UserProfile = UserProfile(),
    val tasks: List<TaskOffer> = emptyList(),
    val rewards: List<RedeemReward> = emptyList(),
    val transactions: List<TransactionItem> = emptyList(),
    val activeTaskOffer: TaskOffer? = null,
    val selectedRedeemReward: RedeemReward? = null,
    val isRedeemDialogVisible: Boolean = false,
    val isTaskDialogVisible: Boolean = false,
    val isPostbackSimulating: Boolean = false,
    val messageToast: String? = null,
    val recentRedeemedCode: String? = null,
    // NEW flag to control the TasksScreen "View All" behavior
    val showAllTasks: Boolean = false
)

class RewardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.transactionDao()

    private val completedTaskIds = mutableSetOf<String>()

    private val masterTaskPool = listOf(
        TaskOffer("t1", "Dream11 Fantasy Sports", TaskType.GAME, "Create team & enter free contest", 1200, "🏏", "Offerwall", isHot = true),
        TaskOffer("t2", "WinZO Games", TaskType.GAME, "Install app & play 1 match", 950, "🎮", "Offerwall", isHot = true),
        TaskOffer("t3", "Zupee Ludo Ninja", TaskType.GAME, "Play 2 matches of Ludo Ninja", 850, "🎲", "OfferToro", isHot = true),
        TaskOffer("t4", "Moj App", TaskType.APP, "Download & watch 3 video clips", 800, "📱", "Offerwall", isHot = true),
        TaskOffer("t5", "PhonePe UPI", TaskType.APP, "Install & register account", 750, "💸", "AdGate"),
        TaskOffer("t6", "Junglee Rummy", TaskType.GAME, "Register & play practice game", 900, "🃏", "Offerwall", isHot = true),
        TaskOffer("t7", "Flipkart Online Shopping", TaskType.APP, "Install & explore daily deals", 550, "🛍️", "AdGate"),
        TaskOffer("t8", "Josh Short Videos", TaskType.APP, "Install & watch 5 videos", 600, "🎬", "OfferToro"),
        TaskOffer("t9", "Tata Neu Super App", TaskType.APP, "Download & check NeuCoins", 700, "⚡", "Offerwall"),
        TaskOffer("t10", "Rush by Hike", TaskType.GAME, "Play Speed Ludo match", 800, "🏎️", "OfferToro"),
        TaskOffer("t11", "MPL Mobile Premier League", TaskType.GAME, "Install & play 1 casual game", 950, "🎯", "Offerwall"),
        TaskOffer("t12", "CashKaro Cashback", TaskType.APP, "Sign up & check cashback deals", 500, "💰", "AdGate"),
        TaskOffer("t13", "Temple Run 2", TaskType.GAME, "Reach score of 50,000", 600, "🏃", "Offerwall"),
        TaskOffer("t14", "Ludo King", TaskType.GAME, "Win 1 online multiplayer match", 500, "🎲", "OfferToro"),
        TaskOffer("t15", "Subway Surfers", TaskType.GAME, "Collect 200 coins in single run", 450, "🛹", "AdGate"),
        TaskOffer("t16", "ShareChat", TaskType.APP, "Install & scroll videos for 2 min", 400, "💬", "Offerwall"),
        TaskOffer("t17", "Call of Duty Mobile", TaskType.GAME, "Complete tutorial match", 1500, "🔫", "Offerwall", isHot = true),
        TaskOffer("t18", "Free Fire MAX", TaskType.GAME, "Install & log in with guest ID", 1100, "🔥", "Offerwall", isHot = true),
        TaskOffer("t19", "RummyCircle", TaskType.GAME, "Download & play 1 practice match", 1000, "♠️", "Offerwall", isHot = true),
        TaskOffer("t20", "Paytm Money", TaskType.APP, "Open free demat account / explore stocks", 1300, "📈", "AdGate", isHot = true),
        TaskOffer("t21", "Candy Crush Saga", TaskType.GAME, "Complete Level 15", 700, "🍬", "OfferToro"),
        TaskOffer("t22", "Spotify Music", TaskType.APP, "Install & listen to 1 song", 450, "🎧", "AdGate"),
        TaskOffer("t23", "Asphalt 9 Legends", TaskType.GAME, "Finish 2 career mode races", 1150, "🏎️", "Offerwall", isHot = true),
        TaskOffer("t24", "Unacademy Learning", TaskType.APP, "Install & watch 1 free live class", 650, "📚", "AdGate"),
        TaskOffer("t25", "Clash of Clans", TaskType.GAME, "Upgrade Town Hall to Level 3", 900, "⚔️", "OfferToro"),
        TaskOffer("t26", "A23 Rummy", TaskType.GAME, "Sign up & play 1 multiplayer table", 850, "🎴", "Offerwall"),
        TaskOffer("t27", "Cred UPI & Rewards", TaskType.APP, "Check credit score for free", 1250, "💳", "AdGate", isHot = true),
        TaskOffer("t28", "8 Ball Pool", TaskType.GAME, "Win 1 1v1 match in London arena", 600, "🎱", "OfferToro"),
        TaskOffer("t29", "JioCinema Sports", TaskType.APP, "Watch 3 min of live highlights", 500, "📺", "Offerwall"),
        TaskOffer("t30", "Ludo Supreme Gold", TaskType.GAME, "Play 1 quick 10-minute contest", 750, "🎲", "OfferToro"),
        TaskOffer("t31", "Garena Free Fire Survey", TaskType.SURVEY, "Answer 5 quick gamer feedback questions", 600, "📋", "Offerwall"),
        TaskOffer("t32", "Inshorts 60-Word News", TaskType.APP, "Install & read 5 trending news headlines", 350, "📰", "AdGate"),
        TaskOffer("t33", "Brawl Stars", TaskType.GAME, "Win 2 Gem Grab matches", 850, "🌟", "OfferToro", isHot = true),
        TaskOffer("t34", "Airtel Thanks App", TaskType.APP, "Install & claim free data reward", 700, "📡", "AdGate"),
        TaskOffer("t35", "Carrom Pool", TaskType.GAME, "Win 1 disc pool classic match", 550, "🎯", "OfferToro"),
        TaskOffer("t36", "Urban Company", TaskType.APP, "Download & browse home service offers", 450, "🧰", "Offerwall"),
        TaskOffer("t37", "Daily Gamer Opinion Poll", TaskType.SURVEY, "Complete 2-minute gaming habits survey", 500, "📊", "CPALead"),
        TaskOffer("t38", "Genshin Impact", TaskType.GAME, "Install & create Traveler avatar", 1600, "✨", "Offerwall", isHot = true),
        TaskOffer("t39", "My11Circle Fantasy", TaskType.GAME, "Form XI team & play practice contest", 1100, "🏏", "Offerwall", isHot = true),
        TaskOffer("t40", "Zomato Food Delivery", TaskType.APP, "Download & save delivery location", 400, "🍕", "AdGate"),
        TaskOffer("t41", "Swiggy Instamart", TaskType.APP, "Install & browse 10-minute grocery deals", 450, "🛒", "AdGate"),
        TaskOffer("t42", "PUBG Mobile Global", TaskType.GAME, "Survive 10 minutes in Livik map", 1200, "🎖️", "OfferToro", isHot = true),
        TaskOffer("t43", "Pocket FM Audio Series", TaskType.APP, "Listen to 2 episodes of audio stories", 650, "🎙️", "Offerwall"),
        TaskOffer("t44", "Kuku FM Audiobooks", TaskType.APP, "Install & play 1 free audiobook sample", 600, "📻", "AdGate"),
        TaskOffer("t45", "Fast & Furious Arcade", TaskType.GAME, "Win 1 nitro drift street race", 750, "🚗", "OfferToro"),
        TaskOffer("t46", "Tech & Gaming Brand Survey", TaskType.SURVEY, "Rate mobile game graphics & performance", 550, "📝", "Offerwall"),
        TaskOffer("t47", "Groww Stocks & Mutual Funds", TaskType.APP, "Install app & check top equity funds", 900, "💹", "AdGate", isHot = true),
        TaskOffer("t48", "Shadow Fight 3", TaskType.GAME, "Defeat Chapter 1 boss warrior", 1000, "🥷", "OfferToro"),
        TaskOffer("t49", "Nykaa Beauty & Fashion", TaskType.APP, "Install & explore trendy collections", 400, "💄", "AdGate"),
        TaskOffer("t50", "Real Cricket 24", TaskType.GAME, "Play 2 overs of Quick Match", 800, "⚾", "Offerwall"),
        TaskOffer("t51", "Meesho Shopping", TaskType.APP, "Install & view wholesale fashion catalog", 500, "📦", "AdGate"),
        TaskOffer("t52", "Hill Climb Racing 2", TaskType.GAME, "Drive 1000m in Countryside track", 650, "🚜", "OfferToro")
    )

    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    private val _displayedTasks = MutableStateFlow<List<TaskOffer>>(emptyList())
    val displayedTasks: StateFlow<List<TaskOffer>> = _displayedTasks.asStateFlow()

    init {
        getRandom20Tasks()
        loadInitialData()
        observeTransactions()
    }

    fun getRandom20Tasks(): List<TaskOffer> {
        val uncompleted = masterTaskPool.filter { it.id !in completedTaskIds }
        val picked = if (uncompleted.size >= 20) {
            uncompleted.shuffled().take(20)
        } else if (uncompleted.isNotEmpty()) {
            uncompleted.shuffled()
        } else {
            masterTaskPool.shuffled().take(20)
        }
        _displayedTasks.value = picked
        _uiState.update { it.copy(tasks = picked) }
        return picked
    }

    fun refreshTasks() {
        getRandom20Tasks()
        _uiState.update {
            it.copy(
                messageToast = "⚡ Task Pool Refreshed & Rotated!"
            )
        }
    }

    private fun loadInitialData() {
        val initial20Tasks = getRandom20Tasks()

        val initialRewards = listOf(
            RedeemReward("r1", "Google Play Redeem Code (₹10)", RewardCategory.GOOGLE_PLAY, "Instant Code Delivery", 1000, "💎", "Instant Delivery"),
            RedeemReward("r2", "Free Fire (110 Diamonds)", RewardCategory.FREE_FIRE, "Direct Player ID Top-Up", 1000, "🔥", "Instant (1-5 min)"),
            RedeemReward("r3", "BGMI UC (60 UC)", RewardCategory.BGMI, "Character ID Top-Up", 1000, "🔫", "Instant (1-5 min)"),
            RedeemReward("r4", "Paytm Cash (₹50)", RewardCategory.PAYTM, "Direct Bank UPI / Wallet", 4800, "💰", "Within 2 Hours"),
            RedeemReward("r5", "Google Play Code (₹50)", RewardCategory.GOOGLE_PLAY, "Instant Voucher Code", 4800, "💎", "Instant Delivery"),
            RedeemReward("r6", "Free Fire (530 Diamonds)", RewardCategory.FREE_FIRE, "Player ID Top-Up", 4500, "🔥", "Instant (1-5 min)")
        )

        _uiState.update {
            it.copy(
                tasks = initial20Tasks,
                rewards = initialRewards
            )
        }

        // Load saved user state from Room if available
        viewModelScope.launch {
            try {
                delay(300) // Brief initial splash delay
                val savedUser = dao.getUserAccount(_uiState.value.user.uid)
                if (savedUser != null) {
                    _uiState.update {
                        it.copy(
                            user = it.user.copy(
                                coins = savedUser.coins,
                                withdrawalCoins = savedUser.withdrawalCoins,
                                name = savedUser.name,
                                email = savedUser.email
                            )
                        )
                    }
                } else {
                    saveUserToDb(_uiState.value.user)
                }
            } catch (e: Exception) {
                // Fallback gracefully to default initial UserProfile
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            try {
                dao.getAllTransactions().collect { entities ->
                    val items = entities.map { entity ->
                        if (entity.title.startsWith("Completed: ")) {
                            val title = entity.title.removePrefix("Completed: ").trim()
                            masterTaskPool.find { it.title.equals(title, ignoreCase = true) }?.let {
                                completedTaskIds.add(it.id)
                            }
                        }
                        TransactionItem(
                            id = entity.id,
                            title = entity.title,
                            type = try { TransactionType.valueOf(entity.type) } catch (e: Exception) { TransactionType.EARN_TASK },
                            coins = entity.coins,
                            redeemCode = entity.redeemCode,
                            timestamp = entity.timestamp,
                            status = entity.status
                        )
                    }
                    _uiState.update {
                        it.copy(transactions = items)
                    }
                    getRandom20Tasks()
                }
            } catch (e: Exception) {
                // Graceful fallback if transaction collection fails
            }
        }
    }

    // Select top-level tab and reset showAllTasks when switching away from TASKS
    fun selectTab(tab: AppTab) {
        _uiState.update { state ->
            state.copy(currentTab = tab, showAllTasks = if (tab == AppTab.TASKS) state.showAllTasks else false)
        }
    }

    // NEW: toggle the Tasks "View All" state
    fun toggleShowAllTasks() {
        _uiState.update { it.copy(showAllTasks = !it.showAllTasks) }
    }

    // NEW: explicitly show all tasks and switch to Tasks tab
    fun showAllTasks() {
        _uiState.update { it.copy(currentTab = AppTab.TASKS, showAllTasks = true) }
    }

    // NEW: close the All Tasks mode
    fun closeAllTasks() {
        _uiState.update { it.copy(showAllTasks = false) }
    }

    fun openTaskDialog(task: TaskOffer) {
        _uiState.update {
            it.copy(
                activeTaskOffer = task,
                isTaskDialogVisible = true
            )
        }
    }

    fun closeTaskDialog() {
        _uiState.update {
            it.copy(
                activeTaskOffer = null,
                isTaskDialogVisible = false
            )
        }
    }

    fun completeTask(task: TaskOffer) {
        closeTaskDialog()
        completedTaskIds.add(task.id)
        viewModelScope.launch {
            _uiState.update { it.copy(isPostbackSimulating = true) }
            delay(1200) // Simulate S2S HTTP postback delay

            val updatedCoins = _uiState.value.user.coins + task.coinReward
            val updatedUser = _uiState.value.user.copy(coins = updatedCoins)

            val rotated = getRandom20Tasks()

            // Update state
            _uiState.update {
                it.copy(
                    user = updatedUser,
                    tasks = rotated,
                    isPostbackSimulating = false,
                    messageToast = "🎉 Postback Received! +${task.coinReward} Coins credited for ${task.title}!"
                )
            }

            // Save transaction to DB
            val tx = TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                title = "Completed: ${task.title}",
                type = TransactionType.EARN_TASK.name,
                coins = task.coinReward,
                redeemCode = null,
                timestamp = getCurrentTimestamp(),
                status = "CREDITED"
            )
            dao.insertTransaction(tx)
            saveUserToDb(updatedUser)
        }
    }

    fun triggerS2SPostbackSimulation(customCoins: Int = 500, offerTitle: String = "Offerwall Special S2S Offer") {
        viewModelScope.launch {
            _uiState.update { it.copy(isPostbackSimulating = true) }
            delay(1000)

            val updatedCoins = _uiState.value.user.coins + customCoins
            val updatedUser = _uiState.value.user.copy(coins = updatedCoins)

            _uiState.update {
                it.copy(
                    user = updatedUser,
                    isPostbackSimulating = false,
                    messageToast = "⚡ S2S HTTP Postback Validated! +$customCoins Coins added to subId: ${_uiState.value.user.uid}"
                )
            }

            val tx = TransactionEntity(
                id = "s2s_" + UUID.randomUUID().toString().take(8),
                title = "S2S Postback: $offerTitle",
                type = TransactionType.S2S_POSTBACK.name,
                coins = customCoins,
                redeemCode = null,
                timestamp = getCurrentTimestamp(),
                status = "SUCCESS"
            )
            dao.insertTransaction(tx)
            saveUserToDb(updatedUser)
        }
    }

    fun openRedeemDialog(reward: RedeemReward) {
        _uiState.update {
            it.copy(
                selectedRedeemReward = reward,
                isRedeemDialogVisible = true
            )
        }
    }

    fun unlockMonthlySalary() {
        val cal = java.util.Calendar.getInstance()
        val dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val availableCoins = _uiState.value.user.coins

        if (availableCoins <= 0) {
            _uiState.update { it.copy(messageToast = "ℹ️ No Available Coins to transfer to Withdrawal Balance.") }
            return
        }

        if (dayOfMonth < 20) {
            _uiState.update { it.copy(messageToast = "🔒 Monthly Salary unlocks on the 20th! Next Payout: 20th of this month.") }
            return
        }

        val transferred = availableCoins
        val updatedUser = _uiState.value.user.copy(
            coins = 0,
            withdrawalCoins = _uiState.value.user.withdrawalCoins + transferred
        )

        _uiState.update {
            it.copy(
                user = updatedUser,
                messageToast = "🎉 Monthly Salary Unlocked! +$transferred Coins moved to Withdrawal Balance!"
            )
        }

        viewModelScope.launch {
            val tx = TransactionEntity(
                id = "sal_" + UUID.randomUUID().toString().take(8),
                title = "Monthly Salary Transfer (20th Payout)",
                type = TransactionType.EARN_TASK.name,
                coins = transferred,
                redeemCode = "SALARY-UNLOCKED-20TH",
                timestamp = getCurrentTimestamp(),
                status = "UNLOCKED"
            )
            dao.insertTransaction(tx)
            saveUserToDb(updatedUser)
        }
    }

    fun redeemCustomVault(rupees: Int, requiredCoins: Int) {
        if (_uiState.value.user.withdrawalCoins < requiredCoins) {
            _uiState.update { it.copy(messageToast = "❌ Insufficient Withdrawal Balance! Need $requiredCoins coins in Withdrawal Balance (Unlocked on 20th).") }
            return
        }

        val updatedWithdrawal = _uiState.value.user.withdrawalCoins - requiredCoins
        val updatedUser = _uiState.value.user.copy(withdrawalCoins = updatedWithdrawal)
        val pendingCode = "REQ-PAYTM-" + UUID.randomUUID().toString().take(6).uppercase()

        _uiState.update {
            it.copy(
                user = updatedUser,
                messageToast = "⏳ Custom ₹$rupees Redeem Submitted! Status: PENDING (-$requiredCoins Withdrawal Coins)"
            )
        }

        viewModelScope.launch {
            val tx = TransactionEntity(
                id = "cust_" + UUID.randomUUID().toString().take(8),
                title = "Custom ₹$rupees Paytm/UPI Voucher",
                type = TransactionType.REDEEM_CODE.name,
                coins = -requiredCoins,
                redeemCode = pendingCode,
                timestamp = getCurrentTimestamp(),
                status = "PENDING"
            )
            dao.insertTransaction(tx)
            saveUserToDb(updatedUser)
        }
    }

    fun closeRedeemDialog() {
        _uiState.update {
            it.copy(
                selectedRedeemReward = null,
                isRedeemDialogVisible = false
            )
        }
    }

    fun confirmRedeem(reward: RedeemReward, targetInput: String) {
        if (_uiState.value.user.withdrawalCoins < reward.coinsRequired) {
            _uiState.update { it.copy(messageToast = "❌ Insufficient Withdrawal Balance! Need ${reward.coinsRequired} unlocked coins (Unlocked on 20th).") }
            return
        }

        closeRedeemDialog()

        val generatedCode = when (reward.category) {
            RewardCategory.GOOGLE_PLAY -> "GP-" + UUID.randomUUID().toString().take(8).uppercase() + "-" + UUID.randomUUID().toString().take(4).uppercase()
            RewardCategory.FREE_FIRE -> "FF-DIAMOND-" + (targetInput.ifBlank { "UID892147" })
            RewardCategory.BGMI -> "BGMI-UC-" + (targetInput.ifBlank { "CHARACTER9912" })
            RewardCategory.PAYTM -> "PAYTM-UPI-" + (targetInput.ifBlank { "PAYTM9821" })
        }

        val updatedWithdrawal = _uiState.value.user.withdrawalCoins - reward.coinsRequired
        val updatedUser = _uiState.value.user.copy(withdrawalCoins = updatedWithdrawal)

        _uiState.update {
            it.copy(
                user = updatedUser,
                recentRedeemedCode = generatedCode,
                messageToast = "✅ Redeemed ${reward.title}! Code: $generatedCode"
            )
        }

        viewModelScope.launch {
            val tx = TransactionEntity(
                id = "red_" + UUID.randomUUID().toString().take(8),
                title = reward.title,
                type = TransactionType.REDEEM_CODE.name,
                coins = -reward.coinsRequired,
                redeemCode = generatedCode,
                timestamp = getCurrentTimestamp(),
                status = "DELIVERED"
            )
            dao.insertTransaction(tx)
            saveUserToDb(updatedUser)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(messageToast = null) }
    }

    fun clearRecentCode() {
        _uiState.update { it.copy(recentRedeemedCode = null) }
    }

    private suspend fun saveUserToDb(user: UserProfile) {
        try {
            dao.saveUserAccount(
                UserEntity(
                    uid = user.uid,
                    name = user.name,
                    email = user.email,
                    coins = user.coins,
                    withdrawalCoins = user.withdrawalCoins,
                    referralCode = user.referralCode
                )
            )
        } catch (e: Exception) {
            // Silently log or ignore DB persistence error
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
