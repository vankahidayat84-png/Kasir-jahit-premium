package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.JahitViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanScreen(viewModel: JahitViewModel) {
    var showResetDialog by remember { mutableStateOf(false) }
    var resetText by remember { mutableStateOf("") }
    var showTentang by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            item {
                SettingItem("Backup Data") { /*TODO*/ }
                SettingItem("Restore Data") { /*TODO*/ }
                SettingItem("Ekspor PDF") { /*TODO*/ }
                SettingItem("Ekspor CSV") { /*TODO*/ }
                SettingItem("Reset Stok") { 
                    viewModel.resetStock()
                }
                SettingItem("Reset Data Aplikasi", color = DangerRed) { 
                    showResetDialog = true
                }
                SettingItem("Tentang Aplikasi") {
                    showTentang = true
                }
            }
        }
    }

    if (showTentang) {
        AlertDialog(
            onDismissRequest = { showTentang = false },
            title = { Text("Tentang Aplikasi: Jahitkaav Pro Max") },
            text = {
                Column {
                    Text("Jahitkaav Pro Max adalah sistem manajemen usaha jahit profesional yang berjalan 100% offline.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aplikasi ini membantu pengguna mencatat dan menghitung pendapatan, modal, inventaris, piutang, hutang, dan kerugian secara akurat berdasarkan data nyata yang Anda masukkan.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Versi: 1.0.0")
                    Text("Developer: Kaav Studio")
                }
            },
            confirmButton = {
                TextButton(onClick = { showTentang = false }) { Text("Tutup", color = SuccessGreen) }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Data Aplikasi") },
            text = {
                Column {
                    Text("Ketik RESET untuk melanjutkan. Semua data akan dihapus.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetText,
                        onValueChange = { resetText = it },
                        label = { Text("Ketik RESET") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetText == "RESET") {
                            viewModel.factoryReset()
                            showResetDialog = false
                        }
                    },
                    enabled = resetText == "RESET",
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Hapus Semua Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SettingItem(title: String, color: androidx.compose.ui.graphics.Color = TextWhite, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(title, color = color)
        }
    }
}
