package com.kevinnesbitt.legacysecurefreelancercrm.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertDateToMillis(dateString: String): Long? {
    if (dateString == "--/--/----" || dateString.isBlank()) return null
    return try {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        format.parse(dateString)?.time
    } catch (e: Exception) {
        null
    }
}
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
