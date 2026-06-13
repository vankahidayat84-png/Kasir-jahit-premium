package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JahitViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(viewModel: JahitViewModel) {
    val txList by viewModel.transactions.collectAsState()
    val receivables by viewModel.receivables.collectAsState()
    val payables by viewModel.payables.collectAsState()
    val losses by viewModel.losses.collectAsState()
    val modalTerpakai by viewModel.modalTerpakai.collectAsState()

    val formatMoney = { amount: Double -> 
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }.format(amount)
    }

    val pendapatan = receivables.sumOf { it.totalAmount }
    val piutang = receivables.sumOf { it.totalAmount - it.paidAmount }
    val hutang = payables.sumOf { it.totalAmount - it.paidAmount }
    val kerugian = losses.sumOf { it.amount }
    val validModal = modalTerpakai ?: 0.0
    val labaBersih = pendapatan - validModal - kerugian

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Keuangan", color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LaporanItem("Pendapatan", formatMoney(pendapatan), SuccessGreen)
            LaporanItem("Modal Terpakai", formatMoney(validModal), WarningOrange)
            LaporanItem("Piutang Aktif", formatMoney(piutang), WarningOrange)
            LaporanItem("Hutang Aktif", formatMoney(hutang), DangerRed)
            LaporanItem("Kerugian", formatMoney(kerugian), DangerRed)
            Spacer(modifier = Modifier.height(16.dp))
            LaporanItem("Estimasi Laba Bersih", formatMoney(labaBersih), if(labaBersih>=0) SuccessGreen else DangerRed, isBold = true)
        }
    }
}

@Composable
fun LaporanItem(title: String, value: String, valueColor: androidx.compose.ui.graphics.Color, isBold: Boolean = false) {
    Card(colors = CardDefaults.cardColors(containerColor = CardDark), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = TextWhite, fontWeight = if(isBold) androidx.compose.ui.text.font.FontWeight.Bold else null)
            Text(value, color = valueColor, fontWeight = if(isBold) androidx.compose.ui.text.font.FontWeight.Bold else null)
        }
    }
}
