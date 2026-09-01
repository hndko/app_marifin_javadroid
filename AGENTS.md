# MariFin Engineering Rules & Architecture Guide

> **Single Source of Truth** bagi AI Agent dan Developer dalam pengembangan dan pemeliharaan aplikasi **MariFin** (*Kelola Keuangan, Lebih Cerdas*).

---

## 1. Project Overview

- **Product Name**: MariFin
- **Product Owner**: Mari Partner
- **Tagline**: *"Kelola Keuangan, Lebih Cerdas."*
- **AI Financial Assistant**: FinGPT
- **Platform**: Android Mobile Application (Native)
- **Primary Language**: Java 11
- **UI Framework**: XML Layouts + Material Design 3 Components
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Cloud Backend**: Supabase (PostgreSQL with RLS, Supabase Auth, Supabase Storage, Edge Functions)
- **Local Database**: Android Room Database (Offline Cache & Sync Queue)
- **Background Engine**: Android WorkManager (Offline Sync Worker & Spending Alert Worker)
- **Min SDK**: API 26 (Android 8.0 Oreo) | **Target & Compile SDK**: API 37
- **Latest Stable Version**: `v2.0.0` (versionCode `11`)

---

## 2. Tech Stack & Libraries

| Kategori | Teknologi / Library | Keterangan & Penggunaan |
| :--- | :--- | :--- |
| **Language & Tooling** | Java 11, Android Gradle Plugin (AGP), Version Catalog (`libs.versions.toml`) | Standar kompilasi Android Native |
| **Architecture Components** | `ViewModel`, `LiveData`, `Navigation Component`, `ViewBinding` | Clean MVVM & Lifecycle-aware |
| **Local Persistence** | `Room Database` (SQLite), `EncryptedSharedPreferences` (Android Keystore) | Offline-First Cache & Secure Token Storage |
| **Networking & API** | `Retrofit 2`, `OkHttp 3` (Logging Interceptor), `Gson` | Komunikasi RESTful ke Supabase PostgREST & Auth |
| **Data Visualization** | `MPAndroidChart` (Donut Chart, 6-Month Bar Chart) | Grafik analitik pengeluaran & arus kas |
| **Background Tasks** | `WorkManager` (`SyncQueueWorker`, `BudgetCheckWorker`) | Sinkronisasi antrean offline & notifikasi berkala |
| **Image & Media** | `Glide` | Rendering dokumen & avatar pengguna |
| **Cloud Backend** | Supabase PostgreSQL, Supabase GoTrue Auth, Supabase Storage API | Backend serverless dengan RLS aktif |
| **AI Integration** | Secure AI Proxy & Smart Transaction Parser (FinGPT) | Parsing pesan mutasi & asisten finansial cerdas |
| **Testing** | JUnit 4, AndroidX Test, Espresso, Mockito | Pengujian unit kalkulasi domain & use cases |

---

## 3. Architecture & Layering

Aplikasi mengadopsi prinsip **Clean MVVM (Model-View-ViewModel)** dengan pemisahan dependensi modular:

```text
app/src/main/java/com/example/app_marifin_javadroid/
│
├── core/
│   ├── base/               # BaseActivity, BaseFragment, BaseViewModel
│   ├── common/             # Resource<T> state wrapper (Loading, Success, Error, Empty)
│   ├── security/           # SecureSessionManager (Keystore MasterKey)
│   ├── network/            # NetworkCallback, ConnectivityMonitor
│   ├── utils/              # CurrencyHelper (IDR), DateHelper, DebounceHelper, Validator, CsvExportHelper
│   └── worker/             # SyncQueueWorker (Offline Sync), BudgetCheckWorker (Alerts)
│
├── data/
│   ├── local/
│   │   ├── dao/            # AccountDao, TransactionDao, CategoryDao, BudgetDao, BillDao, GoalDao, DocumentDao, SyncQueueDao
│   │   ├── entity/         # 10 Room Entities (Account, Transaction, Category, Budget, Bill, Goal, Document, etc.)
│   │   ├── converters/     # BigDecimal, Date, UUID Room Type Converters
│   │   ├── model/          # BudgetWithProgress, CategoryExpenseAggregate, MonthlyExpenseAggregate
│   │   └── AppDatabase.java# Room Database singleton & automatic category seeder
│   ├── remote/
│   │   ├── api/            # SupabaseAuthApi, SupabaseDataApi, RetrofitClient
│   │   ├── dto/            # Data Transfer Objects (AccountDto, TransactionDto, BillDto, GoalDto, etc.)
│   │   └── mapper/         # DTO <-> Entity <-> Domain mappers
│   └── repository/         # Repository implementations (Coordinating Room Cache + Supabase Sync)
│
├── domain/
│   ├── model/              # Pure Domain models (ChatMessage, DraftTransaction, FinancialReportData)
│   └── usecase/            # CalculateCashFlow, CalculateBudgetUtilization, CalculateGoalProgress, 
│                           # GenerateFinancialReport, SmartTransactionParser, FinGptAdvisor
│
└── presentation/
    ├── auth/               # Login, Register, ForgotPassword, Onboarding
    ├── home/               # Beranda, Financial Summary, Horizontal Account Carousel, Quick Actions
    ├── transaction/        # Transaction List, Filter, Add/Edit/Detail Transaction BottomSheet
    ├── expense/            # Expense Dashboard, 6-Month Bar Chart, Category Donut Chart
    ├── budget/             # Budget List, Add/Edit Budget Modal, 4-Zone Progress Bar Utilization
    ├── bill/               # Pelacak Tagihan Rutin, Due Reminder, Pay Bill Dialog (Atomic mutation)
    ├── goal/               # Financial Goals, Contribution Tracker, Contribute Dialog (Atomic mutation)
    ├── report/             # Financial Reports (Monthly, Weekly, Yearly), CSV Export via Share Sheet
    ├── document/           # Document Vault, Drag & Drop Upload Component, Secure Preview
    ├── ai/                 # FinGPT Chatbot, Smart NLP Transaction Parser, Draft Preview Confirmation Modal
    ├── profile/            # Profile, Security, Preferences, Feature Shortcuts, Logout
    └── main/               # MainActivity (BottomNavigationView 5 destinasi & worker scheduler)
```

---

## 4. Database Rules & Offline-First Strategy

### 4.1. Local Database (Room)
1. **Background Thread Mandatory**: Seluruh operasi database **WAJIB** dijalankan di background thread (menggunakan `Executors` atau `CompletableFuture`), **DILARANG KERAS** melakukan query Room di UI/Main thread.
2. **Foreign Key & Cascading**: Seluruh relasi foreign key harus dideklarasikan secara eksplisit dengan cascading rules yang aman.
3. **Database Indexing**: Terapkan indeks pada kolom yang sering difilter atau diurutkan: `user_id`, `transaction_date`, `type`, `category_id`, `account_id`, `created_at`.
4. **Atomic Multi-Table Mutation**: Operasi multi-tabel (seperti transfer rekening, pembayaran tagihan, atau setoran target tabungan) **WAJIB** menggunakan anotasi `@Transaction` pada DAO.

### 4.2. Cloud Database (Supabase PostgreSQL)
1. **Row Level Security (RLS)**: Wajib aktif di seluruh tabel publik dengan policy isolasi data pengguna:
   ```sql
   CREATE POLICY "Users can only access their own data"
   ON public.<table_name> FOR ALL
   USING (auth.uid() = user_id);
   ```
2. **Default vs Custom Categories**: Default categories bersifat *read-only* bagi authenticated users (`is_default = true`), sedangkan custom categories hanya dapat diakses dan diubah oleh pembuatnya.
3. **Audit Columns & Triggers**: Seluruh tabel wajib memiliki kolom `created_at` dan `updated_at` yang di-update otomatis oleh trigger PostgreSQL.

### 4.3. Offline-First Sync Queue Worker (`SyncQueueWorker`)
1. Jika koneksi internet terputus saat user menambah/mengubah/menghapus data, mutasi dicatat ke dalam tabel lokal `sync_queue` (`SyncQueueEntity`).
2. `SyncQueueWorker` didaftarkan pada `WorkManager` dengan batasan `NetworkType.CONNECTED`.
3. Ketika perangkat kembali online, worker memproses batch mutasi (maks. 30 item) ke Supabase PostgREST.
4. Item yang berhasil dikirim dihapus dari antrean; item yang gagal memiliki batas toleransi `retry_count <= 5` sebelum di-drop guna mencegah *queue deadlock*.

---

## 5. Financial Domain & Monetary Precision Rules

1. **Anti-Floating Point Corruption**:
   - **DILARANG KERAS** menggunakan tipe data `float` atau `double` untuk nominal uang di Java, Domain, ViewModel, maupun Entity.
   - Gunakan `BigDecimal` di Java/Room dan `NUMERIC(15,2)` / `BIGINT` di database PostgreSQL.
2. **Karakteristik Mutasi Keuangan**:
   - **Pemasukan (Income)**: Menambah saldo rekening (`balance += amount`). Dihitung ke Total Income.
   - **Pengeluaran (Expense)**: Mengurangi saldo rekening (`balance -= amount`). Dihitung ke Total Expense.
   - **Transfer Antar-Rekening**: Mengurangi saldo akun asal dan menambah saldo akun tujuan. **TIDAK BOLEH** dihitung sebagai Income ataupun Expense pada laporan arus kas (*Net Cash Flow*).
   - **Net Cash Flow** = `Total Income - Total Expense`.
3. **Sistem 4-Zona Peringatan Anggaran (Budget 4-Zone Alert)**:
   - 🟢 **Aman (*SAFE*)**: Pengeluaran `< 70%` dari limit.
   - 🟡 **Waspada (*WARNING*)**: Pengeluaran `70% – 89%` dari limit.
   - 🟠 **Kritis (*DANGER*)**: Pengeluaran `90% – 99%` dari limit.
   - 🔴 **Over Budget (*OVER_BUDGET*)**: Pengeluaran `≥ 100%` dari limit. Memicu notifikasi lokal via `BudgetCheckWorker`.

---

## 6. UI/UX & Form Rules

1. **Design Theme**:
   - Warna utama adalah **MariFin Blue** (`#1E56A0` / `#1665D8`), didukung Accent Blue (`#3AB4F2`), Success Green (`#10B981`), Danger Red (`#EF4444`), dan Background Neutral (`#FFFFFF` / `#F8FAFC`).
   - Gunakan Card-based UI dengan rounded corner standar (12dp–16dp) dan elevasi halus.
2. **Form Standards**:
   - Setiap input field **WAJIB** memiliki: **Label + Icon + Placeholder + Error/Validation State**.
   - Input nominal harus memiliki format currency mask otomatis (`Rp 1.000.000`).
3. **Buttons**:
   - Button umum **WAJIB** memiliki: **Icon + Text** (contoh: `[+] Tambah Transaksi`, `[✓] Simpan`).
   - Button aksi di dalam item list / table cukup icon, namun **WAJIB** memiliki `contentDescription` dan `tooltipText`.
4. **Drag & Drop File Upload**:
   - Komponen upload dokumen mendukung **Drag & Drop** dan **Click to Pick**.
   - Menampilkan preview file info (nama, ukuran, progress upload, tombol remove) **di bawah form upload**.
5. **State Handling**:
   - Setiap layar **WAJIB** memiliki 4 state: **Loading (Shimmer/Progress)**, **Empty State (Icon + Pesan ramah + CTA Button)**, **Error State**, dan **Content State**.
6. **Accessibility & Responsive**:
   - Touch target minimal 48dp. Kontras warna memenuhi standar WCAG AA.

---

## 7. Security & Cloud Storage Rules

1. **Zero Secret Leakage**:
   - **DILARANG KERAS** menyimpan API Key AI, Supabase Service Role Key, atau password di source code Android.
   - Kunci publik `SUPABASE_ANON_KEY` dan `SUPABASE_URL` wajib dimuat melalui `BuildConfig` dari `local.properties`.
2. **Secure Token Storage**:
   - Simpan session token Supabase menggunakan `EncryptedSharedPreferences` dengan master key dari Android Keystore (`SecureSessionManager`).
3. **Secure AI Flow (FinGPT)**:
   - Request AI dialirkan melalui parser dan secure proxy.
   - **AI TIDAK BOLEH** langsung menyimpan transaksi ke database tanpa persetujuan user. AI wajib menghasilkan **Draft Transaction Preview** yang harus dikonfirmasi manual oleh user (*Confirm / Edit* via `DraftTransactionPreviewDialog`).
4. **Supabase Storage Rules (Receipts Vault)**:
   - **Bucket Name**: Wajib menggunakan nama `documents`.
   - **Private Bucket**: Toggle `public` wajib **OFF / Nonaktif** guna melindungi kerahasiaan dokumen/rekening koran user.
   - **Dual-Layer Validation**:
     - *Client-Side*: Validasi MIME type (`image/jpeg`, `image/png`, `application/pdf`), batas ukuran maks 5MB (`5242880` bytes), dan sanitasi nama berkas di `DocumentRepository`.
     - *Server-Side*: Konfigurasi `Restrict file size = 5MB` dan `Restrict MIME types` pada Supabase Storage.

---

## 8. Documentation Standards

1. **README.md Standar**:
   - Judul Heading 1 (`#`) dengan deskripsi satu kalimat dan *badges* resmi.
   - Daftar Isi (*Table of Contents*) dengan anchor links.
   - Penjelasan Masalah & Fitur Utama.
   - Prasyarat, Instalasi langkah-demi-langkah (blok terminal), dan Panduan Penggunaan.
   - Pohon direktori arsitektur Clean MVVM.
   - Panduan Kontribusi (*Conventional Commits*) dan Lisensi MIT.
2. **Setup Guides (`docs/PANDUAN_SETUP.md`)**:
   - Panduan konfigurasi Supabase (Auth, DDL Skema, RLS, Storage Bucket, local.properties).
   - Skema database tersimpan di [`docs/database/supabase_schema.sql`](file:///c:/Users/Kyoo/AndroidStudioProjects/app_marifin_javadroid/docs/database/supabase_schema.sql).
   - Aset banner tersimpan di `docs/assets/`.

---

## 9. Git Rules & Semantic Commit Messages

Setiap kali menyelesaikan suatu task, sub-task, atau phase yang diminta oleh user, agen **WAJIB** melakukan update versioning (jika berlaku) lalu menjalankan **Git Commit & Push** secara otomatis dengan format **Semantic Commit Messages (Conventional Commits)**.

### 9.1. Format Commit
```text
<type>(<scope>): <short description in imperative mood, lowercase, <= 50 chars>

[optional longer body explaining context and rationale]

[optional footer(s), e.g. BREAKING CHANGE: description]
```

### 9.2. Daftar Tipe Utama
- `feat`: Menambah fitur baru aplikasi (*contoh: `feat(ai-fingpt): implementasikan smart transaction parser`*).
- `fix`: Memperbaiki bug atau kesalahan kode (*contoh: `fix(auth): perbaiki error 500 saat token kedaluwarsa`*).
- `docs`: Mengubah atau menambah dokumentasi saja (*contoh: `docs(guide): tambah panduan setup supabase`*).
- `style`: Mengubah format kode tanpa mengubah logika (*contoh: `style(nav): rapikan padding bottom navigation`*).
- `refactor`: Mengubah kode untuk peningkatan struktur internal (*contoh: `refactor(account): pisahkan kalkulasi saldo ke usecase`*).
- `perf`: Mengubah kode khusus untuk meningkatkan performa (*contoh: `perf(report): optimasikan query aggregasi pengeluaran`*).
- `test`: Menambah atau memperbaiki kode pengujian (*contoh: `test(sync): tambah unit test sync queue entity`*).
- `chore`: Mengubah konfigurasi build tool atau dependensi (*contoh: `chore(deps): tambah workmanager runtime`*).

---

## 10. Semantic Versioning (SemVer) & App Version Bump

Setiap perilisan fitur atau pembaruan yang mengubah kode aplikasi, nomor versi pada `app/build.gradle` (dan version catalog jika relevan) **WAJIB** dinaikkan secara terstruktur mengikuti format:

$$\text{MAJOR}.\text{MINOR}.\text{PATCH}$$

1. **MAJOR (X.0.0)**: Perubahan arsitektur besar / rilis stabil utama (*Breaking Changes* / Rilis Versi 2.0.0).
2. **MINOR (x.Y.0)**: Penambahan fitur baru yang tetap kompatibel dengan versi sebelumnya (`feat`).
3. **PATCH (x.y.Z)**: Perbaikan bug (`fix`), dokumentasi (`docs`), atau refactor kecil.

---

## 11. Development Workflow (Step-by-Step)

```text
1. Terima Instruksi Phase/Task dari User
   ↓
2. Tampilkan Objective, Requirement, Database & File Changes Plan
   ↓
3. Minta / Tunggu Approval (jika berpindah phase utama)
   ↓
4. Implementasikan Kode (Sesuai Clean Architecture & Rules)
   ↓
5. Jalankan Unit Test & Build Verification (.\gradlew.bat test)
   ↓
6. Naikkan Versioning Aplikasi (SemVer di app/build.gradle)
   ↓
7. Lakukan Semantic Git Commit & Push (git add . -> git commit -m "..." -> git push)
   ↓
8. Laporkan Hasil Pengerjaan (Implementation Report)
```

---

## 12. Definition of Done (DoD)

Sebuah task/phase hanya dinyatakan **SELESAI** jika memenuhi kriteria:

- [ ] Seluruh requirement pada task tersebut terimplementasi dengan benar.
- [ ] Struktur folder dan kode mematuhi Clean MVVM dan kaidah Java 11.
- [ ] Desain UI/XML mematuhi Material Design 3 dan palet warna resmi MariFin.
- [ ] State Loading, Empty State, Error State, dan Validation tersedia.
- [ ] Kalkulasi finansial presisi tanpa floating-point bug (`BigDecimal`).
- [ ] Unit Test untuk logika domain/utility telah dibuat dan lulus (*Passed*).
- [ ] Keamanan (RLS, API Key isolation, Input sanitization, Private Storage Bucket) telah terverifikasi.
- [ ] Versi aplikasi (`versionCode` dan `versionName`) telah diperbarui sesuai SemVer.
- [ ] Kode telah di-commit dengan Semantic Commit Message dan di-push ke remote repository.
