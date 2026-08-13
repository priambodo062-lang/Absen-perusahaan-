package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "factory_config")
data class FactoryConfig(
    @PrimaryKey val id: Int = 1,
    val factoryName: String = "PT Pabrik Maju Bersama",
    val factoryAddress: String = "Kawasan Industri Delta, Blok B5 No. 12",
    val latitude: Double = -6.200000,
    val longitude: Double = 106.816666,
    val maxRadiusMeters: Float = 100f,
    val enforceAntiMock: Boolean = true
)
