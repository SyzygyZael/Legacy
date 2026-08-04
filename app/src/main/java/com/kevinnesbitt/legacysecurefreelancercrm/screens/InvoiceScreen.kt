package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Percent
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBox
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBoxSkeleton
import com.kevinnesbitt.legacysecurefreelancercrm.util.convertMillisToDate
import com.kevinnesbitt.legacysecurefreelancercrm.util.getCurrencySymbol
import com.kevinnesbitt.legacysecurefreelancercrm.util.sharePdf
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalLocale
import com.kevinnesbitt.legacysecurefreelancercrm.util.loadBitmapFromPath

@Composable
fun InvoiceScreen(projectId: Int, clientId: Int, viewModel: HomeViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val client = viewModel.clientState.collectAsStateWithLifecycle().value.find { it.id == clientId }
    val project = client?.projects?.find { it.id == projectId }
    val items by viewModel.items.collectAsStateWithLifecycle()
    val tasks  = project?.tasks?: emptyList()
    val timeLogs = project?.timeLogs?: emptyList()
    val clientCurrency = client?.currency?: "Unknown"

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    var localInvoices by remember(project) { mutableStateOf(project?.invoices?: emptyList()) }

    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(settings.dateFormat, LocalLocale.current.platformLocale)
    val currentDateStr = formatter.format(currentDate)

    val lazyListState = rememberLazyListState()
    val lazyListStateTwo = rememberLazyListState()

    val issueDatePickerState = rememberDatePickerState()
    val selectedIssueDate = issueDatePickerState.selectedDateMillis?.let {
        convertMillisToDate(it, settings)
    } ?: ""

    val dueDatePickerState = rememberDatePickerState()
    val selectedDueDate = dueDatePickerState.selectedDateMillis?.let {
        convertMillisToDate(it, settings)
    } ?: ""

    val currencySymbol = getCurrencySymbol(clientCurrency)

    var tempInvoiceId by remember { mutableIntStateOf(0) }
    var tempClientName by remember(client) { mutableStateOf(client?.name?: "") }
    var tempClientEmail by remember(client) { mutableStateOf(client?.email?: "") }
    var tempClientTelephone by remember(client) { mutableStateOf(client?.telp?: "") }
    var tempClientCompanyORAddress by remember(client) { mutableStateOf(client?.company?: "") }

    var tempSelfName by remember(settings) { mutableStateOf(settings.selfName) }
    var tempSelfAddress by remember(settings) { mutableStateOf(settings.selfAddress) }
    var tempSelfEmail by remember(settings) { mutableStateOf(settings.selfEmail) }
    var tempSelfTelephone by remember(settings) { mutableStateOf(settings.selfTelephone) }

    var tempIssueDate by remember { mutableStateOf("--/--/----") }
    var tempDueDate by remember { mutableStateOf("--/--/----") }
    var tempTaxPercentage by remember(settings) { mutableDoubleStateOf(settings.taxBracket) }
    var tempStatus by remember { mutableStateOf("") }

    var tempItemId by remember { mutableIntStateOf(0) }
    var tempItemName by remember { mutableStateOf("") }
    var tempPrice by remember { mutableDoubleStateOf(0.0) }
    var tempQuantity by remember { mutableIntStateOf(0) }

    var tempTaskPrice by remember { mutableDoubleStateOf(0.0) }
    var tempTaskName by remember { mutableStateOf("") }

    var expandInvoiceOptions by remember { mutableStateOf<Int?>(null) }
    var expandItemOptions by remember { mutableStateOf<Int?>(null) }
    var expandInvoiceStatusOptions by remember { mutableStateOf<Int?>(null) }

    val checkedLogs = remember { mutableStateListOf<Int>() }

    var isAddingInvoice by remember { mutableStateOf(false) }
    var isEditingInvoice by remember { mutableStateOf(false) }
    var showIssueDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    var isAddingItem by remember { mutableStateOf(false) }
    var showNameLengthWarningDialog by remember { mutableStateOf(false) }
    var expandPickItemsOptions by remember { mutableStateOf(false) }
    var showTasksDialog by remember { mutableStateOf(false) }
    var showTimeLogsDialog by remember { mutableStateOf(false) }
    var showPriceDialog by remember { mutableStateOf(false) }
    var showDeletionWarning by remember { mutableStateOf(false) }

    val invoiceItems = items.filter { it.invoiceId == tempInvoiceId }

    localInvoices.filter { invoice ->
        currentDateStr > invoice.dueDate
    }.forEach { dueInvoice ->
        if (dueInvoice.status != InvoiceStatus.PAID.name && !isAddingInvoice) {
            viewModel.updateInvoiceStatus(dueInvoice.id, InvoiceStatus.OVERDUE.name)
        }
    }

    // Begin making invoice rows
    LazyColumn(
        state = lazyListState
    ) {
        items(localInvoices, key = { invoice -> invoice.id }) { invoice ->
            android.util.Log.d("Invoice drawing", "invoice: $invoice")

            Card(
                elevation = CardDefaults.elevatedCardElevation(5.dp ,5.dp,5.dp,5.dp,5.dp,5.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(8.dp)
                        .background(color = Color.White)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                expandInvoiceOptions = invoice.id
                            }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = invoice.issueDate,
                            fontSize = 22.sp,
                            fontWeight = Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "${currencySymbol}${invoice.amount}",
                            color = Color.Gray,
                            fontSize = 17.sp
                        )
                    }

                    Row(
                        modifier = Modifier.clickable(
                            onClick = {
                                expandInvoiceStatusOptions = invoice.id
                            }
                        )
                    ) {
                        DropdownMenu(
                            expanded = expandInvoiceStatusOptions == invoice.id,
                            onDismissRequest = { expandInvoiceStatusOptions = null }
                        ) {
                            InvoiceStatus.entries.forEach { status ->
                                if (invoice.status != status.name) {
                                    DropdownMenuItem(
                                        text = { Text(status.name) },
                                        onClick = {
                                            if (status.name == InvoiceStatus.PAID.name) {
                                                viewModel.updateInvoicePaidDate(invoice.id, currentDateStr)
                                            }

                                            viewModel.updateInvoiceStatus(invoice.id, status.name)
                                            expandInvoiceStatusOptions = null
                                        }
                                    )
                                }
                            }
                        }

                        val rotationAngle by animateFloatAsState(
                            targetValue = if (expandInvoiceStatusOptions == invoice.id) -90f else 0f,
                            label = "IconRotationAnimation"
                        )

                        Text(
                            text = invoice.status,
                            color = when(invoice.status) {
                                InvoiceStatus.DRAFT.name -> Color.Gray
                                InvoiceStatus.SENT.name -> Color(0xFFFF7518L)
                                InvoiceStatus.PAID.name -> Color.Green
                                else -> Color.Red
                            },
                            fontSize = 20.sp
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                            contentDescription = "Status Dropdown",
                            tint = Color.Black,
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandInvoiceOptions == invoice.id,
                    onDismissRequest = { expandInvoiceOptions = null }
                ) {
                    DropdownMenuItem(
                        text = { Text("Generate PDF") },
                        onClick = {
                            val itemsForThisInvoice = items.filter { it.invoiceId == invoice.id }

                            val invoice = localInvoices.find { it.id == invoice.id }?: HomeViewModel.InvoiceData(
                                0, 0, "", 0.0, "", "", "", "", "", "", "", "", "", "", "", "", 0.0, emptyList()
                            )

                            val logoBitmap = loadBitmapFromPath(settings.invoiceLogoPath)

                            val uri = HomeViewModel.InvoicePdfGenerator.generate(
                                context = context,
                                invoice = invoice.copy(items = itemsForThisInvoice),
                                currencySymbol = currencySymbol,
                                signatureText = invoice.payTo,
                                logoBitmap = logoBitmap
                            )

                            sharePdf(context, uri)

                            expandInvoiceOptions = null
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            tempInvoiceId = invoice.id
                            tempIssueDate = invoice.issueDate
                            tempDueDate = invoice.dueDate
                            tempClientName = invoice.issueTo
                            tempClientCompanyORAddress = invoice.clientCompany
                            tempClientEmail = invoice.clientEmail
                            tempClientTelephone = invoice.clientTelephone
                            tempSelfName = invoice.payTo
                            tempSelfAddress = invoice.selfAddress
                            tempSelfEmail = invoice.selfEmail
                            tempSelfTelephone = invoice.selfTelephone
                            tempTaxPercentage = invoice.taxPercentage
                            tempStatus = invoice.status

                            android.util.Log.d("Id on tap Edit", "tempInvoiceId: $tempInvoiceId")

                            isAddingInvoice = true
                            isEditingInvoice = true
                            expandInvoiceOptions = null
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            tempInvoiceId = invoice.id
                            showDeletionWarning = true
                            expandInvoiceOptions = null
                        }
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
                        coroutineScope.launch {
                            val newInvoiceId = viewModel.createInvoice(
                                projectId = projectId,
                                invoiceNumber = "",
                                issueDate = tempIssueDate,
                                dueDate = tempDueDate,
                                issueTo = tempClientName,
                                clientCompany = tempClientCompanyORAddress,
                                clientEmail = tempClientEmail,
                                clientTelephone = tempClientTelephone,
                                payTo = tempSelfName,
                                selfAddress = tempSelfAddress,
                                selfEmail = tempSelfEmail,
                                selfTelephone = tempSelfTelephone,
                                taxPercentage = tempTaxPercentage,
                                status = InvoiceStatus.DRAFT.name
                            ).toInt()

                            tempInvoiceId = newInvoiceId

                            android.util.Log.d("Id after creation", "tempInvoiceId: $tempInvoiceId")

                            isAddingInvoice = true
                        }
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

    // DIALOG BOXES

    // add invoice from scratch
    if (isAddingInvoice) {
        DialogBoxSkeleton(
            onDismissRequest = {
                if (!isEditingInvoice) {
                    viewModel.deleteInvoice(tempInvoiceId)
                }

                tempIssueDate = "--/--/----"
                tempDueDate = "--/--/----"
                // tempClientName = ""
                // tempClientCompanyORAddress = ""
                // tempClientEmail = ""
                // tempClientTelephone = ""
                // tempSelfName = ""
                // tempSelfAddress = ""
                // tempSelfEmail = ""
                // tempSelfTelephone = ""
                tempTaxPercentage = 0.0
                // tempInvoiceId = 0

                isEditingInvoice = false
                isAddingInvoice = false
            },
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
                            if (tempClientName.length < 50) {
                                tempClientName = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Client Name") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempClientCompanyORAddress,
                        onValueChange = { text ->
                            tempClientCompanyORAddress = text
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Client Company or Address") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempClientEmail,
                        onValueChange = { text ->
                            tempClientEmail = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        label = { Text("Client Email") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempClientTelephone,
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '+' } <= 1 &&
                                    text.all { it.isDigit() || it == '+' }
                            if (isValidDecimal) {
                                tempClientTelephone = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone
                        ),
                        label = { Text("Client Telephone") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
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
                            if (tempSelfName.length < 50) {
                                tempSelfName = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempSelfAddress,
                        onValueChange = { text ->
                            tempSelfAddress = text
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Address") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempSelfEmail,
                        onValueChange = { text ->
                            tempSelfEmail = text
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
                    )

                    OutlinedTextField(
                        value = tempSelfTelephone,
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '+' } <= 1 &&
                                    text.all { it.isDigit() || it == '+' }
                            if (isValidDecimal) {
                                tempSelfTelephone = text
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone
                        ),
                        label = { Text("Telephone") },
                        singleLine = true,
                        modifier = Modifier.padding(all = padding)
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
                        modifier = Modifier.padding(all = padding),
                        trailingIcon = {
                            IconButton(
                                onClick = { showIssueDatePicker = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    tint = Color.Black,
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = "Issue Date"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = tempDueDate,
                        onValueChange = {  },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Due Date") },
                        singleLine = true,
                        readOnly = true,
                        modifier = Modifier.padding(all = padding),
                        trailingIcon = {
                            IconButton(
                                onClick = { showDueDatePicker = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    tint = Color.Black,
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = "Due Date"
                                )
                            }
                        }
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
                        value = if (tempTaxPercentage == 0.0) "" else tempTaxPercentage.toString(),
                        onValueChange = { text ->
                            val isValidDecimal = text.count { it == '.' } <= 1 &&
                                    text.all { it.isDigit() || it == '.' }
                            if (isValidDecimal) {
                                tempTaxPercentage = text.toDoubleOrNull() ?: 0.0
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        label = { Text("Tax Percentage") },
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Percent,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp),
                                contentDescription = "Tax Percentage"
                            )
                        },
                        modifier = Modifier.padding(all = padding)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Items",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 17.dp),
                            textAlign = TextAlign.Start
                        )


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = {
                                    expandPickItemsOptions = true
                                }
                            )
                        ) {
                            DropdownMenu(
                                expanded = expandPickItemsOptions,
                                onDismissRequest = { expandPickItemsOptions = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Time Logs") },
                                    onClick = {
                                        showTimeLogsDialog = true
                                        expandPickItemsOptions = false
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Tasks") },
                                    onClick = {
                                        showTasksDialog = true
                                        expandPickItemsOptions = false
                                    }
                                )
                            }

                            val angle by animateFloatAsState(
                                targetValue = if (expandPickItemsOptions) -90f else 0f,
                                label = "IconRotationAnimation"
                            )

                            Text(
                                text = "Select From",
                                fontSize = 17.sp,
                                color = Color.DarkGray
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                contentDescription = "Select From",
                                tint = Color.Black,
                                modifier = Modifier.rotate(angle)
                            )
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    LazyColumn(
                        state = lazyListStateTwo,
                        modifier = Modifier
                            .sizeIn(
                                minWidth = screenWidth - 40.dp,
                                maxHeight = 350.dp,
                                maxWidth = screenWidth - 40.dp,
                                minHeight = 100.dp
                            )
                            .background(color = Color.White)
                    ) {
                        items(invoiceItems, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.White)
                                    .clickable(
                                        onClick = { expandItemOptions = item.id }
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 18.sp,
                                    fontWeight = Bold,
                                    color = Color.Black
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Cost: $currencySymbol${item.price}",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    if (item.quantity != 0) {
                                        Text(
                                            text = "Quantity: ${item.quantity}",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    } else {
                                        Text(
                                            text = "Quantity: 0",
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = expandItemOptions == item.id,
                                onDismissRequest = { expandItemOptions = null }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        viewModel.deleteItem(item.id)
                                        expandItemOptions = null
                                    }
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
                                        coroutineScope.launch {
                                            val newItemId = viewModel.createItem(
                                                invoiceId = tempInvoiceId,
                                                name = "",
                                                price = 0.0,
                                                quantity = 0
                                            ).toInt()

                                            tempItemId = newItemId

                                            isAddingItem = true
                                        }
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
                                if (!isEditingInvoice) {
                                    viewModel.deleteInvoice(tempInvoiceId)
                                }

                                tempIssueDate = "--/--/----"
                                tempDueDate = "--/--/----"
                                // tempClientName = ""
                                // tempClientCompanyORAddress = ""
                                // tempClientEmail = ""
                                // tempClientTelephone = ""
                                // tempSelfName = ""
                                // tempSelfAddress = ""
                                // tempSelfEmail = ""
                                // tempSelfTelephone = ""
                                tempTaxPercentage = 0.0
                                // tempInvoiceId = 0

                                isEditingInvoice = false
                                isAddingInvoice = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
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
                                val invoiceNumber = when {
                                    tempSelfName.length >= 3 && tempClientName.length >= 3 -> {
                                        tempSelfName.substring(0, 3)
                                            .lowercase() + tempClientName.substring(0, 3)
                                            .lowercase() + tempInvoiceId
                                    }
                                    else -> {
                                        showNameLengthWarningDialog = true
                                        ""
                                    }
                                }

                                val amount = items.filter { it.invoiceId == tempInvoiceId }.sumOf { item ->
                                    val multiplier = when (item.quantity) {
                                        0 -> 1
                                        -1 -> 1
                                        else -> item.quantity
                                    }
                                    val subtotal = item.price * multiplier
                                    val taxAmount = subtotal * (tempTaxPercentage / 100.0)

                                    subtotal + taxAmount
                                }

                                viewModel.updateInvoice(
                                    invoiceId = tempInvoiceId,
                                    invoiceNumber = invoiceNumber,
                                    issueDate = tempIssueDate,
                                    dueDate = tempDueDate,
                                    issueTo = tempClientName,
                                    clientCompany = tempClientCompanyORAddress,
                                    clientEmail = tempClientEmail,
                                    clientTelephone = tempClientTelephone,
                                    payTo = tempSelfName,
                                    selfAddress = tempSelfAddress,
                                    selfEmail = tempSelfEmail,
                                    selfTelephone = tempSelfTelephone,
                                    taxPercentage = tempTaxPercentage,
                                    amount = amount,
                                    status = tempStatus
                                )

                                tempIssueDate = "--/--/----"
                                tempDueDate = "--/--/----"
                                tempClientName = ""
                                tempClientCompanyORAddress = ""
                                tempClientEmail = ""
                                tempClientTelephone = ""
                                // tempSelfName = ""
                                // tempSelfAddress = ""
                                // tempSelfEmail = ""
                                // tempSelfTelephone = ""
                                tempTaxPercentage = 0.0
                                // tempInvoiceId = 0

                                isEditingInvoice = false
                                isAddingInvoice = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Cyan,
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

    // add item
    if (isAddingItem) {
        DialogBoxSkeleton(
            onDismissRequest = {
                viewModel.deleteItem(tempItemId)
                tempItemName = ""
                tempQuantity = 0
                tempPrice = 0.0

                isAddingItem = false
            },
            width = 550.dp,
            height = 400.dp
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

                OutlinedTextField(
                    value = tempItemName,
                    label = { Text("Name") },
                    onValueChange = { text ->
                        if (tempItemName.length <= 30) {
                            tempItemName = text
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.padding(all = 10.dp)
                )

                OutlinedTextField(
                    value = tempQuantity.toString(),
                    label = { Text("Quantity") },
                    onValueChange = { text ->
                        val isValidDecimal = text.all { it.isDigit() }
                        if (isValidDecimal) {
                            tempQuantity = text.toIntOrNull() ?: 0
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.padding(all = 10.dp)
                )

                OutlinedTextField(
                    value = if (tempPrice == 0.0) "" else tempPrice.toString(),
                    label = { Text("Price Per Single") },
                    onValueChange = { text ->
                        val isValidDecimal = text.count { it == '.' } <= 1 &&
                                text.all { it.isDigit() || it == '.' }
                        if (isValidDecimal) {
                            tempPrice = text.toDoubleOrNull() ?: 0.0
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.padding(all = 10.dp),
                    leadingIcon = {
                        Text(
                            text = currencySymbol
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteItem(tempItemId)
                            tempItemName = ""
                            tempQuantity = 0
                            tempPrice = 0.0
                            tempItemId = 0

                            isAddingItem = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.LightGray,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.LightGray
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
                            viewModel.updateItem(
                                itemId = tempItemId ,
                                name = tempItemName,
                                price = tempPrice,
                                quantity = tempQuantity
                            )

                            tempItemName = ""
                            tempQuantity = 0
                            tempPrice = 0.0
                            tempItemId = 0

                            isAddingItem = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Cyan,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.Cyan
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

    // show issue date picker
    if (showIssueDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showIssueDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempIssueDate = selectedIssueDate
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
                state = issueDatePickerState,
                showModeToggle = false
            )
        }
    }

    // show due date picker
    if (showDueDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        tempDueDate = selectedDueDate

                        showDueDatePicker = false
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
                        showDueDatePicker = false
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
                state = dueDatePickerState,
                showModeToggle = false
            )
        }
    }

    if (showNameLengthWarningDialog) {
        DialogBoxSkeleton(
            onDismissRequest = {
                showNameLengthWarningDialog = false
            },
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
                    contentDescription = "Name Length Warning",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = "Name Length",
                    fontWeight = Bold,
                    fontSize = 25.sp,
                    color = Color.Black
                )

                Text(
                    text = "Client name and your name should be more than 3 characters long.",
                    fontSize = 17.sp,
                    color = Color.Gray
                )

                Button(
                    onClick = {
                        showNameLengthWarningDialog = false
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

    if (showTasksDialog) {
        DialogBox(
            title = "Completed Tasks",
            width = 550.dp,
            height = 300.dp,
            onDismissRequest = { showTasksDialog = false }
        ) {
            val lazyColumnState = rememberLazyListState()

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier.height(200.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                tempTaskName = task.description

                                showPriceDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black,
                                containerColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Add",
                                fontSize = 17.sp,
                                fontWeight = Bold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = task.description,
                            fontSize = 17.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(all = 8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showPriceDialog) {
        DialogBox(
            onDismissRequest = {
                tempTaskPrice = 0.0
                showPriceDialog = false
            },
            title = "Enter Price",
            buttonRow = {
                Button(
                    onClick = {
                        tempTaskPrice = 0.0
                        tempTaskName = ""

                        showPriceDialog = false
                    },
                    colors = ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.LightGray,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.LightGray
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
                        val currentTaskName = tempTaskName
                        val currentTaskPrice = tempTaskPrice

                        coroutineScope.launch {
                            viewModel.createItem(
                                invoiceId = tempInvoiceId,
                                name = currentTaskName,
                                quantity = 0,
                                price = currentTaskPrice
                            )
                        }

                        tempTaskPrice = 0.0
                        tempTaskName = ""

                        showPriceDialog = false
                    },
                    colors = ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Cyan,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.Cyan
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
        ) {
            OutlinedTextField(
                value = if (tempTaskPrice == 0.0) "" else tempTaskPrice.toString(),
                onValueChange = { text ->
                    val isValidDecimal = text.count { it == '.' } <= 1 &&
                            text.all { it.isDigit() || it == '.' }
                    if (isValidDecimal) {
                        tempTaskPrice = text.toDouble()
                    }
                },
                label = { Text("Price") },
                modifier = Modifier.width(400.dp),
                leadingIcon = {
                    Text(currencySymbol)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }

    if (showTimeLogsDialog) {
        DialogBox(
            onDismissRequest = {
                checkedLogs.clear()

                showTimeLogsDialog = false
            },
            title = "Time Logs",
            width = 550.dp,
            height = 400.dp,
            buttonRow = {
                Button(
                    onClick = {
                        checkedLogs.clear()

                        showTimeLogsDialog = false
                    },
                    colors = ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.LightGray,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.LightGray
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
                        var totalPrice = 0.0
                        checkedLogs.forEach { logId ->
                            val log = timeLogs.find { it.id == logId }
                            if (log != null) {
                                val endTime = LocalTime.ofNanoOfDay(log.endTime)
                                val startTime = LocalTime.ofNanoOfDay(log.startTime)
                                val totalTime = when {
                                    startTime.toNanoOfDay() < endTime.toNanoOfDay() -> {
                                        Duration.between(startTime, endTime).toHours()
                                    }
                                    else -> {
                                        Duration.between(startTime, endTime).toHours() + 23
                                    }
                                }

                                val logPrice = totalTime * (project?.payRate?: 1.0)

                                totalPrice += logPrice
                            }
                        }

                        coroutineScope.launch {
                            viewModel.createItem(
                                invoiceId = tempInvoiceId,
                                name = "Time & Labor",
                                quantity = 0,
                                price = totalPrice
                            )
                        }

                        checkedLogs.clear()

                        showTimeLogsDialog = false
                    },
                    colors = ButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Cyan,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.Cyan
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
        ) {
            val lazyColumnState = rememberLazyListState()

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier.height(260.dp)
            ) {
                items(timeLogs, key = { it.id }) { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val checked = checkedLogs.contains(log.id)

                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                if (checkedLogs.contains(log.id)) {
                                    checkedLogs.remove(log.id)
                                } else {
                                    checkedLogs.add(log.id)
                                }
                                android.util.Log.d("Checkbox", "checkedLogs = $checkedLogs")
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Cyan,
                                checkmarkColor = Color.Black
                            )
                        )

                        val formatter = DateTimeFormatter.ofPattern("hh:mm a", LocalLocale.current.platformLocale)
                        val startTimeObj = LocalTime.ofNanoOfDay(log.startTime)
                        val endTimeObj = LocalTime.ofNanoOfDay(log.endTime)

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

                        Text(
                            text = startTimeStr,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Text(
                            text = endTimeStr,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Text(
                            text = log.date,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    if (showDeletionWarning) {
        DialogBox(
            iconImageVector = Icons.Default.Warning,
            title = "Delete?",
            description = "You are about to delete an invoice. This is irreversible and will change the metrics you see on your dashboards.",
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
                        // ("Invoice Id Comparison", "invoice.id: ${invoice.id}, tempInvoiceId: $tempInvoiceId")
                        viewModel.deleteInvoice(tempInvoiceId)
                        tempInvoiceId = 0

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