package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBoxSkeleton
import com.kevinnesbitt.legacysecurefreelancercrm.util.InfoCard
import com.kevinnesbitt.legacysecurefreelancercrm.util.ListCard
import com.kevinnesbitt.legacysecurefreelancercrm.util.TimerText
import com.kevinnesbitt.legacysecurefreelancercrm.util.WaveBarChart
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencySymbol
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import java.time.LocalTime
import androidx.compose.ui.platform.LocalLocale
import com.kevinnesbitt.legacysecurefreelancercrm.util.getLast6MonthsEarnings
import com.kevinnesbitt.legacysecurefreelancercrm.util.getLastSixMonths
import com.kevinnesbitt.legacysecurefreelancercrm.util.getMonthFromShortForm

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
    // val activeProjectClient = clientState.find { it.id == (activeProject?.clientId?: 0) }

    val userName = settings.selfName.split(" ")[0]

    android.util.Log.d("User Name", "Name: ${settings.selfName}")

    val allProjects = clientState.flatMap { client ->
        client.projects
    }

    val paidInvoices = clientState.flatMap { client -> client.projects }
        .flatMap { project -> project.invoices }
        .filter { invoice -> invoice.status == InvoiceStatus.PAID.name }

    val earnings = getLast6MonthsEarnings(paidInvoices, settings, clientState, allProjects)

    val lazyListState = rememberLazyListState()

    val currencySymbol = getCurrencySymbol(settings.preferredCurrency)

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
                    text = if (userName == "") " Dashboard" else " Hello $userName.",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )
            }
        }

        LazyColumn(
            state = lazyListState
        ) {
            item {
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
                            .padding(top = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Monthly Earnings",
                                tint = Color.White,
                                modifier = Modifier.padding(all = 8.dp)

                            )

                            Text(
                                text = "Monthly Earnings",
                                color = Color.White,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(all = 8.dp)
                            )
                        }

                        val thisMonthEarnings = String.format(
                            LocalLocale.current.platformLocale,
                            "%.2f",
                            earnings.last() * ((100 - settings.taxBracket) / 100)
                        )
                        Text(
                            text = "${currencySymbol}${thisMonthEarnings} ${getMonthFromShortForm(getLastSixMonths().last())}",
                            fontSize = 25.sp,
                            fontWeight = Bold,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 50.dp, start = 20.dp),
                            textAlign = TextAlign.Start
                        )

                        WaveBarChart(
                            dataPoints = earnings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            color = Color(0xFF64B5F6),
                            labels = getLastSixMonths()
                        )
                    }
                }

                // METRICS
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 15.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Total earnings
                        InfoCard(
                            title = "Total Earnings",
                            icon = Icons.Default.StackedLineChart,
                            iconTint = Color.Blue,
                            iconBackgroundColor = Color(0xFF00FFFFL).copy(alpha = 0.3f),
                            value = "${currencySymbol}${
                                String.format(
                                    LocalLocale.current.platformLocale,
                                    "%.2f",
                                    uiState.totalEarnings * ((100 - settings.taxBracket) / 100)
                                )
                            }",
                            fontSize = 18.sp
                        )

                        // Pending Invoices
                        InfoCard(
                            title = "Pending Invoices",
                            icon = Icons.Default.CurrencyExchange,
                            iconTint = Color.Blue,
                            iconBackgroundColor = Color(0xFF00FFFFL).copy(alpha = 0.3f),
                            value = uiState.pendingInvoices.toString(),
                            fontSize = 30.sp
                        )

                        // Timer
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
                                val timeLog: HomeViewModel.TimeLogData? = if (activeProject?.timeLogs?.isNotEmpty() == true) {
                                    activeProject.timeLogs.first()
                                } else {
                                    null
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
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
                                                modifier = Modifier.size(28.dp, 28.dp),
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
                                                        .size(28.dp)
                                                        .padding(4.dp),
                                                    tint = Color.Black
                                                )
                                            }
                                        } else {
                                            IconButton(
                                                modifier = Modifier.size(28.dp, 28.dp),
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
                                                        .size(28.dp)
                                                        .padding(4.dp),
                                                    tint = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }

                                TimerText(timeLog, settings)

                                Text(
                                    text = activeProject?.title?: " No Active Project ",
                                    color = Color.Gray,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Column {
                        // Priority Tasks
                        ListCard(
                            title = "Priority Tasks",
                            icon = Icons.Default.Task,
                            iconTint = Color.Blue,
                            iconBackgroundColor = Color(0xFF00FFFFL).copy(alpha = 0.3f)
                        ) {
                            val list = uiState.highPriorityTasks

                            items(list) { task ->
                                val taskIndex = list.indexOf(task) + 1

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${taskIndex}. ${task.description}",
                                        fontSize = 17.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        // Projects
                        InfoCard(
                            title = "Projects",
                            icon = Icons.Default.Book,
                            iconTint = Color.Blue,
                            iconBackgroundColor = Color(0xFF00FFFFL).copy(alpha = 0.3f),
                            value = "${allProjects.size}",
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }

    // DIALOG BOXES

    if (showNullActiveProjectDialog) {
        DialogBoxSkeleton(
            onDismissRequest = { showNullActiveProjectDialog = false },
            width = 550.dp,
            height = 200.dp
        ) {
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