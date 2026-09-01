# MariFin — Kelola Keuangan, Lebih Cerdas 📱💰

> **MariFin** adalah aplikasi manajemen keuangan pribadi *native* Android berbasis Clean MVVM yang dilengkapi asisten cerdas **FinGPT** untuk pencatatan transaksi otomatis, pemantauan anggaran multi-zona, pelacak tagihan rutin, target tabungan, dan penyimpanan bukti struk transaksi terenkripsi.

---

[![Android Platform](https://img.shields.io/badge/Platform-Android%20Native-3DDC84?logo=android&logoColor=white)](#)
[![Language Java 11](https://img.shields.io/badge/Language-Java%2011-ED8B00?logo=openjdk&logoColor=white)](#)
[![Architecture MVVM](https://img.shields.io/badge/Architecture-Clean%20MVVM-1E56A0?logo=blueprint&logoColor=white)](#)
[![Database Room](https://img.shields.io/badge/Local%20DB-Android%20Room-4285F4?logo=sqlite&logoColor=white)](#)
[![Backend Supabase](https://img.shields.io/badge/Cloud-Supabase%20PostgreSQL-3ECF8E?logo=supabase&logoColor=white)](#)
[![AI FinGPT](https://img.shields.io/badge/AI%20Assistant-FinGPT-9333EA?logo=openai&logoColor=white)](#)
[![Version](https://img.shields.io/badge/Version-2.0.0-blue)](#)
[![License](https://img.shields.io/badge/License-MIT-green)](#)

---

## 📑 Daftar Isi

- [📖 Deskripsi Proyek](#-deskripsi-proyek)
  - [Tujuan & Masalah yang Diselesaikan](#tujuan--masalah-yang-diselesaikan)
  - [Fitur Utama](#fitur-utama)
- [⚙️ Prasyarat](#️-prasyarat)
- [🚀 Instalasi](#-instalasi)
- [💡 Penggunaan](#-penggunaan)
  - [Menjalankan Aplikasi](#menjalankan-aplikasi)
  - [Pencatatan Transaksi Cerdas via FinGPT](#pencatatan-transaksi-cerdas-via-fingpt)
  - [Ekspor Laporan Keuangan](#ekspor-laporan-keuangan)
- [🏛️ Arsitektur & Struktur Direktori](#️-arsitektur--struktur-direktori)
- [🤝 Kontribusi](#-kontribusi)
  - [Standar Pesan Komit (Conventional Commits)](#standar-pesan-komit-conventional-commits)
  - [Alur Kerja Branching](#alur-kerja-branching)
- [📄 Lisensi](#-lisensi)

---

## 📖 Deskripsi Proyek

### Tujuan & Masalah yang Diselesaikan
Banyak pengguna kesulitan mengelola arus kas pribadi akibat pencatatan manual yang rumit, ketidakjelasan batas anggaran (*over budget* tanpa peringatan), keterlambatan pembayaran tagihan rutin, serta hilangnya bukti pembayaran fisik. 

**MariFin** hadir menyelesaikan masalah tersebut dengan:
1. **Presisi Finansial Nol Kebocoran (*Zero Floating-Point Error*)**: Menggunakan `BigDecimal` di seluruh lapisan perhitungan uang.
2. **Offline-First & Auto-Sync**: Pengguna tetap dapat mencatat mutasi saat offline; data akan disinkronkan otomatis via `WorkManager` ke Supabase PostgREST saat jaringan tersedia.
3. **AI Smart Transaction Parser (FinGPT)**: Mengonversi pesan mutasi bank atau prompt percakapan bebas menjadi draf transaksi terstruktur dengan alur konfirmasi aman (*User Confirmation Mandatory*).
4. **Penyimpanan Struk Terenkripsi**: Fasilitas *Document Receipt Vault* dengan validasi tipe berkas dan fitur *Drag & Drop* / *File Picker*.

### Fitur Utama
* 📊 **Dashboard Arus Kas & Analisis Grafik**: Ringkasan saldo total multi-rekening (*Cash, Bank, E-Wallet*), grafik batang pengeluaran 6 bulan, dan diagram donat proporsi kategori via *MPAndroidChart*.
* 💳 **Multi-Rekening & Kategori Fleksibel**: Pengelolaan rekening finansial mandiri dan kategori kustom maupun bawaan sistem.
* 💸 **Mesin Transaksi Inti**: Pencatatan Pemasukan, Pengeluaran, dan Transfer Antar-Rekening dengan kalkulasi saldo atomik (`@Transaction`).
* 🎯 **Manajemen Anggaran 4-Zona Alert**: 
  - 🟢 **Aman**: `< 70%`
  - 🟡 **Waspada**: `70% – 89%`
  - 🟠 **Kritis**: `90% – 99%`
  - 🔴 **Over Budget**: `≥ 100%` (dengan notifikasi lokal otomatis di background).
* 📅 **Pelacak Tagihan Rutin (*Bills Tracker*)**: Jatuh tempo tagihan berkala dengan fitur pembayaran satu klik yang otomatis mengurangi saldo rekening dan membuat mutasi pengeluaran.
* 🏆 **Target Finansial (*Savings Goals*)**: Monitor persentase impian tabungan, sisa hari tenggat waktu, dan setoran tabungan langsung.
* 📈 **Laporan Keuangan & Ekspor CSV**: Agregasi metrik rasio tabungan (*Savings Rate %*), rata-rata pengeluaran harian, dan fitur ekspor data ke format `.csv`.
* 🗄️ **Gudang Dokumen & Bukti Transaksi (*Vault*)**: Simpan berkas struk, kuitansi, atau faktur (maks. 5MB, JPG/PNG/PDF) dengan antarmuka *Drag & Drop*.
* 🤖 **Asisten AI FinGPT**: Konsultasi strategi finansial, tanya jawab arus kas, serta pembuat draf mutasi otomatis.

---

## ⚙️ Prasyarat

Sebelum memasang dan menjalankan proyek ini, pastikan lingkungan pengembangan Anda telah memenuhi spesifikasi berikut:

* **JDK (Java Development Kit)**: Versi 11 (Amazon Corretto, OpenJDK, atau Oracle JDK 11)
* **Android Studio**: Android Studio Koala / Ladybug atau versi yang lebih baru
* **Android SDK**: 
  - Minimum SDK: `API 26` (Android 8.0 Oreo)
  - Target & Compile SDK: `API 37`
* **Gradle Build Tool**: AGP (Android Gradle Plugin) `8.x`
* **Akun Cloud Supabase**: Proyek Supabase aktif dengan URL & Anon Key publik

---

## 🚀 Instalasi

Ikuti langkah-langkah berikut di terminal untuk memasang dan menyiapkan repositori MariFin:

```bash
# 1. Kloning repositori proyek
git clone https://github.com/hndko/app_marifin_javadroid.git

# 2. Masuk ke direktori proyek
cd app_marifin_javadroid

# 3. Konfigurasi kredensial lokal
# Buat atau sesuaikan berkas local.properties di root folder:
echo "SUPABASE_URL=https://your-project.supabase.co" >> local.properties
echo "SUPABASE_ANON_KEY=your-supabase-anon-key-here" >> local.properties

# 4. Sinkronkan dependensi Gradle & jalankan pengujian unit
./gradlew test

# 5. Build berkas APK Debug
./gradlew assembleDebug
```

> [!TIP]
> Skema database PostgreSQL Supabase lengkap telah tersedia pada berkas [`docs/database/supabase_schema.sql`](file:///c:/Users/Kyoo/AndroidStudioProjects/app_marifin_javadroid/docs/database/supabase_schema.sql). Anda dapat langsung mengeksekusinya di SQL Editor dashboard Supabase Anda.

---

## 💡 Penggunaan

### Menjalankan Aplikasi
1. Buka proyek di **Android Studio**.
2. Sambungkan perangkat fisik via USB Debugging atau jalankan Android Emulator (Pixel API 34+ direkomendasikan).
3. Klik tombol **Run 'app'** (`Shift + F10`) atau jalankan perintah:
```bash
./gradlew installDebug
```

### Pencatatan Transaksi Cerdas via FinGPT
1. Buka menu **FinGPT** melalui tombol pintasan di Beranda (*Home*).
2. Tuliskan pesan transaksi dalam bahasa natural, contoh:
   - *"Beli bensin 50rb di Pertamina kemarin"*
   - *"Makan siang di McD 45.000"*
   - *"Terima transfer gaji 5.5jt"*
3. FinGPT akan mem-parse pesan dan menampilkan kartu **Pratinjau Draf Transaksi**.
4. Klik **[✓] Konfirmasi & Simpan** untuk menyimpan transaksi secara aman ke database.

### Ekspor Laporan Keuangan
1. Masuk ke menu **Profil** $\rightarrow$ pilih **Laporan Keuangan & Ekspor CSV**.
2. Pilih filter rentang waktu: **Mingguan**, **Bulanan**, atau **Tahunan**.
3. Tekan tombol **[↓] Export Laporan (CSV)** untuk membagikan berkas via email, WhatsApp, atau menyimpannya ke penyimpanan lokal perangkat.

---

## 🏛️ Arsitektur & Struktur Direktori

Aplikasi dibangun di atas arsitektur **Clean MVVM** dengan pemisahan dependensi yang modular:

```text
app/src/main/java/com/example/app_marifin_javadroid/
├── core/                   # Fondasi umum, helper, security keystore, & background workers
│   ├── base/               # BaseActivity, BaseFragment, BaseViewModel
│   ├── common/             # Resource<T> state handler (Loading, Success, Error)
│   ├── security/           # SecureSessionManager (EncryptedSharedPreferences)
│   ├── utils/              # CurrencyHelper, DateHelper, CsvExportHelper, Validator
│   └── worker/             # SyncQueueWorker, BudgetCheckWorker
├── data/                   # Lapisan data Room & remote Supabase
│   ├── local/              # Room Entities, DAOs, Type Converters, & AppDatabase
│   ├── remote/             # Supabase Retrofit APIs, DTOs, & Bidirectional Mappers
│   └── repository/         # Implementasi Repository (Koordinasi Cache & Network)
├── domain/                 # Entitas bisnis murni & use cases
│   ├── model/              # ChatMessage, DraftTransaction, FinancialReportData
│   └── usecase/            # CalculateCashFlow, CalculateBudgetUtilization, SmartParser
└── presentation/           # Antarmuka Pengguna (UI)
    ├── auth/               # Login, Register, Forgot Password, Onboarding
    ├── home/               # Beranda, Carousel Rekening, Ringkasan Arus Kas
    ├── expense/            # Analisis Pengeluaran & Grafik MPAndroidChart
    ├── transaction/        # Mutasi Keuangan, Tambah/Edit, & BottomSheet Detail
    ├── budget/             # Daftar Anggaran & Peringatan Batas 4-Zona
    ├── bill/               # Pelacak Tagihan Rutin & Bayar Tagihan
    ├── goal/               # Target Tabungan Finansial & Setoran
    ├── report/             # Laporan Keuangan & Ekspor CSV
    ├── document/           # Gudang Bukti Struk & Upload Drag-Drop
    ├── ai/                 # FinGPT Chatbot & Dialog Konfirmasi Transaksi AI
    └── profile/            # Profil Pengguna, Preferensi, & Pengaturan
```

---

## 🤝 Kontribusi

Kami menyambut kontribusi dari pengembang luar untuk memajukan fitur MariFin. Silakan ikuti aturan standar berikut:

### Standar Pesan Komit (Conventional Commits)
Setiap komit wajib menggunakan format baku:
```text
<tipe>(<ruang-lingkup>): <deskripsi ringkas dalam kalimat perintah huruf kecil>

[deskripsi detail opsional]
```
* **`feat`**: Menambah fitur baru (*contoh: `feat(bill): tambah reminder jatuh tempo`*)
* **`fix`**: Memperbaiki bug (*contoh: `fix(auth): perbaiki handling token kedaluwarsa`*)
* **`refactor`**: Restrukturisasi kode tanpa mengubah fungsionalitas
* **`test`**: Menambah atau memperbarui pengujian unit
* **`perf`**: Peningkatan performa kode atau query database
* **`chore`**: Perubahan konfigurasi build gradle atau dependensi

### Alur Kerja Branching
1. *Fork* repositori ini.
2. Buat *feature branch* baru:
   ```bash
   git checkout -b feat/nama-fitur-baru
   ```
3. Lakukan perubahan kode dan pastikan seluruh unit test lulus:
   ```bash
   ./gradlew test
   ```
4. Lakukan komit dengan format semantik:
   ```bash
   git commit -m "feat(scope): deskripsi fitur"
   ```
5. *Push* ke branch Anda dan buat **Pull Request (PR)** ke branch `master`.

---

## 📄 Lisensi

Proyek ini dilindungi di bawah lisensi **MIT License**. Lihat berkas `LICENSE` untuk rincian hukum lebih lanjut.

```text
Copyright (c) 2026 Mari Partner. All rights reserved.
MariFin - "Kelola Keuangan, Lebih Cerdas."
```
