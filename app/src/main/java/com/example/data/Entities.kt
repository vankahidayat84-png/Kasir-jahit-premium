package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val notes: String
)

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val stockQty: Int,
    val unit: String,
    val costPrice: Double,
    val supplierName: String,
    val notes: String
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val address: String,
    val notes: String
)

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val defaultPrice: Double,
    val notes: String
)

@Entity(tableName = "transactions")
data class Tx(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val txNumber: String,
    val customerName: String,
    val dateIn: Long,
    val dateOut: Long,
    val status: String,
    val notes: String
)

@Entity(tableName = "transaction_items")
data class TxItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val type: String, // "SERVICE" or "INVEN"
    val name: String,
    val qty: Int,
    val price: Double, // for service it's selling price, for inven it's cost price
    val notes: String = "",
    val materials: String = ""
)

@Entity(tableName = "receivables")
data class Receivable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val totalAmount: Double,
    val paidAmount: Double
)

@Entity(tableName = "payables")
data class Payable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val supplierName: String,
    val totalAmount: Double,
    val paidAmount: Double
)

@Entity(tableName = "losses")
data class Loss(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: Long,
    val notes: String
)

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val time: String,
    val description: String
)

@Entity(tableName = "settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
