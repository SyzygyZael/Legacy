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
import com.kevinnesbitt.legacysecurefreelancercrm.variables.ProjectStatus
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.internal.synchronized

@Entity(tableName = "clients")
data class ClientDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val email: String,
    val telp: String,
    val currency: String,
    val status: String = ClientStatus.ACTIVE.name
)

@Entity(tableName = "projects")
data class ProjectDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val clientId: Int,
    val title: String,
    val description: String,
    val status: String = ProjectStatus.ACTIVE.name,
    val deadLine: String,
    val payRate: Double,
    val billingType: String,
    val budget: Double
)

@Entity(tableName = "tasks")
data class TaskDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val description: String,
    val isCompleted: Boolean,
    val dueDate: String
)

@Entity(tableName = "time_logs")
data class TimeLogsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val startTime: Long,
    val endTime: Long,
    val isBilled: Boolean
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val projectId: Int,
    val invoiceNumber: String,
    val amount: Double,
    val issueDate: String,
    val dueDate: String,
    val status: String,
    val taxPercentage: Double
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val backgroundColor: Long = 0xFFFFFFFFL,
    val mainTextColor: Long = 0xFF000000L
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

    // "GET ALL" QUERIES
    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    @Query("SELECT * FROM time_logs")
    fun getAllTimeLogs(): Flow<List<TimeLogsEntity>>

    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<ProjectDataEntity>>

    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<ClientDataEntity>>

    // UPDATE QUERIES
    @Query("UPDATE clients SET name = :newName, email = :newEmail, telp = :newNum, currency = :newCurrency WHERE id = :clientId")
    suspend fun updateClientInfo(clientId: Int, newName: String, newEmail: String, newNum: String, newCurrency: String)

    @Query("UPDATE projects SET title = :newTitle, description = :newDesc, deadline = :newDeadline, payRate = :newPayrate, billingType = :newBT, budget = :newBudget WHERE id = :projectId")
    suspend fun updateProjectInfo(projectId: Int, newTitle: String, newDesc: String, newDeadline: String, newPayrate: Double, newBT: String, newBudget: Double)

    @Query("UPDATE clients SET status = :status WHERE id = :clientId")
    suspend fun updateClientStatus(status: String, clientId: Int)

    @Query("UPDATE projects SET status = :status WHERE id = :projectId AND clientId = :clientId")
    suspend fun updateProjectStatus(status: String, projectId: Int, clientId: Int)

    @Query("UPDATE projects SET description = :newDesc WHERE id = :projectId")
    suspend fun updateProjectDescription(projectId: Int, newDesc: String)

    @Query("UPDATE tasks SET isCompleted = :taskStatus WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, taskStatus: Boolean)

    @Database(entities = [
        ClientDataEntity::class,
        ProjectDataEntity::class,
        TaskDataEntity::class,
        TimeLogsEntity::class,
        InvoiceEntity::class,
        SettingsEntity::class
                         ],
        version = 9
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