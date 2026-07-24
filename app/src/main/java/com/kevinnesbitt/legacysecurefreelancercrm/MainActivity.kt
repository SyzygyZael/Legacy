package com.kevinnesbitt.legacysecurefreelancercrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LegacySecureFreelancerCRMTheme {
                val windowInfo = LocalWindowInfo.current
                val screenWidth = windowInfo.containerDpSize.width

                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )

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
                                IconButton(
                                    modifier = Modifier.size(55.dp, 55.dp),
                                    onClick = {
                                        navController.navigate("home")
                                    },
                                    colors = IconButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black,
                                        disabledContentColor = Color.Black,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Dashboard, // ✨ Matches your document file image asset
                                        contentDescription = "Home",
                                        modifier = Modifier.size(35.dp),
                                        tint = Color.Black // Automatically changes to your theme's font color state
                                    )
                                }

                                IconButton(
                                    modifier = Modifier.size(55.dp, 55.dp),
                                    onClick = {
                                        navController.navigate("clients")
                                    },
                                    colors = IconButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black,
                                        disabledContentColor = Color.Black,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.People, // ✨ Matches your document file image asset
                                        contentDescription = "Clients",
                                        modifier = Modifier.size(35.dp),
                                        tint = Color.Black // Automatically changes to your theme's font color state
                                    )
                                }

                                IconButton(
                                    modifier = Modifier.size(55.dp, 55.dp),
                                    onClick = {
                                        navController.navigate("settings")
                                    },
                                    colors = IconButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black,
                                        disabledContentColor = Color.Black,
                                        disabledContainerColor = Color.White
                                    ),
                                    shape = CircleShape
                                ) {
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
                            route = "client/{clientId}/{hubTab}",
                            arguments = listOf(
                                navArgument("clientId") { type = NavType.IntType },
                                navArgument("hubTab") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val clientId = backStackEntry.arguments?.getInt("clientId")?: 0
                            val hubTab = backStackEntry.arguments?.getString("hubTab")?: "overview"

                            when(hubTab) {
                                "overview" -> ClientOverviewScreen(clientId, viewModel, innerPadding, navController)
                            }
                        }

                        composable(
                            route = "project/{projectName}/{projectId}/{clientId}/{hubTab}",
                            arguments = listOf(
                                navArgument("projectName") { type = NavType.StringType },
                                navArgument("clientId") { type = NavType.IntType },
                                navArgument("projectId") { type = NavType.IntType },
                                navArgument("hubTab") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val projectName = backStackEntry.arguments?.getString("projectName")?: ""
                            val clientId = backStackEntry.arguments?.getInt("clientId")?: 0
                            val projectId = backStackEntry.arguments?.getInt("projectId")?: 0
                            val hubTab = backStackEntry.arguments?.getString("hubTab")?: "overview"

                            when(hubTab) {
                                "overview" -> ProjectOverviewScreen(projectName, projectId, clientId, hubTab, viewModel, innerPadding, navController)
                                "tasks" -> TasksScreen(projectName, projectId, clientId, viewModel, innerPadding, navController)
                                "logs" -> TimeLogsScreen(projectName, projectId, clientId, viewModel, innerPadding, navController)
                                "invoices" -> InvoiceScreen(projectName, projectId, clientId, viewModel, innerPadding, navController)
                            }
                        }

                        composable("profile") {
                            ProfileScreen(viewModel, navController, innerPadding)
                        }

                        composable("settings") {
                            SettingsScreen(viewModel, navController, innerPadding)
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