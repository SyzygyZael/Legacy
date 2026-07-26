package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.convertMillisToDate
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencySymbol
import com.kevinnesbitt.legacysecurefreelancercrm.variables.BillingType

@Composable
fun ProjectOverviewScreen(projectName: String, projectId: Int, clientId: Int, hubTab: String, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val project = clientState?.projects?.find { projectId == it.id }
    val currentTab = hubTab.replaceRange(0, 1, hubTab[0].uppercase())
    val currencySymbol = getCurrencySymbol(clientState?.currency?: "Unknown")

    val description = project?.description?: ""
    val completedTasks = project?.tasks?.filter { it.isCompleted }?.size

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it, settings)
    } ?: ""

    var tempProjectTitle by remember { mutableStateOf("") }
    var tempProjectDescription by remember { mutableStateOf("") }
    var localDescription by remember(description) { mutableStateOf(TextFieldValue(text = description)) }
    var tempProjectRate by remember { mutableDoubleStateOf(0.0) }
    var tempProjectBudget by remember { mutableDoubleStateOf(0.0) }
    var tempProjectBillingType by remember { mutableStateOf("") }
    var tempProjectDeadline by remember { mutableStateOf("--/--/----") }
    var tempProjectStatus by remember { mutableStateOf("") }

    var expandBillingTypeChoice by remember { mutableStateOf(false) }
    var editProjectInfo by remember { mutableStateOf(false) }
    var editDescription by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(color = Color(0xFFF2F2F2L)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(5.dp, 5.dp, 5.dp, 5.dp, 5.dp, 5.dp),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
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

            Text(
                text = "Budget: ${currencySymbol}${project?.budget?: "N/A"}",
                fontSize = 17.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
                    .background(color = Color.White),
                textAlign = TextAlign.Start
            )

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
                                value = tempProjectDeadline,
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
                        singleLine = true,
                        leadingIcon = {
                            Text(currencySymbol)
                        }
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
                        singleLine = true,
                        leadingIcon = {
                            Text(currencySymbol)
                        }
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
                                tempProjectTitle = ""
                                tempProjectDescription = ""
                                tempProjectBudget = 0.0
                                tempProjectRate = 0.0
                                tempProjectDeadline = "--/--/----"
                                tempProjectBillingType = ""
                                tempProjectStatus = ""

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