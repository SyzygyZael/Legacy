package com.kevinnesbitt.legacysecurefreelancercrm.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.text.get

class FirebaseSyncRepository(private val appDao: AppDao) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // -------------------------------------------------------------
    // EXPORT / BACKUP (Room -> Firestore)
    // -------------------------------------------------------------
    suspend fun backupDataToFirebase(): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid
            ?: return@withContext Result.failure(Exception("User not signed in to Firebase"))

        try {
            // Reference to the current user's document root
            val userDocRef = db.collection("users").document(userId)

            // 1. Read all local data from Room DAO
            val clients = appDao.getAllClientsList()
            val projects = appDao.getAllProjectsList()
            val tasks = appDao.getAllTasksList()
            val timeLogs = appDao.getAllTimeLogs().toString() // Or create a list query in DAO
            val invoices = appDao.getAllInvoicesList()
            val projectReminders = appDao.getAllProjectRemindersList()
            val taskReminders = appDao.getAllTaskRemindersList()
            val invoiceReminders = appDao.getAllInvoiceRemindersList()

            // Get settings and strip out image paths
            val settings = appDao.getSettingsOnce()?.copy(invoiceLogoPath = "")

            // 2. Prepare payload batch or structured documents
            val batch = db.batch()

            // Save Settings
            if (settings != null) {
                val settingsRef = userDocRef.collection("settings").document("user_settings")
                batch.set(settingsRef, settings)
            }

            // Save Collections
            clients.forEach { client ->
                val ref = userDocRef.collection("clients").document(client.id.toString())
                batch.set(ref, client)
            }

            projects.forEach { project ->
                val ref = userDocRef.collection("projects").document(project.id.toString())
                batch.set(ref, project)
            }

            tasks.forEach { task ->
                val ref = userDocRef.collection("tasks").document(task.id.toString())
                batch.set(ref, task)
            }

            invoices.forEach { invoice ->
                val ref = userDocRef.collection("invoices").document(invoice.id.toString())
                batch.set(ref, invoice)
            }

            projectReminders.forEach { reminder ->
                val ref = userDocRef.collection("project_reminders").document(reminder.id.toString())
                batch.set(ref, reminder)
            }

            taskReminders.forEach { reminder ->
                val ref = userDocRef.collection("task_reminders").document(reminder.id.toString())
                batch.set(ref, reminder)
            }

            invoiceReminders.forEach { reminder ->
                val ref = userDocRef.collection("invoice_reminders").document(reminder.id.toString())
                batch.set(ref, reminder)
            }

            // Commit all records in a single atomic batch write
            batch.commit().await()
            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // IMPORT / RESTORE (Firestore -> Room)
    // -------------------------------------------------------------
    suspend fun restoreDataFromFirebase(): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid
            ?: return@withContext Result.failure(Exception("User not signed in to Firebase"))

        try {
            val userDocRef = db.collection("users").document(userId)

            // Restore Clients
            val clientsSnap = userDocRef.collection("clients").get().await()
            clientsSnap.documents.forEach { doc ->
                val client = doc.toObject(ClientDataEntity::class.java)
                client?.let { appDao.insertClient(it) }
            }

            // Restore Projects
            val projectsSnap = userDocRef.collection("projects").get().await()
            projectsSnap.documents.forEach { doc ->
                val project = doc.toObject(ProjectDataEntity::class.java)
                project?.let { appDao.insertProject(it) }
            }

            // Restore Tasks
            val tasksSnap = userDocRef.collection("tasks").get().await()
            tasksSnap.documents.forEach { doc ->
                val task = doc.toObject(TaskDataEntity::class.java)
                task?.let { appDao.insertTask(it) }
            }

            // Restore Invoices
            val invoicesSnap = userDocRef.collection("invoices").get().await()
            invoicesSnap.documents.forEach { doc ->
                val invoice = doc.toObject(InvoiceEntity::class.java)
                invoice?.let { appDao.insertInvoice(it) }
            }

            // Restore Settings (Reset image path so it defaults to empty on the new device)
            val settingsSnap = userDocRef.collection("settings").document("user_settings").get().await()
            if (settingsSnap.exists()) {
                val settings = settingsSnap.toObject(SettingsEntity::class.java)?.copy(invoiceLogoPath = "")
                settings?.let { appDao.insertSettings(it) }
            }

            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}