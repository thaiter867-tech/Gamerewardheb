package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OFFERWALL_API_KEY
import com.example.data.models.AppTab
import com.example.ui.components.CyberBottomNav
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HeaderSection
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RedeemConfirmDialog
import com.example.ui.screens.TaskDetailDialog
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.launchCustomTab
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.GameRewardHubTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.viewmodel.RewardViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RewardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContent {
                GameRewardHubTheme {
                    GameRewardHubApp(viewModel = viewModel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun GameRewardHubApp(viewModel: RewardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.messageToast) {
        uiState.messageToast?.let { msg ->
            try {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Ignore toast display errors
            } finally {
                viewModel.clearToast()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CyberBottomNav(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x1EFF2D95), Color.Transparent),
                        radius = 800f
                    )
                )
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "INITIALIZING GAMEREWARD HUB...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connecting to Secure Firebase Server",
                            fontSize = 10.sp,
                            color = Color(0x80FFFFFF)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    HeaderSection(
                        user = uiState.user,
                        onUnlockSalary = { viewModel.unlockMonthlySalary() },
                        modifier = Modifier.statusBarsPadding()
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        when (uiState.currentTab) {
                            AppTab.TASKS -> TasksScreen(
                                tasks = uiState.tasks,
                                rewards = uiState.rewards,
                                userCoins = uiState.user.coins,
                                userWithdrawalCoins = uiState.user.withdrawalCoins,
                                onTaskClick = { task ->
                                    if (OFFERWALL_API_KEY.isEmpty()) {
                                        // Demo Mode: Show the Demo Notice Dialog before opening Chrome Custom Tab
                                        viewModel.openTaskDialog(task)
                                    } else {
                                        // Live Mode: Automatically hide/bypass the Demo Dialog completely and directly launch Chrome Custom Tab
                                        val offerwallUrl = "https://offerwall.ai/APP_9821?subId=${uiState.user.uid}&taskId=${task.id}"
                                        launchCustomTab(context, offerwallUrl)
                                        viewModel.completeTask(task)
                                    }
                                },
                                onRedeemClick = { viewModel.openRedeemDialog(it) },
                                onCustomRedeem = { rupees, coins -> viewModel.redeemCustomVault(rupees, coins) },
                                onUnlockSalary = { viewModel.unlockMonthlySalary() },
                                onRefreshTasks = { viewModel.refreshTasks() },
                                // wire TasksScreen's View All to ViewModel state
                                showAll = uiState.showAllTasks,
                                onToggleShowAll = { viewModel.toggleShowAllTasks() }
                            )
                            AppTab.DASHBOARD -> DashboardScreen(
                                user = uiState.user
                            )
                            AppTab.TRANSACTIONS -> TransactionsScreen(
                                transactions = uiState.transactions
                            )
                            AppTab.PROFILE -> ProfileScreen(
                                user = uiState.user
                            )
                        }
                    }
                }
            }

            // Dialogs
            if (uiState.isTaskDialogVisible && uiState.activeTaskOffer != null) {
                if (OFFERWALL_API_KEY.isNotEmpty()) {
                    val task = uiState.activeTaskOffer!!
                    val offerwallUrl = "https://offerwall.ai/APP_9821?subId=${uiState.user.uid}&taskId=${task.id}"
                    launchCustomTab(context, offerwallUrl)
                    viewModel.completeTask(task)
                } else {
                    TaskDetailDialog(
                        task = uiState.activeTaskOffer!!,
                        userUid = uiState.user.uid,
                        onDismiss = { viewModel.closeTaskDialog() },
                        onCompleteTask = { viewModel.completeTask(it) }
                    )
                }
            }

            if (uiState.isRedeemDialogVisible && uiState.selectedRedeemReward != null) {
                RedeemConfirmDialog(
                    reward = uiState.selectedRedeemReward!!,
                    userCoins = uiState.user.withdrawalCoins,
                    onDismiss = { viewModel.closeRedeemDialog() },
                    onConfirm = { reward, targetInput ->
                        viewModel.confirmRedeem(reward, targetInput)
                    }
                )
            }
        }
    }
}
