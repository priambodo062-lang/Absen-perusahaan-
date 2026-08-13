package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FactoryConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface FactoryConfigDao {
    @Query("SELECT * FROM factory_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<FactoryConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: FactoryConfig)
}
