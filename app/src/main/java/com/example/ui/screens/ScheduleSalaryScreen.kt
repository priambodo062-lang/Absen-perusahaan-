package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AttendanceViewModel
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndustrialNavy
import com.example.ui.theme.LightBg
import com.example.ui.theme.SlateGray
import com.example.util.LocationUtils
import java.util.Calendar

enum class DayType {
    WORK_SHIFT_1, WORK_SHIFT_2, OFF_DAY, PAYDAY
}

@Composable
fun ScheduleSalaryScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf(13) } // default 13th
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Calculate days until payday (25th)
    val daysUntilPayday = if (currentDayOfMonth <= 25) (25 - currentDayOfMonth) else (daysInMonth - currentDayOfMonth + 25)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Calendar Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_calendar_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = IndustrialNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jadwal Shift & Kalender Kerja",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Agustus 2026",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Days Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendBadge(color = BrightGreen, text = "Shift 1 Pagi")
                        LegendBadge(color = IndustrialNavy, text = "Shift 2 Malam")
                        LegendBadge(color = Color.LightGray, text = "Libur")
                        LegendBadge(color = AmberGold, text = "Hari Gaji (25)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Calendar Grid (7 columns)
                    val daysOfWeek = listOf("Sn", "Sl", "Rb", "Km", "Jm", "Sb", "Mg")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysOfWeek.forEach { dayName ->
                            Text(
                                text = dayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Day Cells
                    val rows = (daysInMonth + 6) / 7
                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (c in 0 until 7) {
                                val dayNumber = r * 7 + c + 1
                                if (dayNumber <= daysInMonth) {
                                    val dayType = when {
                                        dayNumber == 25 -> DayType.PAYDAY
                                        dayNumber % 7 == 0 || dayNumber % 7 == 6 -> DayType.OFF_DAY
                                        dayNumber % 2 == 0 -> DayType.WORK_SHIFT_1
                                        else -> DayType.WORK_SHIFT_2
                                    }

                                    val isSelected = (dayNumber == selectedDay)

                                    CalendarDayCell(
                                        dayNumber = dayNumber,
                                        dayType = dayType,
                                        isSelected = isSelected,
                                        onClick = { selectedDay = dayNumber },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Selected Day Detail Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_detail_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val detailType = when {
                        selectedDay == 25 -> DayType.PAYDAY
                        selectedDay % 7 == 0 || selectedDay % 7 == 6 -> DayType.OFF_DAY
                        selectedDay % 2 == 0 -> DayType.WORK_SHIFT_1
                        else -> DayType.WORK_SHIFT_2
                    }

                    val (badgeText, badgeColor, description) = when (detailType) {
                        DayType.PAYDAY -> Triple("HARI PENGGAJIAN", AmberGold, "Transfer gaji bulanan & insentif otomatis dilakukan pukul 08:00 WIB.")
                        DayType.OFF_DAY -> Triple("HARI LIBUR", SlateGray, "Jadwal istirahat mingguan / Libur pabrik.")
                        DayType.WORK_SHIFT_1 -> Triple("SHIFT 1 PAGI", BrightGreen, "Jam Masuk: 07:00 • Jam Pulang: 15:00 WIB • Istirahat: 12:00 WIB")
                        DayType.WORK_SHIFT_2 -> Triple("SHIFT 2 MALAM", IndustrialNavy, "Jam Masuk: 15:00 • Jam Pulang: 23:00 WIB • Istirahat: 19:00 WIB")
                    }

                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = selectedDay.toString(),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = badgeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "$selectedDay Agustus 2026 - $badgeText",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = badgeColor
                        )
                        Text(
                            text = description,
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }
            }
        }

        // 3. Payday & Salary Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndustrialNavy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salary_summary_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Payday Banner
                    Surface(
                        color = AmberContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pengingat Penggajian (25 Agustus)",
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (daysUntilPayday == 0) "Hari ini adalah Hari Penggajian!" else "Tinggal $daysUntilPayday hari lagi menuju pencairan gaji.",
                                    color = IndustrialNavy,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimasi Gaji Bersih Bulan Ini",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = LocationUtils.formatRupiah(7100000L),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = BrightGreen
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown List
                    SalaryItemRow(label = "Gaji Pokok", amount = 4900000L, isWhite = true)
                    SalaryItemRow(label = "Tunjangan Transport & Makan", amount = 1250000L, isWhite = true)
                    SalaryItemRow(label = "Bonus Kehadiran Penuh (100%)", amount = 500000L, isWhite = true)
                    SalaryItemRow(label = "Uang Lembur (8 Jam)", amount = 450000L, isWhite = true)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.clearUserMessage() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_slip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Unduh Slip Gaji Digital (PDF)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dayNumber: Int,
    dayType: DayType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellBg = when {
        isSelected -> IndustrialNavy
        dayType == DayType.PAYDAY -> AmberContainer
        dayType == DayType.OFF_DAY -> Color.Transparent
        dayType == DayType.WORK_SHIFT_1 -> EmeraldContainer.copy(alpha = 0.5f)
        dayType == DayType.WORK_SHIFT_2 -> Color.LightGray.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        dayType == DayType.PAYDAY -> AmberGold
        dayType == DayType.WORK_SHIFT_1 -> EmeraldGreen
        dayType == DayType.WORK_SHIFT_2 -> IndustrialNavy
        else -> SlateGray
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(cellBg)
            .clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayNumber.toString(),
                fontSize = 12.sp,
                fontWeight = if (isSelected || dayType == DayType.PAYDAY) FontWeight.Black else FontWeight.Medium,
                color = textColor
            )
            if (dayType == DayType.PAYDAY && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(AmberGold)
                )
            }
        }
    }
}

@Composable
fun LegendBadge(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SalaryItemRow(label: String, amount: Long, isWhite: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isWhite) Color.White.copy(alpha = 0.8f) else SlateGray
        )
        Text(
            text = LocationUtils.formatRupiah(amount),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isWhite) Color.White else IndustrialNavy
        )
    }
}
