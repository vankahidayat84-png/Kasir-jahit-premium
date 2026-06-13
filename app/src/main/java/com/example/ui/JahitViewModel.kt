package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JahitViewModel(private val dao: JahitDao) : ViewModel() {

    val customers = dao.getAllCustomers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val inventory = dao.getAllInventory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val suppliers = dao.getAllSuppliers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val services = dao.getAllServices().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = dao.getAllTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val receivables = dao.getAllReceivables().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val payables = dao.getAllPayables().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val losses = dao.getAllLosses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val logs = dao.getAllLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val modalTerpakai = dao.getTotalModalTerpakai().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private fun logActivity(desc: String) {
        viewModelScope.launch {
            val date = System.currentTimeMillis()
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            dao.insertLog(ActivityLog(date = date, time = time, description = desc))
        }
    }

    // --- Customers ---
    fun addCustomer(c: Customer) = viewModelScope.launch { dao.insertCustomer(c); logActivity("Tambah Pelanggan: ${c.name}") }
    fun updateCustomer(c: Customer) = viewModelScope.launch { dao.updateCustomer(c); logActivity("Edit Pelanggan: ${c.name}") }
    fun deleteCustomer(c: Customer) = viewModelScope.launch { dao.deleteCustomer(c); logActivity("Hapus Pelanggan: ${c.name}") }

    // --- Inventory ---
    fun addInventory(i: Inventory) = viewModelScope.launch { dao.insertInventory(i); logActivity("Tambah Inventaris: ${i.name}") }
    fun updateInventory(i: Inventory) = viewModelScope.launch { dao.updateInventory(i); logActivity("Update Inventaris: ${i.name}") }
    fun deleteInventory(i: Inventory) = viewModelScope.launch { dao.deleteInventory(i); logActivity("Hapus Inventaris: ${i.name}") }
    fun adjustStock(i: Inventory, diff: Int, reason: String) = viewModelScope.launch {
        dao.updateInventory(i.copy(stockQty = i.stockQty + diff))
        logActivity("$reason: ${i.name} -> ${i.stockQty + diff}")
    }

    // --- Suppliers ---
    fun addSupplier(s: Supplier) = viewModelScope.launch { dao.insertSupplier(s); logActivity("Tambah Supplier: ${s.name}") }

    // --- Services ---
    fun addService(s: ServiceItem) = viewModelScope.launch { dao.insertService(s); logActivity("Tambah Layanan: ${s.name}") }

    // --- Transactions ---
    // A Tx flow needs saving the Tx and TxItems. Then optionally updating stock and calculating receivables.
    fun addTransaction(tx: Tx, items: List<TxItem>, inventoryUsed: List<Pair<Inventory, Int>>, paidAmount: Double = 0.0) = viewModelScope.launch {
        val txId = dao.insertTx(tx).toInt()
        val finalItems = items.map { it.copy(transactionId = txId) }
        dao.insertTxItems(finalItems)
        
        // deduct stock
        inventoryUsed.forEach { (inv, qty) ->
            dao.updateInventory(inv.copy(stockQty = inv.stockQty - qty))
        }
        
        // update receivables (only services counted towards revenue/payment)
        val currentTotal = items.filter { it.type == "SERVICE" }.sumOf { it.price * it.qty }
        if (currentTotal > 0) {
            val actualPaid = if (paidAmount > currentTotal) currentTotal else paidAmount
            dao.insertReceivable(Receivable(customerName = tx.customerName, totalAmount = currentTotal, paidAmount = actualPaid))
        }
        
        logActivity("Tambah Transaksi: ${tx.txNumber}")
    }
    
    fun getTxItems(txId: Int) = dao.getItemsForTx(txId)
    
    fun updateTxStatus(tx: Tx, newStatus: String) = viewModelScope.launch {
        dao.updateTx(tx.copy(status = newStatus))
        logActivity("Update Transaksi ${tx.txNumber} status ke $newStatus")
    }

    // --- Losses ---
    fun addLoss(loss: Loss) = viewModelScope.launch { dao.insertLoss(loss); logActivity("Tambah Kerugian: ${loss.name} (${loss.amount})") }

    // --- Reset ---
    fun resetStock() = viewModelScope.launch {
        dao.deleteAllInventory()
        logActivity("Reset Semua Stok Inventaris")
    }

    fun factoryReset() = viewModelScope.launch {
        dao.wipeCustomers()
        dao.wipeSuppliers()
        dao.wipeServices()
        dao.wipeTransactions()
        dao.wipeTxItems()
        dao.wipeReceivables()
        dao.wipePayables()
        dao.wipeLosses()
        dao.deleteAllInventory()
        // we keep logs or wipe too? wiping logs too
        dao.wipeLogs()
        logActivity("FACTORY RESET")
    }
}
