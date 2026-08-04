package com.kevinnesbitt.legacysecurefreelancercrm.screens

import android.app.Activity
import com.kevinnesbitt.legacysecurefreelancercrm.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevinnesbitt.legacysecurefreelancercrm.util.BillingHelper

@Composable
fun SubscriptionSplashScreen(billingHelper: BillingHelper) {
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.subscription_splash),
            contentDescription = "Subscription Splash",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = {
                activity?.let { billingHelper.launchBillingFlow(it) }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 105.dp)
                .size(275.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue
            )
        ) {
            Text(
                text = "Get Full Access",
                fontWeight = Bold,
                fontSize = 17.sp,
                color = Color.White
            )
        }
    }
}