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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ClientStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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