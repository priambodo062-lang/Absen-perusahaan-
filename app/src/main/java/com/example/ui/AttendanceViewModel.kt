package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.FactoryConfig
import com.example.data.model.PermitRequest
import com.example.data.repository.AttendanceRepository
import com.example.util.LocationUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class LocationMode {
    SIMULATION_INSIDE,   // e.g. 35m from factory
    SIMULATION_OUTSIDE,  // e.g. 250m from factory
    SIMULATION_MOCK_GPS, // Fake GPS detected
    REAL_GPS             // Real device GPS
}

data class CurrentUserLocation(
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Float,
    val isWithinRadius: Boolean,
    val isMockLocation: Boolean
)

data class WorkerProfile(
    val id: String = "BRH-8821",
    val name: String = "Budi Santoso",
    val role: String = "Operator Machine CNC",
    val department: String = "Divisi Produksi Line A",
    val shiftName: String = "Shift Pagi (07:00 - 15:00)"
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = AttendanceRepository(db.attendanceDao(), db.permitDao(), db.factoryConfigDao())

        // Seed initial factory config if absent
        viewModelScope.launch {
            repository.updateFactoryConfig(FactoryConfig())
        }
    }

    val workerProfile = WorkerProfile()

    val factoryConfig: StateFlow<FactoryConfig> = repository.factoryConfig
        .combine(MutableStateFlow(FactoryConfig())) { config, defaultConfig ->
            config ?: defaultConfig
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FactoryConfig()
        )

    val attendanceHistory: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val permitRequests: StateFlow<List<PermitRequest>> = repository.allPermitRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Location Simulation & GPS States
    private val _locationMode = MutableStateFlow(LocationMode.SIMULATION_INSIDE)
    val locationMode: StateFlow<LocationMode> = _locationMode.asStateFlow()

    private val _realGpsLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    // Message alert snackbar state
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // Dynamic Live Clock
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    init {
        // Ticker for digital clock
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    val userLocationState: StateFlow<CurrentUserLocation> = combine(
        factoryConfig,
        _locationMode,
        _realGpsLocation
    ) { config, mode, realGps ->
        val factoryLat = config.latitude
        val factoryLng = config.longitude
        val maxRadius = config.maxRadiusMeters

        when (mode) {
            LocationMode.SIMULATION_INSIDE -> {
                // Offset by ~30 meters
                val lat = factoryLat + 0.00025
                val lng = factoryLng + 0.00015
                val dist = LocationUtils.calculateDistanceMeters(lat, lng, factoryLat, factoryLng)
                CurrentUserLocation(
                    latitude = lat,
                    longitude = lng,
                    distanceMeters = dist,
                    isWithinRadius = dist <= maxRadius,
                    isMockLocation = false
                )
            }
            LocationMode.SIMULATION_OUTSIDE -> {
                // Offset by ~350 meters
                val lat = factoryLat + 0.0028
                val lng = factoryLng + 0.0021
                val dist = LocationUtils.calculateDistanceMeters(lat, lng, factoryLat, factoryLng)
                CurrentUserLocation(
                    latitude = lat,
                    longitude = lng,
                    distanceMeters = dist,
                    isWithinRadius = dist <= maxRadius,
                    isMockLocation = false
                )
            }
            LocationMode.SIMULATION_MOCK_GPS -> {
                // Mock GPS detected flag
                val lat = factoryLat + 0.0001
                val lng = factoryLng + 0.0001
                val dist = LocationUtils.calculateDistanceMeters(lat, lng, factoryLat, factoryLng)
                CurrentUserLocation(
                    latitude = lat,
                    longitude = lng,
                    distanceMeters = dist,
                    isWithinRadius = false, // Disallowed due to mock location
                    isMockLocation = true
                )
            }
            LocationMode.REAL_GPS -> {
                val (lat, lng) = realGps ?: Pair(factoryLat + 0.0002, factoryLng + 0.0002)
                val dist = LocationUtils.calculateDistanceMeters(lat, lng, factoryLat, factoryLng)
                CurrentUserLocation(
                    latitude = lat,
                    longitude = lng,
                    distanceMeters = dist,
                    isWithinRadius = dist <= maxRadius,
                    isMockLocation = false
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CurrentUserLocation(
            latitude = -6.200000,
            longitude = 106.816666,
            distanceMeters = 35f,
            isWithinRadius = true,
            isMockLocation = false
        )
    )

    fun setLocationMode(mode: LocationMode) {
        _locationMode.value = mode
    }

    fun updateRealGps(lat: Double, lng: Double) {
        _realGpsLocation.value = Pair(lat, lng)
    }

    // Determine current attendance action (MASUK vs PULANG) based on today's latest record
    fun performAttendance(type: String) {
        val loc = userLocationState.value
        if (loc.isMockLocation) {
            _userMessage.value = "❌ GAGAL: Terdeteksi Fake GPS/Mock Location. Harap matikan aplikasi lokasi palsu!"
            return
        }
        if (!loc.isWithinRadius) {
            _userMessage.value = "❌ GAGAL: Anda berada di luar radius pabrik (${loc.distanceMeters.toInt()}m dari lokasi pabrik)."
            return
        }

        viewModelScope.launch {
            val record = AttendanceRecord(
                dateString = LocationUtils.formatDateShort(),
                timeString = LocationUtils.formatTime(),
                type = type, // "MASUK" or "PULANG"
                workerId = workerProfile.id,
                workerName = workerProfile.name,
                latitude = loc.latitude,
                longitude = loc.longitude,
                distanceMeters = loc.distanceMeters,
                status = if (type == "MASUK") "TEPAT_WAKTU" else "SELESAI_SHIFT",
                shiftName = workerProfile.shiftName,
                isMockLocation = loc.isMockLocation
            )
            repository.saveAttendance(record)
            _userMessage.value = "✅ ABSEN $type BERHASIL! Diterima pada ${LocationUtils.formatTimeShort()} (${loc.distanceMeters.toInt()}m dari area pabrik)."
        }
    }

    fun submitPermit(type: String, startDate: String, endDate: String, reason: String) {
        viewModelScope.launch {
            val request = PermitRequest(
                workerId = workerProfile.id,
                workerName = workerProfile.name,
                type = type,
                startDate = startDate,
                endDate = endDate,
                reason = reason,
                status = "MENUNGGU"
            )
            repository.submitPermit(request)
            _userMessage.value = "✅ Pengajuan $type berhasil dikirim ke HRD. Menunggu verifikasi."
        }
    }

    fun updatePermitStatus(id: Long, status: String, notes: String) {
        viewModelScope.launch {
            repository.updatePermitStatus(id, status, notes)
            _userMessage.value = "Status pengajuan izin berhasil diperbarui ke: $status"
        }
    }

    fun updateFactoryConfig(name: String, lat: Double, lng: Double, radiusMeters: Float) {
        viewModelScope.launch {
            val config = FactoryConfig(
                factoryName = name,
                latitude = lat,
                longitude = lng,
                maxRadiusMeters = radiusMeters
            )
            repository.updateFactoryConfig(config)
            _userMessage.value = "✅ Parameter Geofencing Pabrik berhasil diperbarui."
        }
    }
}
