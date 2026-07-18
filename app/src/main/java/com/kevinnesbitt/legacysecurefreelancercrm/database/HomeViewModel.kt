package com.kevinnesbitt.legacysecurefreelancercrm.database

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Path.Companion.combine
import androidx.compose.ui.text.style.TextDecoration.Companion.combine
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Currency

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDao.AppDatabase.getDatabase(application).appDao()

    init {
        viewModelScope.launch {
            val existing = dao.getSettingsOnce()
            if (existing == null) {
                dao.insertSettings(SettingsEntity())
            }
        }
    }

    val settings = dao.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsEntity())

    val clientState: StateFlow<List<ClientData>> = combine(
        flow = dao.getAllProjects(),
        flow2 = dao.getAllTasks(),
        flow3 = dao.getAllTimeLogs(),
        flow4 = dao.getAllInvoices(),
        flow5 = dao.getAllClients()
    ) { projects, tasks, timeLogs, invoices, clients ->
        clients.map { client ->
            ClientData(
                id = client.id,
                name = client.name,
                email = client.email,
                telp = client.telp,
                currency = client.currency,
                status = client.status,
                projects = projects
                    .filter { it.clientId == client.id }
                    .map { project ->
                        ProjectData(
                            id = project.id,
                            clientId = project.clientId,
                            title = project.title,
                            description = project.description,
                            status = project.status,
                            deadLine = project.deadLine,
                            payRate = project.payRate,
                            billingType = project.billingType,
                            budget = project.budget,
                            tasks = tasks
                                .filter { it.projectId == project.id }
                                .map { task ->
                                TaskData(
                                    id = task.id,
                                    projectId = task.projectId,
                                    description = task.description,
                                    isCompleted = task.isCompleted,
                                    dueDate = task.dueDate
                                )
                            },
                            timeLogs = timeLogs
                                .filter { it.projectId == project.id }
                                .map { timeLog ->
                                    TimeLogData(
                                        id = timeLog.id,
                                        projectId = timeLog.id,
                                        startTime = timeLog.startTime,
                                        endTime = timeLog.endTime,
                                        isBilled = timeLog.isBilled
                                    )
                                },
                            invoices = invoices
                                .filter { it.projectId == project.id }
                                .map { invoice ->
                                    InvoiceData(
                                        id = invoice.id,
                                        projectId = invoice.projectId,
                                        invoiceNumber = invoice.invoiceNumber,
                                        amount = invoice.amount,
                                        issueDate = invoice.issueDate,
                                        dueDate = invoice.issueDate,
                                        status = invoice.status,
                                        taxPercentage = invoice.taxPercentage
                                    )
                                }
                        )
                    }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Pipeline: Seamlessly merges independent Room tables down into 1 state object
    val uiState: StateFlow<DashboardUiState> = combine(
        dao.getAllInvoices(),
        dao.getAllTasks(),
        dao.getAllTimeLogs(),
        dao.getAllProjects()
    ) { invoices, tasks, timeLogs, projects ->

        // 🗓️ Calculate Epoch timestamp ranges for the current active calendar month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonthMs = calendar.timeInMillis

        // 💵 Metric A: Active Month Earnings (Paid Invoices generated/marked within this month)
        val monthlyEarnings = invoices
            .filter { it.status.equals("Paid", ignoreCase = true) && it.issueDate >= startOfMonthMs.toString() }
            .sumOf { it.amount }

        // 📁 Metric B: Outstanding/Pending Accounts Receivables (Sent but unpaid)
        val pendingAmount = invoices
            .filter { it.status.equals("Sent", ignoreCase = true) }

        // 🛑 Filter C: Overdue Invoices lists
        val currentTime = System.currentTimeMillis()
        val overdueList = invoices.filter {
            it.status.equals("Sent", ignoreCase = true) && it.dueDate < currentTime.toString()
        }

        // ⏱️ Find any active running timer log (where endTime is not logged yet)
        val activeTimer = timeLogs.find { it.endTime == 0L }
        val boundProject = activeTimer?.let { timer ->
            projects.find { it.id == timer.projectId }?.title
        }

        // 📋 Urgent Tasks Filter (Incomplete tasks sorted sequentially by closest due date)
        val urgentTasks = tasks
            .filter { !it.isCompleted }
            .sortedBy { it.dueDate }
            .take(5) // Don't overwhelm dashboard; keep it tight to top 5 priorities

        DashboardUiState(
            isLoading = false,
            activeEarningsThisMonth = monthlyEarnings,
            pendingInvoices = pendingAmount.size,
            runningTimer = activeTimer,
            activeProjectTitle = boundProject ?: "No Active Session",
            highPriorityTasks = urgentTasks,
            overdueInvoices = overdueList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Clean resource loop on activity teardowns
        initialValue = DashboardUiState(isLoading = true)
    )

    // ==========================================
    // USER ACTIONS & INTENTS (Database Writes)
    // ==========================================

    fun createClient(name: String, email: String, telp: String, currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentClientsCount = dao.getAllClientsList().size

            dao.insertClient(
                ClientDataEntity(
                    id = 0,
                    name = name,
                    email = email,
                    telp = telp,
                    currency = currency,
                    orderIndex = currentClientsCount
                )
            )
        }
    }

    fun createProject(clientId: Int, title: String, description: String, budget: Double, type: String, payRate: Double, deadline: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentProjectsCount = dao.getProjectFromClientId(clientId).size

            dao.insertProject(
                ProjectDataEntity(
                    clientId = clientId,
                    title = title,
                    description = description,
                    deadLine = deadline,
                    payRate = payRate,
                    billingType = type,
                    budget = budget,
                    id = 0,
                    orderIndex = currentProjectsCount
                )
            )
        }
    }

    fun createTask(projectId: Int, description: String, dueDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTasksCount = dao.getTasksFromProjectId(projectId).size

            dao.insertTask(
                TaskDataEntity(
                    id = 0,
                    projectId = projectId,
                    description = description,
                    isCompleted = false,
                    dueDate = dueDate,
                    orderIndex = currentTasksCount
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TaskDataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertTask(task.copy(isCompleted = !task.isCompleted)) // REPLACE strategy updates it
        }
    }

    // ==========================================
    // THE STOPWATCH TIMER LOGIC
    // ==========================================

    fun startTrackingTime(projectId: Int, startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertTimeLog(
                TimeLogsEntity(id = 0, projectId = projectId, startTime = startTime)
            )
        }
    }

    fun stopTrackingTime(activeLog: TimeLogsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedLog = activeLog.copy(endTime = System.currentTimeMillis())
            dao.insertTimeLog(updatedLog)
        }
    }

    fun quickMarkInvoicePaid(invoice: InvoiceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertInvoice(invoice.copy(status = "Paid"))
        }
    }

    fun updateClientStatus(status: String, clientId: Int) {
        viewModelScope.launch {
            dao.updateClientStatus(status, clientId)
        }
    }

    fun updateProjectStatus(status: String, projectId: Int, clientId: Int) {
        viewModelScope.launch {
            dao.updateProjectStatus(status, projectId, clientId)
        }
    }

    fun updateProjectOrder(reorderedTasks: List<ProjectData>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Map each task to its new position index value and save it to Room
            reorderedTasks.forEachIndexed { index, project ->
                dao.updateProjectPosition(project.id, index)
            }
        }
    }

    fun updateClientInfo(clientId: Int, newName: String, newEmail: String, newTelp: String, newCurrency: String) {
        viewModelScope.launch {
            dao.updateClientInfo(clientId, newName, newEmail, newTelp, newCurrency)
        }
    }

    fun updateClientOrder(reorderedTasks: List<ClientData>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Map each task to its new position index value and save it to Room
            reorderedTasks.forEachIndexed { index, client ->
                dao.updateClientPosition(client.id, index)
            }
        }
    }

    fun updateProjectInfo(projectId: Int, newTitle: String, newDesc: String, newStatus: String, newDeadline: String, newPayrate: Double, newBT: String, newBudget: Double) {
        viewModelScope.launch {
            dao.updateProjectInfo(projectId, newTitle, newDesc, newDeadline, newPayrate, newBT, newBudget)
        }
    }

    fun updateProjectDescription(projectId: Int, newDesc: String) {
        viewModelScope.launch {
            dao.updateProjectDescription(projectId, newDesc)
        }
    }

    fun updateTaskStatus(taskId: Int, taskStatus: Boolean) {
        viewModelScope.launch {
            dao.updateTaskStatus(taskId, taskStatus)
        }
    }

    fun editTaskInfo(taskId: Int, newName: String, newDueDate: String) {
        viewModelScope.launch {
            dao.editTaskInfo(taskId, newName, newDueDate)
        }
    }

    fun updateTasksOrder(reorderedTasks: List<TaskData>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Map each task to its new position index value and save it to Room
            reorderedTasks.forEachIndexed { index, task ->
                dao.updateTaskPosition(task.id, index)
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            dao.deleteTask(taskId)
        }
    }

    fun updateTimerState(isActive: Boolean) {
        viewModelScope.launch {
            dao.updateTimerState(isActive)
        }
    }

    fun updateTimerPausedState(isPaused: Boolean) {
        viewModelScope.launch {
            dao.updateTimerPausedState(isPaused)
        }
    }

    fun updateStartTime(timeLogId: Int, startTime: Long) {
        viewModelScope.launch {
            dao.updateStartTime(timeLogId, startTime)
        }
    }

    fun updateEndTime(timeLogId: Int, endTime: Long) {
        viewModelScope.launch {
            dao.updateEndTime(timeLogId, endTime)
        }
    }

    fun updatePauseStartTime(timeLogId: Int, startTime: Long) {
        viewModelScope.launch {
            dao.updatePauseStartTime(timeLogId, startTime)
        }
    }

    fun updatePauseEndTime(timeLogId: Int, endTime: Long) {
        viewModelScope.launch {
            dao.updatePauseEndTime(timeLogId, endTime)
        }
    }

    fun updateTotalEndTime(timeLogId: Int, numOfTime: Long) {
        viewModelScope.launch {
            dao.updateTotalPauseTime(timeLogId, numOfTime)
        }
    }

    data class DashboardUiState(
        val isLoading: Boolean = true,

        // Financial Metric Row
        val activeEarningsThisMonth: Double = 0.0,
        val pendingInvoices: Int = 0,

        // Persistent Stopwatch/Timer Tracker
        val runningTimer: TimeLogsEntity? = null,
        val activeProjectTitle: String? = null,

        // Actionable Items lists
        val highPriorityTasks: List<TaskDataEntity> = emptyList(),
        val overdueInvoices: List<InvoiceEntity> = emptyList()
    )

    data class ClientData(
        val id: Int,
        val name: String,
        val email: String,
        val telp: String,
        val currency: String,
        val status: String,
        val projects: List<ProjectData>
    )

    data class ProjectData(
        val id: Int,
        val clientId: Int,
        val title: String,
        val description: String,
        val status: String,
        val deadLine: String,
        val payRate: Double,
        val billingType: String,
        val budget: Double,
        val tasks: List<TaskData>,
        val timeLogs: List<TimeLogData>,
        val invoices: List<InvoiceData>
    )

    data class TaskData(
        val id: Int,
        val projectId: Int,
        val description: String,
        val isCompleted: Boolean,
        val dueDate: String
    )

    data class TimeLogData(
        val id: Int,
        val projectId: Int,
        val startTime: Long,
        val endTime: Long,
        val pauseStartTime: Long = 0L,
        val pauseEndTime: Long = 0L,
        val totalPauseTime: Long = 0L,
        val isBilled: Boolean
    )

    data class InvoiceData(
        val id: Int,
        val projectId: Int,
        val invoiceNumber: String,
        val amount: Double,
        val issueDate: String,
        val dueDate: String,
        val status: String,
        val taxPercentage: Double
    )
}