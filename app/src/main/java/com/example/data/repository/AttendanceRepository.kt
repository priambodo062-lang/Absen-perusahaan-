package com.example.data.repository

import com.example.data.dao.AttendanceDao
import com.example.data.dao.FactoryConfigDao
import com.example.data.dao.PermitDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.FactoryConfig
import com.example.data.model.PermitRequest
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val permitDao: PermitDao,
    private val factoryConfigDao: FactoryConfigDao
) {
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendanceRecords()
    val latestAttendance: Flow<AttendanceRecord?> = attendanceDao.getLatestRecord()
    val allPermitRequests: Flow<List<PermitRequest>> = permitDao.getAllPermitRequests()
    val factoryConfig: Flow<FactoryConfig?> = factoryConfigDao.getConfig()

    suspend fun saveAttendance(record: AttendanceRecord): Long {
        return attendanceDao.insertRecord(record)
    }

    suspend fun submitPermit(request: PermitRequest): Long {
        return permitDao.insertPermit(request)
    }

    suspend fun updatePermitStatus(id: Long, status: String, notes: String) {
        permitDao.updatePermitStatus(id, status, notes)
    }

    suspend fun updateFactoryConfig(config: FactoryConfig) {
        factoryConfigDao.saveConfig(config)
    }
}
