package com.kevinnesbitt.legacysecurefreelancercrm

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.database.SettingsEntity
import com.kevinnesbitt.legacysecurefreelancercrm.ui.theme.LegacySecureFreelancerCRMTheme
import com.kevinnesbitt.legacysecurefreelancercrm.variables.BillingType
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ClientStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

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

                        composable("settings") {
                            SettingsScreen(viewModel, navController, innerPadding)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel, innerPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clientState by viewModel.clientState.collectAsStateWithLifecycle()
    val activeProject = clientState.find { client ->
        client.projects.any { project ->
            project.status == ProjectStatus.ACTIVE.name
        }
    }?.projects?.find { it.status == ProjectStatus.ACTIVE.name }
    val activeProjectClient = clientState.find { it.id == (activeProject?.clientId?: 0) }

    val allProjects = clientState.flatMap { client ->
        client.projects
    }

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    val graphGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF87CEEBL),
            Color(0xFF00FFFFL)
        )
    )

    val timerState by remember(settings) { mutableStateOf(settings.isTiming) }

    var showNullActiveProjectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " Dashboard",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )
            }
        }

        // GRAPH
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(width = screenWidth - 15.dp, height = screenWidth - 125.dp)
                    .background(graphGradient)
            ) {

            }
        }

        // METRICS
        FlowRow(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            itemVerticalAlignment = Alignment.CenterVertically

        ) {
            // Active earnings
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StackedLineChart,
                                contentDescription = "Active Earnings",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Active Earnings",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.activeEarningsThisMonth.toString(),
                            fontSize = 30.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Surface(
                            modifier = Modifier
                                .size(52.dp, 20.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF98FF98L).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "^ +0.0%",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(2.dp),
                                color = Color(0xFF228B22L),
                                textAlign = TextAlign.Center,
                                fontWeight = Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Pending Invoices
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Pending Invoices",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Pending Invoices",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.pendingInvoices.toString(),
                            fontSize = 30.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Projects
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Projects",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Projects",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${allProjects.size}",
                            fontSize = 30.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Tax Bracket
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DomainVerification,
                                contentDescription = "Tax Bracket",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Tax Bracket",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.activeEarningsThisMonth.toString(),
                            fontSize = 30.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Surface(
                            modifier = Modifier
                                .size(52.dp, 20.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF98FF98L).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "^ +0.0%",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(2.dp),
                                color = Color(0xFF228B22L),
                                textAlign = TextAlign.Center,
                                fontWeight = Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Timer
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(width = screenWidth - 15.dp, height = 115.dp)
                    .background(color = Color.White)
                    .padding(bottom = 15.dp, start = 8.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val timeLog: HomeViewModel.TimeLogData? = if (activeProject?.timeLogs?.isNotEmpty() == true) {
                    activeProject.timeLogs.first()
                } else {
                    null
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Time Worked",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Time Worked",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // if (settings.isPaused || !settings.isTiming) {
                        //     Row(
                        //         verticalAlignment = Alignment.CenterVertically
                        //     ) {
                        //         IconButton(
                        //             modifier = Modifier.size(55.dp, 55.dp),
                        //             onClick = {
                        //                 val currentTime = LocalTime.now()
                        //                 val currentTimeLong = currentTime.toNanoOfDay()
//
                        //                 if (settings.isPaused) {
                        //                     val activeTimeLog = activeProject?.timeLogs?.last()
                        //                     val pausedTime = currentTimeLong - (activeTimeLog?.pauseStartTime?: 0L)
//
                        //                     viewModel.updatePauseStartTime(activeTimeLog?.id?: 0, 0L)
                        //                     viewModel.updateTotalPauseTime(activeTimeLog?.id?: 0, pausedTime + (activeTimeLog?.totalPauseTime?: 0L))
//
                        //                     viewModel.updateTimerPausedState(false)
                        //                 } else {
                        //                     viewModel.startTrackingTime(
                        //                         activeProject?.id?: 0,
                        //                         currentTime.toNanoOfDay()
                        //                     )
//
                        //                     viewModel.updateTimerState(true)
                        //                 }
//
                        //             },
                        //             colors = IconButtonColors(
                        //                 containerColor = Color.White,
                        //                 contentColor = Color.Black,
                        //                 disabledContentColor = Color.Black,
                        //                 disabledContainerColor = Color.White
                        //             ),
                        //             shape = CircleShape
                        //         ) {
                        //             Icon(
                        //                 imageVector = Icons.Default.PlayArrow,
                        //                 contentDescription = "Start Timer",
                        //                 modifier = Modifier
                        //                     .size(30.dp)
                        //                     .padding(4.dp),
                        //                 tint = Color.Black
                        //             )
                        //         }
                        //     }
                        // } else {
                        //     IconButton(
                        //         modifier = Modifier.size(55.dp, 55.dp),
                        //         onClick = {
                        //             val activeTimeLog = activeProject?.timeLogs?.last()
                        //             val currentTime = LocalTime.now().toNanoOfDay()
//
                        //             viewModel.updateTimerPausedState(true)
                        //             viewModel.updatePauseStartTime(activeTimeLog?.id?: 0, currentTime)
                        //         },
                        //         colors = IconButtonColors(
                        //             containerColor = Color.White,
                        //             contentColor = Color.Black,
                        //             disabledContentColor = Color.Black,
                        //             disabledContainerColor = Color.White
                        //         ),
                        //         shape = CircleShape
                        //     ) {
                        //         Icon(
                        //             imageVector = Icons.Default.Pause,
                        //             contentDescription = "Pause Timer",
                        //             modifier = Modifier
                        //                 .size(30.dp)
                        //                 .padding(4.dp),
                        //             tint = Color.Black
                        //         )
                        //     }
                        // }
//
                        // IconButton(
                        //     modifier = Modifier.size(55.dp, 55.dp),
                        //     onClick = {
                        //         val activeTimeLog = activeProject?.timeLogs?.last()
                        //         val currentTime = LocalTime.now().toNanoOfDay()
//
                        //         viewModel.updateTimerState(false)
                        //         viewModel.updateEndTime(activeTimeLog?.id?: 0, currentTime)
                        //     },
                        //     colors = IconButtonColors(
                        //         containerColor = Color.White,
                        //         contentColor = Color.Black,
                        //         disabledContentColor = Color.Black,
                        //         disabledContainerColor = Color.White
                        //     ),
                        //     shape = CircleShape
                        // ) {
                        //     Icon(
                        //         imageVector = Icons.Default.Stop,
                        //         contentDescription = "Stop Timer",
                        //         modifier = Modifier
                        //             .size(30.dp)
                        //             .padding(4.dp),
                        //         tint = Color.Black
                        //     )
                        // }

                        if (timerState) {
                            IconButton(
                                modifier = Modifier.size(55.dp, 55.dp),
                                onClick = {
                                    val activeTimeLog = activeProject?.timeLogs?.first()
                                    val currentTime = LocalTime.now().toNanoOfDay()

                                    viewModel.updateTimerState(false)
                                    viewModel.updateEndTime(activeTimeLog?.id?: 0, currentTime)
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
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop Timer",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(4.dp),
                                    tint = Color.Black
                                )
                            }
                        } else {
                            IconButton(
                                modifier = Modifier.size(55.dp, 55.dp),
                                onClick = {
                                    if (activeProject != null) {
                                        val currentTime = LocalTime.now().toNanoOfDay()

                                        viewModel.startTrackingTime(
                                            activeProject.id,
                                            currentTime
                                        )

                                        viewModel.updateTimerState(true)
                                    } else {
                                        showNullActiveProjectDialog = true
                                    }
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
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Timer",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(4.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }

                TimerText(timeLog, settings)

                Text(
                    text = "${activeProject?.title?: " No Active Project "}, ${activeProjectClient?.name?: "Unknown Name"}",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // DIALOG BOXES

    if (showNullActiveProjectDialog) {
        Dialog(
            onDismissRequest = { showNullActiveProjectDialog = false }
        ) {
            DialogBoxSkeleton(550.dp, 200.dp) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "No Active Project",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        tint = Color.Gray
                    )

                    Text(
                        text = "Warning",
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Active project not set",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Button(
                        onClick = { showNullActiveProjectDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Cyan,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Dismiss",
                            fontWeight = Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClientsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val clientStates by viewModel.clientState.collectAsStateWithLifecycle()

    var localClientStates by remember(clientStates) { mutableStateOf(clientStates) }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
        localClientStates = localClientStates.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    var tempNameText by remember {
        mutableStateOf("")
    }

    var tempEmailText by remember {
        mutableStateOf("")
    }

    var tempTelpNum by remember {
        mutableStateOf("")
    }

    var tempCurrency by remember {
        mutableStateOf("")
    }

    var isAddingClient by remember {
        mutableStateOf(false)
    }

    var expandCurrencyChoice by remember {
        mutableStateOf(false)
    }

    var expandClientOptions by remember {
        mutableStateOf<Int?>(null)
    }

    var expandClientScreenMenu by remember {
        mutableStateOf(false)
    }

    var showArchived by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(clientStates) {
        localClientStates = clientStates
    }

    var isReordering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showArchived) " Archived Clients" else " Clients",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                if (isReordering) {
                    IconButton(
                        modifier = Modifier.size(55.dp, 55.dp),
                        onClick = {
                            isReordering = false
                            viewModel.updateClientOrder(localClientStates)
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done Reordering",
                            modifier = Modifier.size(23.dp),
                            tint = Color.Black
                        )
                    }
                } else {
                    IconButton(
                        modifier = Modifier.size(55.dp, 55.dp),
                        onClick = {
                            expandClientScreenMenu = true
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
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Black
                        )

                        DropdownMenu(
                            expanded = expandClientScreenMenu,
                            onDismissRequest = { expandClientScreenMenu = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = showArchived,
                                    onCheckedChange = { showArchived = !showArchived }
                                )

                                Text(
                                    text = "Show Archived"
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(6.dp),
            state = lazyListState
        ) {
            if (localClientStates.isNotEmpty()) {
                items(localClientStates, key = { client -> client.id }) { client ->
                    ReorderableItem(
                        reorderableState,
                        key = client.id
                    ) {
                        if ((client.status != ClientStatus.ARCHIVED.name) && !showArchived) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .combinedClickable(
                                        onClick = { navController.navigate("client/${client.id}/overview") },
                                        onLongClick = { expandClientOptions = client.id }
                                    ),
                                elevation = CardDefaults.cardElevation(
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                        .background(color = Color.White),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    if (isReordering) {
                                        Text(
                                            text = "⋮⋮",
                                            fontSize = 25.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .draggableHandle()
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                    ) {
                                        Text(
                                            text = client.name,
                                            fontWeight = Bold,
                                            fontSize = 25.sp,
                                            color = Color.Black
                                        )

                                        Text(
                                            text = client.email,
                                            fontSize = 15.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = expandClientOptions == client.id,
                                    onDismissRequest = { expandClientOptions = null }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Archive",
                                                fontSize = 15.sp,
                                                fontWeight = Bold
                                            )
                                        },
                                        onClick = {
                                            viewModel.updateClientStatus(
                                                ClientStatus.ARCHIVED.name,
                                                client.id
                                            )

                                            expandClientOptions = null
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Move",
                                                fontSize = 15.sp,
                                                fontWeight = Bold
                                            )
                                        },
                                        onClick = {
                                            isReordering = true
                                            expandClientOptions = null
                                        }
                                    )
                                }
                            }
                        } else if ((client.status == ClientStatus.ARCHIVED.name) && showArchived) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .combinedClickable(
                                        onClick = { navController.navigate("client/${client.id}") },
                                        onLongClick = { expandClientOptions = client.id }
                                    ),
                                elevation = CardDefaults.cardElevation(
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp,
                                    5.dp
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                        .background(color = Color.White),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                    ) {
                                        Text(
                                            text = client.name,
                                            fontWeight = Bold,
                                            fontSize = 25.sp,
                                            color = Color.Black
                                        )

                                        Text(
                                            text = client.email,
                                            fontSize = 15.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Text(
                                        text = "ARCHIVED",
                                        fontSize = 18.sp,
                                        color = Color(0xFFDF0000L),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                DropdownMenu(
                                    expanded = expandClientOptions == client.id,
                                    onDismissRequest = { expandClientOptions = null }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Recover",
                                                fontSize = 18.sp,
                                                fontWeight = Bold
                                            )
                                        },
                                        onClick = {
                                            viewModel.updateClientStatus(
                                                ClientStatus.ACTIVE.name,
                                                client.id
                                            )

                                            expandClientOptions = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            isAddingClient = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Blue
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Client",
                            modifier = Modifier.size(23.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }


//=======================================
// DIALOG BOXES
//=======================================

    // Client adding
    if (isAddingClient) {
        Dialog(
            onDismissRequest = { isAddingClient = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 450.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),

                ) {
                    Text(
                        text = "New Client",
                        textAlign = TextAlign.Center,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 7.dp),
                        fontWeight = Bold
                    )

                    Text(
                        text = "  Name",
                        textAlign = TextAlign.Left,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = Bold
                    )
                    OutlinedTextField(
                        value = tempNameText,
                        onValueChange = { text ->
                            tempNameText = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    Text(
                        text = "  Email",
                        textAlign = TextAlign.Left,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = tempEmailText,
                        onValueChange = { text ->
                            tempEmailText = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    Text(
                        text = "  Phone",
                        fontSize = 15.sp,
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = tempTelpNum,
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '+' } <= 1 &&
                                    text.all { it.isDigit() || it == '+' }
                            if (isValidDecimal) {
                                tempTelpNum = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White, thickness = 15.dp)

                    Row {
                        Text(
                            text = "Currency: ",
                            fontSize = 15.sp,
                            fontWeight = Bold
                        )
                        Surface(
                            modifier = Modifier
                                .size(60.dp, 30.dp)
                                .background(color = Color.LightGray)
                                .clickable(
                                    onClick = {
                                        expandCurrencyChoice = true
                                    }
                                ),
                            border = BorderStroke(2.dp, Color.Gray)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tempCurrency
                                )
                            }

                            DropdownMenu(
                                expanded = expandCurrencyChoice,
                                onDismissRequest = { expandCurrencyChoice = false },
                                modifier = Modifier.size(width = 60.dp, height = 135.dp)
                            ) {
                                SupportedCurrency.entries.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(text = currency.code) },
                                        onClick = {
                                            tempCurrency = currency.code
                                            expandCurrencyChoice = false
                                        }
                                    )
                                }

                            }
                        }
                    }

                    HorizontalDivider(color = Color.White, thickness = 30.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                isAddingClient = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempNameText.isNotBlank()) {
                                    viewModel.createClient(
                                        name = tempNameText,
                                        email = tempEmailText,
                                        currency = tempCurrency,
                                        telp = tempTelpNum
                                    )
                                    tempNameText = ""
                                    tempEmailText = ""
                                    tempCurrency = ""
                                    tempTelpNum = "0.0"
                                    isAddingClient = false
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientOverviewScreen(clientId: Int, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val clientName = clientState?.name?: ""
    val clientEmail = clientState?.email?: ""
    val clientPhoneNum = clientState?.telp?: ""
    val clientProjects = clientState?.projects?: emptyList()
    val clientCurrency = clientState?.currency?: ""

    val clients = viewModel.clientState.collectAsStateWithLifecycle().value
    val activeProjects = clients.flatMap { client ->
        client.projects.filter { it.status == ProjectStatus.ACTIVE.name }
    }
    // android.util.Log.d("Active Projects", "Num Active Projects: ${activeProjects.size}")


    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var localProjects by remember(clientProjects) { mutableStateOf(clientProjects) }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
        localProjects = localProjects.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    var showEditClientDialog by remember { mutableStateOf(false) }

    var tempNameText by remember { mutableStateOf("") }

    var tempEmailText by remember { mutableStateOf("") }

    var tempTelpNum by remember { mutableStateOf("") }

    var tempCurrency by remember { mutableStateOf("") }

    var expandCurrencyChoice by remember { mutableStateOf(false) }

    var expandBillingTypeChoice by remember { mutableStateOf(false) }

    var isAddingProject by remember { mutableStateOf(false) }

    var tempProjectTitle by remember { mutableStateOf("") }

    var tempProjectDescription by remember { mutableStateOf("") }

    var tempProjectRate by remember { mutableDoubleStateOf(0.0) }

    var tempProjectBudget by remember { mutableDoubleStateOf(0.0) }

    var tempProjectBillingType by remember { mutableStateOf("") }

    var tempProjectDeadline by remember { mutableStateOf("--/--/----") }

    var expandProjectOptions by remember { mutableStateOf<Int?>(null) }

    var isReordering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = clientName,
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )
                    Text(
                        text = clientEmail,
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = clientPhoneNum,
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Currency: $clientCurrency",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }

                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        modifier = Modifier.size(45.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        IconButton(
                            modifier = Modifier.fillMaxSize(),
                            onClick = {
                                tempNameText = clientName
                                tempEmailText = clientEmail
                                tempCurrency = clientCurrency
                                tempTelpNum = clientPhoneNum
                                showEditClientDialog = true
                            },
                            colors = IconButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                disabledContentColor = Color.Black,
                                disabledContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Client",
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(4.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 15.dp, color = Color.White)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val surfaceWidth = (screenWidth / 2) - 12.dp
                val surfaceHeight = 110.dp
                val headerTextSize = 16.sp
                val valueTextSize = 27.sp

                Surface(
                    modifier = Modifier
                        .size(surfaceWidth, surfaceHeight)
                        .padding(end = 2.dp),
                    border = BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(25.dp),
                                shape = CircleShape,
                                color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AllInbox,
                                    contentDescription = "Active Projects",
                                    modifier = Modifier
                                        .size(10.dp)
                                        .padding(4.dp),
                                    tint = Color.Blue
                                )
                            }

                            Text(
                                text = "  Active Projects",
                                color = Color.Gray,
                                fontSize = headerTextSize
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${clientProjects.size}",
                                fontWeight = Bold,
                                fontSize = valueTextSize,
                                color = Color.Black,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(surfaceWidth, surfaceHeight)
                        .padding(horizontal = 2.dp),
                    border = BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(25.dp),
                                shape = CircleShape,
                                color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Money,
                                    contentDescription = "Earnings",
                                    modifier = Modifier
                                        .size(10.dp)
                                        .padding(4.dp),
                                    tint = Color.Blue
                                )
                            }

                            Text(
                                text = "  Earnings",
                                color = Color.Gray,
                                fontSize = headerTextSize
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${clientProjects.size}",
                                fontWeight = Bold,
                                fontSize = valueTextSize,
                                color = Color.Black,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projects",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(all = 15.dp)
                )

                if (isReordering) {
                    IconButton(
                        modifier = Modifier.size(55.dp, 55.dp),
                        onClick = {
                            isReordering = false
                            viewModel.updateProjectOrder(localProjects)
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done Reordering",
                            modifier = Modifier.size(23.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = lazyListState
        ) {
            items(localProjects, key = { project -> project.id }) { project ->
                if (localProjects.isNotEmpty()) {
                    ReorderableItem(
                        reorderableState,
                        key = project.id
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                5.dp,
                                5.dp,
                                5.dp,
                                5.dp,
                                5.dp,
                                5.dp
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .combinedClickable(
                                    onClick = {
                                        navController.navigate("project/${project.title}/${project.id}/${clientId}/overview")
                                    },
                                    onLongClick = { expandProjectOptions = project.id }
                                ),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(15.dp)
                                    .background(color = Color.White),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    if (isReordering) {
                                        Text(
                                            text = "⋮⋮",
                                            fontSize = 30.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .draggableHandle()
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = project.title,
                                            fontSize = 23.sp,
                                            fontWeight = Bold,
                                            color = Color.Black
                                        )

                                        Text(
                                            text = project.deadLine,
                                            fontSize = 15.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                var statusColor: Color = Color.White
                                when (project.status) {
                                    "ARCHIVED" -> statusColor = Color.Red
                                    "PAUSED" -> statusColor = Color.Gray
                                    "ACTIVE" -> statusColor = Color.Green
                                }

                                Text(
                                    text = project.status,
                                    fontSize = 22.sp,
                                    color = statusColor
                                )
                            }

                            DropdownMenu(
                                expanded = expandProjectOptions == project.id,
                                onDismissRequest = { expandProjectOptions = null }
                            ) {
                                if (project.status != ProjectStatus.ARCHIVED.name) {
                                    DropdownMenuItem(
                                        text = { Text("Archive") },
                                        onClick = {
                                            viewModel.updateProjectStatus(
                                                ProjectStatus.ARCHIVED.name,
                                                project.id,
                                                clientId
                                            )

                                            expandProjectOptions = null
                                        }
                                    )
                                }

                                if (project.status != ProjectStatus.PAUSED.name) {
                                    DropdownMenuItem(
                                        text = { Text("Pause") },
                                        onClick = {
                                            viewModel.updateProjectStatus(
                                                ProjectStatus.PAUSED.name,
                                                project.id,
                                                clientId
                                            )

                                            expandProjectOptions = null
                                        }
                                    )
                                }

                                if (project.status != ProjectStatus.ACTIVE.name) {
                                    DropdownMenuItem(
                                        text = { Text("Active") },
                                        onClick = {
                                            if (activeProjects.isNotEmpty()) {
                                                // android.util.Log.d("Active Projects", "Num Active Projects: ${activeProjects.size}")
                                                activeProjects.forEach { activeProject ->
                                                    viewModel.updateProjectStatus(
                                                        clientId = activeProject.clientId,
                                                        projectId = activeProject.id,
                                                        status = ProjectStatus.PAUSED.name
                                                    )
                                                }
                                            }
                                            // android.util.Log.d("Active Projects", "Num Active Projects: ${activeProjects.size}")

                                            viewModel.updateProjectStatus(
                                                ProjectStatus.ACTIVE.name,
                                                project.id,
                                                clientId
                                            )

                                            expandProjectOptions = null
                                        }
                                    )
                                }

                                DropdownMenuItem(
                                    text = { Text("Move") },
                                    onClick = {
                                        isReordering = true
                                        expandProjectOptions = null
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            isAddingProject = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Blue
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Client",
                            modifier = Modifier.size(23.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // edit client
    if (showEditClientDialog) {
        Dialog(
            onDismissRequest = { showEditClientDialog = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 450.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),

                    ) {
                    Text(
                        text = "Edit Client",
                        textAlign = TextAlign.Center,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 7.dp),
                        fontWeight = Bold
                    )

                    Text(
                        text = "  Name",
                        textAlign = TextAlign.Left,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = Bold
                    )
                    OutlinedTextField(
                        value = tempNameText,
                        onValueChange = { text ->
                            tempNameText = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    Text(
                        text = "  Email",
                        textAlign = TextAlign.Left,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = tempEmailText,
                        onValueChange = { text ->
                            tempEmailText = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    Text(
                        text = "  Phone",
                        fontSize = 15.sp,
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = tempTelpNum,
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '+' } <= 1 &&
                                    text.all { it.isDigit() || it == '+' }
                            if (isValidDecimal) {
                                tempTelpNum = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White, thickness = 15.dp)

                    Row {
                        Text(
                            text = "Currency: ",
                            fontSize = 15.sp,
                            fontWeight = Bold
                        )
                        Surface(
                            modifier = Modifier
                                .size(60.dp, 30.dp)
                                .background(color = Color.LightGray)
                                .clickable(
                                    onClick = {
                                        expandCurrencyChoice = true
                                    }
                                ),
                            border = BorderStroke(2.dp, Color.Gray)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tempCurrency
                                )
                            }

                            DropdownMenu(
                                expanded = expandCurrencyChoice,
                                onDismissRequest = { expandCurrencyChoice = false },
                                modifier = Modifier.size(width = 60.dp, height = 135.dp)
                            ) {
                                SupportedCurrency.entries.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text(text = currency.code) },
                                        onClick = {
                                            tempCurrency = currency.code
                                            expandCurrencyChoice = false
                                        }
                                    )
                                }

                            }
                        }
                    }

                    HorizontalDivider(color = Color.White, thickness = 30.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showEditClientDialog = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempNameText.isNotBlank()) {
                                    viewModel.updateClientInfo(
                                        newName = tempNameText,
                                        newEmail = tempEmailText,
                                        newCurrency = tempCurrency,
                                        newTelp = tempTelpNum,
                                        clientId = clientId
                                    )
                                    tempNameText = ""
                                    tempEmailText = ""
                                    tempCurrency = ""
                                    tempTelpNum = "0.0"
                                    showEditClientDialog = false
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }

    // add project
    if (isAddingProject) {
        Dialog(
            onDismissRequest = { isAddingProject = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 720.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),

                    ) {
                    Text(
                        text = "New Project",
                        textAlign = TextAlign.Center,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 7.dp),
                        fontWeight = Bold
                    )

                    // Text(
                    //     text = "  Title",
                    //     textAlign = TextAlign.Left,
                    //     fontSize = 15.sp,
                    //     modifier = Modifier.fillMaxWidth(),
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = tempProjectTitle,
                        label = { Text(text = "Title") },
                        onValueChange = { text ->
                            if (tempProjectTitle.length <= 50) tempProjectTitle = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Description",
                    //     textAlign = TextAlign.Left,
                    //     fontSize = 15.sp,
                    //     modifier = Modifier.fillMaxWidth(),
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = tempProjectDescription,
                        label = { Text(text = "Description") },
                        onValueChange = { text ->
                            if (tempProjectDescription.length <= 500) tempProjectDescription = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp)
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Deadline: ",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // 1. Create a container specifically to act as the coordinate anchor
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedDate,
                                onValueChange = { },
                                label = { Text("Project Deadline") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Pay Rate",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = if (tempProjectRate == 0.0) "" else tempProjectRate.toString(),
                        label = { Text(text = "Pay Rate") },
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '.' } <= 1 &&
                                    text.all { it.isDigit() || it == '.' }
                            if (isValidDecimal) {
                                tempProjectRate = text.toDouble()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Budget",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = if (tempProjectBudget == 0.0) "" else tempProjectBudget.toString(),
                        label = { Text(text = "Budget") },
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '.' } <= 1 &&
                                    text.all { it.isDigit() || it == '.' }
                            if (isValidDecimal) {
                                tempProjectBudget = text.toDouble()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White, thickness = 15.dp)

                    Row {
                        Text(
                            text = "  Billing Type: ",
                            fontSize = 15.sp,
                            fontWeight = Bold
                        )
                        Surface(
                            modifier = Modifier
                                .size(95.dp, 30.dp)
                                .background(color = Color.LightGray)
                                .clickable(
                                    onClick = {
                                        expandBillingTypeChoice = true
                                    }
                                ),
                            border = BorderStroke(2.dp, Color.Gray)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tempProjectBillingType
                                )
                            }

                            DropdownMenu(
                                expanded = expandBillingTypeChoice,
                                onDismissRequest = { expandBillingTypeChoice = false },
                                modifier = Modifier.size(width = 95.dp, height = 100.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = BillingType.FIXED.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.FIXED.name
                                        expandBillingTypeChoice = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.HOURLY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.HOURLY.name
                                        expandBillingTypeChoice = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White, thickness = 30.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                isAddingProject = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempProjectTitle.isNotBlank()) {
                                    viewModel.createProject(
                                        clientId = clientId,
                                        title = tempProjectTitle,
                                        description = tempProjectDescription,
                                        deadline = tempProjectDeadline,
                                        type = tempProjectBillingType,
                                        payRate = tempProjectRate,
                                        budget = tempProjectBudget
                                    )
                                    tempProjectTitle = ""
                                    tempProjectDescription = ""
                                    tempProjectBudget = 0.0
                                    tempProjectRate = 0.0
                                    tempProjectDeadline = "--/--/----"
                                    tempProjectBillingType = ""
                                    isAddingProject = false

                                    android.util.Log.d("Project Added", "Num Projects: ${clientProjects.size}")
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }

    // show date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempProjectDeadline = selectedDate
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun ProjectOverviewScreen(projectName: String, projectId: Int, clientId: Int, hubTab: String, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val project = clientState?.projects?.find { projectId == it.id }
    val currentTab = hubTab.replaceRange(0, 1, hubTab[0].uppercase())

    val description = project?.description?: ""
    val completedTasks = project?.tasks?.filter { it.isCompleted }?.size

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var tempProjectTitle by remember {
        mutableStateOf("")
    }

    var tempProjectDescription by remember {
        mutableStateOf("")
    }

    var localDescription by remember(description) {
        mutableStateOf(TextFieldValue(text = description))
    }

    var tempProjectRate by remember {
        mutableDoubleStateOf(0.0)
    }

    var tempProjectBudget by remember {
        mutableDoubleStateOf(0.0)
    }

    var tempProjectBillingType by remember {
        mutableStateOf("")
    }

    var tempProjectDeadline by remember {
        mutableStateOf("--/--/----")
    }

    var tempProjectStatus by remember {
        mutableStateOf("")
    }

    var expandBillingTypeChoice by remember {
        mutableStateOf(false)
    }

    var editProjectInfo by remember {
        mutableStateOf(false)
    }

    var editDescription by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$projectName $currentTab",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                IconButton(
                    modifier = Modifier.size(20.dp),
                    onClick = {
                        tempProjectTitle = project?.title?: ""
                        tempProjectDescription = project?.description?: ""
                        tempProjectDeadline = project?.deadLine?: "--/--/----"
                        tempProjectBillingType = project?.billingType?: ""
                        tempProjectRate = project?.payRate?: 0.0
                        tempProjectBudget = project?.budget?: 0.0
                        tempProjectStatus = project?.status?: ""
                        editProjectInfo = true
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
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Project Info",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/overview") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Overview")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/tasks") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Tasks")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/logs") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Time Logs")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/invoices") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Invoices")
                }
            }
        }

        // Description
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(width = screenWidth - 15.dp, height = screenWidth - 200.dp)
                    .background(color = Color.White)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " Description",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }

                if (editDescription){
                    val focusRequester = remember { FocusRequester() }
                    BasicTextField(
                        value = localDescription,
                        onValueChange = { text ->
                            if (localDescription.text.length <= 500) {
                                localDescription = text
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 17.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.updateProjectDescription(projectId, localDescription.text)
                                editDescription = false
                            }
                        )
                    )
                    // request keyboard
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                } else {
                    LazyColumn {
                        item {
                            Text(
                                text = description,
                                fontSize = 17.sp,
                                color = Color.Black,
                                modifier = Modifier.clickable(
                                    onClick = {
                                        editDescription = true
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }

        // METRICS
        FlowRow(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            itemVerticalAlignment = Alignment.CenterVertically

        ) {
            // Active earnings
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StackedLineChart,
                                contentDescription = "Active Earnings",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Active Earnings",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Surface(
                            modifier = Modifier
                                .size(52.dp, 20.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF98FF98L).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "^ +0.0%",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(2.dp),
                                color = Color(0xFF228B22L),
                                textAlign = TextAlign.Center,
                                fontWeight = Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Deadline
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Deadline",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Deadline",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = project?.deadLine?: "--/--/----",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Current Task
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DomainVerification,
                                contentDescription = "Current Task",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Current Task",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Task name",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Pending Invoices
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Pending Invoices",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Pending Invoices",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Pending Tasks
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Completed Tasks",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Completed Tasks",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$completedTasks/${project?.tasks?.size}",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Rate per Billing Type
            Card(
                elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .size(width = 185.dp, height = 115.dp)
                        .background(color = Color.White)
                        .padding(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(25.dp),
                            shape = CircleShape,
                            color = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CorporateFare,
                                contentDescription = "Rate",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Rate",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var rateType = ""
                        when(project?.billingType) {
                            BillingType.HOURLY.name -> { rateType = "/hr" }
                            BillingType.FIXED.name -> { rateType = " Total" }
                        }

                        Text(
                            text = "${project?.payRate}${rateType}",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    // DIALOG BOXES

    // edit project
    if (editProjectInfo) {
        Dialog(
            onDismissRequest = { editProjectInfo = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 720.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),

                    ) {
                    Text(
                        text = "Edit Project",
                        textAlign = TextAlign.Center,
                        fontSize = 21.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 7.dp),
                        fontWeight = Bold
                    )

                    // Text(
                    //     text = "  Title",
                    //     textAlign = TextAlign.Left,
                    //     fontSize = 15.sp,
                    //     modifier = Modifier.fillMaxWidth(),
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = tempProjectTitle,
                        label = { Text(text = "Title") },
                        onValueChange = { text ->
                            if (tempProjectTitle.length <= 50) tempProjectTitle = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Description",
                    //     textAlign = TextAlign.Left,
                    //     fontSize = 15.sp,
                    //     modifier = Modifier.fillMaxWidth(),
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = tempProjectDescription,
                        label = { Text(text = "Description") },
                        onValueChange = { text ->
                            if (tempProjectDescription.length <= 500) tempProjectDescription = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp)
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Deadline: ",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // 1. Create a container specifically to act as the coordinate anchor
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedDate,
                                onValueChange = { },
                                label = { Text("Project Deadline") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Pay Rate",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = if (tempProjectRate == 0.0) "" else tempProjectRate.toString(),
                        label = { Text(text = "Pay Rate") },
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '.' } <= 1 &&
                                    text.all { it.isDigit() || it == '.' }
                            if (isValidDecimal) {
                                tempProjectRate = text.toDouble()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White)

                    // Text(
                    //     text = "  Budget",
                    //     fontSize = 15.sp,
                    //     fontWeight = Bold
                    // )

                    OutlinedTextField(
                        value = if (tempProjectBudget == 0.0) "" else tempProjectBudget.toString(),
                        label = { Text(text = "Budget") },
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '.' } <= 1 &&
                                    text.all { it.isDigit() || it == '.' }
                            if (isValidDecimal) {
                                tempProjectBudget = text.toDouble()
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        textStyle = TextStyle(fontSize = 20.sp),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color.White, thickness = 15.dp)

                    Row {
                        Text(
                            text = "  Billing Type: ",
                            fontSize = 15.sp,
                            fontWeight = Bold
                        )
                        Surface(
                            modifier = Modifier
                                .size(95.dp, 30.dp)
                                .background(color = Color.LightGray)
                                .clickable(
                                    onClick = {
                                        expandBillingTypeChoice = true
                                    }
                                ),
                            border = BorderStroke(2.dp, Color.Gray)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tempProjectBillingType
                                )
                            }

                            DropdownMenu(
                                expanded = expandBillingTypeChoice,
                                onDismissRequest = { expandBillingTypeChoice = false },
                                modifier = Modifier.size(width = 95.dp, height = 100.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = BillingType.FIXED.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.FIXED.name
                                        expandBillingTypeChoice = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.HOURLY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.HOURLY.name
                                        expandBillingTypeChoice = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White, thickness = 30.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                editProjectInfo = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempProjectTitle.isNotBlank()) {
                                    viewModel.updateProjectInfo(
                                        projectId = projectId,
                                        newTitle = tempProjectTitle,
                                        newDesc = tempProjectDescription,
                                        newDeadline = tempProjectDeadline,
                                        newBT = tempProjectBillingType,
                                        newPayrate = tempProjectRate,
                                        newBudget = tempProjectBudget
                                    )
                                    tempProjectTitle = ""
                                    tempProjectDescription = ""
                                    tempProjectBudget = 0.0
                                    tempProjectRate = 0.0
                                    tempProjectDeadline = "--/--/----"
                                    tempProjectBillingType = ""
                                    tempProjectStatus = ""
                                    editProjectInfo = false
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }

    // show date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempProjectDeadline = selectedDate
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun TasksScreen(projectName: String, projectId: Int, clientId: Int, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val project = clientState?.projects?.find { it.id == projectId }
    val tasks = project?.tasks?: emptyList()

    var localTasks by remember(tasks) { mutableStateOf(tasks) }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
        localTasks = localTasks.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    var isAddingTask by remember { mutableStateOf(false) }
    var isEditingTask by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var tempTaskId by remember { mutableIntStateOf(0) }
    var tempTaskName by remember { mutableStateOf("") }
    var tempTaskDeadline by remember { mutableStateOf("--/--/----") }

    var expandTaskOptions by remember { mutableStateOf<Int?>(null) }
    var isReordering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$projectName Tasks",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                if (isReordering) {
                    IconButton(
                        modifier = Modifier.size(55.dp, 55.dp),
                        onClick = {
                            isReordering = false
                            viewModel.updateTasksOrder(localTasks)
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done Reordering",
                            modifier = Modifier.size(23.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/overview") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Overview")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/tasks") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Tasks")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/logs") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Time Logs")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/invoices") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Invoices")
                }
            }
        }

        LazyColumn(state = lazyListState) {
            items(localTasks, key = { task -> task.id }) { task ->
                ReorderableItem(reorderableState, key = task.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .animateItem(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isReordering) {
                            Text(
                                text = "⋮⋮",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .draggableHandle()
                            )
                        }

                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = {
                                viewModel.updateTaskStatus(task.id, !task.isCompleted)
                            },
                            colors = CheckboxDefaults.colors(
                                Color.Cyan,
                                Color.LightGray,
                                Color.Black
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .padding(end = 8.dp)
                                .combinedClickable(
                                    onClick = { },
                                    onLongClick = {
                                        expandTaskOptions = task.id
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = task.description,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            DropdownMenu(
                                expanded = expandTaskOptions == task.id,
                                onDismissRequest = { expandTaskOptions = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        viewModel.deleteTask(task.id)
                                        expandTaskOptions = null
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        tempTaskId = task.id
                                        tempTaskName = task.description
                                        tempTaskDeadline = task.dueDate
                                        isEditingTask = true
                                        expandTaskOptions = null
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Move") },
                                    onClick = {
                                        isReordering = true
                                        expandTaskOptions = null
                                    }
                                )
                            }

                            Text(
                                text = task.dueDate,
                                fontSize = 17.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            isAddingTask = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Blue
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            modifier = Modifier.size(23.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // DIALOG BOXES

    // add task
    if (isAddingTask) {
        Dialog(
            onDismissRequest = { isAddingTask = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 270.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "New Task",
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = tempTaskName,
                        onValueChange = { text ->
                            if (tempTaskName.length <= 20) { tempTaskName = text }
                        },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { },
                        label = { Text("Task Deadline") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isAddingTask = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempTaskName.isNotBlank()) {
                                    viewModel.createTask(
                                        projectId = projectId,
                                        description = tempTaskName,
                                        dueDate = tempTaskDeadline
                                    )
                                    tempTaskName = ""
                                    tempTaskDeadline = "--/--/----"
                                    isAddingTask = false
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }

    // add task
    if (isEditingTask) {
        Dialog(
            onDismissRequest = { isEditingTask = false }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 270.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(2.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "New Task",
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = tempTaskName,
                        onValueChange = { text ->
                            if (tempTaskName.length <= 20) { tempTaskName = text }
                        },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { },
                        label = { Text("Task Deadline") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isEditingTask = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                if (tempTaskName.isNotBlank()) {
                                    viewModel.editTaskInfo(
                                        taskId = tempTaskId,
                                        newName = tempTaskName,
                                        newDueDate = tempTaskDeadline
                                    )
                                    tempTaskName = ""
                                    tempTaskDeadline = "--/--/----"
                                    isEditingTask = false
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White,
                                disabledContentColor = Color.White,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }
    }

    // show date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempTaskDeadline = selectedDate
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeLogsScreen(projectName: String, projectId: Int, clientId: Int, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val settings by viewModel.settings.collectAsState()

    val clientState =
        viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val project = clientState?.projects?.find { it.id == projectId }
    val timeLogs = project?.timeLogs?: emptyList()

    var localTimeLogs by remember(timeLogs) { mutableStateOf(timeLogs) }

    val lazyListState = rememberLazyListState()

    // 2. State to hold the chosen time display text
    var selectedStartTimeText by remember { mutableStateOf("No time selected") }
    var selectedEndTimeText by remember { mutableStateOf("No time selected") }

    var longSelectedStartTime by remember { mutableLongStateOf(0L) }
    var longSelectedEndTime by remember { mutableLongStateOf(0L) }

    var tempLogId by remember { mutableIntStateOf(0) }

    // 3. Setup the initial state of the clock picker (defaults to the current device hour/minute)
    val currentTime = Calendar.getInstance()
    var startTimePickerState by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = settings.timeFormat == "24-Hour"
            )
        )
    }

    var endTimePickerState by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = settings.timeFormat == "24-Hour"
            )
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: "No date selected"

    var expandTaskOptions by remember { mutableStateOf<Int?>(null) }
    var isReordering by remember { mutableStateOf(false) }
    var isAddingTimeLog by remember { mutableStateOf(false) }
    var openStartTimePicker by remember { mutableStateOf(false) }
    var openEndTimePicker by remember { mutableStateOf(false) }
    var tempSelectedDate by remember { mutableStateOf("--/--/----") }
    var showEmptyFieldDialog by remember { mutableStateOf(false) }
    var showTimeDifferenceErrorDialog by remember { mutableStateOf(false) }
    var isEditingTimeLog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$projectName Time Logs",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                if (isReordering) {
                    IconButton(
                        modifier = Modifier.size(55.dp, 55.dp),
                        onClick = {
                            isReordering = false
                            viewModel.updateTimeLogsOrder(localTimeLogs)
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done Reordering",
                            modifier = Modifier.size(23.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/overview") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Overview")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/tasks") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Tasks")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/logs") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Time Logs")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/invoices") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Invoices")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Start",
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "End",
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Billable",
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Date",
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(thickness = 1.dp, color = Color.Gray)

        LazyColumn(state = lazyListState) {
            items(localTimeLogs, key = { timeLog -> timeLog.id }) { timeLog ->
                val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                val startTimeObj = LocalTime.ofNanoOfDay(timeLog.startTime)
                val endTimeObj = LocalTime.ofNanoOfDay(timeLog.endTime)

                val startTimeStr = when {
                    settings.timeFormat == "12-Hour" -> {
                        startTimeObj.format(formatter)
                    }
                    else -> {
                        val startHour = startTimeObj.hour
                        val startMinute = startTimeObj.minute

                        "$startHour:$startMinute"
                    }
                }

                val endTimeStr = when {
                    settings.timeFormat == "12-Hour" -> {
                        endTimeObj.format(formatter)
                    }
                    else -> {
                        val endHour = endTimeObj.hour
                        val endMinute = endTimeObj.minute

                        "$endHour:$endMinute"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(vertical = 8.dp)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                expandTaskOptions = timeLog.id
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = startTimeStr,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    DropdownMenu(
                        expanded = expandTaskOptions == timeLog.id,
                        onDismissRequest = { expandTaskOptions = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                viewModel.deleteLog(timeLog.id)
                                expandTaskOptions = null
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                val localStartTime = LocalTime.ofNanoOfDay(timeLog.startTime)
                                val localEndTime = LocalTime.ofNanoOfDay(timeLog.endTime)
                                val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

                                val startTimeStr = when {
                                    settings.timeFormat == "12-Hour" -> {
                                        localStartTime.format(formatter)
                                    }
                                    else -> {
                                        val startHour = localStartTime.hour
                                        val startMinute = localStartTime.minute

                                        "$startHour:$startMinute"
                                    }
                                }

                                val endTimeStr = when {
                                    settings.timeFormat == "12-Hour" -> {
                                        localEndTime.format(formatter)
                                    }
                                    else -> {
                                        val endHour = localEndTime.hour
                                        val endMinute = localEndTime.minute

                                        "$endHour:$endMinute"
                                    }
                                }

                                startTimePickerState = TimePickerState(
                                    initialHour = localStartTime.hour,
                                    initialMinute = localStartTime.minute,
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )

                                endTimePickerState = TimePickerState(
                                    initialHour = localEndTime.hour,
                                    initialMinute = localEndTime.minute,
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )

                                tempLogId = timeLog.id
                                selectedStartTimeText = startTimeStr
                                selectedEndTimeText = endTimeStr
                                tempSelectedDate = timeLog.date
                                longSelectedStartTime = timeLog.startTime
                                longSelectedEndTime = timeLog.endTime

                                isEditingTimeLog = true
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Move") },
                            onClick = {
                                isReordering = true
                                expandTaskOptions = null
                            }
                        )
                    }

                    Text(
                        text = endTimeStr,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 15.dp)
                    )

                    val endTime = LocalTime.ofNanoOfDay(timeLog.endTime)
                    val startTime = LocalTime.ofNanoOfDay(timeLog.startTime)
                    val totalTime = when {
                        startTime.toNanoOfDay() < endTime.toNanoOfDay() -> {
                            Duration.between(startTime, endTime).toHours()
                        }
                        else -> {
                            Duration.between(startTime, endTime).toHours() + 23
                        }
                    }
                    Text(
                        text = "${totalTime}H",
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    val year = timeLog.date.substring(0, 4)
                    val month = timeLog.date.substring(5, 7)
                    val day = timeLog.date.substring(8, 10)
                    Text(
                        text = "$month/$day/$year",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                if (isReordering) {
                    Text(
                        text = "…\n…",
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            isAddingTimeLog = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Blue
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Client",
                            modifier = Modifier.size(23.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // DIALOG BOXES

    // add time log
    if (isAddingTimeLog) {
        Dialog(
            onDismissRequest = {
                tempLogId = 0
                longSelectedStartTime = 0L
                longSelectedEndTime = 0L
                tempSelectedDate = "--/--/----"
                selectedStartTimeText = "No selected time"
                selectedEndTimeText = "No selected time"

                startTimePickerState = TimePickerState(
                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                    initialMinute = currentTime.get(Calendar.MINUTE),
                    is24Hour = when {
                        settings.timeFormat == "24-Hour" -> true
                        else -> false
                    }
                )
                endTimePickerState = TimePickerState(
                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                    initialMinute = currentTime.get(Calendar.MINUTE),
                    is24Hour = when {
                        settings.timeFormat == "24-Hour" -> true
                        else -> false
                    }
                )

                isAddingTimeLog = false
            }
        ) {
            DialogBoxSkeleton(
                width = 550.dp,
                height = 350.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Add Time Log",
                        fontSize = 25.sp,
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = selectedStartTimeText,
                        onValueChange = {  },
                        label = { Text("Start Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.size(40.dp, 40.dp),
                                onClick = {
                                    openStartTimePicker = true
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
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Start Time",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = selectedEndTimeText,
                        onValueChange = {  },
                        label = { Text("End Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.size(40.dp, 40.dp),
                                onClick = {
                                    endTimePickerState = startTimePickerState
                                    openEndTimePicker = true
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
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "End Time",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { },
                        label = { Text("Task Deadline") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                tempLogId = 0
                                longSelectedStartTime = 0L
                                longSelectedEndTime = 0L
                                tempSelectedDate = "--/--/----"
                                selectedStartTimeText = "No selected time"
                                selectedEndTimeText = "No selected time"

                                startTimePickerState = TimePickerState(
                                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                    initialMinute = currentTime.get(Calendar.MINUTE),
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )
                                endTimePickerState = TimePickerState(
                                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                    initialMinute = currentTime.get(Calendar.MINUTE),
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )

                                isAddingTimeLog = false
                            },
                            colors = ButtonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = {
                                if (longSelectedStartTime != 0L && longSelectedEndTime != 0L && tempSelectedDate != "--/--/----") {
                                    viewModel.createTimeLog(
                                        projectId = projectId,
                                        startTime = longSelectedStartTime,
                                        endTime = longSelectedEndTime,
                                        date = tempSelectedDate
                                    )

                                    longSelectedStartTime = 0L
                                    longSelectedEndTime = 0L
                                    tempSelectedDate = "--/--/----"
                                    selectedStartTimeText = "No selected time"
                                    selectedEndTimeText = "No selected time"

                                    startTimePickerState = TimePickerState(
                                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                        initialMinute = currentTime.get(Calendar.MINUTE),
                                        is24Hour = when {
                                            settings.timeFormat == "24-Hour" -> true
                                            else -> false
                                        }
                                    )
                                    endTimePickerState = TimePickerState(
                                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                        initialMinute = currentTime.get(Calendar.MINUTE),
                                        is24Hour = when {
                                            settings.timeFormat == "24-Hour" -> true
                                            else -> false
                                        }
                                    )

                                    isAddingTimeLog = false
                                } else {
                                    showEmptyFieldDialog = true
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Confirm",
                                fontWeight = Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // edit time log
    if (isEditingTimeLog) {
        Dialog(
            onDismissRequest = {
                tempLogId = 0
                longSelectedStartTime = 0L
                longSelectedEndTime = 0L
                tempSelectedDate = "--/--/----"
                selectedStartTimeText = "No selected time"
                selectedEndTimeText = "No selected time"

                startTimePickerState = TimePickerState(
                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                    initialMinute = currentTime.get(Calendar.MINUTE),
                    is24Hour = when {
                        settings.timeFormat == "24-Hour" -> true
                        else -> false
                    }
                )
                endTimePickerState = TimePickerState(
                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                    initialMinute = currentTime.get(Calendar.MINUTE),
                    is24Hour = when {
                        settings.timeFormat == "24-Hour" -> true
                        else -> false
                    }
                )

                isEditingTimeLog = false
                expandTaskOptions = null
            }
        ) {
            DialogBoxSkeleton(
                width = 550.dp,
                height = 350.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Edit Time Log",
                        fontSize = 25.sp,
                        fontWeight = Bold
                    )

                    OutlinedTextField(
                        value = selectedStartTimeText,
                        onValueChange = {  },
                        label = { Text("Start Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.size(40.dp, 40.dp),
                                onClick = {
                                    openStartTimePicker = true
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
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Start Time",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = selectedEndTimeText,
                        onValueChange = {  },
                        label = { Text("End Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.size(40.dp, 40.dp),
                                onClick = {
                                    openEndTimePicker = true
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
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "End Time",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = if (tempSelectedDate == "--/--/----") selectedDate else tempSelectedDate,
                        onValueChange = { },
                        label = { Text("Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                tempLogId = 0
                                longSelectedStartTime = 0L
                                longSelectedEndTime = 0L
                                tempSelectedDate = "--/--/----"
                                selectedStartTimeText = "No selected time"
                                selectedEndTimeText = "No selected time"

                                startTimePickerState = TimePickerState(
                                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                    initialMinute = currentTime.get(Calendar.MINUTE),
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )
                                endTimePickerState = TimePickerState(
                                    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                    initialMinute = currentTime.get(Calendar.MINUTE),
                                    is24Hour = when {
                                        settings.timeFormat == "24-Hour" -> true
                                        else -> false
                                    }
                                )

                                isEditingTimeLog = false
                                expandTaskOptions = null
                            },
                            colors = ButtonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontWeight = Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = {
                                if (longSelectedStartTime != 0L && longSelectedEndTime != 0L && tempSelectedDate != "--/--/----") {
                                    viewModel.editTimeLogInfo(
                                        logId = tempLogId,
                                        startTime = longSelectedStartTime,
                                        endTime = longSelectedEndTime,
                                        date = tempSelectedDate
                                    )

                                    tempLogId = 0
                                    longSelectedStartTime = 0L
                                    longSelectedEndTime = 0L
                                    tempSelectedDate = "--/--/----"
                                    selectedStartTimeText = "No selected time"
                                    selectedEndTimeText = "No selected time"

                                    startTimePickerState = TimePickerState(
                                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                        initialMinute = currentTime.get(Calendar.MINUTE),
                                        is24Hour = when {
                                            settings.timeFormat == "24-Hour" -> true
                                            else -> false
                                        }
                                    )
                                    endTimePickerState = TimePickerState(
                                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                                        initialMinute = currentTime.get(Calendar.MINUTE),
                                        is24Hour = when {
                                            settings.timeFormat == "24-Hour" -> true
                                            else -> false
                                        }
                                    )

                                    isEditingTimeLog = false
                                    expandTaskOptions = null
                                } else {
                                    showEmptyFieldDialog = true
                                }
                            },
                            colors = ButtonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Confirm",
                                fontWeight = Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // start time picker
    if (openStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { openStartTimePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val hour = startTimePickerState.hour
                        val minute = startTimePickerState.minute

                        longSelectedStartTime = LocalTime.of(hour, minute).toNanoOfDay()

                        selectedStartTimeText = when {
                            settings.timeFormat == "12-Hour" -> {
                                val amPm = if (startTimePickerState.hour >= 12) "PM" else "AM"
                                val displayHour = when {
                                    startTimePickerState.hour == 0 -> 12
                                    startTimePickerState.hour > 12 -> startTimePickerState.hour - 12
                                    else -> startTimePickerState.hour
                                }

                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d %s",
                                    displayHour,
                                    startTimePickerState.minute,
                                    amPm
                                )
                            }
                            else -> {
                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    hour,
                                    minute
                                )
                            }
                        }

                        openStartTimePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    )
                    // modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openStartTimePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 90.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            title = { Text("Start Time") }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    // end time picker
    if (openEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { openEndTimePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val hour = endTimePickerState.hour
                        val minute = endTimePickerState.minute

                        longSelectedEndTime = LocalTime.of(hour, minute).toNanoOfDay()

                        selectedEndTimeText = when {
                            settings.timeFormat == "12-Hour" -> {
                                val amPm = if (endTimePickerState.hour >= 12) "PM" else "AM"
                                val displayHour = when {
                                    endTimePickerState.hour == 0 -> 12
                                    endTimePickerState.hour > 12 -> endTimePickerState.hour - 12
                                    else -> endTimePickerState.hour
                                }

                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d %s",
                                    displayHour,
                                    endTimePickerState.minute,
                                    amPm
                                )
                            }
                            else -> {
                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    hour,
                                    minute
                                )
                            }
                        }
                        openEndTimePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openEndTimePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 90.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            },
            title = { Text("End Time") }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }

    // show date picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempSelectedDate = selectedDate
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    if (showEmptyFieldDialog) {
        Dialog(
            onDismissRequest = {
                showEmptyFieldDialog = false
            }
        ) {
            DialogBoxSkeleton(
                width = 550.dp,
                height = 200.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Empty Field Warning",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Empty Field",
                        fontWeight = Bold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "Please fill in all fields.",
                        fontSize = 17.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = {
                            showEmptyFieldDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Cyan,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Dismiss",
                            fontWeight = Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    if (showTimeDifferenceErrorDialog) {
        Dialog(
            onDismissRequest = {
                showTimeDifferenceErrorDialog = false
            }
        ) {
            DialogBoxSkeleton(
                width = 550.dp,
                height = 200.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Time Difference Warning",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Time Difference",
                        fontWeight = Bold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "End time cannot be before start time.",
                        fontSize = 17.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = {
                            showTimeDifferenceErrorDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Cyan,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Cyan,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Dismiss",
                            fontWeight = Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceScreen(projectName: String, projectId: Int, clientId: Int, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val client = viewModel.clientState.collectAsStateWithLifecycle().value.find { it.id == clientId }
    val project = client?.projects?.find { it.id == projectId }
    val items = viewModel.items.collectAsStateWithLifecycle().value

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    var localInvoices by remember(project) { mutableStateOf(project?.invoices?: emptyList()) }
    var localItems by remember(items) { mutableStateOf(items) }

    val lazyListState = rememberLazyListState()
    val lazyListStateTwo = rememberLazyListState()

    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var tempInvoiceId by remember { mutableIntStateOf(0) }
    var tempClientName by remember { mutableStateOf("") }
    var tempClientEmail by remember { mutableStateOf("") }
    var tempClientTelephone by remember { mutableStateOf("") }
    var tempClientCompany by remember { mutableStateOf("") }
    var tempSelfName by remember { mutableStateOf("") }
    var tempSelfAddress by remember { mutableStateOf("") }
    var tempSelfEmail by remember { mutableStateOf("") }
    var tempSelfTelephone by remember { mutableStateOf("") }
    var tempIssueDate by remember { mutableStateOf("--/--/----") }
    var tempDueDate by remember { mutableStateOf("--/--/----") }
    var tempTaxPercentage by remember { mutableDoubleStateOf(0.0) }

    var isAddingInvoice by remember { mutableStateOf(false) }
    var showIssueDatePicker by remember { mutableStateOf(false) }
    var isAddingItem by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$projectName Invoices",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/overview") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Overview")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/tasks") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Tasks")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/logs") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Time Logs")
                }

                Button(
                    onClick = { navController.navigate("project/${projectName}/${projectId}/${clientId}/invoices") },
                    shape = RectangleShape,
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Invoices")
                }
            }
        }

        // Begin making invoice rows
        LazyColumn(
            state = lazyListState
        ) {
            items(localInvoices, key = { invoice -> invoice.id }) { invoice ->
                Card(
                    elevation = CardDefaults.elevatedCardElevation(5.dp ,5.dp,5.dp,5.dp,5.dp,5.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(

                        ) {
                            Text(
                                text = invoice.issueDate,
                                fontSize = 22.sp,
                                fontWeight = Bold,
                                color = Color.Black
                            )

                            Text(
                                text = "${invoice.amount}",
                                color = Color.Gray,
                                fontSize = 17.sp
                            )
                        }

                        Text(
                            text = invoice.status,
                            color = when(invoice.status) {
                                InvoiceStatus.DRAFT.name -> Color.Gray
                                InvoiceStatus.SENT.name -> Color.Yellow
                                InvoiceStatus.PAID.name -> Color.Green
                                else -> Color.Red
                            },
                            fontSize = 20.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            tempInvoiceId = localInvoices.size

                            viewModel.createInvoice(
                                projectId = projectId,
                                invoiceNumber = "",
                                issueDate = tempIssueDate,
                                dueDate = tempDueDate,
                                issueTo = tempClientName,
                                clientCompany = tempClientCompany,
                                clientEmail = tempClientEmail,
                                clientTelephone = tempClientTelephone,
                                payTo = tempSelfName,
                                selfAddress = tempSelfAddress,
                                selfEmail = tempSelfEmail,
                                selfTelephone = tempSelfTelephone,
                                taxPercentage = tempTaxPercentage
                            )

                            isAddingInvoice = true
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Blue
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Invoice",
                            modifier = Modifier.size(23.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // DIALOG BOXES

    // add invoice from scratch
    if (isAddingInvoice) {
        Dialog(
            onDismissRequest = {
                viewModel.deleteInvoice(localInvoices.lastIndex)
                isAddingInvoice = false
            }
        ) {
            DialogBoxSkeleton(
                width = screenWidth - 40.dp,
                height = screenHeight - 100.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    item {
                        val padding = 10.dp

                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = Color.Gray,
                            contentDescription = "GenerateInvoice",
                            modifier = Modifier.size(30.dp)
                        )

                        Text(
                            text = "Generate Invoice",
                            fontSize = 25.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.Black)

                        Text(
                            text = "Client Information",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        OutlinedTextField(
                            value = tempClientName,
                            onValueChange = { text ->
                                if (tempClientName.length < 30) {
                                    tempClientName = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Client Name") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempClientCompany,
                            onValueChange = { text ->
                                if (tempClientCompany.length < 30) {
                                    tempClientCompany = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Client Company") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempClientEmail,
                            onValueChange = { text ->
                                if (tempClientEmail.length < 30) {
                                    tempClientEmail = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Client Email") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempClientTelephone,
                            onValueChange = { text ->
                                if (tempClientTelephone.length < 30) {
                                    tempClientTelephone = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Client Telephone") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        Text(
                            text = "Self Information",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        OutlinedTextField(
                            value = tempSelfName,
                            onValueChange = { text ->
                                if (tempSelfName.length < 30) {
                                    tempSelfName = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempSelfAddress,
                            onValueChange = { text ->
                                if (tempSelfAddress.length < 30) {
                                    tempSelfAddress = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Address") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempSelfEmail,
                            onValueChange = { text ->
                                if (tempSelfEmail.length < 30) {
                                    tempSelfEmail = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempSelfTelephone,
                            onValueChange = { text ->
                                if (tempSelfTelephone.length < 30) {
                                    tempSelfTelephone = text
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Telephone") },
                            singleLine = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        Text(
                            text = "Dates",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        OutlinedTextField(
                            value = tempIssueDate,
                            onValueChange = {  },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Issue Date") },
                            singleLine = true,
                            readOnly = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        OutlinedTextField(
                            value = tempDueDate,
                            onValueChange = {  },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Due Date") },
                            singleLine = true,
                            readOnly = true,
                            modifier = Modifier.padding(vertical = padding)
                        )

                        Text(
                            text = "Tax Percentage",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        OutlinedTextField(
                            value = tempTaxPercentage.toString(),
                            onValueChange = { text ->
                                val isValidDecimal = text.count { it == '.' } <= 1 &&
                                        text.all { it.isDigit() || it == '.' }
                                if (isValidDecimal) {
                                    tempTaxPercentage = text.toDouble()
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            label = { Text("Tax Percentage") },
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    tint = Color.Black,
                                    modifier = Modifier.size(35.dp),
                                    contentDescription = "Tax Percentage"
                                )
                            },
                            modifier = Modifier.padding(vertical = padding)
                        )

                        Text(
                            text = "Items",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        LazyColumn(
                            state = lazyListStateTwo,
                            modifier = Modifier.size(screenWidth - 40.dp, 350.dp)
                        ) {
                            items(localItems.filter { it.invoiceId == tempInvoiceId }) {
                                Row() { }
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        modifier = Modifier.size(40.dp),
                                        onClick = {
                                            viewModel.createItem(
                                                invoiceId = tempInvoiceId,
                                                name = "",
                                                price = 0.0,
                                                quantity = 0
                                            )

                                            isAddingItem = true
                                        },
                                        colors = IconButtonColors(
                                            containerColor = Color.Blue,
                                            contentColor = Color.White,
                                            disabledContentColor = Color.White,
                                            disabledContainerColor = Color.Blue
                                        ),
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Invoice Item",
                                            modifier = Modifier.size(23.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.deleteInvoice(localInvoices.lastIndex)
                                    isAddingInvoice = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 14.sp,
                                    fontWeight = Bold,
                                    color = Color.Black
                                )
                            }

                            Button(
                                onClick = {


                                    isAddingInvoice = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "Confirm",
                                    fontSize = 14.sp,
                                    fontWeight = Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // add item
    if (isAddingItem) {
        Dialog(
            onDismissRequest = {
                viewModel.deleteItem(items.lastIndex)

                isAddingItem = false
            }
        ) {
            DialogBoxSkeleton(
                width = 550.dp,
                height = 600.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Add Item",
                        fontSize = 25.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )


                }
            }
        }
    }

    // show issue date picker
    if (showIssueDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showIssueDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempIssueDate = selectedDate
                        showIssueDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Cyan,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Cyan,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showIssueDatePicker = false
                    },
                    // shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Black
                    ),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun SettingsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var timeFormatChoiceString by remember(settings) { mutableStateOf(settings.timeFormat) }

    var isChoosingTimeFormat by remember { mutableStateOf(false) }

    val changedSettings = when {
        (timeFormatChoiceString != settings.timeFormat) -> true
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // save button
                Button(
                    enabled = changedSettings,
                    onClick = {
                        viewModel.updateSettings(
                            timeFormat = timeFormatChoiceString
                        )

                        Toast.makeText(context, "Saved Changes", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContentColor = Color.LightGray,
                        disabledContainerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 17.sp,
                        fontWeight = Bold
                    )
                }

                Text(
                    text = " Settings",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                // filler padding
                Button(
                    onClick = {  },
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 17.sp,
                        fontWeight = Bold
                    )
                }
            }
        }

        // Time Format
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Time Format",
                modifier = Modifier.padding(21.dp),
                fontSize = 18.sp,
                color = Color.Black
            )

            Card(
                border = BorderStroke(2.dp, color = Color.Gray),
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(10.dp)
                    .clickable(
                        onClick = {
                            isChoosingTimeFormat = true
                        }
                    )
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = timeFormatChoiceString)
                }
                DropdownMenu(
                    expanded = isChoosingTimeFormat,
                    onDismissRequest = { isChoosingTimeFormat = false },
                    modifier = Modifier.heightIn(max = 180.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("12-Hour") },
                        onClick = {
                            timeFormatChoiceString = "12-Hour"
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("24-Hour") },
                        onClick = {
                            timeFormatChoiceString = "24-Hour"
                        }
                    )
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

// HELPER FUNCTIONS
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun TimerText(timeLog: HomeViewModel.TimeLogData?, settings: SettingsEntity) {
    val longStartTime = timeLog?.startTime

    val startTime = LocalTime.ofNanoOfDay(longStartTime?: 0)
    var timeRightNow by remember { mutableStateOf(LocalTime.now()) }

    // val longPausedStartTime = timeLog?.pauseStartTime?: timeRightNow.toNanoOfDay()

    // val longCurrentPausedTime = timeRightNow.toNanoOfDay() - longPausedStartTime

    // val longCorrectedCurrentTime = timeRightNow.toNanoOfDay() - (timeLog?.totalPauseTime?: 0) - longCurrentPausedTime

    // val correctedCurrentTime = LocalTime.ofNanoOfDay(longCorrectedCurrentTime)

    // 2. Calculate the duration dynamically based on the living state variable
    val timerTime = Duration.between(startTime, timeRightNow)

    // 3. Keep your ticking engine running every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000.milliseconds)
            // 4. ✅ FIX: Grab a fresh snapshot of the clock every second to trigger a screen refresh
            timeRightNow = LocalTime.now()
        }
    }

    Text(
        text = if (settings.isTiming && timeLog != null) {
            "${timerTime.toHours()}:${String.format("%02d", timerTime.toMinutes() % 60)}:${String.format("%02d", timerTime.seconds % 60)}"
        } else {
            "0:00:00"
        },
        fontSize = 20.sp,
        fontWeight = Bold,
        fontFamily = FontFamily.SansSerif,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DialogBoxSkeleton(width: Dp, height: Dp, content: @Composable (() -> Unit)) {
    Surface(
        color = Color.White,
        modifier = Modifier.size(width, height),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(2.dp, Color.Gray)
    ) { content() }
}