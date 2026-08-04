package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencySymbol
import com.kevinnesbitt.legacysecurefreelancercrm.variables.BillingType
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus

@Composable
fun ProjectOverviewScreen(projectId: Int, clientId: Int, viewModel: HomeViewModel) {
    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val project = clientState?.projects?.find { projectId == it.id }
    val currencySymbol = getCurrencySymbol(clientState?.currency?: "Unknown")

    val description = project?.description?: ""
    val completedTasks = project?.tasks?.filter { it.isCompleted }?.size

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    val tasks = project?.tasks?: emptyList()
    val currentTask = when(tasks) {
        emptyList<HomeViewModel.TaskData>() -> HomeViewModel.TaskData(id = -1, description = "No Tasks", projectId = -1, isCompleted = false, dueDate = "")
        else -> tasks.first()
    }

    val pendingInvoices = (project?.invoices?: emptyList()).filter { invoice -> invoice.status == InvoiceStatus.SENT.name }
    val totalEarnings = (project?.invoices?: emptyList()).filter { invoice -> invoice.status == InvoiceStatus.PAID.name }
        .sumOf { paidInvoice -> paidInvoice.amount }

    var localDescription by remember(description) { mutableStateOf(TextFieldValue(text = description)) }

    var editDescription by remember { mutableStateOf(false) }

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
                        text = "${currencySymbol}$totalEarnings",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )

                    // Surface(
                    //     modifier = Modifier
                    //         .size(52.dp, 20.dp),
                    //     shape = RoundedCornerShape(8.dp),
                    //     color = Color(0xFF98FF98L).copy(alpha = 0.2f)
                    // ) {
                    //     Text(
                    //         text = "^ +0.0%",
                    //         modifier = Modifier
                    //             .size(10.dp)
                    //             .padding(2.dp),
                    //         color = Color(0xFF228B22L),
                    //         textAlign = TextAlign.Center,
                    //         fontWeight = Bold,
                    //         fontSize = 11.sp
                    //     )
                    // }
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
                        text = currentTask.description,
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
                        text = pendingInvoices.size.toString(),
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
                        text = "${currencySymbol}${project?.payRate}${rateType}",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}