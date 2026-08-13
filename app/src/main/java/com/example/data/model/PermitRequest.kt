package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permit_requests")
data class PermitRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: String = "BRH-8821",
    val workerName: String = "Budi Santoso",
    val type: String, // "Sakit", "Izin Khusus", "Cuti Tahunan"
    val startDate: String,
    val endDate: String,
    val reason: String,
    val attachmentUri: String? = null, // Photo attachment for doctor's note
    val status: String = "MENUNGGU", // "MENUNGGU", "DISETUJUI", "DITOLAK"
    val submissionTimestamp: Long = System.currentTimeMillis(),
    val hrdNotes: String = ""
)
