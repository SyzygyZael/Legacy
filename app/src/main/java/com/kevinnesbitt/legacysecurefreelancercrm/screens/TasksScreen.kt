package com.kevinnesbitt.legacysecurefreelancercrm.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.convertMillisToDate
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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