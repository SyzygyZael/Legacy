package com.kevinnesbitt.legacysecurefreelancercrm

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.material.datepicker.MaterialDatePicker
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.ui.theme.LegacySecureFreelancerCRMTheme
import com.kevinnesbitt.legacysecurefreelancercrm.variables.BillingType
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ClientStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                                    .background(color = Color.White),
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
                            HomeScreen(viewModel, navController, innerPadding)
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
                                "tasks" -> TasksScreen(projectName, projectId, clientId, hubTab, viewModel, innerPadding, navController)
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
fun HomeScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    val graphGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF87CEEBL),
            Color(0xFF00FFFFL)
        )
    )

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

            // Active Projects
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
                                contentDescription = "Active Projects",
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(4.dp),
                                tint = Color.Blue
                            )
                        }

                        Text(
                            text = " Active Projects",
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
                .clickable(
                    onClick = {

                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .size(width = screenWidth - 15.dp, height = 115.dp)
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


            }
        }
    }
}

@Composable
fun ClientsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val clientStates by viewModel.clientState.collectAsStateWithLifecycle()

    var localClientStates by remember {
        mutableStateOf(emptyList<HomeViewModel.ClientData>())
    }

    var tempNameText by remember {
        mutableStateOf("")
    }

    var tempEmailText by remember {
        mutableStateOf("")
    }

    var tempHourlyRate by remember {
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

        LazyColumn(
            modifier = Modifier.padding(6.dp)
        ) {
            if (localClientStates.isNotEmpty()) {
                items(localClientStates) { client ->
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
                            }

                            DropdownMenu(
                                expanded = expandClientOptions == client.id,
                                onDismissRequest = { expandClientOptions = null }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Archive",
                                            fontSize = 18.sp,
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

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var showEditClientDialog by remember {
        mutableStateOf(false)
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

    var expandCurrencyChoice by remember {
        mutableStateOf(false)
    }

    var expandBillingTypeChoice by remember {
        mutableStateOf(false)
    }

    var isAddingProject by remember {
        mutableStateOf(false)
    }

    var tempProjectTitle by remember {
        mutableStateOf("")
    }

    var tempProjectDescription by remember {
        mutableStateOf("")
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

    var expandProjectOptions by remember {
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projects",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(all = 15.dp)
                )
            }
        }

        if (clientProjects.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(clientProjects) { project ->
                    Card(
                        elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                                .background(color = Color.White)
                                .combinedClickable(
                                    onClick = {
                                        navController.navigate("project/${project.title}/${project.id}/${clientId}/overview")
                                    },
                                    onLongClick = { expandProjectOptions = true }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

                            var statusColor: Color = Color.White
                            when(project.status) {
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
                            expanded = expandProjectOptions,
                            onDismissRequest = { expandProjectOptions = false }
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

                                        expandProjectOptions = false
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

                                        expandProjectOptions = false
                                    }
                                )
                            }

                            if (project.status != ProjectStatus.ACTIVE.name) {
                                DropdownMenuItem(
                                    text = { Text("Active") },
                                    onClick = {
                                        viewModel.updateProjectStatus(
                                            ProjectStatus.ACTIVE.name,
                                            project.id,
                                            clientId
                                        )

                                        expandProjectOptions = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

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

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.WEEKLY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.WEEKLY.name
                                        expandBillingTypeChoice = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.DAILY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.DAILY.name
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
    var currentTab = hubTab.replaceRange(0, 1, hubTab[0].uppercase())

    val description = project?.description?: ""

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

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
                            text = "${project?.tasks?.size}",
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
                            BillingType.DAILY.name -> { rateType = "/day" }
                            BillingType.WEEKLY.name -> { rateType = "/week" }
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

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.WEEKLY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.WEEKLY.name
                                        expandBillingTypeChoice = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(text = BillingType.DAILY.name) },
                                    onClick = {
                                        tempProjectBillingType = BillingType.DAILY.name
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
                                        newBudget = tempProjectBudget,
                                        newStatus = tempProjectStatus
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
fun TasksScreen(projectName: String, projectId: Int, clientId: Int, hubTab: String, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
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
                            viewModel.updateTasksOrder(projectId, localTasks)
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

@Composable
fun SettingsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
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
                    text = " Settings",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )
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
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun editTask(taskId: Int, taskName: String, taskDeadline: String) {

}