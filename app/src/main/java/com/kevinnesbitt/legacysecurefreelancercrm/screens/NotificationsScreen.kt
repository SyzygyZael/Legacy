package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DialogBox

@Composable
fun NotificationsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val projectLazyListState = rememberLazyListState()
    val taskLazyListState = rememberLazyListState()
    val invoiceLazyListState = rememberLazyListState()

    val projectReminders by viewModel.projectReminders.collectAsStateWithLifecycle()
    val taskReminders by viewModel.taskReminders.collectAsStateWithLifecycle()
    val invoiceReminders by viewModel.invoiceReminders.collectAsStateWithLifecycle()

    var tempProjectUnit by remember { mutableStateOf("") }

    var tempProjectDays by remember { mutableIntStateOf(0) }
    var isAddingNotification by remember { mutableStateOf(false) }

    var projectNotification by remember { mutableStateOf(false) }
    var taskNotification by remember { mutableStateOf(false) }
    var invoiceNotification by remember { mutableStateOf(false) }

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
                // filler padding
                Button(
                    enabled = false,
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

                Text(
                    text = " Notifications",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                // save button
                Button(
                    enabled = false,
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

        // Project Reminders
        Text(
            text = "Project Reminders",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 17.dp),
            textAlign = TextAlign.Start
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        LazyColumn(
            state = projectLazyListState
        ) {
            items(projectReminders, key = { it.id }) { reminder ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notify",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(
                            text = "   ${reminder.numUnits} ${reminder.unit.lowercase()} before",
                            fontSize = 17.sp,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteProjectReminder(reminder.id) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Reminder",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                                projectNotification = true
                                isAddingNotification = true
                            }
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add new notification",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Task Reminders
        Text(
            text = "Task Reminders",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 17.dp),
            textAlign = TextAlign.Start
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        LazyColumn(
            state = taskLazyListState
        ) {
            items(taskReminders, key = { it.id }) { reminder ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notify",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(
                            text = "   ${reminder.numUnits} ${reminder.unit.lowercase()} before",
                            fontSize = 17.sp,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteTaskReminder(reminder.id) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Reminder",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                                taskNotification = true
                                isAddingNotification = true
                            }
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add new notification",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Invoice Reminders
        Text(
            text = "Invoice Reminders",
            fontSize = 20.sp,
            fontWeight = Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 17.dp),
            textAlign = TextAlign.Start
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        LazyColumn(
            state = invoiceLazyListState
        ) {
            items(invoiceReminders, key = { it.id }) { reminder ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notify",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text(
                            text = "   ${reminder.numUnits} ${reminder.unit.lowercase()} before",
                            fontSize = 17.sp,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteInvoiceReminder(reminder.id) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Reminder",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clickable(
                            onClick = {
                                invoiceNotification = true
                                isAddingNotification = true
                            }
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add new notification",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }

    // DIALOG BOXES

    // Is adding notification
    if (isAddingNotification) {
        val options = listOf("Days", "Weeks", "Months", "Years")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }

        DialogBox(
            title = "New Notification",
            onDismissRequest = {
                tempProjectDays = 0
                tempProjectUnit = ""

                projectNotification = false
                taskNotification = false
                invoiceNotification = false

                isAddingNotification = false
                },
            height = 600.dp,
            buttonRow = {
                Button(
                    onClick = {
                        when {
                            projectNotification -> viewModel.createProjectReminder(tempProjectDays, selectedOption)
                            taskNotification -> viewModel.createTaskReminder(tempProjectDays, selectedOption)
                            invoiceNotification -> viewModel.createInvoiceReminder(tempProjectDays, selectedOption)
                        }

                        tempProjectDays = 0
                        tempProjectUnit = ""

                        projectNotification = false
                        taskNotification = false
                        invoiceNotification = false

                        isAddingNotification = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Done",
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = Color.Black
                    )
                }
            }
        ) {
            OutlinedTextField(
                value = if (tempProjectDays == 0) "" else tempProjectDays.toString(),
                onValueChange = { text ->
                    val isValidDecimal = text.all { it.isDigit() }
                    if (isValidDecimal) {
                        tempProjectDays = text.toInt()
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Column(
                modifier = Modifier.selectableGroup()
            ) {
                options.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}