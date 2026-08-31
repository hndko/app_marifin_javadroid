# MariFin Engineering Rules & Architecture Guide

> **Single Source of Truth** bagi AI Agent dan Developer dalam pengembangan aplikasi **MariFin** (*Kelola Keuangan, Lebih Cerdas*).

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
- **Min SDK**: API 26 (Android 8.0 Oreo) | **Target & Compile SDK**: API 37

---

## 2. Tech Stack & Libraries

| Kategori | Teknologi / Library |
| :--- | :--- |
| **Language & Tooling** | Java 11, Android Gradle Plugin (AGP), Version Catalog (`libs.versions.toml`) |
| **Architecture Components** | `ViewModel`, `LiveData`, `Navigation Component`, `ViewBinding` |
| **Local Persistence** | `Room Database` (SQLite), `EncryptedSharedPreferences` (Android Keystore) |
| **Networking & API** | `Retrofit 2`, `OkHttp 3` (Logging Interceptor), `Gson` |
| **Data Visualization** | `MPAndroidChart` (Donut, Pie, Bar, Line Charts) |
| **Background Tasks** | `WorkManager` (Offline synchronization & scheduled checks) |
| **Image & Media** | `Glide` |
| **Cloud Backend** | Supabase PostgreSQL, Supabase GoTrue Auth, Supabase Storage API |
| **AI Integration** | Secure AI Proxy via Supabase Edge Function (FinGPT) |
| **Testing** | JUnit 4, AndroidX Test, Espresso, Mockito |

---

## 3. Architecture & Layering

Aplikasi mengadopsi prinsip **Clean MVVM (Model-View-ViewModel)** dengan pemisahan tanggung jawab yang ketat:

```
app/src/main/java/com/example/app_marifin_javadroid/
│
├── core/
│   ├── base/               # BaseActivity, BaseFragment, BaseViewModel
│   ├── common/             # Resource<T> state wrapper (Loading, Success, Error, Empty)
│   ├── security/           # SecureSessionManager, Keystore helpers
│   ├── network/            # NetworkCallback, ConnectivityMonitor
│   └── utils/              # CurrencyHelper (IDR), DateHelper, DebounceHelper, Validator
│
├── data/
│   ├── local/
│   │   ├── dao/            # AccountDao, TransactionDao, CategoryDao, BudgetDao, BillDao, GoalDao
│   │   ├── entity/         # AccountEntity, TransactionEntity, CategoryEntity, etc.
│   │   ├── converters/     # BigDecimal, Date, UUID Room converters
│   │   └── AppDatabase.java# Room Database singleton
│   ├── remote/
│   │   ├── api/            # Supabase API services (Auth, PostgREST, Storage, Functions)
│   │   ├── dto/            # Data Transfer Objects
│   │   └── mapper/         # DTO <-> Entity <-> Domain mappers
│   └── repository/         # Repository implementations (Coordinating Room + Supabase)
│
├── domain/
│   ├── model/              # Pure Domain models (Account, Transaction, Budget, Bill, Goal, Category)
│   ├── repository/         # Domain Repository interfaces
│   └── usecase/            # Business logic (CalculateCashFlow, ExecuteTransfer, CheckBudgetAlert)
│
└── presentation/
    ├── auth/               # Login, Register, ForgotPassword, Onboarding
    ├── home/               # Beranda, Financial Summary, Horizontal Account Carousel
    ├── transaction/        # Transaction List, Filter, Add/Edit/Detail Transaction
    ├── expense/            # Expense Dashboard, 6-Month Bar Chart, Category Donut Chart
    ├── budget/             # Budget List, Add/Edit Budget, Progress Bar Utilization
    ├── bill/               # Tagihan / Bills List, Due Reminder, Pay Bill
    ├── goal/               # Financial Goals, Contribution Tracker
    ├── report/             # Financial Reports (Monthly, Weekly, Yearly, Custom tabs)
    ├── document/           # Document List, Drag & Drop Upload Component, Viewer
    ├── ai/                 # FinGPT Chat, AI Transaction Parser Dialog (Draft Preview & Confirm)
    └── profile/            # Profile, Security, Currency/Timezone Preferences, Logout
```

---

## 4. Database Rules

### 4.1. Local Database (Room)
1. Seluruh operasi database **WAJIB** dijalankan di background thread (menggunakan `Executors` atau `CompletableFuture`), **DILARANG** melakukan query Room di UI/Main thread.
2. Seluruh foreign key harus dideklarasikan secara eksplisit dengan cascading rules yang aman.
3. Terapkan indeks pada kolom yang sering difilter atau diurutkan: `user_id`, `transaction_date`, `type`, `category_id`, `account_id`.
4. Operasi multi-tabel (seperti transfer rekening yang mengurangi saldo sumber dan menambah saldo tujuan) **WAJIB** menggunakan anotasi `@Transaction`.

### 4.2. Cloud Database (Supabase PostgreSQL)
1. **Row Level Security (RLS)** wajib aktif di seluruh tabel dengan policy: `auth.uid() = user_id`.
2. Default categories bersifat *read-only* bagi authenticated users (`is_default = true`), sedangkan custom categories hanya dapat diakses oleh pembuatnya.
3. Seluruh tabel wajib memiliki kolom audit: `created_at` dan `updated_at`.

---

## 5. Financial Domain & Monetary Precision Rules

1. **Anti-Floating Point Corruption**:
   - **DILARANG KERAS** menggunakan `float` atau `double` untuk nominal uang.
   - Gunakan `BigDecimal` di Java/Domain/Entity dan `NUMERIC(15,2)` / `BIGINT` di database PostgreSQL & SQLite.
2. **Kategori & Karakteristik Transaksi**:
   - **Pemasukan (Income)**: Menambah saldo akun (`balance += amount`). Dihitung ke Total Income.
   - **Pengeluaran (Expense)**: Mengurangi saldo akun (`balance -= amount`). Dihitung ke Total Expense.
   - **Transfer**: Mengurangi saldo akun asal dan menambah saldo akun tujuan. **TIDAK BOLEH** dihitung sebagai Income ataupun Expense pada laporan arus kas (*Net Cash Flow*).
   - **Net Cash Flow** = `Total Income - Total Expense`.
3. **Idempotency & Concurrency**:
   - Setiap operasi transfer memiliki `transfer_group_id` / `reference_id` unik untuk mencegah mutasi ganda.

---

## 6. UI/UX & Form Rules

1. **Design Theme**:
   - Warna utama adalah **MariFin Blue** (`#1E56A0` / `#1665D8`), didukung Accent Blue (`#3AB4F2`), Success Green (`#10B981`), Danger Red (`#EF4444`), dan Card Background White/Neutral (`#FFFFFF` / `#F8FAFC`).
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

## 7. Security Rules

1. **Zero Secret Leakage**:
   - **DILARANG KERAS** menyimpan API Key AI, Supabase Service Role Key, atau password di source code Android.
   - Kunci publik / anon key dimuat melalui `BuildConfig` dari `local.properties`.
2. **Secure Token Storage**:
   - Simpan session token Supabase menggunakan `EncryptedSharedPreferences` dengan master key dari Android Keystore.
3. **Secure AI Flow (FinGPT)**:
   - Request AI dialirkan melalui secure backend / Edge Function proxy.
   - **AI TIDAK BOLEH** langsung menyimpan transaksi ke database tanpa persetujuan user. AI wajib menghasilkan **Draft Transaction Preview** yang harus dikonfirmasi manual oleh user (*Confirm / Edit*).
4. **File Validation**:
   - Validasi MIME type, ukuran file maksimal (5MB), dan sanitasi nama file sebelum diunggah ke storage.

---

## 8. Performance Rules

1. **Debounce Mechanism**:
   - Seluruh live search, filter text, dan autocomplete input wajib menggunakan debounce (300–500ms).
2. **Pagination & Lazy Loading**:
   - Transaction list dan report lists wajib menerapkan pagination (batch 20–30 item) untuk menghemat memori.
3. **Database Indexing & Optimized Aggregation**:
   - Penghitungan total dan aggregasi bulanan dilakukan via database aggregation SQL, bukan me-loop ribuan objek di Java.

---

## 9. Testing & Code Quality Rules

1. **Unit Testing**:
   - Seluruh logika kalkulasi finansial (`CurrencyHelper`, `DateHelper`, `CalculateCashFlow`, `BudgetUtilization`) **WAJIB** memiliki Unit Test komprehensif (Happy path, Zero value, Negative value, Large amount).
2. **Clean Code**:
   - Terapkan prinsip Single Responsibility. Activity/Fragment hanya mengurus rendering UI dan delegasi event ke ViewModel.
   - Tidak ada duplikasi kode logika bisnis, tidak ada magic string/number (gunakan Enum dan Constants).

---

## 10. Git Rules & Semantic Commit Messages

Setiap kali menyelesaikan suatu task, sub-task, atau phase yang diminta oleh user, agen **WAJIB** melakukan update versioning (jika berlaku) lalu menjalankan **Git Commit & Push** secara otomatis dengan format **Semantic Commit Messages (Conventional Commits)**.

### 10.1. Format Commit
```text
<type>(<scope>): <short description in imperative mood, lowercase, <= 50 chars>

[optional longer body explaining context and rationale]

[optional footer(s), e.g. BREAKING CHANGE: description]
```

### 10.2. Daftar Tipe Utama
- `feat`: Menambah fitur baru aplikasi.
  - *Contoh*: `feat(auth): tambah validasi nomor telepon saat registrasi`
- `fix`: Memperbaiki bug atau kesalahan kode.
  - *Contoh*: `fix(api): perbaiki error 500 saat token kedaluwarsa`
- `docs`: Mengubah atau menambah dokumentasi saja (`README.md`, `AGENTS.md`, PRD).
  - *Contoh*: `docs(agents): buat engineering rules dan panduan semver`
- `style`: Mengubah format kode tanpa mengubah logika (spasi, indentasi, resource XML layout cleanup).
  - *Contoh*: `style(nav): rapikan padding dan icon bottom navigation`
- `refactor`: Mengubah kode untuk peningkatan struktur internal (bukan perbaikan bug atau fitur baru).
  - *Contoh*: `refactor(account): pisahkan kalkulasi saldo ke usecase`
- `perf`: Mengubah kode khusus untuk meningkatkan performa.
  - *Contoh*: `perf(report): optimasikan query aggregasi pengeluaran`
- `test`: Menambah atau memperbaiki kode pengujian (Unit / Instrumented Test).
  - *Contoh*: `test(transaction): tambah skenario uji transfer antar rekening`
- `chore`: Mengubah konfigurasi build tool, Gradle dependencies, atau environment.
  - *Contoh*: `chore(deps): tambah room dan navigation component`
- `ci`: Mengubah berkas konfigurasi CI/CD workflows.
  - *Contoh*: `ci(build): tambah tahap verifikasi build otomatis`

### 10.3. Aturan Penulisan
- **Huruf kecil**: Tipe dan deskripsi singkat wajib huruf kecil.
- **Kalimat perintah/singkat**: Gunakan kata kerja imperatif (`tambah`, `perbaiki`, `optimasi`, bukan `menambahkan` / `diperbaiki`).
- **Batas karakter**: Baris judul tidak lebih dari 50–72 karakter.
- **Breaking Change**: Gunakan tanda seru `!` setelah tipe/scope (contoh: `feat(database)!: ubah skema relasi akun`) atau cantumkan `BREAKING CHANGE:` di footer.

---

## 11. Semantic Versioning (SemVer) & App Version Bump

Setiap perilisan fitur atau pembaruan yang mengubah kode aplikasi, nomor versi pada `app/build.gradle` (dan version catalog jika relevan) **WAJIB** dinaikkan secara terstruktur mengikuti format:

$$\text{MAJOR}.\text{MINOR}.\text{PATCH}$$

### 11.1. Aturan Penomoran
1. **MAJOR (X.0.0)**:
   - Naikkan angka MAJOR ketika ada perubahan arsitektur besar atau perubahan skema/API yang merusak kompatibilitas lama (*Breaking Change*).
   - `versionCode` bertambah `+1`, `versionName` berubah misal `1.0.0` $\rightarrow$ `2.0.0`.
2. **MINOR (x.Y.0)**:
   - Naikkan angka MINOR ketika menambah fitur baru yang tetap kompatibel dengan versi sebelumnya (`feat`).
   - `versionCode` bertambah `+1`, `versionName` berubah misal `1.0.0` $\rightarrow$ `1.1.0`.
3. **PATCH (x.y.Z)**:
   - Naikkan angka PATCH ketika hanya memperbaiki bug (`fix`), perbaikan performa kecil (`perf`), atau perbaikan formatting/dokumentasi/refactor kecil.
   - `versionCode` bertambah `+1`, `versionName` berubah misal `1.1.0` $\rightarrow$ `1.1.1`.

### 11.2. Lokasi Konfigurasi Versi
Di [app/build.gradle](file:///c:/Users/Kyoo/AndroidStudioProjects/app_marifin_javadroid/app/build.gradle):
```groovy
defaultConfig {
    applicationId "com.example.app_marifin_javadroid"
    minSdk 26
    targetSdk 37
    versionCode 1       // Naikkan +1 pada setiap perubahan rilis
    versionName "1.0.0" // Format SemVer MAJOR.MINOR.PATCH
}
```

---

## 12. Development Workflow (Step-by-Step)

```
1. Terima Instruksi Phase/Task dari User
   ↓
2. Tampilkan Objective, Requirement, Database & File Changes Plan
   ↓
3. Minta / Tunggu Approval (jika berpindah phase utama)
   ↓
4. Implementasikan Kode (Sesuai Clean Architecture & Rules)
   ↓
5. Jalankan Unit Test & Build Verification
   ↓
6. Naikkan Versioning Aplikasi (SemVer di app/build.gradle)
   ↓
7. Lakukan Semantic Git Commit & Push (git add . -> git commit -m "..." -> git push)
   ↓
8. Laporkan Hasil Pengerjaan (Implementation Report)
```

---

## 13. Do & Don't

### DO:
- ✅ Selalu gunakan `BigDecimal` untuk operasi nominal uang.
- ✅ Pastikan setiap form memiliki label, icon, placeholder, dan validasi jelas.
- ✅ Sediakan Empty State yang ramah dan informatif dengan CTA button.
- ✅ Jalankan query database di background thread.
- ✅ Mintalah konfirmasi user sebelum menyimpan data transaksi yang di-generate AI.
- ✅ Gunakan Semantic Commit Messages dan naikkan SemVer secara konsisten.

### DON'T:
- ❌ JANGAN gunakan `float` atau `double` untuk nominal uang.
- ❌ JANGAN hardcode API Key rahasia di source code Android.
- ❌ JANGAN biarkan AI menyimpan transaksi langsung tanpa konfirmasi preview.
- ❌ JANGAN masukkan logika bisnis berat di dalam Activity / Fragment.
- ❌ JANGAN hitung transfer rekening sebagai pemasukan atau pengeluaran.
- ❌ JANGAN melakukan push atau commit dengan pesan sembarangan tanpa semantic format.

---

## 14. Definition of Done (DoD)

Sebuah task/phase hanya dinyatakan **SELESAI** jika memenuhi kriteria:

- [ ] Seluruh requirement pada task tersebut terimplementasi dengan benar.
- [ ] Struktur folder dan kode mematuhi Clean MVVM dan kaidah Java 11.
- [ ] Desain UI/XML mematuhi Material Design 3 dan palet warna resmi MariFin.
- [ ] State Loading, Empty State, Error State, dan Validation tersedia.
- [ ] Kalkulasi finansial presisi tanpa floating-point bug.
- [ ] Unit Test untuk logika domain/utility telah dibuat dan lulus (*Passed*).
- [ ] Keamanan (RLS, API Key isolation, Input sanitization) telah terverifikasi.
- [ ] Versi aplikasi (`versionCode` dan `versionName`) telah diperbarui sesuai SemVer.
- [ ] Kode telah di-commit dengan Semantic Commit Message dan di-push ke remote repository.
