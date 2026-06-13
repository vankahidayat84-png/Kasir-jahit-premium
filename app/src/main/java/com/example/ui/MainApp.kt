package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ui.JahitViewModel
import com.example.ui.theme.*
import com.example.ui.screens.*

@Composable
fun MainApp(navController: NavHostController, viewModel: JahitViewModel) {
    val items = listOf(
        Pair("dashboard", Icons.Default.Dashboard),
        Pair("data", Icons.Default.Storage),
        Pair("transaksi", Icons.Default.Receipt),
        Pair("laporan", Icons.Default.BarChart),
        Pair("pengaturan", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF141414), // Navigation background
                contentColor = TextWhite,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { (route, icon) ->
                    val isSelected = currentRoute == route
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = route, modifier = Modifier.size(24.dp)) },
                        label = { Text(route.capitalize(), fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentMaroon,
                            unselectedIconColor = TextWhite.copy(alpha = 0.4f),
                            selectedTextColor = AccentMaroon,
                            unselectedTextColor = TextWhite.copy(alpha = 0.4f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("data") { DataScreen(viewModel) }
            composable("transaksi") { TransaksiScreen(viewModel) }
            composable("laporan") { LaporanScreen(viewModel) }
            composable("pengaturan") { PengaturanScreen(viewModel) }
        }
    }
}
