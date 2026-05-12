package com.example.myapplication.data

import com.example.myapplication.data.db.CustomerEntity
import com.example.myapplication.data.db.TransactionEntity
import java.util.UUID

data class Customer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val nameKannada: String,
    val village: String,
    val villageKannada: String,
    val phoneNumber: String,
    val balance: Double = 0.0,
    val lastTransactionDate: Long = System.currentTimeMillis(),
    val profileImageUri: String? = null
)

enum class TransactionType {
    CREDIT,  // Koduvudu — you gave goods on credit, customer owes more
    PAYMENT  // Tegedukolluvudu — customer paid, balance decreases
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val amount: Double,
    val type: TransactionType,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ── Mapping helpers ──────────────────────────────────────────────────────────

fun CustomerEntity.toCustomer() = Customer(
    id = id, name = name, nameKannada = nameKannada,
    village = village, villageKannada = villageKannada,
    phoneNumber = phoneNumber, balance = balance,
    lastTransactionDate = lastTransactionDate, profileImageUri = profileImageUri
)

fun Customer.toEntity() = CustomerEntity(
    id = id, name = name, nameKannada = nameKannada,
    village = village, villageKannada = villageKannada,
    phoneNumber = phoneNumber, balance = balance,
    lastTransactionDate = lastTransactionDate, profileImageUri = profileImageUri
)

fun TransactionEntity.toTransaction() = Transaction(
    id = id, customerId = customerId, amount = amount,
    type = TransactionType.valueOf(type), note = note, timestamp = timestamp
)

fun Transaction.toEntity() = TransactionEntity(
    id = id, customerId = customerId, amount = amount,
    type = type.name, note = note, timestamp = timestamp
)
