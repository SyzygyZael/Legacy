package com.kevinnesbitt.legacysecurefreelancercrm.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ClientStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.internal.synchronized

@Entity(tableName = "clients")
data class ClientDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val company: String,
    val email: String,
    val telp: String,
    val currency: String,
    val status: String = ClientStatus.ACTIVE.name,
    val orderIndex: Int
)

@Entity(tableName = "projects")
data class ProjectDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val clientId: Int,
    val title: String,
    val description: String,
    val status: String = ProjectStatus.PAUSED.name,
    val deadLine: String,
    val payRate: Double,
    val billingType: String,
    val budget: Double,
    val orderIndex: Int
)

@Entity(tableName = "tasks")
data class TaskDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val description: String,
    val isCompleted: Boolean,
    val dueDate: String,
    val orderIndex: Int
)

@Entity(tableName = "time_logs")
data class TimeLogsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    // val pauseStartTime: Long = 0L,
    // val pauseEndTime: Long = 0L,
    // val totalPauseTime: Long = 0L,
    val isBilled: Boolean = false,
    val date: String = "--/--/----",
    val orderIndex: Int
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val invoiceNumber: String,
    val amount: Double = 0.0,
    val issueDate: String,
    val dueDate: String,
    val paidDate: String = "--/--/----",
    val issueTo: String,
    val clientCompany: String,
    val clientEmail: String,
    val clientTelephone: String,
    val payTo: String,
    val selfAddress: String,
    val selfEmail: String,
    val selfTelephone: String,
    val status: String = InvoiceStatus.DRAFT.name,
    val taxPercentage: Double
)

@Entity(tableName = "invoice_item")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val invoiceId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    val isTiming: Boolean = false,
    // val isPaused: Boolean = false,
    val timeFormat: String = "12-Hour",
    val dateFormat: String = "MM/dd/yyyy",
    val selfName: String = "",
    val selfAddress: String = "",
    val selfEmail: String = "",
    val selfTelephone: String = "",
    val preferredCurrency: String = "USD",
    val taxBracket: Double = 0.0
)

@Dao
interface AppDao {
    // INSERT QUERIES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskDataEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeLog(timeLog: TimeLogsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    // "GET ALL" QUERIES
    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM tasks ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    @Query("SELECT * FROM time_logs ORDER BY date DESC, startTime DESC")
    fun getAllTimeLogs(): Flow<List<TimeLogsEntity>>

    @Query("SELECT * FROM projects ORDER BY orderIndex ASC")
    fun getAllProjects(): Flow<List<ProjectDataEntity>>

    @Query("SELECT * FROM clients ORDER BY orderIndex ASC")
    fun getAllClients(): Flow<List<ClientDataEntity>>

    @Query("SELECT * FROM invoice_item")
    fun getAllInvoiceItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsOnce(): SettingsEntity?

    // UPDATE QUERIES
    @Query("UPDATE clients SET name = :newName, company = :company, email = :newEmail, telp = :newNum, currency = :newCurrency WHERE id = :clientId")
    suspend fun updateClientInfo(clientId: Int, newName: String, company: String, newEmail: String, newNum: String, newCurrency: String)

    @Query("UPDATE projects SET title = :newTitle, description = :newDesc, deadline = :newDeadline, payRate = :newPayrate, billingType = :newBT, budget = :newBudget WHERE id = :projectId")
    suspend fun updateProjectInfo(projectId: Int, newTitle: String, newDesc: String, newDeadline: String, newPayrate: Double, newBT: String, newBudget: Double)

    @Query("UPDATE clients SET status = :status WHERE id = :clientId")
    suspend fun updateClientStatus(status: String, clientId: Int)

    @Query("UPDATE clients SET orderIndex = :newIndex WHERE id = :clientId")
    suspend fun updateClientPosition(clientId: Int, newIndex: Int)

    @Query("UPDATE projects SET status = :status WHERE id = :projectId AND clientId = :clientId")
    suspend fun updateProjectStatus(status: String, projectId: Int, clientId: Int)

    @Query("UPDATE projects SET description = :newDesc WHERE id = :projectId")
    suspend fun updateProjectDescription(projectId: Int, newDesc: String)

    @Query("UPDATE projects SET orderIndex = :newIndex WHERE id = :projectId")
    suspend fun updateProjectPosition(projectId: Int, newIndex: Int)

    @Query("UPDATE tasks SET isCompleted = :taskStatus WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, taskStatus: Boolean)

    @Query("UPDATE tasks SET description = :newName, dueDate = :newDueDate WHERE id = :taskId")
    suspend fun editTaskInfo(taskId: Int, newName: String, newDueDate: String)

    @Query("UPDATE tasks SET orderIndex = :newIndex WHERE id = :taskId")
    suspend fun updateTaskPosition(taskId: Int, newIndex: Int)

    @Query("UPDATE time_logs SET orderIndex = :newIndex WHERE id = :logId")
    suspend fun updateTimeLogPosition(logId: Int, newIndex: Int)

    @Query("UPDATE time_logs SET startTime = :startTime, endTime = :endTime, date = :date WHERE id = :logId")
    suspend fun updateTimeLogInfo(logId: Int, startTime: Long, endTime: Long, date: String)

    @Query("UPDATE invoice_item SET name = :name, price = :price, quantity = :quantity WHERE id = :itemId")
    suspend fun updateItem(itemId: Int, name: String, price: Double, quantity: Int)

    @Query("UPDATE invoices SET invoiceNumber = :invoiceNumber, issueDate = :issueDate, dueDate = :dueDate, issueTo = :issueTo, clientCompany = :clientCompany, clientEmail = :clientEmail, clientTelephone = :clientTelephone, payTo = :payTo, selfAddress = :selfAddress, selfEmail = :selfEmail, selfTelephone = :selfTelephone, taxPercentage = :taxPercentage, amount = :amount, status = :status WHERE id = :invoiceId")
    suspend fun updateInvoice(invoiceId: Int, invoiceNumber: String, issueDate: String, dueDate: String, issueTo: String, clientCompany: String, clientEmail: String, clientTelephone: String, payTo: String, selfAddress: String, selfEmail: String, selfTelephone: String, taxPercentage: Double, amount: Double, status: String)

    @Query("UPDATE invoices SET status = :status WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: Int, status: String)

    @Query("UPDATE settings SET preferredCurrency = :currency WHERE id = 1")
    suspend fun updatePreferredCurrency(currency: String)

    @Query("UPDATE invoices SET paidDate = :paidDate WHERE id = :invoiceId")
    suspend fun updateInvoicePaidDate(invoiceId: Int, paidDate: String)

    @Query("UPDATE settings SET timeFormat = :timeFormat, dateFormat = :dateFormat, selfName = :selfName, selfAddress = :selfAddress, selfEmail = :selfEmail, selfTelephone = :selfTelephone, preferredCurrency = :currency, taxBracket = :taxBracket WHERE id = 1")
    suspend fun updateSettings(
        timeFormat: String,
        dateFormat: String,
        selfName: String,
        selfAddress: String,
        selfEmail: String,
        selfTelephone: String,
        currency: String,
        taxBracket: Double
    )

    // DELETE QUERIES
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM time_logs WHERE id =:logId")
    suspend fun deleteLog(logId: Int)

    @Query("DELETE FROM invoice_item WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)

    @Query("DELETE FROM invoice_item WHERE invoiceId = :invoiceId")
    suspend fun deleteItemsInInvoice(invoiceId: Int)

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: Int)

    // OTHER QUERIES
    @Query("SELECT * FROM projects WHERE clientId = :clientId")
    suspend fun getProjectFromClientId(clientId: Int): List<ProjectDataEntity>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectFromId(projectId: Int): ProjectDataEntity

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    suspend fun getTasksFromProjectId(projectId: Int): List<TaskDataEntity>

    @Query("SELECT * FROM clients")
    suspend fun getAllClientsList(): List<ClientDataEntity>

    @Query("SELECT * FROM time_logs WHERE projectId = :projectId")
    suspend fun getTimeLogsFromProjectId(projectId: Int): List<TimeLogsEntity>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoice(invoiceId: Int): InvoiceEntity

    @Query("SELECT * FROM invoice_item WHERE invoiceId = :invoiceId")
    suspend fun getItemsFromInvoice(invoiceId: Int): List<ItemEntity>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientFromId(clientId: Int): ClientDataEntity

    // TIMER QUERIES
    @Query("UPDATE settings SET isTiming = :timeState WHERE id = 1")
    suspend fun updateTimerState(timeState: Boolean)

    // @Query("UPDATE settings SET isPaused = :timeState WHERE id = 1")
    // suspend fun updateTimerPausedState(timeState: Boolean)

    @Query("UPDATE time_logs SET startTime = :startTime WHERE id = :timeLogId")
    suspend fun updateStartTime(timeLogId: Int, startTime: Long)

    @Query("UPDATE time_logs SET endTime = :endTime WHERE id = :timeLogId")
    suspend fun updateEndTime(timeLogId: Int, endTime: Long)

    // @Query("UPDATE time_logs SET pauseStartTime = :startTime WHERE id = :timeLogId")
    // suspend fun updatePauseStartTime(timeLogId: Int, startTime: Long)

    // @Query("UPDATE time_logs SET pauseEndTime = :endTime WHERE id = :timeLogId")
    // suspend fun updatePauseEndTime(timeLogId: Int, endTime: Long)

    // @Query("UPDATE time_logs SET totalPauseTime = :numOfTime WHERE id = :timeLogId")
    // suspend fun updateTotalPauseTime(timeLogId: Int, numOfTime: Long?)

    @Query("UPDATE time_logs SET date = :date WHERE id = :timeLogId")
    suspend fun updateTimeLogDate(timeLogId: Int, date: String)

    @Database(entities = [
        ClientDataEntity::class,
        ProjectDataEntity::class,
        TaskDataEntity::class,
        TimeLogsEntity::class,
        InvoiceEntity::class,
        ItemEntity::class,
        SettingsEntity::class
                         ],
        version = 34
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun appDao(): AppDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            @OptIn(InternalCoroutinesApi::class)
            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "grocery_database"
                    )
                        .fallbackToDestructiveMigration(true)
                        .build().also { INSTANCE = it }
                }
            }
        }
    }
}