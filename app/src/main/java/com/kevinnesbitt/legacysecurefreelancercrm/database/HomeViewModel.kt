package com.kevinnesbitt.legacysecurefreelancercrm.database

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import android.graphics.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinnesbitt.legacysecurefreelancercrm.variables.InvoiceStatus
import com.kevinnesbitt.legacysecurefreelancercrm.variables.SupportedCurrency
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.nio.DoubleBuffer

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val dao = AppDao.AppDatabase.getDatabase(application).appDao()

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
            android.util.Log.d("Client Company", "Client Company: ${client.company}")
            ClientData(
                id = client.id,
                name = client.name,
                company = client.company,
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
                                        projectId = timeLog.projectId,
                                        startTime = timeLog.startTime,
                                        endTime = timeLog.endTime,
                                        isBilled = timeLog.isBilled,
                                        date = timeLog.date
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
                                        dueDate = invoice.dueDate,
                                        issueTo = invoice.issueTo,
                                        clientCompany = invoice.clientCompany,
                                        clientEmail = invoice.clientEmail,
                                        clientTelephone = invoice.clientTelephone,
                                        payTo = invoice.payTo,
                                        selfAddress = invoice.selfAddress,
                                        selfEmail = invoice.selfEmail,
                                        selfTelephone = invoice.selfTelephone,
                                        status = invoice.status,
                                        taxPercentage = invoice.taxPercentage,
                                        items = emptyList()
                                    )
                                }
                        )
                    }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Pipeline: Seamlessly merges independent Room tables down into 1 state object
    val uiState: StateFlow<DashboardUiState> = combine(
        dao.getAllClients(),
        dao.getAllInvoices(),
        dao.getAllTasks(),
        dao.getAllProjects()
    ) { clients, invoices, tasks, projects ->

        // Calculate Epoch timestamp ranges for the current active calendar month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonthMs = calendar.timeInMillis

        // Active Month Earnings (Paid Invoices generated/marked within this month)
        val monthlyEarnings = invoices
            .filter { it.status.equals(InvoiceStatus.PAID.name, ignoreCase = true) && it.issueDate >= startOfMonthMs.toString() }
            .sumOf { it.amount }

        // Outstanding/Pending Accounts Receivables (Sent but unpaid)
        val pendingAmount = invoices
            .filter { it.status.equals(InvoiceStatus.SENT.name, ignoreCase = true) }

        // All Time Earnings
        var totalEarnings = 0.0

        invoices.filter { invoice -> invoice.status == InvoiceStatus.PAID.name }
            .forEach { paidInvoice ->
                val thisProject = dao.getProjectFromId(paidInvoice.projectId)
                val thisClient = dao.getClientFromId(thisProject.clientId)
                val toDollarConversion = SupportedCurrency.entries.find { it.code == thisClient.currency }?.USDConversion?: 0f
                val toTargetCurrencyConversion = SupportedCurrency.entries.find { it.code == settings.value.preferredCurrency }?.USDConversion?: 0f

                totalEarnings += (paidInvoice.amount / toDollarConversion) * toTargetCurrencyConversion


            }

        // Overdue Invoices lists
        val currentTime = System.currentTimeMillis()
        val overdueList = invoices.filter {
            it.status.equals("Sent", ignoreCase = true) && it.dueDate < currentTime.toString()
        }

        // Urgent Tasks Filter (Incomplete tasks sorted sequentially by closest due date)
        val urgentTasks = tasks
            .filter { !it.isCompleted }
            .sortedBy { it.dueDate }
            .take(5) // Don't overwhelm dashboard; keep it tight to top 5 priorities

        DashboardUiState(
            activeEarningsThisMonth = monthlyEarnings,
            pendingInvoices = pendingAmount.size,
            highPriorityTasks = urgentTasks,
            overdueInvoices = overdueList,
            totalEarnings = totalEarnings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Clean resource loop on activity teardowns
        initialValue = DashboardUiState()
    )

    val items: StateFlow<List<ItemData>> = dao.getAllInvoiceItems().map { invoiceItems ->
        invoiceItems.map { item ->
            ItemData(
                id = item.id,
                invoiceId = item.invoiceId,
                name = item.name,
                quantity = item.quantity,
                price = item.price
            )
        }
    }

        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Clean resource loop on activity teardowns
            initialValue = emptyList()
        )

    // ==========================================
    // USER ACTIONS & INTENTS (Database Writes)
    // ==========================================

    fun updateSettings(
        timeFormat: String,
        dateFormat: String,
        selfName: String,
        selfAddress: String,
        selfEmail: String,
        selfTelephone: String,
        currency: String,
        taxBracket: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateSettings(
                timeFormat = timeFormat,
                dateFormat = dateFormat,
                selfName = selfName,
                selfAddress = selfAddress,
                selfTelephone = selfTelephone,
                selfEmail = selfEmail,
                currency = currency,
                taxBracket = taxBracket
            )
        }
    }

    fun createClient(name: String, company: String, email: String, telp: String, currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentClientsCount = dao.getAllClientsList().size

            dao.insertClient(
                ClientDataEntity(
                    id = 0,
                    name = name,
                    company = company,
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

    fun createTimeLog(projectId: Int, startTime: Long, endTime: Long, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTimeLogsCount = dao.getTimeLogsFromProjectId(projectId).size

            dao.insertTimeLog(
                TimeLogsEntity(
                    id = 0,
                    projectId = projectId,
                    startTime = startTime,
                    endTime = endTime,
                    date = date,
                    orderIndex = currentTimeLogsCount
                )
            )
        }
    }

    suspend fun createInvoice(
        projectId: Int,
        invoiceNumber: String,
        issueDate: String,
        dueDate: String,
        issueTo: String,
        clientCompany: String,
        clientEmail: String,
        clientTelephone: String,
        payTo: String,
        selfAddress: String,
        selfEmail: String,
        selfTelephone: String,
        taxPercentage: Double
    ): Long {
        return withContext(Dispatchers.IO) {
            dao.insertInvoice(
                InvoiceEntity(
                    id = 0,
                    projectId = projectId,
                    invoiceNumber = invoiceNumber,
                    issueDate = issueDate,
                    dueDate = dueDate,
                    issueTo = issueTo,
                    clientCompany = clientCompany,
                    clientEmail = clientEmail,
                    clientTelephone = clientTelephone,
                    payTo = payTo,
                    selfAddress = selfAddress,
                    selfEmail = selfEmail,
                    selfTelephone = selfTelephone,
                    taxPercentage = taxPercentage
                )
            )
        }
    }

    fun updateInvoice(
        invoiceId: Int,
        invoiceNumber: String,
        issueDate: String,
        dueDate: String,
        issueTo: String,
        clientCompany: String,
        clientEmail: String,
        clientTelephone: String,
        payTo: String,
        selfAddress: String,
        selfEmail: String,
        selfTelephone: String,
        taxPercentage: Double,
        amount: Double,
        status: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            android.util.Log.d("HomeViewModel Dates $invoiceId", "Issue Date: $issueDate, Due Date: $dueDate")

            dao.updateInvoice(
                invoiceId = invoiceId,
                invoiceNumber = invoiceNumber,
                issueDate = issueDate,
                dueDate = dueDate,
                issueTo = issueTo,
                clientCompany = clientCompany,
                clientEmail = clientEmail,
                clientTelephone = clientTelephone,
                payTo = payTo,
                selfAddress = selfAddress,
                selfEmail = selfEmail,
                selfTelephone = selfTelephone,
                taxPercentage = taxPercentage,
                amount = amount,
                status = status
            )
        }
    }

    fun deleteInvoice(invoiceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteInvoice(invoiceId)
        }
    }

    fun getInvoice(invoiceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getInvoice(invoiceId)
        }
    }

    fun updateInvoiceStatus(invoiceId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateInvoiceStatus(invoiceId, status)
        }
    }

    suspend fun createItem(invoiceId: Int, name: String, price: Double, quantity: Int): Long {
        return withContext(Dispatchers.IO) {
            dao.insertItem(
                ItemEntity(
                    id = 0,
                    invoiceId = invoiceId,
                    name = name,
                    price = price,
                    quantity = quantity
                )
            )
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteItem(itemId)
        }
    }

    fun updateItem(itemId: Int, name: String, price: Double, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateItem(itemId, name, price, quantity)
        }
    }

    fun editTimeLogInfo(logId: Int, startTime: Long, endTime: Long, date: String) {
        viewModelScope.launch {
            dao.updateTimeLogInfo(
                logId = logId,
                startTime = startTime,
                endTime = endTime,
                date = date
            )
        }
    }

    // fun toggleTaskCompletion(task: TaskDataEntity) {
    //     viewModelScope.launch(Dispatchers.IO) {
    //         dao.insertTask(task.copy(isCompleted = !task.isCompleted)) // REPLACE strategy updates it
    //     }
    // }

    // ==========================================
    // THE STOPWATCH TIMER LOGIC
    // ==========================================

    fun startTrackingTime(projectId: Int, startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTimeLogsCount = dao.getTimeLogsFromProjectId(projectId).size

            val date = LocalDate.now()
            val formatterA = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            val formattedDateA = date.format(formatterA)

            dao.insertTimeLog(
                TimeLogsEntity(
                    id = 0,
                    projectId = projectId,
                    startTime = startTime,
                    date = formattedDateA,
                    orderIndex = currentTimeLogsCount

                )
            )
        }
    }

    // fun stopTrackingTime(activeLog: TimeLogsEntity) {
    //     viewModelScope.launch(Dispatchers.IO) {
    //         val updatedLog = activeLog.copy(endTime = System.currentTimeMillis())
    //         dao.insertTimeLog(updatedLog)
    //     }
    // }

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

    fun updateClientInfo(clientId: Int, newName: String, company: String, newEmail: String, newTelp: String, newCurrency: String) {
        viewModelScope.launch {
            dao.updateClientInfo(clientId, newName, company, newEmail, newTelp, newCurrency)
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

    fun updateProjectInfo(projectId: Int, newTitle: String, newDesc: String, newDeadline: String, newPayrate: Double, newBT: String, newBudget: Double) {
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

    fun updateTimeLogsOrder(reorderedLogs: List<TimeLogData>) {
        viewModelScope.launch(Dispatchers.IO) {
            reorderedLogs.forEachIndexed { index, log ->
                dao.updateTimeLogPosition(log.id, index)
            }
        }
    }

    fun deleteLog(logId: Int) {
        viewModelScope.launch {
            dao.deleteLog(logId)
        }
    }

    fun updateTimerState(isActive: Boolean) {
        viewModelScope.launch {
            dao.updateTimerState(isActive)
        }
    }

    // fun updateTimerPausedState(isPaused: Boolean) {
    //     viewModelScope.launch {
    //         dao.updateTimerPausedState(isPaused)
    //     }
    // }

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

    // fun updatePauseStartTime(timeLogId: Int, startTime: Long) {
    //     viewModelScope.launch {
    //         dao.updatePauseStartTime(timeLogId, startTime)
    //     }
    // }

    // fun updatePauseEndTime(timeLogId: Int, endTime: Long) {
    //     viewModelScope.launch {
    //         dao.updatePauseEndTime(timeLogId, endTime)
    //     }
    // }

    // fun updateTotalPauseTime(timeLogId: Int, numOfTime: Long?) {
    //     viewModelScope.launch {
    //         dao.updateTotalPauseTime(timeLogId, numOfTime)
    //     }
    // }

    fun updateTimeLogDate(timeLogId: Int, date: String) {
        viewModelScope.launch {
            dao.updateTimeLogDate(timeLogId, date)
        }
    }

// ─── PDF Generator ───────────────────────────────────────────

    object InvoicePdfGenerator {

        private const val PAGE_WIDTH = 595f
        private const val PAGE_HEIGHT = 842f
        private const val MARGIN_LEFT = 55f
        private const val MARGIN_RIGHT = 540f

        /**
         * @param signatureText Optional handwritten-style signature at the bottom.
         */
        fun generate(
            context: Context,
            invoice: InvoiceData,
            currencySymbol: String,
            signatureText: String? = null,
            fileName: String = "invoice_${invoice.invoiceNumber}.pdf"
        ): Uri {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH.toInt(), PAGE_HEIGHT.toInt(), 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            drawInvoice(canvas, invoice, currencySymbol, signatureText)

            document.finishPage(page)

            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { document.writeTo(it) }
            document.close()

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        private fun drawInvoice(canvas: Canvas, invoice: InvoiceData, currencySymbol: String, signatureText: String?) {
            val thinLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                strokeWidth = 0.8f
                style = Paint.Style.STROKE
            }

            val boldLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                strokeWidth = 1.2f
                style = Paint.Style.STROKE
            }

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 26f
                typeface = Typeface.DEFAULT_BOLD
                letterSpacing = 0.2f
            }

            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 9f
                typeface = Typeface.DEFAULT_BOLD
                letterSpacing = 0.08f
            }

            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 9.5f
                typeface = Typeface.DEFAULT
            }

            val moneyFormat = DecimalFormat("${currencySymbol}#,##0.00")
            val percentFormat = DecimalFormat("0")

            var y = 85f

            // ═══════════════════════════════════════════════════════
            // HEADER
            // ═══════════════════════════════════════════════════════
            canvas.drawLine(MARGIN_LEFT, y, 310f, y, thinLinePaint)
            canvas.drawText("INVOICE", 330f, y + 8f, titlePaint)

            y += 70f

            // ═══════════════════════════════════════════════════════
            // METADATA BLOCKS
            // ═══════════════════════════════════════════════════════
            val leftColX = MARGIN_LEFT
            val rightColX = 380f
            var leftY = y
            var rightY = y

            // ── Left: Issued To ──
            canvas.drawText("ISSUED TO:", leftColX, leftY, labelPaint)
            leftY += 14f
            canvas.drawText(invoice.issueTo, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.clientCompany, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.clientEmail, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.clientTelephone, leftColX, leftY, bodyPaint)

            // ── Left: Pay To ──
            leftY += 26f
            canvas.drawText("PAY TO:", leftColX, leftY, labelPaint)
            leftY += 14f
            canvas.drawText(invoice.payTo, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.selfAddress, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.selfEmail, leftColX, leftY, bodyPaint)
            leftY += 12f
            canvas.drawText(invoice.selfTelephone, leftColX, leftY, bodyPaint)

            // ── Right: Invoice Details ──
            val detailLabelX = rightColX
            val detailValueX = rightColX + 85f

            canvas.drawText("INVOICE NO:", detailLabelX, rightY, labelPaint)
            drawRightAlignedText(canvas, invoice.invoiceNumber, detailValueX + 70f, rightY, bodyPaint)
            rightY += 14f

            canvas.drawText("DATE:", detailLabelX, rightY, labelPaint)
            drawRightAlignedText(canvas, invoice.issueDate, detailValueX + 70f, rightY, bodyPaint)
            rightY += 14f

            canvas.drawText("DUE DATE:", detailLabelX, rightY, labelPaint)
            drawRightAlignedText(canvas, invoice.dueDate, detailValueX + 70f, rightY, bodyPaint)

            y = maxOf(leftY, rightY) + 55f

            // ═══════════════════════════════════════════════════════
            // ITEMS TABLE
            // ═══════════════════════════════════════════════════════
            val tableTop = y
            val descX = MARGIN_LEFT
            val priceX = 330f
            val qtyX = 405f
            val totalX = MARGIN_RIGHT

            canvas.drawLine(MARGIN_LEFT, tableTop, MARGIN_RIGHT, tableTop, boldLinePaint)

            y = tableTop + 18f
            canvas.drawText("DESCRIPTION", descX, y, labelPaint)
            drawRightAlignedText(canvas, "UNIT PRICE", priceX + 40f, y, labelPaint)
            drawRightAlignedText(canvas, "QTY", qtyX + 10f, y, labelPaint)
            drawRightAlignedText(canvas, "TOTAL", totalX, y, labelPaint)

            y += 8f
            canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, thinLinePaint)
            y += 18f

            var subtotal = 0.0
            invoice.items.forEach { item ->
                val multiplier = when(item.quantity) {
                    0 -> 1
                    -1 -> 1
                    else -> item.quantity
                }

                val quantityType = when(item.quantity) {
                    0 -> "-"
                    -1 -> "-"
                    else -> item.quantity.toString()
                }

                val lineTotal = item.price * multiplier
                subtotal += lineTotal

                canvas.drawText(item.name, descX, y, bodyPaint)
                drawRightAlignedText(canvas, moneyFormat.format(item.price), priceX + 40f, y, bodyPaint)
                drawRightAlignedText(canvas, quantityType, qtyX + 10f, y, bodyPaint)
                drawRightAlignedText(canvas, moneyFormat.format(lineTotal), totalX, y, bodyPaint)

                y += 16f
            }

            y += 4f
            canvas.drawLine(MARGIN_LEFT, y, MARGIN_RIGHT, y, boldLinePaint)
            y += 20f

            // ═══════════════════════════════════════════════════════
            // TOTALS
            // ═══════════════════════════════════════════════════════
            val taxAmount = subtotal * (invoice.taxPercentage / 100.0)
            val grandTotal = subtotal + taxAmount

            val totalsLabelX = 420f
            val totalsValueX = MARGIN_RIGHT

            canvas.drawText("SUBTOTAL", totalsLabelX, y, labelPaint)
            drawRightAlignedText(canvas, moneyFormat.format(subtotal), totalsValueX, y, bodyPaint)
            y += 14f

            canvas.drawText("Tax", totalsLabelX, y, bodyPaint)
            drawRightAlignedText(canvas, "${percentFormat.format(invoice.taxPercentage)}%", totalsValueX, y, bodyPaint)
            y += 16f

            canvas.drawText("TOTAL", totalsLabelX, y, labelPaint)
            drawRightAlignedText(canvas, moneyFormat.format(grandTotal), totalsValueX, y, labelPaint)

            // ═══════════════════════════════════════════════════════
            // SIGNATURE (optional)
            // ═══════════════════════════════════════════════════════
            signatureText?.let { sig ->
                y += 70f
                val sigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.BLACK
                    textSize = 22f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                }
                val path = Path().apply {
                    moveTo(MARGIN_LEFT + 180f, y)
                    quadTo(MARGIN_LEFT + 260f, y - 10f, MARGIN_LEFT + 340f, y + 5f)
                }
                canvas.drawTextOnPath(sig, path, 0f, 0f, sigPaint)
            }
        }

        private fun drawRightAlignedText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
            val width = paint.measureText(text)
            canvas.drawText(text, x - width, y, paint)
        }
    }

    // ─── Data Models ─────────────────────────────────────────────

    data class DashboardUiState(
        val activeEarningsThisMonth: Double = 0.0,
        val pendingInvoices: Int = 0,
        val totalEarnings: Double = 0.0,
        val highPriorityTasks: List<TaskDataEntity> = emptyList(),
        val overdueInvoices: List<InvoiceEntity> = emptyList()
    )

    data class ClientData(
        val id: Int,
        val name: String,
        val company: String,
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
        // val pauseStartTime: Long = 0L,
        // val pauseEndTime: Long = 0L,
        // val totalPauseTime: Long = 0L,
        val isBilled: Boolean,
        val date: String
    )

    data class InvoiceData(
        val id: Int,
        val projectId: Int,
        val invoiceNumber: String,
        val amount: Double,
        val issueDate: String,
        val dueDate: String,
        val issueTo: String,
        val clientCompany: String,
        val clientEmail: String,
        val clientTelephone: String,
        val payTo: String,
        val selfAddress: String,
        val selfEmail: String,
        val selfTelephone: String,
        val status: String,
        val taxPercentage: Double,
        val items: List<ItemData>
    )

    data class ItemData(
        val id: Int,
        val invoiceId: Int,
        val name: String,
        val price: Double,
        val quantity: Int
    )
}