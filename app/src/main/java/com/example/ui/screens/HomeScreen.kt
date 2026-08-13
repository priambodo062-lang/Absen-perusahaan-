package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AttendanceViewModel
import com.example.ui.LocationMode
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndustrialNavy
import com.example.ui.theme.RedContainer
import com.example.ui.theme.SlateGray
import com.example.util.LocationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AttendanceViewModel,
    currentTimeMillis: Long,
    attendanceHistory: List<AttendanceRecord>,
    modifier: Modifier = Modifier
) {
    val locationState by viewModel.userLocationState.collectAsStateWithLifecycle()
    val locationMode by viewModel.locationMode.collectAsStateWithLifecycle()
    val factoryConfig by viewModel.factoryConfig.collectAsStateWithLifecycle()
    val profile = viewModel.workerProfile

    val todayRecords = attendanceHistory.filter { it.dateString == LocationUtils.formatDateShort() }
    val hasClockedInToday = todayRecords.any { it.type == "MASUK" }
    val hasClockedOutToday = todayRecords.any { it.type == "PULANG" }

    val nextActionType = if (!hasClockedInToday) "MASUK" else "PULANG"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Digital Clock & Profile Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndustrialNavy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .testTag("home_header_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${profile.id} • ${profile.department}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            color = EmeraldGreen,
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Aktif",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Digital Clock Display
                    Text(
                        text = LocationUtils.formatTime(currentTimeMillis),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = BrightGreen
                    )
                    Text(
                        text = LocationUtils.formatDateIndonesian(currentTimeMillis),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. Geofence Status Card & Location Mode Switcher
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("geofence_status_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Factory,
                                contentDescription = null,
                                tint = IndustrialNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = factoryConfig.factoryName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = "Max Radius: ${factoryConfig.maxRadiusMeters.toInt()}m",
                            fontSize = 12.sp,
                            color = SlateGray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Geofence Banner
                    val (bgColor, textColor, icon, statusText) = when {
                        locationState.isMockLocation -> Quadruple(
                            RedContainer,
                            AlertRed,
                            Icons.Default.Warning,
                            "TERDETEKSI FAKE GPS / MOCK LOCATION!"
                        )
                        locationState.isWithinRadius -> Quadruple(
                            EmeraldContainer,
                            EmeraldGreen,
                            Icons.Default.CheckCircle,
                            "Di Dalam Area Pabrik (${locationState.distanceMeters.toInt()}m dari titik pusat)"
                        )
                        else -> Quadruple(
                            RedContainer,
                            AlertRed,
                            Icons.Default.GpsOff,
                            "Di Luar Area Pabrik (${locationState.distanceMeters.toInt()}m dari titik pusat)"
                        )
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = statusText,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (locationState.isWithinRadius && !locationState.isMockLocation)
                                        "Akses Absensi Terbuka"
                                    else
                                        "Dekati area pabrik untuk membuka tombol absen",
                                    color = textColor.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Geofence Location Simulator Controls
                    Text(
                        text = "Simulasi Pengujian Lokasi Pekerja:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = locationMode == LocationMode.SIMULATION_INSIDE,
                            onClick = { viewModel.setLocationMode(LocationMode.SIMULATION_INSIDE) },
                            label = { Text("Dalam Pabrik (35m)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldContainer,
                                selectedLabelColor = EmeraldGreen
                            ),
                            modifier = Modifier.testTag("sim_inside_chip")
                        )
                        FilterChip(
                            selected = locationMode == LocationMode.SIMULATION_OUTSIDE,
                            onClick = { viewModel.setLocationMode(LocationMode.SIMULATION_OUTSIDE) },
                            label = { Text("Luar Area (250m)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedContainer,
                                selectedLabelColor = AlertRed
                            ),
                            modifier = Modifier.testTag("sim_outside_chip")
                        )
                        FilterChip(
                            selected = locationMode == LocationMode.SIMULATION_MOCK_GPS,
                            onClick = { viewModel.setLocationMode(LocationMode.SIMULATION_MOCK_GPS) },
                            label = { Text("Fake GPS", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberContainer,
                                selectedLabelColor = AmberGold
                            ),
                            modifier = Modifier.testTag("sim_mock_chip")
                        )
                    }
                }
            }
        }

        // 3. Main Attendance Action Button
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_action_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isActionAllowed = locationState.isWithinRadius && !locationState.isMockLocation && (!hasClockedInToday || !hasClockedOutToday)

                    val buttonColor = when {
                        !hasClockedInToday -> BrightGreen
                        !hasClockedOutToday -> IndustrialNavy
                        else -> SlateGray
                    }

                    val buttonLabel = when {
                        !hasClockedInToday -> "ABSEN MASUK"
                        !hasClockedOutToday -> "ABSEN PULANG"
                        else -> "SUDAH ABSEN HARI INI"
                    }

                    // Pulsing Ring Button Frame
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActionAllowed) buttonColor.copy(alpha = 0.15f)
                                else Color.LightGray.copy(alpha = 0.3f)
                            )
                            .border(
                                width = 4.dp,
                                color = if (isActionAllowed) buttonColor else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable(enabled = isActionAllowed) {
                                if (isActionAllowed) {
                                    viewModel.performAttendance(nextActionType)
                                }
                            }
                            .testTag("absen_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isActionAllowed) Icons.Default.GpsFixed else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isActionAllowed) buttonColor else Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = buttonLabel,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = if (isActionAllowed) buttonColor else Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isActionAllowed)
                            "Tekan tombol di atas untuk mencatat absensi $nextActionType"
                        else if (hasClockedInToday && hasClockedOutToday)
                            "Anda telah menyelesaikan absensi Masuk & Pulang untuk hari ini."
                        else
                            "Peringatan: Berada di luar radius pabrik. Tombol terkunci.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 4. Today's Shift Details Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_shift_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Jadwal Shift Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = profile.shiftName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = "Jam Kerja: 07:00 - 15:00 WIB", fontSize = 12.sp, color = SlateGray)
                        }
                        Surface(
                            color = AmberContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Shift 1 Pagi",
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Clock In Status
                        val clockInRecord = todayRecords.find { it.type == "MASUK" }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Jam Masuk", fontSize = 12.sp, color = SlateGray)
                            Text(
                                text = clockInRecord?.timeString ?: "-- : --",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (clockInRecord != null) EmeraldGreen else Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color.LightGray)
                        )

                        // Clock Out Status
                        val clockOutRecord = todayRecords.find { it.type == "PULANG" }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Jam Pulang", fontSize = 12.sp, color = SlateGray)
                            Text(
                                text = clockOutRecord?.timeString ?: "-- : --",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (clockOutRecord != null) IndustrialNavy else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // 5. Recent Attendance Logs
        item {
            Text(
                text = "Riwayat Absensi Terakhir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (attendanceHistory.isEmpty()) {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada riwayat absensi tercatat.",
                            color = SlateGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(attendanceHistory.take(5)) { record ->
                AttendanceLogItem(record = record)
            }
        }
    }
}

@Composable
fun AttendanceLogItem(record: AttendanceRecord) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_log_item")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (record.type == "MASUK") EmeraldContainer else AmberContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (record.type == "MASUK") EmeraldGreen else AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Absen ${record.type}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${record.dateString} • ${record.timeString} WIB",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.status,
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${record.distanceMeters.toInt()}m dari pabrik",
                    fontSize = 11.sp,
                    color = SlateGray
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
