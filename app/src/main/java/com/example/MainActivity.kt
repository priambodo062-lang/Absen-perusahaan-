package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FactoryConfig
import com.example.ui.AttendanceViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HrdDashboardScreen
import com.example.ui.screens.PermitScreen
import com.example.ui.screens.ScheduleSalaryScreen
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.IndustrialNavy
import com.example.ui.theme.MyApplicationTheme

enum class NavTab(val title: String, val icon: ImageVector) {
    HOME("Beranda", Icons.Default.Home),
    SCHEDULE("Jadwal & Gaji", Icons.Default.CalendarMonth),
    PERMIT("Izin / Cuti", Icons.Default.Assignment),
    HRD("Admin HRD", Icons.Default.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AttendanceViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
    val attendanceHistory by viewModel.attendanceHistory.collectAsStateWithLifecycle()
    val permitList by viewModel.permitRequests.collectAsStateWithLifecycle()
    val factoryConfig by viewModel.factoryConfig.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = IndustrialNavy,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndustrialNavy,
                            selectedTextColor = BrightGreen,
                            indicatorColor = BrightGreen,
                            unselectedIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                            unselectedTextColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(
                viewModel = viewModel,
                currentTimeMillis = currentTimeMillis,
                attendanceHistory = attendanceHistory,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> ScheduleSalaryScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> PermitScreen(
                viewModel = viewModel,
                permitList = permitList,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> HrdDashboardScreen(
                viewModel = viewModel,
                factoryConfig = factoryConfig,
                permitList = permitList,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
