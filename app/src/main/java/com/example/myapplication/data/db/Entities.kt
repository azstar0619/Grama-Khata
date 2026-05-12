package com.example.myapplication.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameKannada: String,
    val village: String,
    val villageKannada: String,
    val phoneNumber: String,
    val balance: Double,
    val lastTransactionDate: Long,
    val profileImageUri: String?
)

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = CustomerEntity::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val amount: Double,
    val type: String,
    val note: String,
    val timestamp: Long
)
