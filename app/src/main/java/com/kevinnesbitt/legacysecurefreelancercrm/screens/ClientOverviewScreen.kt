package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.TextDropDown
import com.kevinnesbitt.legacysecurefreelancercrm.util.convertMillisToDate
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencyName
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencySymbol
import com.kevinnesbitt.legacysecurefreelancercrm.variables.BillingType
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalLocale
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBox
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus

@Composable
fun ClientOverviewScreen(clientId: Int, viewModel: HomeViewModel, innerPadding: PaddingValues, navController: NavController) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val clientState = viewModel.clientState.collectAsStateWithLifecycle().value.find { clientId == it.id }
    val clientName = clientState?.name?: ""
    val clientCompany = clientState?.company?: ""
    val clientEmail = clientState?.email?: ""
    val clientPhoneNum = clientState?.telp?: ""
    val clientProjects = clientState?.projects?: emptyList()
    val clientCurrency = clientState?.currency?: "Unknown"
    val currencySymbol = getCurrencySymbol(clientCurrency)
    val currencyDisplayName = getCurrencyName(clientCurrency)

    val totalEarnings = clientProjects.flatMap { project -> project.invoices }
        .filter { invoice -> invoice.status == InvoiceStatus.PAID.name }
        .sumOf { paidInvoice -> paidInvoice.amount }

    val clients = viewModel.clientState.collectAsStateWithLifecycle().value
    val activeProjects = clients.flatMap { client ->
        client.projects.filter { it.status == ProjectStatus.ACTIVE.name }
    }

    val currentDateObj = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(settings.dateFormat, LocalLocale.current.platformLocale)
    val currentDateStr = formatter.format(currentDateObj)

    clientProjects.forEach { project ->
        if (currentDateStr > project.deadLine) {
            viewModel.updateProjectStatus(
                projectId = project.id,
                clientId = clientId,
                status = ProjectStatus.OVERDUE.name
            )
        } else if (project.status == ProjectStatus.OVERDUE.name) {
            viewModel.updateProjectStatus(
                projectId = project.id,
                clientId = clientId,
                status = ProjectStatus.PAUSED.name
            )
        }
    }


    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it, settings)
    } ?: ""

    var localProjects by remember(clientProjects) { mutableStateOf(clientProjects) }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState = lazyListState) { from, to ->
        localProjects = localProjects.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    var showEditClientDialog by remember { mutableStateOf(false) }

    var tempProjectId by remember { mutableIntStateOf(0) }

    var tempNameText by remember { mutableStateOf("") }
    var tempCompanyText by remember { mutableStateOf("") }
    var tempEmailText by remember { mutableStateOf("") }
    var tempTelpNum by remember { mutableStateOf("") }
    var tempCurrency by remember { mutableStateOf("") }

    var expandCurrencyChoice by remember { mutableStateOf(false) }
    var expandBillingTypeChoice by remember { mutableStateOf(false) }
    var isAddingProject by remember { mutableStateOf(false) }
    var expandProjectStatusOptions by remember { mutableStateOf<Int?>(null) }
    var showDeletionWarning by remember { mutableStateOf(false) }

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
                        text = "Currency: $currencyDisplayName",
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
                                tempCompanyText = clientCompany
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
                                text = "${currencySymbol}$totalEarnings",
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
                                        navController.navigate("project/${project.title}/${project.id}/${clientId}")
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
                                            text = "Deadline: ${project.deadLine}",
                                            fontSize = 15.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                var statusColor: Color = Color.White
                                when (project.status) {
                                    "ARCHIVED" -> statusColor = Color(0xFFFF7518L)
                                    "PAUSED" -> statusColor = Color.Gray
                                    "ACTIVE" -> statusColor = Color.Green
                                    "OVERDUE" -> statusColor = Color.Red
                                }

                                TextDropDown(
                                    expanded = expandProjectStatusOptions == project.id,
                                    onDismissRequest = { expandProjectStatusOptions = null },
                                    onClick = {
                                        expandProjectStatusOptions = project.id
                                    },
                                    text = project.status,
                                    color = statusColor,
                                    fontSize = 22.sp
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

                                                expandProjectStatusOptions = null
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

                                                expandProjectStatusOptions = null
                                            }
                                        )
                                    }

                                    if (project.status != ProjectStatus.ACTIVE.name) {
                                        DropdownMenuItem(
                                            text = { Text("Active") },
                                            onClick = {
                                                if (activeProjects.isNotEmpty()) {
                                                    // ("Active Projects", "Num Active Projects: ${activeProjects.size}")
                                                    activeProjects.forEach { activeProject ->
                                                        viewModel.updateProjectStatus(
                                                            clientId = activeProject.clientId,
                                                            projectId = activeProject.id,
                                                            status = ProjectStatus.PAUSED.name
                                                        )
                                                    }
                                                }
                                                // ("Active Projects", "Num Active Projects: ${activeProjects.size}")

                                                viewModel.updateProjectStatus(
                                                    ProjectStatus.ACTIVE.name,
                                                    project.id,
                                                    clientId
                                                )

                                                expandProjectStatusOptions = null
                                            }
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = expandProjectOptions == project.id,
                                onDismissRequest = { expandProjectOptions = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Move") },
                                    onClick = {
                                        isReordering = true
                                        expandProjectOptions = null
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        tempProjectId = project.id
                                        showDeletionWarning = true
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
            onDismissRequest = {
                tempNameText = ""
                tempCompanyText = ""
                tempEmailText = ""
                tempCurrency = ""
                tempTelpNum = "0.0"

                showEditClientDialog = false
            }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier.size(350.dp, 550.dp),
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
                        text = "  Company or Address",
                        textAlign = TextAlign.Left,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = Bold
                    )
                    OutlinedTextField(
                        value = tempCompanyText,
                        onValueChange = { text ->
                            tempCompanyText = text
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
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
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
                            keyboardType = KeyboardType.Phone
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
                                tempNameText = ""
                                tempCompanyText = ""
                                tempEmailText = ""
                                tempCurrency = ""
                                tempTelpNum = "0.0"

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
                                        company = tempCompanyText,
                                        newEmail = tempEmailText,
                                        newCurrency = tempCurrency,
                                        newTelp = tempTelpNum,
                                        clientId = clientId
                                    )
                                    tempNameText = ""
                                    tempCompanyText = ""
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

                                    // ("Project Added", "Num Projects: ${clientProjects.size}")
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

    if (showDeletionWarning) {
        DialogBox(
            iconImageVector = Icons.Default.Warning,
            title = "Delete?",
            description = "You are about to delete a project. This is irreversible and will change the metrics you see on your dashboards.",
            onDismissRequest = { showDeletionWarning = false },
            buttonRow = {
                Button(
                    onClick = { showDeletionWarning = false },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.LightGray
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
                        val thisProject = clientProjects.find { project -> project.id == tempProjectId }
                        val thisProjectId = thisProject?.id?: 0
                        // val theseTasks = thisProject?.tasks?: emptyList()
                        // val theseTimeLogs = thisProject?.timeLogs?: emptyList()
                        // val theseInvoices = thisProject?.invoices?: emptyList()

                        viewModel.deleteTasksUnderProject(thisProjectId)
                        viewModel.deleteTimeLogsUnderProject(thisProjectId)
                        viewModel.deleteInvoicesUnderProject(thisProjectId)

                        viewModel.deleteProject(thisProjectId)

                        tempProjectId = 0

                        showDeletionWarning = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Cyan
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 14.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )
                }
            }
        ) {

        }
    }
}