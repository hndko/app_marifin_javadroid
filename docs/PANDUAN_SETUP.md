# 📘 Panduan Lengkap Setup & Menjalankan Aplikasi MariFin

> **MariFin** (*Kelola Keuangan, Lebih Cerdas*) — Native Android Application (Java 11, Clean Architecture, Room Database, & Supabase Cloud Backend).

Dokumen ini berisi panduan langkah demi langkah dari awal hingga aplikasi berhasil dijalankan (*running*) di emulator atau perangkat fisik Android, dengan penjelasan mendalam mengenai konfigurasi **Cloud Backend Supabase**.

---

## 📑 Daftar Isi

1. [Prasyarat Perangkat & Software](#1-prasyarat-perangkat--software)
2. [Panduan Lengkap Setup Supabase (Cloud Backend)](#2-panduan-lengkap-setup-supabase-cloud-backend)
   - [2.1. Membuat Proyek Baru di Supabase](#21-membuat-proyek-baru-di-supabase)
   - [2.2. Mengambil Kredensial URL & API Anon Key](#22-mengambil-kredensial-url--api-anon-key)
   - [2.3. Konfigurasi Supabase Auth](#23-konfigurasi-supabase-auth)
   - [2.4. Eksekusi Skema Database SQL & RLS Policies](#24-eksekusi-skema-database-sql--rls-policies)
   - [2.5. Konfigurasi Supabase Storage (Receipts Vault)](#25-konfigurasi-supabase-storage-receipts-vault)
3. [Konfigurasi Proyek Android Studio](#3-konfigurasi-proyek-android-studio)
   - [3.1. Kloning Repositori](#31-kloning-repositori)
   - [3.2. Mengatur Berkas local.properties](#32-mengatur-berkas-localproperties)
   - [3.3. Sinkronisasi Gradle](#33-sinkronisasi-gradle)
4. [Menjalankan Aplikasi (Running the App)](#4-menjalankan-aplikasi-running-the-app)
   - [4.1. Menjalankan via Android Studio](#41-menjalankan-via-android-studio)
   - [4.2. Menjalankan via Terminal](#42-menjalankan-via-terminal)
5. [Verifikasi Fitur Utama & Testing](#5-verifikasi-fitur-utama--testing)
6. [Troubleshooting & Solusi Kendala Umum](#6-troubleshooting--solusi-kendala-umum)

---

## 1. Prasyarat Perangkat & Software

Pastikan komputer/laptop Anda telah terpasang perangkat lunak berikut:

| Software / Tool | Versi Minimum | Rekomendasi |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | JDK 11 | OpenJDK 11 / Amazon Corretto 11 |
| **Android Studio** | Koala (2024.1.1) / Ladybug | Versi terbaru dengan Android SDK 34–37 |
| **Android SDK Platform** | API 26 (Android 8.0 Oreo) | API 34+ (Android 14 / 15) |
| **Git** | 2.30+ | Versi terbaru |
| **Akun Supabase** | Gratis (*Free Tier*) | [supabase.com](https://supabase.com) |

---

## 2. Panduan Lengkap Setup Supabase (Cloud Backend)

MariFin menggunakan **Supabase** sebagai cloud backend serverless yang mencakup:
* **Supabase Auth (GoTrue)**: Autentikasi pengguna berbasis JWT Token.
* **PostgreSQL Database**: Penyimpanan data relasional dengan *Row Level Security (RLS)*.
* **PostgREST API**: RESTful API otomatis untuk setiap tabel PostgreSQL.
* **Supabase Storage**: Penyimpanan berkas struk kuitansi dan dokumen transaksi.

### 2.1. Membuat Proyek Baru di Supabase
1. Buka [https://supabase.com](https://supabase.com) dan masuk ke akun Anda.
2. Pada halaman Dashboard, klik tombol **New Project**.
3. Pilih **Organization** Anda.
4. Isi detail proyek:
   - **Name**: `marifin-backend` (atau nama pilihan Anda).
   - **Database Password**: Buat password database yang kuat (catat password ini).
   - **Region**: Pilih region terdekat (misal: `Singapore (ap-southeast-1)`).
   - **Pricing Plan**: `Free Plan`.
5. Klik **Create new project** dan tunggu proses inisialisasi (~1-2 menit).

---

### 2.2. Mengambil Kredensial URL & API Anon Key
Setelah proyek Supabase siap:
1. Di sidebar kiri dashboard Supabase, klik ikon **Project Settings** (ikon roda gigi ⚙️ di kiri bawah).
2. Pilih menu **API**.
3. Cari dan salin dua nilai penting berikut:
   - **Project URL**: Formatnya seperti `https://xxxxxxxxxxxxxxxxxxxx.supabase.co`
   - **Project API Keys** $\rightarrow$ `anon` / `public`: Berupa string panjang berformat JWT token (kunci publik yang aman dipakai di aplikasi mobile).

> [!CAUTION]
> **JANGAN PERNAH** menggunakan atau membagikan `service_role` / `secret` key di aplikasi Android. Hanya gunakan `anon` / `public` key!

---

### 2.3. Konfigurasi Supabase Auth
1. Di sidebar kiri, buka menu **Authentication** $\rightarrow$ **Providers**.
2. Pastikan provider **Email** berstatus **Enabled**.
3. Buka tab **Authentication** $\rightarrow$ **Email Templates / Settings**:
   - Pada opsi *Confirm email*, jika Anda ingin pengguna langsung bisa login setelah registrasi tanpa verifikasi email (cocok untuk testing/development), nonaktifkan toggle **Enable Email Confirmations** (*Confirm email: OFF*).

---

### 2.4. Eksekusi Skema Database SQL & RLS Policies
Seluruh tabel dan aturan keamanan database telah dirancang dan disediakan di repositori pada berkas [`docs/database/supabase_schema.sql`](file:///c:/Users/Kyoo/AndroidStudioProjects/app_marifin_javadroid/docs/database/supabase_schema.sql).

Langkah instalasi skema:
1. Di sidebar kiri dashboard Supabase, buka menu **SQL Editor**.
2. Klik **+ New query**.
3. Buka berkas [`docs/database/supabase_schema.sql`](file:///c:/Users/Kyoo/AndroidStudioProjects/app_marifin_javadroid/docs/database/supabase_schema.sql) dari proyek ini, salin (*copy*) seluruh isinya, lalu tempel (*paste*) ke dalam SQL Editor Supabase.
4. Klik tombol **Run** (atau `Ctrl + Enter`).
5. Pastikan muncul pesan sukses `Success. No rows returned`.

#### Struktur Tabel yang Dibuat oleh Skema:
* `public.profiles`: Profil pengguna (nama lengkap, preferensi mata uang, zona waktu) yang otomatis terbuat via PostgreSQL Trigger saat user registrasi di Supabase Auth.
* `public.accounts`: Rekening finansial (*Cash, Bank, E-Wallet, Kartu Kredit, Investasi*).
* `public.categories`: Kategori transaksi (kategori default sistem & kategori kustom buatan pengguna).
* `public.transactions`: Riwayat transaksi (*Income, Expense, Transfer, Bill*).
* `public.budgets` & `public.budget_categories`: Anggaran bulanan dan relasi multi-kategori.
* `public.bills`: Pelacak tagihan rutin (*due date*, frekuensi perulangan, status bayar).
* `public.financial_goals` & `public.goal_contributions`: Target impian tabungan dan riwayat setoran.
* `public.documents`: Metadata berkas struk dan bukti transfer.

#### Keamanan Row Level Security (RLS):
Skema telah secara otomatis mengaktifkan RLS pada **semua tabel** dengan policy:
```sql
CREATE POLICY "Users can only access their own data"
ON public.transactions FOR ALL
USING (auth.uid() = user_id);
```
Aturan ini menjamin bahwa setiap pengguna hanya dapat membaca, menambah, mengubah, dan menghapus data milik mereka sendiri secara aman.

---

### 2.5. Konfigurasi Supabase Storage (Receipts Vault)
Untuk mendukung penyimpanan berkas struk belanja, kuitansi, dan faktur transaksi:
1. Di sidebar kiri Supabase, buka menu **Storage**.
2. Klik tombol **New bucket** (atau **Create file bucket**).
3. Isi konfigurasi pada modal pembuatan bucket sebagai berikut:
   - **Bucket name**: `documents` *(wajib persis, tidak boleh diubah setelah dibuat)*.
   - **Public bucket**: **OFF / Nonaktifkan** *(PENTING: Bucket wajib berstatus Private agar berkas finansial sensitif pengguna tidak bisa diakses publik tanpa token autentikasi)*.
   - **Restrict file size** *(Opsional tapi Direkomendasikan untuk Proteksi Ganda)*: Aktifkan dan isi `5MB` (atau `5242880` bytes).
   - **Restrict MIME types** *(Opsional tapi Direkomendasikan)*: Aktifkan dan masukkan format aman:
     ```text
     image/jpeg, image/png, application/pdf
     ```
4. Klik tombol **Create** / **Save bucket**.

> [!TIP]
> Aplikasi Android MariFin sudah memvalidasi keamanan berkas di sisi klien (*Client-Side Validation*), namun mengaktifkan pembatasan di Supabase Storage memberikan proteksi ganda (*Server-Side Defense-in-Depth*).

---

## 3. Konfigurasi Proyek Android Studio

### 3.1. Kloning Repositori
Jalankan perintah berikut di terminal / PowerShell:
```bash
git clone https://github.com/hndko/app_marifin_javadroid.git
cd app_marifin_javadroid
```

---

### 3.2. Mengatur Berkas `local.properties`
Buka berkas `local.properties` yang terletak di root direktori proyek (jika belum ada, buat berkas baru bernama `local.properties`).

Tambahkan URL dan Anon Key Supabase yang telah Anda salin pada langkah 2.2:

```properties
sdk.dir=C\:\\Users\\NamaUser\\AppData\\Local\\Android\\Sdk

# Konfigurasi Supabase Cloud Backend MariFin
SUPABASE_URL=https://xxxxxxxxxxxxxxxxxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxxxxxxxxxxxxxxxxx
```

> [!NOTE]
> Berkas `local.properties` diabaikan oleh Git (`.gitignore`), sehingga kunci akses proyek Anda tidak akan pernah bocor ke publik repository.

---

### 3.3. Sinkronisasi Gradle
1. Buka **Android Studio**.
2. Pilih **File $\rightarrow$ Open** $\rightarrow$ arahkan ke folder `app_marifin_javadroid`.
3. Tunggu hingga Android Studio selesai mengunduh dependensi Gradle.
4. Jika tidak otomatis sinkron, klik tombol **Sync Project with Gradle Files** (ikon gajah dengan panah biru di pojok kanan atas toolbar).

---

## 4. Menjalankan Aplikasi (Running the App)

### 4.1. Menjalankan via Android Studio
1. Siapkan perangkat uji:
   - **Emulator**: Buka **Device Manager** di Android Studio, pilih perangkat virtual (misal: *Pixel 8 - API 34*), lalu jalankan emulator.
   - **Perangkat Fisik**: Hubungkan HP Android ke komputer menggunakan kabel USB, aktifkan **Developer Options** dan **USB Debugging**.
2. Di toolbar atas Android Studio, pastikan modul konfigurasi terpilih adalah `app` dan target perangkat Anda sudah terdeteksi.
3. Klik tombol hijau **Run 'app'** (`Shift + F10`).
4. Aplikasi MariFin akan terkompilasi, terpasang, dan otomatis terbuka di layar perangkat.

---

### 4.2. Menjalankan via Terminal
Anda juga dapat melakukan kompilasi dan pemasangan langsung via terminal:

```bash
# Windows (PowerShell)
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

---

## 5. Verifikasi Fitur Utama & Testing

Setelah aplikasi terbuka di layar, Anda dapat menguji alur sistem:

1. **Registrasi Akun Baru**:
   - Di layar autentikasi, pilih tab **Daftar**.
   - Masukkan Nama Lengkap, Email, dan Password (minimal 6 karakter).
   - Tekan **Daftar Akun Baru**. Sistem akan mendaftarkan akun ke Supabase Auth dan otomatis mengarahkan ke halaman Beranda.
2. **Carousel Rekening Finansial**:
   - Buat rekening baru (misal: *BCA - Rp 5.000.000* dan *Dompet Tunai - Rp 500.000*).
3. **Mencatat Transaksi**:
   - Tekan tombol **[+] Catat Transaksi** di navigasi bawah untuk mencatat Pengeluaran, Pemasukan, atau Transfer Antar-Rekening.
4. **Mencoba Asisten FinGPT**:
   - Buka tombol pintasan **FinGPT** di Beranda.
   - Ketik: *"Beli bensin 50rb di Pertamina kemarin"* $\rightarrow$ FinGPT akan mem-parse pesan dan memunculkan dialog pratinjau konfirmasi transaksi.
5. **Menjalankan Unit Test Mandiri**:
   Untuk memverifikasi kebenaran seluruh kalkulasi finansial (`BigDecimal`), ekspor CSV, dan parser:
   ```bash
   .\gradlew.bat test
   ```
   *(Hasil yang diharapkan: `BUILD SUCCESSFUL` dengan 60+ test passed).*

---

## 6. Troubleshooting & Solusi Kendala Umum

### 1. `Error: Could not resolve SUPABASE_URL / BuildConfig`
- **Penyebab**: Variabel `SUPABASE_URL` belum terdefinisi di `local.properties`.
- **Solusi**: Pastikan `local.properties` memuat `SUPABASE_URL` dan `SUPABASE_ANON_KEY`, lalu lakukan **Build $\rightarrow$ Clean Project** dan **Build $\rightarrow$ Rebuild Project**.

### 2. `HTTP 401 Unauthorized saat Registrasi / Login`
- **Penyebab**: String `SUPABASE_ANON_KEY` salah atau terpotong spasi.
- **Solusi**: Periksa kembali Anon Key di menu *Project Settings $\rightarrow$ API* pada dashboard Supabase dan pastikan tidak ada karakter terpotong.

### 3. `Email Not Confirmed Error`
- **Penyebab**: Pengaturan *Confirm Email* di Supabase Auth masih aktif tetapi email belum diverifikasi.
- **Solusi**: Di dashboard Supabase, buka *Authentication $\rightarrow$ Providers $\rightarrow$ Email*, lalu matikan toggle *Confirm email* untuk mode testing.

### 4. `Room Migration Error / Schema Mismatch`
- **Penyebab**: Data lokal Room lama di perangkat emulator bentrok dengan entity baru.
- **Solusi**: Hapus data aplikasi (*Clear Storage*) di menu pengaturan HP/Emulator atau copot pemasangan (*Uninstall*) aplikasi lalu install ulang.

---

**Selamat! Aplikasi MariFin kini siap digunakan untuk mengelola keuangan dengan lebih cerdas! 🚀**
