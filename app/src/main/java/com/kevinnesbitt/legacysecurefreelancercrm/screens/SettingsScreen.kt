package com.kevinnesbitt.legacysecurefreelancercrm.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DropdownSettingsRow
import com.kevinnesbitt.legacysecurefreelancercrm.util.NavigationSettingsRow

@Composable
fun SettingsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var timeFormatChoiceString by remember(settings) { mutableStateOf(settings.timeFormat) }
    var dateFormatChoiceString by remember(settings) { mutableStateOf(settings.dateFormat) }

    var isChoosingTimeFormat by remember { mutableStateOf(false) }
    var isChoosingDateFormat by remember { mutableStateOf(false) }

    val changedSettings = when {
        (timeFormatChoiceString != settings.timeFormat) -> true
        (dateFormatChoiceString != settings.dateFormat) -> true
        else -> false
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
                // filler padding
                Button(
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
                    text = " Settings",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                // save button
                Button(
                    enabled = changedSettings,
                    onClick = {
                        viewModel.updateSettings(
                            timeFormat = timeFormatChoiceString,
                            dateFormat = dateFormatChoiceString,
                            selfName = settings.selfName,
                            selfEmail = settings.selfEmail,
                            selfAddress = settings.selfAddress,
                            selfTelephone = settings.selfTelephone
                        )

                        Toast.makeText(context, "Saved Changes", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContentColor = Color.LightGray,
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

        // Profile
        NavigationSettingsRow(
            title = "Profile",
            navigateTo = "profile",
            navController = navController
        )

        // Time Format
        DropdownSettingsRow(
            expanded = isChoosingTimeFormat,
            onDismissRequest = { isChoosingTimeFormat = false },
            title = "Time Format",
            value = timeFormatChoiceString,
            onClick = { isChoosingTimeFormat = true }
        ) {
            DropdownMenuItem(
                text = { Text("12-Hour") },
                onClick = {
                    timeFormatChoiceString = "12-Hour"
                }
            )

            DropdownMenuItem(
                text = { Text("24-Hour") },
                onClick = {
                    timeFormatChoiceString = "24-Hour"
                }
            )
        }

        DropdownSettingsRow(
            expanded = isChoosingDateFormat,
            onDismissRequest = { isChoosingDateFormat = false },
            title = "Date Format",
            value = dateFormatChoiceString,
            onClick = { isChoosingDateFormat = true }
        ) {
            if (dateFormatChoiceString != "MM/dd/yyyy") {
                DropdownMenuItem(
                    text = { Text("MM/dd/yyyy") },
                    onClick = {
                        dateFormatChoiceString = "MM/dd/yyyy"
                        isChoosingDateFormat = false
                    }
                )
            }

            if (dateFormatChoiceString != "dd/MM/yyyy") {
                DropdownMenuItem(
                    text = { Text("dd/MM/yyyy") },
                    onClick = {
                        dateFormatChoiceString = "dd/MM/yyyy"
                        isChoosingDateFormat = false
                    }
                )
            }

            if (dateFormatChoiceString != "yyyy-MM-dd") {
                DropdownMenuItem(
                    text = { Text("yyyy-MM-dd") },
                    onClick = {
                        dateFormatChoiceString = "yyyy-MM-dd"
                        isChoosingDateFormat = false
                    }
                )
            }

            if (dateFormatChoiceString != "yyyy/MM/dd") {
                DropdownMenuItem(
                    text = { Text("yyyy/MM/dd") },
                    onClick = {
                        dateFormatChoiceString = "yyyy/MM/dd"
                        isChoosingDateFormat = false
                    }
                )
            }
        }
    }
}