package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FactoryConfig
import com.example.data.model.PermitRequest
import com.example.ui.AttendanceViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.IndustrialNavy
import com.example.ui.theme.RedContainer
import com.example.ui.theme.SlateGray

@Composable
fun HrdDashboardScreen(
    viewModel: AttendanceViewModel,
    factoryConfig: FactoryConfig,
    permitList: List<PermitRequest>,
    modifier: Modifier = Modifier
) {
    var factoryName by remember(factoryConfig) { mutableStateOf(factoryConfig.factoryName) }
    var latText by remember(factoryConfig) { mutableStateOf(factoryConfig.latitude.toString()) }
    var lngText by remember(factoryConfig) { mutableStateOf(factoryConfig.longitude.toString()) }
    var radiusText by remember(factoryConfig) { mutableStateOf(factoryConfig.maxRadiusMeters.toInt().toString()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HRD Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndustrialNavy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hrd_header_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dashboard Admin HRD",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pengaturan Geofencing & Persetujuan Izin",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // HRD Analytics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnalyticsMetric(number = "142", label = "Hadir", color = EmeraldGreen)
                        AnalyticsMetric(number = "3", label = "Terlambat", color = AmberGold)
                        AnalyticsMetric(number = "5", label = "Izin/Sakit", color = AlertRed)
                    }
                }
            }
        }

        // 2. Geofence Configuration Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hrd_geofence_config_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = IndustrialNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Konfigurasi Koordinat Pabrik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = factoryName,
                        onValueChange = { factoryName = it },
                        label = { Text("Nama Lokasi Pabrik", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("factory_name_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = latText,
                            onValueChange = { latText = it },
                            label = { Text("Latitude", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("factory_lat_field")
                        )
                        OutlinedTextField(
                            value = lngText,
                            onValueChange = { lngText = it },
                            label = { Text("Longitude", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("factory_lng_field")
                        )
                        OutlinedTextField(
                            value = radiusText,
                            onValueChange = { radiusText = it },
                            label = { Text("Radius (m)", fontSize = 11.sp) },
                            modifier = Modifier.weight(0.8f).testTag("factory_radius_field")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val lat = latText.toDoubleOrNull() ?: -6.200000
                            val lng = lngText.toDoubleOrNull() ?: 106.816666
                            val rad = radiusText.toFloatOrNull() ?: 100f
                            viewModel.updateFactoryConfig(factoryName, lat, lng, rad)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndustrialNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_geofence_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SIMPAN KOORDINAT GEOFENCE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 3. Pending Permits Queue for HRD Approval
        item {
            Text(
                text = "Pengajuan Izin Masuk (Verifikasi HRD)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val pendingPermits = permitList.filter { it.status == "MENUNGGU" }

        if (pendingPermits.isEmpty()) {
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
                            text = "Tidak ada pengajuan izin baru yang perlu diverifikasi.",
                            color = SlateGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(pendingPermits) { permit ->
                HrdPermitApprovalCard(
                    permit = permit,
                    onApprove = { notes -> viewModel.updatePermitStatus(permit.id, "DISETUJUI", notes) },
                    onReject = { notes -> viewModel.updatePermitStatus(permit.id, "DITOLAK", notes) }
                )
            }
        }
    }
}

@Composable
fun HrdPermitApprovalCard(
    permit: PermitRequest,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    var hrdNoteInput by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hrd_permit_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${permit.workerName} (${permit.workerId})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Jenis: ${permit.type} • Tgl: ${permit.startDate} - ${permit.endDate}",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                }
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Lampiran ✓",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Alasan Pekerja: ${permit.reason}",
                fontSize = 12.sp,
                color = IndustrialNavy
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = hrdNoteInput,
                onValueChange = { hrdNoteInput = it },
                placeholder = { Text("Catatan HRD (Opsional)", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth().testTag("hrd_note_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onReject(if (hrdNoteInput.isBlank()) "Sesuai ketentuan HRD" else hrdNoteInput) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("reject_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TOLAK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onApprove(if (hrdNoteInput.isBlank()) "Disetujui Tim HRD" else hrdNoteInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("approve_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SETUJU", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnalyticsMetric(number: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
