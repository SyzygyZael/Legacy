package com.kevinnesbitt.legacysecurefreelancercrm

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.ui.theme.LegacySecureFreelancerCRMTheme
import com.kevinnesbitt.legacysecurefreelancercrm.screens.*
import com.kevinnesbitt.legacysecurefreelancercrm.util.scheduleDueDateChecker
import com.kevinnesbitt.legacysecurefreelancercrm.util.BillingHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleDueDateChecker(applicationContext)
        enableEdgeToEdge()

        val billingHelper = BillingHelper(applicationContext)

        setContent {
            LegacySecureFreelancerCRMTheme {
                val isSubscribed by billingHelper.isSubscribed.collectAsState()
                // val isSubscribed = false

                // Inside your main Composable:
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        // You can send notifications!

                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val windowInfo = LocalWindowInfo.current
                val screenWidth = windowInfo.containerDpSize.width

                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )

                var currentScreen by remember { mutableStateOf("home") }

                var padding by remember { mutableStateOf(8.dp) }

                when(isSubscribed) {
                    null -> {
                        // Show a blank screen or loading spinner while checking Play Store
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    true -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                Row(
                                    modifier = Modifier
                                        .size(width = screenWidth, height = 55.dp)
                                        .background(color = Color.White)
                                ) {  }
                            },
                            bottomBar = {
                                Card(
                                    elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                                    shape = RectangleShape
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .size(width = screenWidth, height = 55.dp)
                                            .background(color = Color.White)
                                            .navigationBarsPadding(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        val verticalBrush = Brush.linearGradient(
                                            colors = listOf(Color.Cyan, Color.White),
                                            start = Offset(x = 0f, y = 0f), // Starts at top-left
                                            end = Offset(x = 0f, y = Float.POSITIVE_INFINITY) // Ends at bottom-left
                                        )
                                        val offVerticalBrush = Brush.linearGradient(
                                            colors = listOf(Color.White, Color.White),
                                            start = Offset(x = 0f, y = 0f), // Starts at top-left
                                            end = Offset(x = 0f, y = Float.POSITIVE_INFINITY) // Ends at bottom-left
                                        )

                                        Column(
                                            modifier = Modifier
                                                .size(height = 55.dp, width = 70.dp)
                                                .background(
                                                    brush = if (currentScreen == "home") verticalBrush else offVerticalBrush,
                                                    alpha = 0.4f
                                                )
                                                .clickable(
                                                    onClick = {
                                                        currentScreen = "home"
                                                        navController.navigate("home")
                                                    }
                                                ),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (currentScreen == "home") {
                                                HorizontalDivider(color = Color.Cyan, thickness = 2.dp)
                                            }

                                            Icon(
                                                imageVector = Icons.Default.Dashboard, // ✨ Matches your document file image asset
                                                contentDescription = "Home",
                                                modifier = Modifier.size(35.dp),
                                                tint = Color.Black // Automatically changes to your theme's font color state
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .size(height = 55.dp, width = 70.dp)
                                                .background(
                                                    brush = if (currentScreen == "clients") verticalBrush else offVerticalBrush,
                                                    alpha = 0.4f
                                                )
                                                .clickable(
                                                    onClick = {
                                                        currentScreen = "clients"
                                                        navController.navigate("clients")
                                                    }
                                                ),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (currentScreen == "clients") {
                                                HorizontalDivider(color = Color.Cyan, thickness = 2.dp)
                                            }

                                            Icon(
                                                imageVector = Icons.Default.People, // ✨ Matches your document file image asset
                                                contentDescription = "Clients",
                                                modifier = Modifier.size(35.dp),
                                                tint = Color.Black // Automatically changes to your theme's font color state
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .size(height = 55.dp, width = 70.dp)
                                                .background(
                                                    brush = if (currentScreen == "settings") verticalBrush else offVerticalBrush,
                                                    alpha = 0.4f
                                                )
                                                .clickable(
                                                    onClick = {
                                                        currentScreen = "settings"
                                                        navController.navigate("settings")
                                                    }
                                                ),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (currentScreen == "settings") {
                                                HorizontalDivider(color = Color.Cyan, thickness = 2.dp)
                                            }

                                            Icon(
                                                imageVector = Icons.Default.Settings, // ✨ Matches your document file image asset
                                                contentDescription = "Settings",
                                                modifier = Modifier.size(35.dp),
                                                tint = Color.Black // Automatically changes to your theme's font color state
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            padding = innerPadding.calculateRightPadding(LayoutDirection.Rtl)

                            NavHost(
                                navController = navController,
                                startDestination = "home"
                            ) {
                                composable("home") {
                                    HomeScreen(viewModel, innerPadding)
                                }

                                composable("clients") {
                                    ClientsScreen(viewModel, navController, innerPadding)
                                }

                                composable(
                                    route = "client/{clientId}",
                                    arguments = listOf(
                                        navArgument("clientId") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val clientId = backStackEntry.arguments?.getInt("clientId")?: 0

                                    ClientOverviewScreen(clientId, viewModel, innerPadding, navController)
                                }

                                composable(
                                    route = "project/{projectName}/{projectId}/{clientId}",
                                    arguments = listOf(
                                        navArgument("projectName") { type = NavType.StringType },
                                        navArgument("clientId") { type = NavType.IntType },
                                        navArgument("projectId") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val projectName = backStackEntry.arguments?.getString("projectName")?: ""
                                    val clientId = backStackEntry.arguments?.getInt("clientId")?: 0
                                    val projectId = backStackEntry.arguments?.getInt("projectId")?: 0

                                    ProjectSkeletonScreen(projectName, projectId, clientId, viewModel, innerPadding)
                                }

                                composable("profile") {
                                    ProfileScreen(viewModel, navController, innerPadding)
                                }

                                composable("notifications") {
                                    NotificationsScreen(viewModel, navController, innerPadding)
                                }

                                composable("settings") {
                                    SettingsScreen(viewModel, navController, innerPadding)
                                }
                            }
                        }
                    }
                    else -> {
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                SubscriptionSplashScreen(billingHelper)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LegacySecureFreelancerCRMTheme {
    }
}