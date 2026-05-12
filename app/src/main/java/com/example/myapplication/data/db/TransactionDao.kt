package com.example.myapplication.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 15")
    fun getRecentTransactions(): Flow<List<TransactionEntity>>

    @Query(
        "SELECT SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END) FROM transactions WHERE timestamp >= :sinceMs"
    )
    suspend fun getTotalPaymentsSince(sinceMs: Long): Double?

    @Query("SELECT * FROM transactions WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC")
    suspend fun getTransactionsBetween(start: Long, end: Long): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT COUNT(*) FROM transactions WHERE customerId = :customerId")
    suspend fun getTransactionCount(customerId: String): Int
}
