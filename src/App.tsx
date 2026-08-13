import React, { useState, useEffect } from 'react';
import { 
  Clock, MapPin, CheckCircle2, AlertTriangle, ShieldAlert, Navigation, 
  Calendar, FileText, UserCheck, Settings, Download, Camera, 
  Plus, Check, X, RefreshCw, ChevronRight, AlertCircle, Building2,
  User, Lock, ShieldCheck, Monitor, Smartphone, Users, Search, 
  Database, LogOut, Filter, Upload, Scan, ChevronLeft, ArrowRight
} from 'lucide-react';

interface Employee {
  nip: string;
  name: string;
  position: string;
  department: string;
  facePhoto: string;
  status: 'AKTIF' | 'CUTI' | 'NONAKTIF';
  joinDate: string;
}

interface AttendanceRecord {
  id: string;
  nip: string;
  employeeName: string;
  type: 'MASUK' | 'PULANG';
  time: string;
  date: string;
  location: string;
  distanceMeters: number;
  status: 'TEPAT WAKTU' | 'TERLAMBAT' | 'PULANG CEPAT';
  faceMatchScore: number;
}

interface PermitRequest {
  id: string;
  nip: string;
  employeeName: string;
  type: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'MENUNGGU' | 'DISETUJUI' | 'DITOLAK';
  dateSubmitted: string;
}

interface ShiftDay {
  dayNumber: number;
  dateStr: string;
  dayName: string;
  shiftType: 'PAGI' | 'SIANG' | 'MALAM' | 'LIBUR';
  timeRange: string;
  line: string;
  isToday?: boolean;
}

export default function App() {
  // Simulated Employee DB in Company System
  const [employeeDb, setEmployeeDb] = useState<Employee[]>([
    {
      nip: 'NIP-8821',
      name: 'Budi Santoso',
      position: 'Operator CNC Line 3',
      department: 'Produksi Plant A',
      facePhoto: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
      status: 'AKTIF',
      joinDate: '12 Jan 2022'
    },
    {
      nip: 'NIP-8822',
      name: 'Siti Rahmawati',
      position: 'Quality Control Inspector',
      department: 'Quality Assurance',
      facePhoto: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80',
      status: 'AKTIF',
      joinDate: '15 Mar 2023'
    },
    {
      nip: 'NIP-8823',
      name: 'Agus Setiawan',
      position: 'Technician Maintenance',
      department: 'Engineering',
      facePhoto: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
      status: 'AKTIF',
      joinDate: '01 Jun 2021'
    }
  ]);

  // Auth & Profile state
  const [currentUser, setCurrentUser] = useState<Employee | null>(null);
  const [loginStep, setLoginStep] = useState<'form' | 'face' | 'sync'>('form');
  const [inputNip, setInputNip] = useState('');
  const [inputName, setInputName] = useState('');
  const [inputDept, setInputDept] = useState('Produksi Plant A');
  const [inputPhoto, setInputPhoto] = useState<string | null>(null);
  const [isCapturingPhoto, setIsCapturingPhoto] = useState(false);

  // App Navigation state
  const [activeTab, setActiveTab] = useState<'home' | 'schedule' | 'permit' | 'admin'>('home');
  const [isDesktopMode, setIsDesktopMode] = useState<boolean>(false);
  const [currentTime, setCurrentTime] = useState<Date>(new Date());
  
  // Location simulation state
  const [locationMode, setLocationMode] = useState<'inside' | 'outside' | 'mock'>('inside');
  const [distance, setDistance] = useState<number>(35);
  
  // Face Verification scanning modal for attendance
  const [isScanningFace, setIsScanningFace] = useState<boolean>(false);
  const [scanStep, setScanStep] = useState<'detecting' | 'verifying' | 'matched'>('detecting');

  // Attendance state
  const [hasClockedInToday, setHasClockedInToday] = useState<boolean>(false);
  const [attendanceLogs, setAttendanceLogs] = useState<AttendanceRecord[]>([
    { id: '1', nip: 'NIP-8821', employeeName: 'Budi Santoso', type: 'PULANG', time: '15:02:10 WIB', date: '12 Ags 2026', location: 'Plant A - Gate 1', distanceMeters: 28, status: 'TEPAT WAKTU', faceMatchScore: 99.4 },
    { id: '2', nip: 'NIP-8821', employeeName: 'Budi Santoso', type: 'MASUK', time: '06:51:30 WIB', date: '12 Ags 2026', location: 'Plant A - Gate 1', distanceMeters: 32, status: 'TEPAT WAKTU', faceMatchScore: 98.9 },
    { id: '3', nip: 'NIP-8822', employeeName: 'Siti Rahmawati', type: 'MASUK', time: '06:55:00 WIB', date: '12 Ags 2026', location: 'Plant B - Gate QC', distanceMeters: 15, status: 'TEPAT WAKTU', faceMatchScore: 99.8 },
    { id: '4', nip: 'NIP-8823', employeeName: 'Agus Setiawan', type: 'MASUK', time: '07:15:20 WIB', date: '12 Ags 2026', location: 'Maintenance Workshop', distanceMeters: 45, status: 'TERLAMBAT', faceMatchScore: 97.6 },
  ]);

  // Toast Notification
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);

  // Permit state
  const [permitRequests, setPermitRequests] = useState<PermitRequest[]>([
    { id: 'PM-101', nip: 'NIP-8821', employeeName: 'Budi Santoso', type: 'Izin Sakit (Surat Dokter)', startDate: '2026-08-04', endDate: '2026-08-05', reason: 'Demam tinggi & flu', status: 'DISETUJUI', dateSubmitted: '03 Ags 2026' },
    { id: 'PM-102', nip: 'NIP-8821', employeeName: 'Budi Santoso', type: 'Cuti Tahunan', startDate: '2026-08-20', endDate: '2026-08-21', reason: 'Acara keluarga di kampung', status: 'MENUNGGU', dateSubmitted: '10 Ags 2026' },
    { id: 'PM-103', nip: 'NIP-8822', employeeName: 'Siti Rahmawati', type: 'Izin Sakit (Surat Dokter)', startDate: '2026-08-14', endDate: '2026-08-14', reason: 'Cek kesehatan rutin', status: 'MENUNGGU', dateSubmitted: '13 Ags 2026' }
  ]);

  // Permit Form Input
  const [permitType, setPermitType] = useState('Izin Sakit (Surat Dokter)');
  const [permitStart, setPermitStart] = useState('2026-08-15');
  const [permitEnd, setPermitEnd] = useState('2026-08-15');
  const [permitReason, setPermitReason] = useState('');
  const [hasDoctorNote, setHasDoctorNote] = useState(false);

  // HRD Admin Search
  const [employeeSearchQuery, setEmployeeSearchQuery] = useState('');

  // HRD Settings
  const [factoryRadius, setFactoryRadius] = useState<number>(100);
  const [factoryLat, setFactoryLat] = useState<string>('-6.284192');
  const [factoryLng, setFactoryLng] = useState<string>('107.170651');

  // Selected Day in Calendar
  const [selectedDayIndex, setSelectedDayIndex] = useState<number>(12); // Day 13 August

  // Generate 31 Days for August 2026
  const generateMonthSchedule = (): ShiftDay[] => {
    const days: ShiftDay[] = [];
    const dayNames = ['Min', 'Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab'];
    
    for (let day = 1; day <= 31; day++) {
      // 1 Aug 2026 is Saturday (index 6)
      const dayOfWeekIndex = (6 + day - 1) % 7;
      const dayName = dayNames[dayOfWeekIndex];
      
      let shiftType: 'PAGI' | 'SIANG' | 'MALAM' | 'LIBUR' = 'PAGI';
      let timeRange = '07:00 - 15:00 WIB';
      
      if (dayOfWeekIndex === 0) {
        shiftType = 'LIBUR';
        timeRange = 'Libur Mingguan';
      } else if (day >= 1 && day <= 10) {
        shiftType = 'PAGI';
        timeRange = '07:00 - 15:00 WIB';
      } else if (day >= 11 && day <= 20) {
        shiftType = 'SIANG';
        timeRange = '15:00 - 23:00 WIB';
      } else {
        shiftType = 'MALAM';
        timeRange = '23:00 - 07:00 WIB';
      }

      days.push({
        dayNumber: day,
        dateStr: `${day} Ags 2026`,
        dayName,
        shiftType,
        timeRange,
        line: 'Plant A - Line 3 CNC',
        isToday: day === 13
      });
    }
    return days;
  };

  const fullMonthDays = generateMonthSchedule();

  // Real-time clock update
  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  // Update distance when location mode changes
  useEffect(() => {
    if (locationMode === 'inside') setDistance(35);
    else if (locationMode === 'outside') setDistance(285);
    else setDistance(12);
  }, [locationMode]);

  const showToast = (msg: string, type: 'success' | 'error' | 'info' = 'success') => {
    setNotification({ message: msg, type });
    setTimeout(() => setNotification(null), 3500);
  };

  // Login & Synchronize Flow
  const handleRegisterFaceProfile = () => {
    if (!inputNip.trim() || !inputName.trim()) {
      showToast('Masukkan NIP dan Nama Lengkap terlebih dahulu!', 'error');
      return;
    }
    setLoginStep('face');
  };

  const handleSimulateCapture = () => {
    setIsCapturingPhoto(true);
    setTimeout(() => {
      // Set sample or custom profile image
      setInputPhoto('https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80');
      setIsCapturingPhoto(false);
      showToast('Foto Wajah Berhasil Diambil!');
    }, 1200);
  };

  const handleSyncCompanyDb = () => {
    if (!inputPhoto) {
      showToast('Wajib mengambil foto profil wajah terlebih dahulu!', 'error');
      return;
    }

    setLoginStep('sync');
    
    setTimeout(() => {
      const newUser: Employee = {
        nip: inputNip.startsWith('NIP-') ? inputNip : `NIP-${inputNip}`,
        name: inputName,
        position: 'Operator Line Production',
        department: inputDept,
        facePhoto: inputPhoto,
        status: 'AKTIF',
        joinDate: 'Hari Ini, 13 Ags 2026'
      };

      // Check if employee already exists in DB
      const existingIdx = employeeDb.findIndex(e => e.nip === newUser.nip);
      if (existingIdx >= 0) {
        const updatedDb = [...employeeDb];
        updatedDb[existingIdx] = newUser;
        setEmployeeDb(updatedDb);
      } else {
        setEmployeeDb([...employeeDb, newUser]);
      }

      setCurrentUser(newUser);
      showToast(`Login Berhasil! Data Karyawan ${newUser.name} tersinkronisasi dengan Database Perusahaan.`);
    }, 1800);
  };

  // Clock in/out with face scan popup
  const startClockingProcess = () => {
    if (locationMode === 'outside') {
      showToast('Gagal Absen! Anda berada di luar radius geofence pabrik (>100m).', 'error');
      return;
    }
    if (locationMode === 'mock') {
      showToast('DITOLAK: Terdeteksi Fake GPS / Aplikasi Lokasi Palsu!', 'error');
      return;
    }

    // Trigger Face Recognition Modal
    setIsScanningFace(true);
    setScanStep('detecting');

    setTimeout(() => setScanStep('verifying'), 1000);
    setTimeout(() => setScanStep('matched'), 2200);

    setTimeout(() => {
      setIsScanningFace(false);
      executeAttendance();
    }, 3200);
  };

  const executeAttendance = () => {
    const actionType: 'MASUK' | 'PULANG' = hasClockedInToday ? 'PULANG' : 'MASUK';
    const hours = currentTime.getHours();
    const minutes = currentTime.getMinutes();
    const isLate = actionType === 'MASUK' && (hours > 7 || (hours === 7 && minutes > 0));

    const newRecord: AttendanceRecord = {
      id: Date.now().toString(),
      nip: currentUser?.nip || 'NIP-8821',
      employeeName: currentUser?.name || 'Budi Santoso',
      type: actionType,
      time: currentTime.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit', second: '2-digit' }) + ' WIB',
      date: 'Hari Ini, 13 Ags 2026',
      location: 'Plant A - Gate Utama',
      distanceMeters: distance,
      status: isLate ? 'TERLAMBAT' : 'TEPAT WAKTU',
      faceMatchScore: 99.2
    };

    setAttendanceLogs([newRecord, ...attendanceLogs]);
    setHasClockedInToday(!hasClockedInToday);
    showToast(`Berhasil ${actionType === 'MASUK' ? 'Absen Masuk' : 'Absen Pulang'}! Wajah Terverifikasi 99.2%.`);
  };

  const handleNewPermit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!permitReason.trim()) {
      showToast('Isi alasan pengajuan izin terlebih dahulu.', 'error');
      return;
    }

    const newPermit: PermitRequest = {
      id: 'PM-' + Math.floor(100 + Math.random() * 900),
      nip: currentUser?.nip || 'NIP-8821',
      employeeName: currentUser?.name || 'Budi Santoso',
      type: permitType,
      startDate: permitStart,
      endDate: permitEnd,
      reason: permitReason,
      status: 'MENUNGGU',
      dateSubmitted: 'Hari Ini, 13 Ags 2026',
    };

    setPermitRequests([newPermit, ...permitRequests]);
    setPermitReason('');
    setHasDoctorNote(false);
    showToast('Pengajuan izin berhasil dikirim ke Admin HRD!');
  };

  const handleApprovePermit = (id: string, approve: boolean) => {
    setPermitRequests(permitRequests.map(p => 
      p.id === id ? { ...p, status: approve ? 'DISETUJUI' : 'DITOLAK' } : p
    ));
    showToast(`Pengajuan Izin #${id} telah ${approve ? 'DISETUJUI' : 'DITOLAK'}.`, approve ? 'success' : 'error');
  };

  // ==================== SCREEN 1: LOGIN & FACE SYNC ====================
  if (!currentUser) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center p-4">
        
        {/* Toast */}
        {notification && (
          <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 w-11/12 max-w-sm p-4 rounded-xl shadow-2xl flex items-center space-x-3 text-sm font-semibold bg-emerald-900/90 text-emerald-100 border border-emerald-500">
            <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
            <p>{notification.message}</p>
          </div>
        )}

        <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-2xl space-y-6 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-40 h-40 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none"></div>

          {/* Header */}
          <div className="text-center space-y-2">
            <div className="w-16 h-16 rounded-2xl bg-indigo-600 mx-auto flex items-center justify-center text-white shadow-xl shadow-indigo-600/30">
              <Building2 className="w-8 h-8" />
            </div>
            <h1 className="text-2xl font-black text-white tracking-tight">Absensi Pabrik</h1>
            <p className="text-xs text-indigo-400 font-medium">Sistem Verifikasi Wajah & Database HRD Perusahaan</p>
          </div>

          {/* Step Indicators */}
          <div className="flex items-center justify-center space-x-2 text-xs font-semibold">
            <div className={`px-3 py-1 rounded-full flex items-center space-x-1 ${loginStep === 'form' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-400'}`}>
              <span>1. NIP & Nama</span>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-600" />
            <div className={`px-3 py-1 rounded-full flex items-center space-x-1 ${loginStep === 'face' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-400'}`}>
              <span>2. Profil Wajah</span>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-600" />
            <div className={`px-3 py-1 rounded-full flex items-center space-x-1 ${loginStep === 'sync' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-400'}`}>
              <span>3. Sync DB</span>
            </div>
          </div>

          {/* STEP 1: FORM INPUT */}
          {loginStep === 'form' && (
            <div className="space-y-4 pt-2">
              <div className="space-y-1 text-xs">
                <label className="text-slate-300 font-semibold flex items-center space-x-1">
                  <User className="w-3.5 h-3.5 text-indigo-400" />
                  <span>Nomor Induk Pegawai (NIP / ID Buruh)</span>
                </label>
                <input 
                  type="text" 
                  placeholder="Contoh: 8821 atau NIP-8821"
                  value={inputNip}
                  onChange={(e) => setInputNip(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl p-3 text-slate-100 font-bold focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div className="space-y-1 text-xs">
                <label className="text-slate-300 font-semibold flex items-center space-x-1">
                  <UserCheck className="w-3.5 h-3.5 text-indigo-400" />
                  <span>Nama Lengkap Karyawan</span>
                </label>
                <input 
                  type="text" 
                  placeholder="Masukkan nama lengkap sesuai KTP"
                  value={inputName}
                  onChange={(e) => setInputName(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl p-3 text-slate-100 font-bold focus:outline-none focus:border-indigo-500 text-sm"
                />
              </div>

              <div className="space-y-1 text-xs">
                <label className="text-slate-300 font-semibold flex items-center space-x-1">
                  <Building2 className="w-3.5 h-3.5 text-indigo-400" />
                  <span>Departemen / Plant Pabrik</span>
                </label>
                <select 
                  value={inputDept}
                  onChange={(e) => setInputDept(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl p-3 text-slate-100 font-medium focus:outline-none focus:border-indigo-500 text-sm">
                  <option>Produksi Plant A (Line 1 - 5)</option>
                  <option>Quality Assurance & QC</option>
                  <option>Engineering & Maintenance</option>
                  <option>Logistik & Gudang Material</option>
                </select>
              </div>

              <button 
                onClick={handleRegisterFaceProfile}
                className="w-full py-3.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold text-sm shadow-lg shadow-indigo-600/30 flex items-center justify-center space-x-2 transition-all">
                <span>Lanjut ke Ambil Profil Wajah</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          )}

          {/* STEP 2: FACE PROFILE PHOTO CAPTURE */}
          {loginStep === 'face' && (
            <div className="space-y-4 text-center pt-2">
              <p className="text-xs text-slate-300">
                Ambil foto profil wajah depan Anda secara jelas untuk disinkronkan dengan sistem AI Biometrik HRD.
              </p>

              <div className="w-44 h-44 rounded-full mx-auto border-4 border-dashed border-indigo-500/60 bg-slate-800 flex flex-col items-center justify-center relative overflow-hidden group shadow-inner">
                {inputPhoto ? (
                  <img src={inputPhoto} alt="Profil Wajah" className="w-full h-full object-cover" />
                ) : (
                  <div className="flex flex-col items-center space-y-2 p-4">
                    <Scan className="w-10 h-10 text-indigo-400 animate-pulse" />
                    <span className="text-[11px] text-slate-400 font-medium">Area Wajah</span>
                  </div>
                )}

                {isCapturingPhoto && (
                  <div className="absolute inset-0 bg-slate-950/80 flex flex-col items-center justify-center space-y-2">
                    <RefreshCw className="w-8 h-8 text-indigo-400 animate-spin" />
                    <span className="text-xs text-indigo-300 font-bold">Memindai Wajah...</span>
                  </div>
                )}
              </div>

              <div className="flex space-x-2">
                <button 
                  onClick={handleSimulateCapture}
                  className="flex-1 py-3 bg-slate-800 hover:bg-slate-700 text-indigo-300 border border-slate-700 rounded-xl font-bold text-xs flex items-center justify-center space-x-2">
                  <Camera className="w-4 h-4" />
                  <span>{inputPhoto ? 'Foto Ulang Wajah' : 'Ambil Foto Wajah'}</span>
                </button>
              </div>

              <div className="flex space-x-2 pt-2">
                <button 
                  onClick={() => setLoginStep('form')}
                  className="py-3 px-4 bg-slate-800 text-slate-300 rounded-xl font-bold text-xs">
                  Kembali
                </button>
                <button 
                  onClick={handleSyncCompanyDb}
                  disabled={!inputPhoto}
                  className={`flex-1 py-3 rounded-xl font-bold text-xs shadow-lg flex items-center justify-center space-x-2 ${
                    inputPhoto ? 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-600/30' : 'bg-slate-800 text-slate-500 cursor-not-allowed'
                  }`}>
                  <Database className="w-4 h-4" />
                  <span>Sinkronkan ke Database Perusahaan</span>
                </button>
              </div>
            </div>
          )}

          {/* STEP 3: SYNCING WITH COMPANY DATABASE */}
          {loginStep === 'sync' && (
            <div className="py-10 text-center space-y-4">
              <RefreshCw className="w-12 h-12 text-indigo-400 animate-spin mx-auto" />
              <div className="space-y-1">
                <h3 className="font-bold text-lg text-white">Mencocokkan Data Biometrik...</h3>
                <p className="text-xs text-slate-400">Menyimpan data profil {inputName} ({inputNip}) ke server HRD Pabrik.</p>
              </div>
              <div className="w-48 bg-slate-800 h-2 rounded-full mx-auto overflow-hidden">
                <div className="bg-indigo-500 h-full w-3/4 animate-pulse"></div>
              </div>
            </div>
          )}

        </div>
      </div>
    );
  }

  // ==================== MAIN APP SCREEN ====================
  return (
    <div className={`min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between mx-auto relative border-x border-slate-800 shadow-2xl ${
      isDesktopMode && activeTab === 'admin' ? 'max-w-6xl' : 'max-w-md'
    }`}>
      
      {/* Toast Notification */}
      {notification && (
        <div className={`fixed top-4 left-1/2 -translate-x-1/2 z-50 w-11/12 max-w-sm p-4 rounded-xl shadow-2xl flex items-center space-x-3 text-sm font-semibold border ${
          notification.type === 'success' ? 'bg-emerald-900/90 text-emerald-100 border-emerald-500' :
          notification.type === 'error' ? 'bg-rose-900/90 text-rose-100 border-rose-500' :
          'bg-indigo-900/90 text-indigo-100 border-indigo-500'
        }`}>
          {notification.type === 'success' && <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />}
          {notification.type === 'error' && <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0" />}
          {notification.type === 'info' && <AlertCircle className="w-5 h-5 text-indigo-400 shrink-0" />}
          <p>{notification.message}</p>
        </div>
      )}

      {/* Face Scanning Popup Verification Modal */}
      {isScanningFace && (
        <div className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-md flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-indigo-500/50 p-6 rounded-3xl max-w-xs w-full text-center space-y-4 shadow-2xl animate-fade-in">
            <div className="relative w-36 h-36 mx-auto rounded-full border-4 border-indigo-500 overflow-hidden flex items-center justify-center">
              <img src={currentUser.facePhoto} alt="Face Scan" className="w-full h-full object-cover" />
              <div className="absolute inset-0 bg-indigo-500/20 animate-pulse border-b-2 border-indigo-400"></div>
            </div>

            {scanStep === 'detecting' && (
              <div className="space-y-1">
                <p className="font-bold text-sm text-indigo-300">Mendeteksi Wajah Karyawan...</p>
                <p className="text-[11px] text-slate-400">Posisikan wajah di tengah kamera</p>
              </div>
            )}
            {scanStep === 'verifying' && (
              <div className="space-y-1">
                <p className="font-bold text-sm text-amber-300">Mencocokkan Biometrik HRD...</p>
                <p className="text-[11px] text-slate-400">NIP: {currentUser.nip} • {currentUser.name}</p>
              </div>
            )}
            {scanStep === 'matched' && (
              <div className="space-y-1 text-emerald-400">
                <CheckCircle2 className="w-8 h-8 mx-auto" />
                <p className="font-extrabold text-base">VERIFIKASI WAJAH 99.2% MATCH!</p>
                <p className="text-[11px] text-emerald-200">Database Perusahaan Terverifikasi</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Top App Header */}
      <header className="bg-slate-900 border-b border-slate-800 p-4 sticky top-0 z-20">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center font-bold text-white shadow-lg shadow-indigo-600/30">
              <Building2 className="w-5 h-5" />
            </div>
            <div>
              <h1 className="font-bold text-base text-slate-100 leading-tight">Absensi Pabrik</h1>
              <p className="text-xs text-indigo-400 font-medium">PT Mitra Industri Cikarang</p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            {activeTab === 'admin' && (
              <button 
                onClick={() => setIsDesktopMode(!isDesktopMode)}
                className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-indigo-300 text-xs font-semibold border border-slate-700 flex items-center space-x-1"
                title="Toggle Mode Tampilan Desktop HRD">
                {isDesktopMode ? <Smartphone className="w-4 h-4" /> : <Monitor className="w-4 h-4" />}
                <span className="hidden sm:inline">{isDesktopMode ? 'Mode HP' : 'Mode Desktop'}</span>
              </button>
            )}
            <button 
              onClick={() => setCurrentUser(null)}
              className="p-2 rounded-xl bg-slate-800 hover:bg-rose-950 text-slate-400 hover:text-rose-300 text-xs font-semibold border border-slate-700"
              title="Keluar / Ganti Akun">
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Worker Profile Card Header */}
        <div className="mt-3 bg-slate-800/60 rounded-xl p-2.5 border border-slate-700/50 flex items-center justify-between text-xs">
          <div className="flex items-center space-x-3">
            <img src={currentUser.facePhoto} alt="Wajah Profil" className="w-9 h-9 rounded-full object-cover border-2 border-indigo-500 shadow" />
            <div>
              <p className="font-bold text-slate-100 flex items-center space-x-1">
                <span>{currentUser.name}</span>
                <ShieldCheck className="w-3.5 h-3.5 text-indigo-400" />
              </p>
              <p className="text-slate-400">{currentUser.nip} • {currentUser.position}</p>
            </div>
          </div>
          <div className="text-right">
            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-950 text-emerald-300 border border-emerald-800">
              Tersinkron DB
            </span>
          </div>
        </div>
      </header>

      {/* Main Content Body */}
      <main className="flex-1 p-4 overflow-y-auto pb-24">
        
        {/* ==================== TAB 1: BERANDA ==================== */}
        {activeTab === 'home' && (
          <div className="space-y-4">
            
            {/* Real-time Clock */}
            <div className="bg-gradient-to-br from-indigo-900/40 via-slate-900 to-slate-900 p-5 rounded-2xl border border-indigo-500/20 text-center relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-indigo-500/10 rounded-full blur-2xl pointer-events-none"></div>
              <p className="text-xs text-indigo-300 font-semibold tracking-wider uppercase">Waktu Server Pabrik</p>
              <div className="text-3xl font-extrabold text-white tracking-tight my-1 font-mono">
                {currentTime.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                <span className="text-xs text-indigo-400 font-sans ml-1">WIB</span>
              </div>
              <p className="text-xs text-slate-400 font-medium">
                {currentTime.toLocaleDateString('id-ID', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
              </p>
            </div>

            {/* Geofencing Status Card */}
            <div className={`p-4 rounded-2xl border transition-all duration-300 ${
              locationMode === 'inside' ? 'bg-emerald-950/40 border-emerald-500/30 text-emerald-200' :
              locationMode === 'outside' ? 'bg-rose-950/40 border-rose-500/30 text-rose-200' :
              'bg-amber-950/40 border-amber-500/30 text-amber-200'
            }`}>
              <div className="flex items-start justify-between">
                <div className="flex items-center space-x-2.5">
                  {locationMode === 'inside' && <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />}
                  {locationMode === 'outside' && <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0" />}
                  {locationMode === 'mock' && <ShieldAlert className="w-5 h-5 text-amber-400 shrink-0" />}
                  <div>
                    <h3 className="font-bold text-sm">
                      {locationMode === 'inside' && 'Lokasi Terverifikasi (Dalam Radius)'}
                      {locationMode === 'outside' && 'Di Luar Radius Pabrik (>100m)'}
                      {locationMode === 'mock' && 'Peringatan: Fake GPS Terdeteksi'}
                    </h3>
                    <p className="text-xs opacity-80 mt-0.5">
                      {locationMode === 'inside' && `Jarak dari Gate Pabrik: ${distance} Meter (Maks 100m)`}
                      {locationMode === 'outside' && `Jarak dari Gate Pabrik: ${distance} Meter (Terlalu Jauh)`}
                      {locationMode === 'mock' && 'Aplikasi lokasi palsu aktif'}
                    </p>
                  </div>
                </div>
              </div>

              {/* Simulation Mode Selector */}
              <div className="mt-3 pt-3 border-t border-white/10">
                <p className="text-[10px] font-semibold tracking-wider text-slate-400 uppercase mb-2">Simulasi Lokasi GPS:</p>
                <div className="grid grid-cols-3 gap-1.5">
                  <button 
                    onClick={() => setLocationMode('inside')}
                    className={`py-1.5 px-2 rounded-lg text-xs font-semibold border transition-all ${
                      locationMode === 'inside' ? 'bg-emerald-600 text-white border-emerald-400' : 'bg-slate-800 text-slate-300 border-slate-700'
                    }`}>
                    ✓ Dalam (35m)
                  </button>
                  <button 
                    onClick={() => setLocationMode('outside')}
                    className={`py-1.5 px-2 rounded-lg text-xs font-semibold border transition-all ${
                      locationMode === 'outside' ? 'bg-rose-600 text-white border-rose-400' : 'bg-slate-800 text-slate-300 border-slate-700'
                    }`}>
                    ✕ Luar (285m)
                  </button>
                  <button 
                    onClick={() => setLocationMode('mock')}
                    className={`py-1.5 px-2 rounded-lg text-xs font-semibold border transition-all ${
                      locationMode === 'mock' ? 'bg-amber-600 text-white border-amber-400' : 'bg-slate-800 text-slate-300 border-slate-700'
                    }`}>
                    ⚠ Fake GPS
                  </button>
                </div>
              </div>
            </div>

            {/* Giant Action Clocking Button */}
            <div className="py-4 text-center">
              <button
                onClick={startClockingProcess}
                disabled={locationMode !== 'inside'}
                className={`w-44 h-44 rounded-full mx-auto flex flex-col items-center justify-center p-4 transition-all duration-300 transform active:scale-95 shadow-2xl relative ${
                  locationMode !== 'inside' ? 'bg-slate-800 border-4 border-slate-700 text-slate-500 cursor-not-allowed opacity-60' :
                  !hasClockedInToday ? 'bg-gradient-to-tr from-emerald-600 to-teal-500 border-4 border-emerald-400/50 text-white shadow-emerald-600/40 animate-pulse' :
                  'bg-gradient-to-tr from-indigo-600 to-blue-500 border-4 border-indigo-400/50 text-white shadow-indigo-600/40'
                }`}
              >
                <Scan className="w-10 h-10 mb-1" />
                <span className="font-extrabold text-base tracking-wider">
                  {!hasClockedInToday ? 'ABSEN MASUK' : 'ABSEN PULANG'}
                </span>
                <span className="text-[10px] opacity-90 mt-1 font-medium bg-black/20 px-2 py-0.5 rounded-full">
                  Scan Biometrik Wajah
                </span>
              </button>
            </div>

            {/* Recent Attendance Logs */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-sm text-slate-200">Riwayat Absensi Terakhir</h3>
                <span className="text-xs text-indigo-400 font-medium">{attendanceLogs.length} Log Terdeteksi</span>
              </div>

              <div className="space-y-2 max-h-52 overflow-y-auto pr-1">
                {attendanceLogs.map((log) => (
                  <div key={log.id} className="p-3 bg-slate-800/70 rounded-xl border border-slate-700/40 flex items-center justify-between text-xs">
                    <div className="flex items-center space-x-3">
                      <div className={`w-8 h-8 rounded-lg flex items-center justify-center font-bold text-white ${
                        log.type === 'MASUK' ? 'bg-emerald-600' : 'bg-indigo-600'
                      }`}>
                        {log.type === 'MASUK' ? 'IN' : 'OUT'}
                      </div>
                      <div>
                        <p className="font-bold text-slate-200">{log.employeeName} • {log.type}</p>
                        <p className="text-slate-400">{log.time} • Match: {log.faceMatchScore}%</p>
                      </div>
                    </div>
                    <span className={`px-2 py-1 rounded-md text-[10px] font-bold ${
                      log.status === 'TEPAT WAKTU' ? 'bg-emerald-950 text-emerald-300 border border-emerald-800/40' :
                      'bg-amber-950 text-amber-300 border border-amber-800/40'
                    }`}>
                      {log.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>

          </div>
        )}

        {/* ==================== TAB 2: JADWAL SHIFT 1 BULAN FULL ==================== */}
        {activeTab === 'schedule' && (
          <div className="space-y-4">
            
            {/* Header & Stats summary */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-extrabold text-base text-white flex items-center space-x-2">
                  <Calendar className="w-5 h-5 text-indigo-400" />
                  <span>Jadwal Shift Kerja Ags 2026</span>
                </h3>
                <span className="px-2.5 py-1 bg-indigo-950 text-indigo-300 border border-indigo-800 text-xs font-bold rounded-lg">
                  31 Hari Full
                </span>
              </div>

              {/* Month summary cards */}
              <div className="grid grid-cols-3 gap-2 text-center text-xs pt-1">
                <div className="bg-emerald-950/50 border border-emerald-800/40 p-2.5 rounded-xl">
                  <p className="text-slate-400 text-[10px]">Hari Kerja</p>
                  <p className="font-black text-emerald-300 text-base">22 Hari</p>
                </div>
                <div className="bg-purple-950/50 border border-purple-800/40 p-2.5 rounded-xl">
                  <p className="text-slate-400 text-[10px]">Shift Malam</p>
                  <p className="font-black text-purple-300 text-base">9 Hari</p>
                </div>
                <div className="bg-slate-800/60 border border-slate-700/50 p-2.5 rounded-xl">
                  <p className="text-slate-400 text-[10px]">Total Libur</p>
                  <p className="font-black text-slate-300 text-base">9 Hari</p>
                </div>
              </div>
            </div>

            {/* Calendar Grid 31 Days */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex justify-between items-center text-xs font-bold text-slate-300 border-b border-slate-800 pb-2">
                <span>Agustus 2026</span>
                <span className="text-indigo-400">Klik tanggal untuk rincian shift</span>
              </div>

              {/* Day headers */}
              <div className="grid grid-cols-7 gap-1 text-center text-[11px] font-bold text-slate-400">
                <span>Sab</span><span>Min</span><span>Sen</span><span>Sel</span><span>Rab</span><span>Kam</span><span>Jum</span>
              </div>

              {/* 31 Days Grid */}
              <div className="grid grid-cols-7 gap-1 text-center text-xs">
                {fullMonthDays.map((d, index) => {
                  const isSelected = index === selectedDayIndex;
                  return (
                    <button
                      key={d.dayNumber}
                      onClick={() => setSelectedDayIndex(index)}
                      className={`p-1.5 rounded-xl border flex flex-col items-center justify-between h-14 transition-all ${
                        d.isToday ? 'ring-2 ring-indigo-400 font-extrabold' : ''
                      } ${
                        isSelected ? 'scale-105 shadow-lg z-10' : ''
                      } ${
                        d.shiftType === 'PAGI' ? 'bg-emerald-950/80 border-emerald-800/60 text-emerald-200' :
                        d.shiftType === 'SIANG' ? 'bg-blue-950/80 border-blue-800/60 text-blue-200' :
                        d.shiftType === 'MALAM' ? 'bg-purple-950/80 border-purple-800/60 text-purple-200' :
                        'bg-slate-800/40 border-slate-700/40 text-slate-500'
                      }`}>
                      <span className="font-extrabold text-xs">{d.dayNumber}</span>
                      <span className="text-[9px] font-semibold tracking-tighter">{d.shiftType}</span>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Detailed View for Selected Day */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex items-center justify-between">
                <h4 className="font-bold text-sm text-slate-200">
                  Rincian Shift: {fullMonthDays[selectedDayIndex].dateStr} ({fullMonthDays[selectedDayIndex].dayName})
                </h4>
                <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold ${
                  fullMonthDays[selectedDayIndex].shiftType === 'PAGI' ? 'bg-emerald-950 text-emerald-300' :
                  fullMonthDays[selectedDayIndex].shiftType === 'SIANG' ? 'bg-blue-950 text-blue-300' :
                  fullMonthDays[selectedDayIndex].shiftType === 'MALAM' ? 'bg-purple-950 text-purple-300' :
                  'bg-slate-800 text-slate-400'
                }`}>
                  SHIFT {fullMonthDays[selectedDayIndex].shiftType}
                </span>
              </div>

              <div className="space-y-2 text-xs bg-slate-800/60 p-3 rounded-xl border border-slate-700/50">
                <div className="flex justify-between">
                  <span className="text-slate-400">Jam Kerja:</span>
                  <span className="font-bold text-slate-100">{fullMonthDays[selectedDayIndex].timeRange}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-400">Lokasi Penugasan:</span>
                  <span className="font-bold text-slate-100">{fullMonthDays[selectedDayIndex].line}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-400">Supervisor Line:</span>
                  <span className="font-bold text-indigo-300">Ir. Bambang Hartono</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-400">Batas Toleransi Telat:</span>
                  <span className="font-bold text-amber-300">10 Menit (07:10 WIB)</span>
                </div>
              </div>
            </div>

          </div>
        )}

        {/* ==================== TAB 3: IZIN / CUTI ==================== */}
        {activeTab === 'permit' && (
          <div className="space-y-4">
            
            {/* Form Request */}
            <form onSubmit={handleNewPermit} className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <h3 className="font-bold text-sm text-slate-200 flex items-center space-x-2">
                <FileText className="w-4 h-4 text-indigo-400" />
                <span>Form Pengajuan Izin / Cuti Karyawan</span>
              </h3>

              <div className="space-y-1 text-xs">
                <label className="text-slate-400 font-medium">Jenis Pengajuan</label>
                <select 
                  value={permitType} 
                  onChange={(e) => setPermitType(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-slate-200 font-medium focus:outline-none focus:border-indigo-500">
                  <option>Izin Sakit (Surat Dokter)</option>
                  <option>Cuti Tahunan</option>
                  <option>Izin Khusus / Pribadi</option>
                  <option>Dispensasi Serikat Buruh</option>
                </select>
              </div>

              <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="space-y-1">
                  <label className="text-slate-400 font-medium">Tanggal Mulai</label>
                  <input 
                    type="date" 
                    value={permitStart} 
                    onChange={(e) => setPermitStart(e.target.value)}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2 text-slate-200 focus:outline-none focus:border-indigo-500" 
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-slate-400 font-medium">Tanggal Selesai</label>
                  <input 
                    type="date" 
                    value={permitEnd} 
                    onChange={(e) => setPermitEnd(e.target.value)}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2 text-slate-200 focus:outline-none focus:border-indigo-500" 
                  />
                </div>
              </div>

              <div className="space-y-1 text-xs">
                <label className="text-slate-400 font-medium">Alasan Pengajuan</label>
                <textarea 
                  rows={2} 
                  placeholder="Jelaskan alasan izin secara rinci..."
                  value={permitReason}
                  onChange={(e) => setPermitReason(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-slate-200 focus:outline-none focus:border-indigo-500 resize-none"
                />
              </div>

              {/* Photo Attachment Button */}
              <div className="text-xs">
                <button
                  type="button"
                  onClick={() => {
                    setHasDoctorNote(!hasDoctorNote);
                    showToast(hasDoctorNote ? 'Lampiran foto dihapus' : 'Foto surat dokter berhasil terlampir!');
                  }}
                  className={`w-full py-2.5 px-3 rounded-xl border border-dashed flex items-center justify-center space-x-2 transition-all ${
                    hasDoctorNote ? 'bg-emerald-950/50 border-emerald-500 text-emerald-300' : 'bg-slate-800/60 border-slate-700 text-slate-400'
                  }`}>
                  <Camera className="w-4 h-4" />
                  <span>{hasDoctorNote ? '✓ Foto Lampiran Terpasang (surat_dokter.jpg)' : '+ Ambil Foto / Lampirkan Surat Dokter'}</span>
                </button>
              </div>

              <button 
                type="submit"
                className="w-full py-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold text-xs shadow-lg shadow-indigo-600/30 transition-all">
                Kirim Pengajuan ke Admin HRD
              </button>
            </form>

            {/* Submitted Permits History */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <h3 className="font-bold text-sm text-slate-200">Riwayat Pengajuan Saya</h3>
              <div className="space-y-2">
                {permitRequests.filter(p => p.nip === currentUser.nip).map((p) => (
                  <div key={p.id} className="p-3 bg-slate-800/60 rounded-xl border border-slate-700/50 space-y-1.5 text-xs">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-slate-200">{p.type}</span>
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                        p.status === 'DISETUJUI' ? 'bg-emerald-950 text-emerald-300 border border-emerald-800' :
                        p.status === 'DITOLAK' ? 'bg-rose-950 text-rose-300 border border-rose-800' :
                        'bg-amber-950 text-amber-300 border border-amber-800'
                      }`}>
                        {p.status}
                      </span>
                    </div>
                    <p className="text-slate-400">Periode: {p.startDate} s.d {p.endDate}</p>
                    <p className="text-slate-300 italic">"{p.reason}"</p>
                  </div>
                ))}
              </div>
            </div>

          </div>
        )}

        {/* ==================== TAB 4: ADMIN HRD (MOBILE OR DESKTOP) ==================== */}
        {activeTab === 'admin' && (
          <div className="space-y-5">
            
            {/* Mode Banner Indicator */}
            <div className="bg-indigo-950/80 border border-indigo-500/40 p-3 rounded-2xl flex items-center justify-between text-xs">
              <div className="flex items-center space-x-2">
                <Monitor className="w-5 h-5 text-indigo-400" />
                <div>
                  <p className="font-bold text-white">Panel Dashboard Admin HRD</p>
                  <p className="text-slate-400">Mode: {isDesktopMode ? 'Tampilan Desktop Lebar' : 'Tampilan Kompak HP'}</p>
                </div>
              </div>
              <button 
                onClick={() => setIsDesktopMode(!isDesktopMode)}
                className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold">
                Ubah ke {isDesktopMode ? 'Tampilan HP' : 'Tampilan Desktop'}
              </button>
            </div>

            {/* Desktop Overview Cards Grid */}
            <div className={`grid gap-3 ${isDesktopMode ? 'grid-cols-4' : 'grid-cols-2'}`}>
              <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
                <p className="text-xs text-slate-400 font-medium">Total Karyawan DB</p>
                <p className="text-2xl font-black text-white my-1">{employeeDb.length} Orang</p>
                <span className="text-[10px] text-emerald-400 font-semibold">Tersinkronisasi 100%</span>
              </div>
              <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
                <p className="text-xs text-slate-400 font-medium">Hadir Hari Ini</p>
                <p className="text-2xl font-black text-emerald-400 my-1">{attendanceLogs.length} Orang</p>
                <span className="text-[10px] text-emerald-300">Scan Wajah Lolos</span>
              </div>
              <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
                <p className="text-xs text-slate-400 font-medium">Izin Menunggu</p>
                <p className="text-2xl font-black text-amber-400 my-1">
                  {permitRequests.filter(p => p.status === 'MENUNGGU').length} Berkas
                </p>
                <span className="text-[10px] text-amber-300">Perlu Persetujuan</span>
              </div>
              <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
                <p className="text-xs text-slate-400 font-medium">Geofence Status</p>
                <p className="text-2xl font-black text-indigo-400 my-1">{factoryRadius} Meter</p>
                <span className="text-[10px] text-indigo-300">Plant A Gate 1</span>
              </div>
            </div>

            {/* Database Karyawan Table Section */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
                <div className="flex items-center space-x-2">
                  <Database className="w-5 h-5 text-indigo-400" />
                  <h3 className="font-bold text-base text-white">Database Karyawan & Profil Wajah</h3>
                </div>
                <div className="relative">
                  <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
                  <input 
                    type="text" 
                    placeholder="Cari NIP atau Nama..." 
                    value={employeeSearchQuery}
                    onChange={(e) => setEmployeeSearchQuery(e.target.value)}
                    className="bg-slate-800 border border-slate-700 rounded-xl pl-9 pr-3 py-1.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500 w-full sm:w-56"
                  />
                </div>
              </div>

              {/* Table list */}
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs text-slate-300">
                  <thead className="bg-slate-800/80 text-slate-400 uppercase text-[10px] font-bold">
                    <tr>
                      <th className="p-3 rounded-l-xl">Foto Wajah</th>
                      <th className="p-3">NIP & Nama</th>
                      <th className="p-3">Departemen</th>
                      <th className="p-3">Status Biometrik</th>
                      <th className="p-3 rounded-r-xl">Tanggal Masuk</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60 font-medium">
                    {employeeDb
                      .filter(e => e.name.toLowerCase().includes(employeeSearchQuery.toLowerCase()) || e.nip.toLowerCase().includes(employeeSearchQuery.toLowerCase()))
                      .map((emp) => (
                        <tr key={emp.nip} className="hover:bg-slate-800/40">
                          <td className="p-3">
                            <img src={emp.facePhoto} alt={emp.name} className="w-10 h-10 rounded-full object-cover border-2 border-indigo-500 shadow" />
                          </td>
                          <td className="p-3">
                            <p className="font-bold text-white">{emp.name}</p>
                            <p className="text-slate-400">{emp.nip} • {emp.position}</p>
                          </td>
                          <td className="p-3 text-slate-300">{emp.department}</td>
                          <td className="p-3">
                            <span className="px-2 py-1 rounded-md text-[10px] font-bold bg-emerald-950 text-emerald-300 border border-emerald-800 flex items-center space-x-1 w-fit">
                              <ShieldCheck className="w-3 h-3" />
                              <span>TERDAFTAR DB</span>
                            </span>
                          </td>
                          <td className="p-3 text-slate-400">{emp.joinDate}</td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* HRD Leave Permit Approval Queue */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <h3 className="font-bold text-base text-white flex items-center justify-between">
                <span>Persetujuan Izin & Cuti Karyawan</span>
                <span className="text-xs text-amber-400 font-normal">Antrean Berkas</span>
              </h3>

              <div className="space-y-2.5">
                {permitRequests.filter(p => p.status === 'MENUNGGU').length === 0 ? (
                  <p className="text-xs text-slate-400 italic py-3 text-center">Tidak ada pengajuan izin menunggu persetujuan.</p>
                ) : (
                  permitRequests.filter(p => p.status === 'MENUNGGU').map((p) => (
                    <div key={p.id} className="p-3.5 bg-slate-800/80 rounded-xl border border-slate-700/60 space-y-2 text-xs">
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-bold text-white text-sm">{p.employeeName} ({p.nip})</p>
                          <p className="text-indigo-400 font-medium">{p.type}</p>
                        </div>
                        <span className="text-[10px] text-slate-400">{p.dateSubmitted}</span>
                      </div>
                      <p className="text-slate-300 bg-slate-900/60 p-2.5 rounded-lg text-xs">"{p.reason}"</p>
                      
                      <div className="flex space-x-2 pt-1">
                        <button 
                          onClick={() => handleApprovePermit(p.id, true)}
                          className="flex-1 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl font-bold text-xs flex items-center justify-center space-x-1">
                          <Check className="w-4 h-4" />
                          <span>Setujui Izin</span>
                        </button>
                        <button 
                          onClick={() => handleApprovePermit(p.id, false)}
                          className="flex-1 py-2 bg-rose-600 hover:bg-rose-500 text-white rounded-xl font-bold text-xs flex items-center justify-center space-x-1">
                          <X className="w-4 h-4" />
                          <span>Tolak Pengajuan</span>
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* Geofence Configuration Panel */}
            <div className="bg-slate-900 rounded-2xl p-4 border border-slate-800 space-y-3">
              <h3 className="font-bold text-base text-white flex items-center space-x-2">
                <Settings className="w-5 h-5 text-indigo-400" />
                <span>Pengaturan Radius Geofence Pabrik</span>
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                <div>
                  <label className="text-slate-400 font-medium">Batas Radius (Meter)</label>
                  <input 
                    type="number" 
                    value={factoryRadius} 
                    onChange={(e) => setFactoryRadius(Number(e.target.value))}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-slate-200 mt-1 font-bold text-indigo-300"
                  />
                </div>
                <div>
                  <label className="text-slate-400 font-medium">Latitude Gate</label>
                  <input 
                    type="text" 
                    value={factoryLat} 
                    onChange={(e) => setFactoryLat(e.target.value)}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-slate-200 mt-1 font-mono"
                  />
                </div>
                <div>
                  <label className="text-slate-400 font-medium">Longitude Gate</label>
                  <input 
                    type="text" 
                    value={factoryLng} 
                    onChange={(e) => setFactoryLng(e.target.value)}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-slate-200 mt-1 font-mono"
                  />
                </div>
              </div>

              <button 
                onClick={() => showToast('Koordinat Geofence Pabrik berhasil disimpan!')}
                className="w-full py-2.5 bg-slate-800 hover:bg-slate-700 text-indigo-300 rounded-xl font-bold text-xs border border-slate-700 transition-all">
                Simpan Konfigurasi Geofence
              </button>
            </div>

          </div>
        )}

      </main>

      {/* Bottom Navigation Bar */}
      <nav className={`bg-slate-900 border-t border-slate-800 fixed bottom-0 left-0 right-0 z-30 px-2 py-2 ${
        isDesktopMode && activeTab === 'admin' ? 'max-w-6xl' : 'max-w-md'
      } mx-auto`}>
        <div className="grid grid-cols-4 gap-1 text-center">
          
          <button 
            onClick={() => setActiveTab('home')}
            className={`py-1.5 px-1 rounded-xl flex flex-col items-center justify-center transition-all ${
              activeTab === 'home' ? 'text-indigo-400 font-bold bg-indigo-950/60' : 'text-slate-400 font-medium hover:text-slate-200'
            }`}>
            <Clock className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">Beranda</span>
          </button>

          <button 
            onClick={() => setActiveTab('schedule')}
            className={`py-1.5 px-1 rounded-xl flex flex-col items-center justify-center transition-all ${
              activeTab === 'schedule' ? 'text-indigo-400 font-bold bg-indigo-950/60' : 'text-slate-400 font-medium hover:text-slate-200'
            }`}>
            <Calendar className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">Shift 1 Bulan</span>
          </button>

          <button 
            onClick={() => setActiveTab('permit')}
            className={`py-1.5 px-1 rounded-xl flex flex-col items-center justify-center transition-all ${
              activeTab === 'permit' ? 'text-indigo-400 font-bold bg-indigo-950/60' : 'text-slate-400 font-medium hover:text-slate-200'
            }`}>
            <FileText className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">Izin / Cuti</span>
          </button>

          <button 
            onClick={() => setActiveTab('admin')}
            className={`py-1.5 px-1 rounded-xl flex flex-col items-center justify-center transition-all ${
              activeTab === 'admin' ? 'text-indigo-400 font-bold bg-indigo-950/60' : 'text-slate-400 font-medium hover:text-slate-200'
            }`}>
            <UserCheck className="w-5 h-5 mb-0.5" />
            <span className="text-[10px]">Admin HRD</span>
          </button>

        </div>
      </nav>

    </div>
  );
}
