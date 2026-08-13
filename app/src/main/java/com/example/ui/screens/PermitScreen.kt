package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PermitRequest
import com.example.ui.AttendanceViewModel
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
fun PermitScreen(
    viewModel: AttendanceViewModel,
    permitList: List<PermitRequest>,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf("Sakit (Dokter)") }
    var startDate by remember { mutableStateOf(LocationUtils.formatDateShort()) }
    var endDate by remember { mutableStateOf(LocationUtils.formatDateShort()) }
    var reasonText by remember { mutableStateOf("") }
    var hasSimulatedPhoto by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Form Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("permit_form_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Form Pengajuan Izin / Cuti",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Type Selection Chips
                    Text(
                        text = "Pilih Kategori Alasan:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val permitTypes = listOf("Sakit (Dokter)", "Izin Khusus", "Cuti Tahunan")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        permitTypes.forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = {
                                    selectedType = type
                                    if (type.contains("Sakit")) hasSimulatedPhoto = true
                                },
                                label = { Text(type, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldContainer,
                                    selectedLabelColor = EmeraldGreen
                                ),
                                modifier = Modifier.testTag("type_chip_$type")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dates Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Tanggal Mulai", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("start_date_field")
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("Tanggal Selesai", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("end_date_field")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reason Field
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Keterangan Alasan", fontSize = 12.sp) },
                        placeholder = { Text("misal: Demam tinggi, perlu istirahat 2 hari", fontSize = 12.sp) },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reason_text_field")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Attachment Simulator Box (Surat Dokter / Dokumen Pendukung)
                    Surface(
                        color = if (hasSimulatedPhoto) EmeraldContainer else Color.LightGray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasSimulatedPhoto = !hasSimulatedPhoto }
                            .testTag("attachment_box")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasSimulatedPhoto) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = if (hasSimulatedPhoto) EmeraldGreen else SlateGray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (hasSimulatedPhoto) "✓ Foto Surat Dokter Terlampir" else "+ Ambil Foto Surat Dokter / Dokumen Pendukung",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = if (hasSimulatedPhoto) EmeraldGreen else IndustrialNavy
                                )
                                Text(
                                    text = "Format: JPG, PNG (Wajib untuk pengajuan Sakit)",
                                    fontSize = 10.sp,
                                    color = SlateGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (reasonText.isNotBlank()) {
                                viewModel.submitPermit(selectedType, startDate, endDate, reasonText)
                                reasonText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_permit_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "KIRIM PENGAJUAN KE HRD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // 2. Status List Header
        item {
            Text(
                text = "Status Pengajuan Izin Anda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (permitList.isEmpty()) {
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
                            text = "Belum ada riwayat pengajuan izin.",
                            color = SlateGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(permitList) { permit ->
                PermitItemCard(permit = permit)
            }
        }
    }
}

@Composable
fun PermitItemCard(permit: PermitRequest) {
    val (statusBg, statusColor, statusIcon, statusText) = when (permit.status) {
        "DISETUJUI" -> Quadruple(EmeraldContainer, EmeraldGreen, Icons.Default.Check, "DISETUJUI HRD")
        "DITOLAK" -> Quadruple(RedContainer, AlertRed, Icons.Default.Close, "DITOLAK HRD")
        else -> Quadruple(AmberContainer, AmberGold, Icons.Default.HourglassTop, "MENUNGGU VERIFIKASI")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("permit_item_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = IndustrialNavy.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = IndustrialNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = permit.type,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tanggal: ${permit.startDate} s/d ${permit.endDate}",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Alasan: ${permit.reason}",
                fontSize = 12.sp,
                color = IndustrialNavy
            )

            if (permit.hrdNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan HRD: ${permit.hrdNotes}",
                    fontSize = 11.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
