package com.kevinnesbitt.legacysecurefreelancercrm.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.util.DropdownSettingsRow
import com.kevinnesbitt.legacysecurefreelancercrm.util.DueDateCheckWorker
import com.kevinnesbitt.legacysecurefreelancercrm.util.NavigationSettingsRow
import com.kevinnesbitt.legacysecurefreelancercrm.util.SettingsRow
import com.kevinnesbitt.legacysecurefreelancercrm.util.uriToBitmap
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var logoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        logoUri = uri

        if (uri != null) {
            // 1. Convert it to a Bitmap
            val bitmap = uriToBitmap(context, uri)
            logoBitmap = bitmap

            // 2. Save it to storage and show the Toast
            bitmap?.let {
                coroutineScope.launch {
                    viewModel.saveImageToInternalStorage(context, it)

                    // Show the toast after it successfully saves
                    Toast.makeText(context, "Image upload successful", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            logoBitmap = null
        }
    }

    var tempTaxPercentage by remember(settings) { mutableDoubleStateOf(settings.taxBracket) }
    var timeFormatChoiceString by remember(settings) { mutableStateOf(settings.timeFormat) }
    var dateFormatChoiceString by remember(settings) { mutableStateOf(settings.dateFormat) }
    var currencyChoiceString by remember(settings) { mutableStateOf(settings.preferredCurrency) }

    var isChoosingTimeFormat by remember { mutableStateOf(false) }
    var isChoosingDateFormat by remember { mutableStateOf(false) }
    var isChoosingCurrency by remember { mutableStateOf(false) }

    val changedSettings = when {
        (timeFormatChoiceString != settings.timeFormat) -> true
        (dateFormatChoiceString != settings.dateFormat) -> true
        (currencyChoiceString != settings.preferredCurrency) -> true
        (tempTaxPercentage != settings.taxBracket) -> true
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
                            selfTelephone = settings.selfTelephone,
                            currency = currencyChoiceString,
                            taxBracket = tempTaxPercentage
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

        // Notifications
        NavigationSettingsRow(
            title = "Notifications",
            navigateTo = "notifications",
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

        // Date Format
        DropdownSettingsRow(
            expanded = isChoosingDateFormat,
            onDismissRequest = { isChoosingDateFormat = false },
            title = "Date Format",
            value = dateFormatChoiceString,
            onClick = { isChoosingDateFormat = true },
            width = 128.dp
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

        // Dashboard Currency
        DropdownSettingsRow(
            expanded = isChoosingCurrency,
            onDismissRequest = { isChoosingCurrency = false },
            title = "Dashboard Currency",
            value = currencyChoiceString,
            onClick = { isChoosingCurrency = true }
        ) {
            SupportedCurrency.entries.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency.code) },
                    onClick = {
                        currencyChoiceString = currency.code
                        isChoosingCurrency = false
                    }
                )
            }
        }

        // Tax Bracket
        SettingsRow(
            title = "Tax Bracket"
        ) {
            OutlinedTextField(
                value = if (tempTaxPercentage == 0.0) "" else tempTaxPercentage.toString(),
                onValueChange = { text ->
                    val isValidDecimal = text.count { it == '.' } <= 1 &&
                            text.all { it.isDigit() || it == '.' }
                    if (isValidDecimal) {
                        tempTaxPercentage = text.toDoubleOrNull() ?: 0.0
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .size(135.dp, 70.dp)
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = "Tax Bracket",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }

        // Invoice Logo Upload
        SettingsRow(
            title = "Upload Invoice Logo"
        ) {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Upload",
                    fontWeight = Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }

        // Test Notifications
        SettingsRow(
            title = "Test Notifications"
        ) {
            Button(
                onClick = {
                    val testRequest = OneTimeWorkRequestBuilder<DueDateCheckWorker>().build()
                    WorkManager.getInstance(context).enqueue(testRequest)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Test",
                    fontWeight = Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}