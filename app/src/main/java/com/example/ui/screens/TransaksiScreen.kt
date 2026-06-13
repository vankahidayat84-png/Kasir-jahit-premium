package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import com.example.ui.JahitViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiScreen(viewModel: JahitViewModel) {
    val txList by viewModel.transactions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = AccentMaroon
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add", tint = TextWhite)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(txList.size) { index ->
                    val tx = txList[index]
                    Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("TRX: ${tx.txNumber}", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Pelanggan: ${tx.customerName}", color = TextGray)
                            val tgl = SimpleDateFormat("dd MMM", Locale("id", "ID")).format(Date(tx.dateIn))
                            Text("Tgl: $tgl | Status: ${tx.status}", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddTransactionScreen(viewModel) { showDialog = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: JahitViewModel, onDismiss: () -> Unit) {
    val services by viewModel.services.collectAsState()
    val inventory by viewModel.inventory.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // Cart items
    val cartServices = remember { mutableStateListOf<com.example.data.ServiceItem>() }
    val cartMaterials = remember { mutableStateListOf<Pair<com.example.data.Inventory, Int>>() }

    var showAddJob by remember { mutableStateOf(false) }
    var showAddMaterial by remember { mutableStateOf(false) }

    var uangPelangganStr by remember { mutableStateOf("") }
    val uangPelanggan = uangPelangganStr.toDoubleOrNull() ?: 0.0

    val totalJasa = cartServices.sumOf { it.defaultPrice }
    val modalBahan = cartMaterials.sumOf { it.first.costPrice * it.second }
    val estimasiLaba = totalJasa - modalBahan
    val total = totalJasa // the total customer pays is just service cost? Or usually service + materials? Wait, "Total Jasa: 35.000, Modal Bahan: 21.000, Estimasi Laba: 14.000. Total: 35.000". So they only pay for Jasa. 
    val kembalian = if (uangPelanggan > total) uangPelanggan - total else 0.0
    val isLunas = uangPelanggan >= total

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Transaksi Baru", color = TextWhite) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(androidx.compose.material.icons.Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(color = CardDark, shadowElevation = 16.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Button(
                            onClick = {
                                val tx = com.example.data.Tx(
                                    txNumber = "TRX-${System.currentTimeMillis() % 10000}",
                                    customerName = customerName.ifBlank { "Umum" },
                                    dateIn = System.currentTimeMillis(),
                                    dateOut = System.currentTimeMillis() + 86400000L * 3,
                                    status = if (isLunas) "Lunas" else "Belum Lunas",
                                    notes = if (customerPhone.isNotBlank()) "HP: $customerPhone\n$notes" else notes
                                )
                                val items = mutableListOf<com.example.data.TxItem>()
                                cartServices.forEach { svc ->
                                    items.add(com.example.data.TxItem(transactionId = 0, type = "SERVICE", name = svc.name, qty = 1, price = svc.defaultPrice))
                                }
                                cartMaterials.forEach { (inv, qty) ->
                                    items.add(com.example.data.TxItem(transactionId = 0, type = "INVEN", name = inv.name, qty = qty, price = inv.costPrice))
                                }
                                viewModel.addTransaction(tx, items.toList(), cartMaterials.toList(), uangPelanggan)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("SIMPAN TRANSAKSI", color = BackgroundDark, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
            },
            containerColor = BackgroundDark
        ) { padding ->
            androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerName, onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customerPhone, onValueChange = { customerPhone = it },
                        label = { Text("Nomor HP") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TextWhite.copy(alpha = 0.1f))
                }

                item {
                    Text("Layanan Jahit", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    cartServices.forEachIndexed { index, svc ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Column {
                                Text(svc.name, color = TextWhite)
                                Text("Rp${svc.defaultPrice.toInt()}", color = TextGray, fontSize = 12.sp)
                            }
                            IconButton(onClick = { cartServices.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed)
                            }
                        }
                    }
                    Button(onClick = { showAddJob = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CardDark)) {
                        Text("+ Tambah Layanan", color = AccentMaroon)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TextWhite.copy(alpha = 0.1f))
                }

                item {
                    Text("Bahan Digunakan", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    cartMaterials.forEachIndexed { index, (inv, qty) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Column {
                                Text("${inv.name} (x$qty)", color = TextWhite)
                                Text("Rp${inv.costPrice.toInt() * qty}", color = TextGray, fontSize = 12.sp)
                            }
                            IconButton(onClick = { cartMaterials.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed)
                            }
                        }
                    }
                    Button(onClick = { showAddMaterial = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CardDark)) {
                        Text("+ Tambah Bahan", color = InfoBlue)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TextWhite.copy(alpha = 0.1f))
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Jasa", color = TextGray)
                        Text("Rp${totalJasa.toInt()}", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Modal Bahan", color = TextGray)
                        Text("Rp${modalBahan.toInt()}", color = WarningOrange, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimasi Laba", color = TextGray)
                        Text("Rp${estimasiLaba.toInt()}", color = SuccessGreen, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = TextWhite.copy(alpha = 0.1f))
                }

                item {
                    OutlinedTextField(
                        value = uangPelangganStr, onValueChange = { uangPelangganStr = it },
                        label = { Text("Uang Pelanggan") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 18.sp)
                        Text("Rp${total.toInt()}", color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 18.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kembalian", color = TextGray)
                        Text("Rp${kembalian.toInt()}", color = if (kembalian > 0) SuccessGreen else TextGray)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showAddJob) {
            AlertDialog(
                onDismissRequest = { showAddJob = false },
                title = { Text("Pilih Layanan", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    if (services.isEmpty()) {
                        Text("Layanan kosong. Tambahkan di menu Data terlebih dahulu.", color = TextGray)
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn {
                            items(services.size) { i ->
                                val svc = services[i]
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardDark),
                                    onClick = {
                                        cartServices.add(svc)
                                        showAddJob = false
                                    }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(svc.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                        Text("Rp${svc.defaultPrice.toInt()}", color = TextGray)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showAddJob = false }) { Text("Tutup", color = DangerRed) } }
            )
        }

        if (showAddMaterial) {
            var selectedInv by remember { mutableStateOf<com.example.data.Inventory?>(null) }
            var qtyStr by remember { mutableStateOf("1") }

            AlertDialog(
                onDismissRequest = { showAddMaterial = false },
                title = { Text(if (selectedInv == null) "Pilih Bahan" else "Masukkan Jumlah", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    if (selectedInv == null) {
                        if (inventory.isEmpty()) {
                            Text("Inventaris kosong. Tambahkan di menu Data terlebih dahulu.", color = TextGray)
                        } else {
                            androidx.compose.foundation.lazy.LazyColumn {
                                items(inventory.size) { i ->
                                    val inv = inventory[i]
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = CardDark),
                                        onClick = { selectedInv = inv }
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(inv.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                            Text("Sisa Stok: ${inv.stockQty}", color = TextGray, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column {
                            Text(selectedInv!!.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Harga Modal: Rp${selectedInv!!.costPrice.toInt()} | Stok: ${selectedInv!!.stockQty}", color = TextGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = qtyStr,
                                onValueChange = { qtyStr = it },
                                label = { Text("Jumlah Dipakai") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            )
                        }
                    }
                },
                confirmButton = {
                    if (selectedInv != null) {
                        TextButton(onClick = {
                            val qty = qtyStr.toIntOrNull() ?: 1
                            cartMaterials.add(selectedInv!! to qty)
                            showAddMaterial = false
                        }) { Text("Tambahkan", color = SuccessGreen) }
                    } else {
                        TextButton(onClick = { showAddMaterial = false }) { Text("Tutup", color = DangerRed) }
                    }
                },
                dismissButton = {
                    if (selectedInv != null) {
                        TextButton(onClick = { selectedInv = null }) { Text("Kembali", color = TextGray) }
                    }
                }
            )
        }
    }
}
