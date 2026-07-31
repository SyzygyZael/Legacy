package com.kevinnesbitt.legacysecurefreelancercrm.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevinnesbitt.legacysecurefreelancercrm.database.AppDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DueDateCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDao.AppDatabase.getDatabase(applicationContext)
            val dao = database.appDao()

            val settings = dao.getSettingsEntity()

            val projectReminders = dao.getAllProjectRemindersList()
            val taskReminders = dao.getAllTaskRemindersList()
            val invoiceReminders = dao.getAllInvoiceRemindersList()

            val projects = dao.getAllProjectsList()
            val clients = dao.getAllClientsList()
            val tasks = dao.getAllTasksList()
            val invoices = dao.getAllInvoicesList()

            val currentDate = LocalDate.now()

            val formatter = DateTimeFormatter.ofPattern(settings.dateFormat)

            projectReminders.forEach { reminder ->
                val targetDate = when (reminder.unit) {
                    "Days" -> { currentDate.plusDays(reminder.numUnits.toLong()) }
                    "Weeks" -> { currentDate.plusWeeks(reminder.numUnits.toLong()) }
                    "Months" -> { currentDate.plusMonths(reminder.numUnits.toLong()) }
                    else -> { currentDate.plusYears(reminder.numUnits.toLong()) }
                }

                val targetDateStr = formatter.format(targetDate)

                for (project in projects) {
                    android.util.Log.d("Checking project: ${project.title}", "Target Date: $targetDateStr, Project Deadline: ${project.deadLine}")

                    // Trigger notification
                    if (project.deadLine == targetDateStr) {
                        NotificationHelper.showNotification(
                            context = applicationContext,
                            title = "Project Due Soon!",
                            message = "Your project '${project.title}' is due in ${reminder.numUnits} ${if (reminder.numUnits == 1) reminder.unit.lowercase().dropLast(1) else reminder.unit.lowercase()}."
                        )
                    }
                }
            }

            taskReminders.forEach { reminder ->
                val targetDate = when (reminder.unit) {
                    "Days" -> { currentDate.plusDays(reminder.numUnits.toLong()) }
                    "Weeks" -> { currentDate.plusWeeks(reminder.numUnits.toLong()) }
                    "Months" -> { currentDate.plusMonths(reminder.numUnits.toLong()) }
                    else -> { currentDate.plusYears(reminder.numUnits.toLong()) }
                }

                val targetDateStr = formatter.format(targetDate)

                for (task in tasks) {
                    if (task.dueDate == targetDateStr) {

                        NotificationHelper.showNotification(
                            context = applicationContext,
                            title = "Task Due Soon!",
                            message = "Your task '${task.description}' is due in ${reminder.numUnits} ${if (reminder.numUnits == 1) reminder.unit.lowercase().dropLast(1) else reminder.unit.lowercase()}."
                        )
                    }
                }
            }

            invoiceReminders.forEach { reminder ->
                val targetDate = when (reminder.unit) {
                    "Days" -> { currentDate.plusDays(reminder.numUnits.toLong()) }
                    "Weeks" -> { currentDate.plusWeeks(reminder.numUnits.toLong()) }
                    "Months" -> { currentDate.plusMonths(reminder.numUnits.toLong()) }
                    else -> { currentDate.plusYears(reminder.numUnits.toLong()) }
                }

                val targetDateStr = formatter.format(targetDate)

                for (invoice in invoices) {
                    if (invoice.dueDate == targetDateStr) {
                        val thisProject = projects.find { it.id == invoice.projectId }
                        val thisClient = clients.find { it.id == thisProject?.clientId }

                        NotificationHelper.showNotification(
                            context = applicationContext,
                            title = "Invoice Due Soon!",
                            message = "'${thisClient?.name?: "No Name"}' has an invoice due in ${reminder.numUnits} ${if (reminder.numUnits == 1) reminder.unit.lowercase().dropLast(1) else reminder.unit.lowercase()}."
                        )
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}