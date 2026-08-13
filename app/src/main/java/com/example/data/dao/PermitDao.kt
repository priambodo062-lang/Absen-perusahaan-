package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PermitRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PermitDao {
    @Query("SELECT * FROM permit_requests ORDER BY submissionTimestamp DESC")
    fun getAllPermitRequests(): Flow<List<PermitRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermit(request: PermitRequest): Long

    @Update
    suspend fun updatePermit(request: PermitRequest)

    @Query("UPDATE permit_requests SET status = :status, hrdNotes = :notes WHERE id = :id")
    suspend fun updatePermitStatus(id: Long, status: String, notes: String)
}
