package com.example.myapplication.data

import com.example.myapplication.data.db.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

class GramaKhataRepository(private val database: AppDatabase) {
    private val customerDao = database.customerDao()
    private val transactionDao = database.transactionDao()

    val customers: Flow<List<Customer>> = customerDao.getAllCustomers()
        .map { it.map { e -> e.toCustomer() } }

    val recentTransactions: Flow<List<Transaction>> = transactionDao.getRecentTransactions()
        .map { it.map { e -> e.toTransaction() } }

    fun getCustomer(id: String): Flow<Customer?> =
        customerDao.getCustomerById(id).map { it?.toCustomer() }

    fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsForCustomer(customerId)
            .map { it.map { e -> e.toTransaction() } }

    suspend fun addCustomer(customer: Customer) {
        customerDao.insertCustomer(customer.toEntity())
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
        val entity = customerDao.getCustomerById(transaction.customerId).first() ?: return
        val change = if (transaction.type == TransactionType.CREDIT) transaction.amount
                     else -transaction.amount
        customerDao.updateCustomer(
            entity.copy(
                balance = entity.balance + change,
                lastTransactionDate = System.currentTimeMillis()
            )
        )
    }

    suspend fun getTodayCollection(): Double {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return transactionDao.getTotalPaymentsSince(cal.timeInMillis) ?: 0.0
    }

    suspend fun getWeeklyReport(): List<Transaction> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -7)
        }
        return transactionDao.getTransactionsBetween(
            cal.timeInMillis, System.currentTimeMillis()
        ).map { it.toTransaction() }
    }

    suspend fun seedInitialDataIfNeeded() {
        if (customerDao.getCustomerCount() > 0) return
        val mockCustomers = listOf(
            Customer(name = "Ningappa Gowda", nameKannada = "ನಿಂಗಪ್ಪ ಗೌಡ",
                village = "Mysore", villageKannada = "ಮೈಸೂರು",
                phoneNumber = "9876543214", balance = 12000.0),
            Customer(name = "Basavaraj H", nameKannada = "ಬಸವರಾಜ್ ಹೆಚ್",
                village = "Tumkur", villageKannada = "ತುಮಕೂರು",
                phoneNumber = "9876543212", balance = 3500.0),
            Customer(name = "Venkatesha R", nameKannada = "ವೆಂಕಟೇಶ ಆರ್",
                village = "Channapatna", villageKannada = "ಚನ್ನಪಟ್ಟಣ",
                phoneNumber = "9876543216", balance = 4200.0),
            Customer(name = "Ramesh Gowda", nameKannada = "ರಮೇಶ್ ಗೌಡ",
                village = "Hassan", villageKannada = "ಹಾಸನ",
                phoneNumber = "9876543210", balance = 1500.0),
            Customer(name = "Parvathi Devi", nameKannada = "ಪಾರ್ವತಿ ದೇವಿ",
                village = "Ramanagar", villageKannada = "ರಾಮನಗರ",
                phoneNumber = "9876543215", balance = 750.0),
            Customer(name = "Mallikarjun B", nameKannada = "ಮಲ್ಲಿಕಾರ್ಜುನ ಬಿ",
                village = "Hassan", villageKannada = "ಹಾಸನ",
                phoneNumber = "9876543213", balance = 50.0),
            Customer(name = "Suresh Kumar", nameKannada = "ಸುರೇಶ್ ಕುಮಾರ್",
                village = "Mandya", villageKannada = "ಮಂಡ್ಯ",
                phoneNumber = "9876543211", balance = -200.0),
        )
        mockCustomers.forEach { addCustomer(it) }
    }
}
