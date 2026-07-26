package com.kevinnesbitt.legacysecurefreelancercrm.util

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.database.SettingsEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DialogBoxSkeleton(onDismissRequest: () -> Unit, width: Dp, height: Dp, content: @Composable (() -> Unit)) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier.size(width, height),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(2.dp, Color.Gray)
        ) { content() }
    }
}

@Composable
fun DialogBox(
    iconImageVector: ImageVector? = null,
    title: String? = null,
    description: String? = null,
    onDismissRequest: () -> Unit,
    width: Dp = 550.dp,
    height: Dp = 300.dp,
    buttonRow: @Composable (() -> Unit) = {  },
    content: @Composable (() -> Unit)
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier.size(width, height),
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
                if (iconImageVector != null) {
                    Icon(
                        imageVector = iconImageVector,
                        contentDescription = "Dialog Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(30.dp)
                    )
                }

                if (title != null) {
                    Text(
                        text = title,
                        fontWeight = Bold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }

                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 17.sp,
                        color = Color.Gray
                    )
                }

                content()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) { buttonRow() }
            }
        }
    }
}

@Composable
fun TimerText(timeLog: HomeViewModel.TimeLogData?, settings: SettingsEntity) {
    val longStartTime = timeLog?.startTime

    val startTime = LocalTime.ofNanoOfDay(longStartTime?: 0)
    var timeRightNow by remember { mutableStateOf(LocalTime.now()) }

    // val longPausedStartTime = timeLog?.pauseStartTime?: timeRightNow.toNanoOfDay()

    // val longCurrentPausedTime = timeRightNow.toNanoOfDay() - longPausedStartTime

    // val longCorrectedCurrentTime = timeRightNow.toNanoOfDay() - (timeLog?.totalPauseTime?: 0) - longCurrentPausedTime

    // val correctedCurrentTime = LocalTime.ofNanoOfDay(longCorrectedCurrentTime)

    // 2. Calculate the duration dynamically based on the living state variable
    val timerTime = Duration.between(startTime, timeRightNow)

    // 3. Keep your ticking engine running every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000.milliseconds)
            // 4. ✅ FIX: Grab a fresh snapshot of the clock every second to trigger a screen refresh
            timeRightNow = LocalTime.now()
        }
    }

    Text(
        text = if (settings.isTiming && timeLog != null) {
            "${timerTime.toHours()}:${String.format("%02d", timerTime.toMinutes() % 60)}:${String.format("%02d", timerTime.seconds % 60)}"
        } else {
            "0:00:00"
        },
        fontSize = 20.sp,
        fontWeight = Bold,
        fontFamily = FontFamily.SansSerif,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DropdownSettingsRow(expanded: Boolean, onDismissRequest: () -> Unit, title: String, value: String, onClick: () -> Unit, width: Dp = 120.dp, height: Dp = 60.dp, content: @Composable ColumnScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(21.dp),
            fontSize = 18.sp,
            color = Color.Black
        )

        Card(
            border = BorderStroke(2.dp, color = Color.Gray),
            modifier = Modifier
                .size(width, height)
                .padding(10.dp)
                .clickable(
                    onClick = onClick
                )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = value)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                modifier = Modifier.heightIn(max = 180.dp)
            ) { content() }
        }
    }
}

@Composable
fun NavigationSettingsRow(title: String, navigateTo: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(horizontal = 8.dp)
            .clickable(
                onClick = {
                    navController.navigate(navigateTo)
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(21.dp),
            fontSize = 18.sp,
            color = Color.Black
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate Row",
            modifier = Modifier
                .size(23.dp),
            tint = Color.Black
        )
    }
}

@Composable
fun TextDropDown(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit,
    color: Color = Color.Black,
    fontSize: TextUnit = 20.sp,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Row(
        modifier = Modifier.clickable(
            onClick = onClick
        )
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            content()
        }

        val rotationAngle by animateFloatAsState(
            targetValue = if (expanded) -90f else 0f,
            label = "IconRotationAnimation"
        )

        Text(
            text = text,
            color = color,
            fontSize = fontSize
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
            contentDescription = "Status Dropdown",
            tint = Color.Black,
            modifier = Modifier.rotate(rotationAngle)
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun TextInputSettingsRow(
    title: String,
    content: @Composable (RowScope.() -> Unit)
) {
    var tempText by remember { mutableDoubleStateOf(0.0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(21.dp),
            fontSize = 18.sp,
            color = Color.Black
        )

        content()
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: ImageVector,
    iconTint: Color = LocalContentColor.current,
    iconBackgroundColor: Color = Color.White,
    value: String,
    fontSize: TextUnit = 30.sp
) {
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
                    color = iconBackgroundColor
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier
                            .size(10.dp)
                            .padding(4.dp),
                        tint = iconTint
                    )
                }

                Text(
                    text = " $title",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = value,
                    fontSize = fontSize,
                    fontWeight = Bold,
                    color = Color.Black
                )

                // Surface(
                //     modifier = Modifier
                //         .size(52.dp, 20.dp),
                //     shape = RoundedCornerShape(8.dp),
                //     color = Color(0xFF98FF98L).copy(alpha = 0.2f)
                // ) {
                //     Text(
                //         text = "^ +0.0%",
                //         modifier = Modifier
                //             .size(10.dp)
                //             .padding(2.dp),
                //         color = Color(0xFF228B22L),
                //         textAlign = TextAlign.Center,
                //         fontWeight = Bold,
                //         fontSize = 11.sp
                //     )
                // }
            }
        }
    }
}