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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel

@Composable
fun ProfileScreen(viewModel: HomeViewModel, navController: NavController, innerPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // val windowInfo = LocalWindowInfo.current
    // val screenWidth = windowInfo.containerDpSize.width
    // val screenHeight = windowInfo.containerDpSize.height

    var tempName by remember(settings) { mutableStateOf(settings.selfName) }
    var tempAddress by remember(settings) { mutableStateOf(settings.selfAddress) }
    var tempEmail by remember(settings) { mutableStateOf(settings.selfEmail) }
    var tempTelephone by remember(settings) { mutableStateOf(settings.selfTelephone)}

    var changedValue by remember { mutableStateOf(false) }

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
                // back button
                IconButton(
                    onClick = { navController.navigate("settings") },
                    colors = IconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .size(20.dp),
                        tint = Color.Black
                    )
                }

                Text(
                    text = " Profile",
                    fontSize = 25.sp,
                    fontWeight = Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black
                )

                // save button
                Button(
                    enabled = changedValue,
                    onClick = {
                        viewModel.updateSettings(
                            timeFormat = settings.timeFormat,
                            dateFormat = settings.dateFormat,
                            selfName = tempName,
                            selfAddress = tempAddress,
                            selfEmail = tempEmail,
                            selfTelephone = tempTelephone,
                            currency = settings.preferredCurrency,
                            taxBracket = settings.taxBracket
                        )

                        Toast.makeText(context, "Saved Changes", Toast.LENGTH_LONG).show()

                        changedValue = false
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

        OutlinedTextField(
            value = tempName,
            label = { Text("Name") },
            onValueChange = { text ->
                if (tempName.length <= 50) {
                    tempName = text
                    changedValue = true
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 60.dp,
                    top = 15.dp
                )
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = tempAddress,
            label = { Text("Address") },
            onValueChange = { text ->
                tempAddress = text
                changedValue = true
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 60.dp,
                    top = 15.dp
                )
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = tempEmail,
            label = { Text("Email") },
            onValueChange = { text ->
                tempEmail = text
                changedValue = true
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            modifier = Modifier
                .padding(
                start = 10.dp,
                end = 60.dp,
                top = 15.dp
                )
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = tempTelephone,
            label = { Text("Telephone") },
            onValueChange = { text ->
                val isValidDecimal = text.count { it == '+' } <= 1 &&
                        text.all { it.isDigit() || it == '+' }
                if (isValidDecimal) {
                    tempTelephone = text
                    changedValue = true
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier
                .padding(
                start = 10.dp,
                end = 60.dp,
                top = 15.dp
                )
                .fillMaxWidth()
        )
    }
}