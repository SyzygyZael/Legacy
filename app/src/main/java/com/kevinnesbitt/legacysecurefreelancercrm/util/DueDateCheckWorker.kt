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
            // 1. Get your database directly
            val database = AppDao.AppDatabase.getDatabase(applicationContext)
            val dao = database.appDao()

            val settings = dao.getSettingsEntity()

            // 2. Fetch the projects directly from the DAO
            val projects = dao.getAllProjectsList()
            val clients = dao.getAllClientsList()

            // val currentDate = LocalDate.now()
            val projectTargetDate = LocalDate.now().plusDays(5)
            val taskTargetDate = LocalDate.now().plusDays(5)
            val invoiceTargetDate = LocalDate.now().plusDays(5)

            val formatter = DateTimeFormatter.ofPattern(settings.dateFormat) // Adjust pattern if needed

            val projectTargetDateStr = formatter.format(projectTargetDate)
            val taskTargetDateStr = formatter.format(taskTargetDate)
            val invoiceTargetDateStr = formatter.format(invoiceTargetDate)

            // 3. Loop through your clean list of projects
            for (project in projects) {
                android.util.Log.d("WorkerCheck", "Checking project: ${project.title}, due date: ${project.deadLine}")

                // 4. Trigger notification
                if (project.deadLine == projectTargetDateStr) {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        title = "Project Due Soon!",
                        message = "Your project '${project.title}' is due in 5 days."
                    )
                }
            }

            val tasks = dao.getAllTasksList() // Query your tasks table from the DAO
            for (task in tasks) {
                if (task.dueDate == taskTargetDateStr) {

                    NotificationHelper.showNotification(
                        context = applicationContext,
                        title = "Task Due Soon!",
                        message = "Your task '${task.description}' is due in 5 days."
                    )
                }
            }

            val invoices = dao.getAllInvoicesList() // Query your tasks table from the DAO
            for (invoice in invoices) {
                if (invoice.dueDate == invoiceTargetDateStr) {
                    val thisProject = projects.find { it.id == invoice.projectId }
                    val thisClient = clients.find { it.id == thisProject?.clientId }

                    NotificationHelper.showNotification(
                        context = applicationContext,
                        title = "Invoice Due Soon!",
                        message = "'${thisClient?.name?: "No Name"}' has an invoice due in 5 days."
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}