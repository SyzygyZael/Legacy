package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBoxSkeleton
import com.kevinnesbitt.legacysecurefreelancercrm.util.convertMillisToDate
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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
        DialogBoxSkeleton(
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
            },
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

    // edit time log
    if (isEditingTimeLog) {
        DialogBoxSkeleton(
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
            },
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
        DialogBoxSkeleton(
            onDismissRequest = {
                showEmptyFieldDialog = false
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

    if (showTimeDifferenceErrorDialog) {
        DialogBoxSkeleton(
            onDismissRequest = {
                showTimeDifferenceErrorDialog = false
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