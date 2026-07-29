package com.kevinnesbitt.legacysecurefreelancercrm.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel
import com.kevinnesbitt.legacysecurefreelancercrm.database.HomeViewModel.InvoiceData
import com.kevinnesbitt.legacysecurefreelancercrm.database.SettingsEntity
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.collections.forEachIndexed
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.kevinnesbitt.legacysecurefreelancercrm.R

fun convertMillisToDate(millis: Long, settings: SettingsEntity): String {
    val formatter = SimpleDateFormat(settings.dateFormat, Locale.getDefault())
    return formatter.format(Date(millis))
}

// fun convertDateToMillis(dateString: String, settings: SettingsEntity): Long? {
//     if (dateString == "--/--/----" || dateString.isBlank()) return null
//     return try {
//         val format = SimpleDateFormat(settings.dateFormat, Locale.getDefault())
//         format.parse(dateString)?.time
//     } catch (e: Exception) {
//         null
//     }
// }
fun sharePdf(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    // Fallback to a chooser if no default PDF viewer exists
    val chooser = Intent.createChooser(intent, "Open Invoice PDF")
    chooser.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(chooser)
}

fun getCurrencySymbol(clientCurrencyCode: String): String {
    SupportedCurrency.entries.forEach { currency ->
        if (clientCurrencyCode == currency.code) {
            return currency.symbol
        }
    }

    return ""
}

fun getCurrencyName(clientCurrencyCode: String): String {
    SupportedCurrency.entries.forEach { currency ->
        if (clientCurrencyCode == currency.code) {
            return currency.displayName
        }
    }

    return ""
}

fun getLastSixMonths(): MutableList<String> {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val intToMonth = mapOf(
        1 to "Jan",
        2 to "Feb",
        3 to "Mar",
        4 to "Apr",
        5 to "May",
        6 to "Jun",
        7 to "Jul",
        8 to "Aug",
        9 to "Sep",
        10 to "Oct",
        11 to "Nov",
        12 to "Dec"
    )

    val regCurrentDateStr = formatter.format(currentDate)
    val currentMonth = when(regCurrentDateStr.substring(5, 7)[0]) {
        '0' -> regCurrentDateStr.substring(5, 7)[1].toString().toInt()
        else -> regCurrentDateStr.substring(5, 7).toInt()
    }

    val months = mutableListOf(
        currentMonth,
        currentMonth - 1,
        currentMonth - 2,
        currentMonth - 3,
        currentMonth - 4,
        currentMonth - 5
    )

    val strMonths = mutableListOf<String>()

    months.forEachIndexed { index, month ->
        when(month) {
            0 -> months[index] = 12
            -1 -> months[index] = 11
            -2 -> months[index] = 10
            -3 -> months[index] = 9
            -4 -> months[index] = 8
        }
    }

    months.forEach { month ->
        strMonths.add(intToMonth[month]?: "N/A")
    }

    android.util.Log.d("months", "Months: $strMonths")
    return strMonths.asReversed()
}


fun getMonthFromShortForm(shortMonth: String): String {
    return when(shortMonth) {
        "Jan" -> "January"
        "Feb" -> "February"
        "Mar" -> "March"
        "Apr" -> "April"
        "May" -> "May"
        "Jun" -> "June"
        "Jul" -> "July"
        "Aug" -> "August"
        "Sep" -> "September"
        "Oct" -> "October"
        "Nov" -> "November"
        else -> "December"
    }
}

fun getLast6MonthsEarnings(
    paidInvoices: List<InvoiceData>,
    settings: SettingsEntity,
    clients: List<HomeViewModel.ClientData>,
    projects: List<HomeViewModel.ProjectData>
): List<Float> {
    // 1. Generate the last 6 months keys (e.g., "2026-02", "2026-03", ..., "2026-07")
    val currentMonth = YearMonth.now()
    val last6Months = (5 downTo 0).map { minusMonths ->
        currentMonth.minusMonths(minusMonths.toLong())
    }

    val formatter = DateTimeFormatter.ofPattern(settings.dateFormat)

    // 2. Map each of the last 6 months to a sum of its converted paid invoices
    return last6Months.map { targetYearMonth ->
        // Filter invoices belonging to this specific month
        val monthlyInvoices = paidInvoices.filter { invoice ->
            try {
                val invoiceDate = LocalDate.parse(invoice.paidDate, formatter)
                YearMonth.from(invoiceDate) == targetYearMonth
            } catch (e: Exception) {
                false
            }
        }

        // 3. Convert and sum up the amounts for this month using your custom logic
        var monthlyTotal = 0.0

        for (invoice in monthlyInvoices) {
            val thisProject = projects.find { project -> project.id == invoice.projectId }
            val thisClient = clients.find { client -> client.id == thisProject?.clientId }

            val toDollarConversion = SupportedCurrency.entries.find { it.code == (thisClient?.currency?: "USD") }?.USDConversion ?: 0f
            val toTargetCurrencyConversion = SupportedCurrency.entries.find { it.code == settings.preferredCurrency }?.USDConversion ?: 0f

            // Prevent division by zero if conversion rate is missing
            val convertedAmount = if (toDollarConversion > 0f) {
                (invoice.amount / toDollarConversion) * toTargetCurrencyConversion
            } else {
                invoice.amount
            }

            monthlyTotal += convertedAmount
        }

        monthlyTotal.toFloat()
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        // Open an input stream from the URI and decode it into a Bitmap
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun loadBitmapFromPath(imagePath: String?): Bitmap? {
    if (imagePath.isNullOrEmpty()) return null

    val file = File(imagePath)
    if (!file.exists()) return null

    // Decodes the file path directly into a Bitmap
    return BitmapFactory.decodeFile(file.absolutePath)
}

object NotificationHelper {

    private const val CHANNEL_ID = "invoice_notifications"

    fun showNotification(context: Context, title: String, message: String) {
        // 1. Create the Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Invoices"
            val descriptionText = "Notifications for invoice updates and generation"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Build the Notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.legacy_notification) // Replace with your app's notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismisses the notification when the user clicks it

        // 3. Show the Notification
        try {
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                val notificationId = System.currentTimeMillis().toInt()
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle the case where the user denied the POST_NOTIFICATIONS permission
            android.util.Log.e("Notification", "Permission denied", e)
        }
    }
}

fun scheduleDueDateChecker(context: Context) {
    // Optional: Add constraints (e.g., only run if the device has battery/isn't in low power mode)
    val constraints = Constraints.Builder()
        // .setRequiresBatteryNotLow(true)
        .build()

    // Create a request to run this worker roughly once every 24 hours
    val workRequest = PeriodicWorkRequestBuilder<DueDateCheckWorker>(
        24, TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .build()

    // Enqueue the work unique name ensures it doesn't duplicate if called multiple times
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DueDateCheckWork",
        ExistingPeriodicWorkPolicy.KEEP, // Keeps the existing schedule if already running
        workRequest
    )
}