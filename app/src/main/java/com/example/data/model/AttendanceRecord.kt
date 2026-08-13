package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // e.g. "2026-08-13"
    val timeString: String, // e.g. "07:15:20"
    val type: String, // "MASUK" or "PULANG"
    val workerId: String = "BRH-8821",
    val workerName: String = "Budi Santoso",
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Float,
    val status: String, // "TEPAT_WAKTU", "TERLAMBAT", "PULANG_CEPAT"
    val shiftName: String = "Shift 1 (07:00 - 15:00)",
    val isMockLocation: Boolean = false,
    val notes: String = ""
)
