package com.kevinnesbitt.legacysecurefreelancercrm.database

import android.app.Application
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

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDao.AppDatabase.getDatabase(application).appDao()

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
                hourlyRate = client.hourlyRate,
                currency = client.currency,
                projects = projects
                    .filter { it.id == client.id }
                    .map { project ->
                        ProjectData(
                            id = project.id,
                            clientId = project.clientId,
                            title = project.title,
                            status = project.status,
                            deadLine = project.deadLine,
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
            .filter { it.status.equals("Paid", ignoreCase = true) && it.issueDate >= startOfMonthMs }
            .sumOf { it.amount }

        // 📁 Metric B: Outstanding/Pending Accounts Receivables (Sent but unpaid)
        val pendingAmount = invoices
            .filter { it.status.equals("Sent", ignoreCase = true) }

        // 🛑 Filter C: Overdue Invoices lists
        val currentTime = System.currentTimeMillis()
        val overdueList = invoices.filter {
            it.status.equals("Sent", ignoreCase = true) && it.dueDate < currentTime
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

    fun createClient(name: String, email: String, rate: Double, currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertClient(ClientDataEntity(id = 0, name = name, email = email, hourlyRate = rate, currency = currency))
        }
    }

    fun createProject(clientId: Int, title: String, budget: Double, type: String, deadline: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertProject(
                ProjectDataEntity(
                    clientId = clientId,
                    title = title,
                    status = 0, // 0 = Active
                    deadLine = deadline,
                    billingType = type,
                    budget = budget,
                    id = 0
                )
            )
        }
    }

    fun createTask(projectId: Int, description: String, dueDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertTask(
                TaskDataEntity(
                    id = 0,
                    projectId = projectId,
                    description = description,
                    isCompleted = false,
                    dueDate = dueDate
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

    fun startTrackingTime(projectId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Safety guard check: Stop any running tracker first before triggering a new one
            val active = uiState.value.runningTimer
            if (active != null) {
                stopTrackingTime(active)
            }
            dao.insertTimeLog(
                TimeLogsEntity(
                    id = 0,
                    projectId = projectId,
                    startTime = System.currentTimeMillis(),
                    endTime = 0L,
                    isBilled = false)
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
        val hourlyRate: Double,
        val currency: String,
        val projects: List<ProjectData>
    )

    data class ProjectData(
        val id: Int,
        val clientId: Int,
        val title: String,
        val status: Int,
        val deadLine: Long,
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
        val dueDate: Long
    )

    data class TimeLogData(
        val id: Int,
        val projectId: Int,
        val startTime: Long,
        val endTime: Long,
        val isBilled: Boolean
    )

    data class InvoiceData(
        val id: Int,
        val projectId: Int,
        val invoiceNumber: String,
        val amount: Double,
        val issueDate: Long,
        val dueDate: Long,
        val status: String,
        val taxPercentage: Double
    )
}