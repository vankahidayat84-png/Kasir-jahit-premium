package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.ui.JahitViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(viewModel: JahitViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Pelanggan", "Inventaris", "Layanan Jahit", "Supplier")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Master Data", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundDark,
                contentColor = TextWhite,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AccentMaroon
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        selectedContentColor = AccentMaroon,
                        unselectedContentColor = TextGray
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTabIndex) {
                    0 -> PelangganTab(viewModel)
                    1 -> InventarisTab(viewModel)
                    2 -> LayananTab(viewModel)
                    3 -> SupplierTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun PelangganTab(viewModel: JahitViewModel) {
    val customers by viewModel.customers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(customers.size) { index ->
                val c = customers[index]
                Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(c.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(c.phone, color = TextGray)
                        Text(c.address, color = TextGray, fontSize = 12.sp)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = AccentMaroon,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add", tint = TextWhite)
        }

        if (showDialog) {
            var name by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var address by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Pelanggan", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No HP") })
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addCustomer(com.example.data.Customer(name = name, phone = phone, address = address, notes = notes))
                        showDialog = false
                    }) { Text("Simpan", color = SuccessGreen) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal", color = DangerRed) }
                }
            )
        }
    }
}

@Composable
fun InventarisTab(viewModel: JahitViewModel) {
    val inventory by viewModel.inventory.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(inventory.size) { index ->
                val inv = inventory[index]
                Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(inv.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Stok: ${inv.stockQty} ${inv.unit}", color = TextGray)
                        Text("Harga Modal: Rp${inv.costPrice}", color = TextGray)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = AccentMaroon,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add", tint = TextWhite)
        }

        if (showDialog) {
            var name by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("") }
            var stock by remember { mutableStateOf("") }
            var unit by remember { mutableStateOf("") }
            var cost by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Inventaris", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") })
                        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") })
                        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stok Awal") })
                        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Satuan (pcs, roll)") })
                        OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Harga Modal") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addInventory(com.example.data.Inventory(
                            name = name, category = category,
                            stockQty = stock.toIntOrNull() ?: 0, unit = unit,
                            costPrice = cost.toDoubleOrNull() ?: 0.0,
                            supplierName = "", notes = ""
                        ))
                        showDialog = false
                    }) { Text("Simpan", color = SuccessGreen) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal", color = DangerRed) }
                }
            )
        }
    }
}

@Composable
fun LayananTab(viewModel: JahitViewModel) {
    val services by viewModel.services.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services.size) { index ->
                val svc = services[index]
                Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(svc.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Harga Default: Rp${svc.defaultPrice}", color = TextGray)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = AccentMaroon,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add", tint = TextWhite)
        }

        if (showDialog) {
            var name by remember { mutableStateOf("") }
            var price by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Layanan", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Layanan") })
                        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga Jual Default") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addService(com.example.data.ServiceItem(
                            name = name, defaultPrice = price.toDoubleOrNull() ?: 0.0, notes = notes
                        ))
                        showDialog = false
                    }) { Text("Simpan", color = SuccessGreen) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal", color = DangerRed) }
                }
            )
        }
    }
}

@Composable
fun SupplierTab(viewModel: JahitViewModel) {
    val suppliers by viewModel.suppliers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suppliers.size) { index ->
                val s = suppliers[index]
                Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(s.name, color = TextWhite, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(s.phone, color = TextGray)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = AccentMaroon,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add", tint = TextWhite)
        }

        if (showDialog) {
            var name by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var address by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Supplier", color = TextWhite) },
                containerColor = BackgroundDark,
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Supplier") })
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No HP") })
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addSupplier(com.example.data.Supplier(
                            name = name, phone = phone, address = address, notes = notes
                        ))
                        showDialog = false
                    }) { Text("Simpan", color = SuccessGreen) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal", color = DangerRed) }
                }
            )
        }
    }
}
