package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JahitViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: JahitViewModel) {
    val txList by viewModel.transactions.collectAsState()
    val invList by viewModel.inventory.collectAsState()
    val losses by viewModel.losses.collectAsState()
    val receivables by viewModel.receivables.collectAsState()
    val payables by viewModel.payables.collectAsState()
    val modalTerpakai by viewModel.modalTerpakai.collectAsState()
    
    val formatMoney = { amount: Double -> 
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }.format(amount)
    }

    val todayStr = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(Date())

    // Calculations
    val pendapatanHariIni = receivables.sumOf { it.totalAmount }
    val validModal = modalTerpakai ?: 0.0
    val labaBersihHariIni = pendapatanHariIni - validModal - losses.sumOf { it.amount }
    val piutangAktif = receivables.sumOf { it.totalAmount - it.paidAmount }
    val hutangAktif = payables.sumOf { it.totalAmount - it.paidAmount }
    val nilaiInventaris = invList.sumOf { it.stockQty * it.costPrice }
    val stokMenipis = invList.count { it.stockQty < 5 }
    val pesananAktif = txList.count { it.status == "Menunggu" || it.status == "Diproses" }
    
    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(BackgroundDark).padding(horizontal = 16.dp, vertical = 24.dp).padding(bottom = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                    Text("JAHITKAAV PRO MAX", fontWeight = FontWeight.Bold, color = AccentMaroon, fontSize = 20.sp, letterSpacing = (-0.5).sp)
                    Text("PROFESSIONAL TOOL", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextWhite.copy(alpha = 0.4f), letterSpacing = 2.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(todayStr, fontSize = 12.sp, color = TextWhite.copy(alpha = 0.6f))
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 100.dp),
            modifier = Modifier.padding(padding).fillMaxSize().navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Ringkasan Utama")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DashboardCard(Modifier.weight(1f), "Pendapatan Hari Ini", formatMoney(pendapatanHariIni), SuccessGreen) // emerald-400
                    DashboardCard(Modifier.weight(1f), "Laba Bersih Hari Ini", formatMoney(labaBersihHariIni), Color(0xFF10B981)) // emerald-500
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DashboardCard(Modifier.weight(1f), "Piutang Aktif", formatMoney(piutangAktif), WarningOrange)
                    DashboardCard(Modifier.weight(1f), "Hutang Aktif", formatMoney(hutangAktif), DangerRed)
                }
            }
            
            item {
                SectionHeader("Ringkasan Operasional")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DashboardCard(Modifier.weight(1f), "Nilai Inventaris", formatMoney(nilaiInventaris))
                    DashboardCard(Modifier.weight(1f), "Stok Menipis", "$stokMenipis Barang", Color(0xFFF97316)) // orange-500
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DashboardCard(Modifier.weight(1f), "Pesanan Aktif", "$pesananAktif Pesanan", InfoBlue)
                    DashboardCard(Modifier.weight(1f), "Kerugian Bulan Ini", formatMoney(losses.sumOf{it.amount}), Color(0xFFEF4444)) // red-500
                }
            }

            item {
                SectionHeader("Statistik Cepat")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DashboardCard(Modifier.weight(1f), "Transaksi Bulan Ini", "${txList.size} Kali")
                    DashboardCard(Modifier.weight(1f), "Total Pelanggan", "${viewModel.customers.collectAsState().value.size} Jiwa")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = TextWhite.copy(alpha = 0.3f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    )
}

@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, value: String, valueColor: Color = TextWhite) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp), // 2xl
        border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.05f)),
        modifier = modifier.height(68.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, color = TextWhite.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
