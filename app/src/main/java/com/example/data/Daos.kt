package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface JahitDao {
    // Customers
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)
    @Update
    suspend fun updateCustomer(customer: Customer)
    @Delete
    suspend fun deleteCustomer(customer: Customer)

    // Inventory
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    fun getAllInventory(): Flow<List<Inventory>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory)
    @Update
    suspend fun updateInventory(inventory: Inventory)
    @Delete
    suspend fun deleteInventory(inventory: Inventory)
    @Query("DELETE FROM inventory")
    suspend fun deleteAllInventory()

    // Suppliers
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<Supplier>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier)
    @Update
    suspend fun updateSupplier(supplier: Supplier)
    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    // Services
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceItem>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceItem)
    @Update
    suspend fun updateService(service: ServiceItem)
    @Delete
    suspend fun deleteService(service: ServiceItem)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY dateIn DESC")
    fun getAllTransactions(): Flow<List<Tx>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTx(tx: Tx): Long
    @Update
    suspend fun updateTx(tx: Tx)
    @Delete
    suspend fun deleteTx(tx: Tx)
    @Query("SELECT * FROM transactions WHERE dateIn >= :start AND dateIn <= :end ORDER BY dateIn DESC")
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Tx>>

    // TxItems
    @Query("SELECT * FROM transaction_items WHERE transactionId = :txId")
    fun getItemsForTx(txId: Int): Flow<List<TxItem>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTxItems(items: List<TxItem>)

    // Receivables
    @Query("SELECT * FROM receivables")
    fun getAllReceivables(): Flow<List<Receivable>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivable(receivable: Receivable)
    @Update
    suspend fun updateReceivable(receivable: Receivable)

    // Payables
    @Query("SELECT * FROM payables")
    fun getAllPayables(): Flow<List<Payable>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayable(payable: Payable)
    @Update
    suspend fun updatePayable(payable: Payable)

    // Losses
    @Query("SELECT * FROM losses ORDER BY date DESC")
    fun getAllLosses(): Flow<List<Loss>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoss(loss: Loss)
    @Update
    suspend fun updateLoss(loss: Loss)
    @Delete
    suspend fun deleteLoss(loss: Loss)

    // Logs
    @Query("SELECT * FROM activity_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>
    @Insert
    suspend fun insertLog(log: ActivityLog)

    @Query("SELECT SUM(price * qty) FROM transaction_items WHERE type = 'INVEN'")
    fun getTotalModalTerpakai(): Flow<Double?>

    // AppData config
    @Query("DELETE FROM customers")
    suspend fun wipeCustomers()
    @Query("DELETE FROM suppliers")
    suspend fun wipeSuppliers()
    @Query("DELETE FROM services")
    suspend fun wipeServices()
    @Query("DELETE FROM transactions")
    suspend fun wipeTransactions()
    @Query("DELETE FROM transaction_items")
    suspend fun wipeTxItems()
    @Query("DELETE FROM receivables")
    suspend fun wipeReceivables()
    @Query("DELETE FROM payables")
    suspend fun wipePayables()
    @Query("DELETE FROM losses")
    suspend fun wipeLosses()
    @Query("DELETE FROM activity_logs")
    suspend fun wipeLogs()
}
