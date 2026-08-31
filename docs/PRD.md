# PRODUCT REQUIREMENT DOCUMENT (PRD)

# MariFin
## Personal Finance Management & AI Financial Assistant

**Product Owner:** Mari Partner  
**Product Name:** MariFin  
**Product Type:** Mobile Application  
**Platform:** Android  
**Primary Language:** Java  
**UI:** XML + Material Design  
**Backend:** Supabase  
**Database:** PostgreSQL  
**Local Database:** Room  
**Architecture:** MVVM + Repository Pattern  
**Authentication:** Supabase Auth  
**Storage:** Supabase Storage  
**AI:** AI Financial Assistant  
**Minimum Android:** Android 8.0+ (API 26)  
**Target:** Production-ready application

---

# 1. PRODUCT OVERVIEW

MariFin adalah aplikasi mobile personal finance yang dikembangkan oleh Mari Partner untuk membantu pengguna:

- mencatat pemasukan
- mencatat pengeluaran
- mencatat transfer antar rekening
- memantau saldo
- mengelola rekening/akun keuangan
- mengelola kategori transaksi
- membuat budget
- mengelola tagihan
- membuat target finansial
- melihat laporan keuangan
- mengunggah dokumen/bukti transaksi
- mencatat transaksi menggunakan AI
- mendapatkan insight keuangan menggunakan AI

Aplikasi harus memiliki pengalaman pengguna yang sederhana, modern, ringan, dan mudah digunakan oleh pengguna awam.

Inspirasi utama UI adalah aplikasi financial tracker modern dengan dominasi warna biru, card-based interface, rounded corners, bottom navigation, financial dashboard, dan AI assistant.

---

# 2. BRANDING

## Brand

Mari Partner

## Product

MariFin

## Tagline

"Kelola Keuangan, Lebih Cerdas."

## AI Assistant

FinGPT

FinGPT merupakan asisten AI di dalam MariFin yang membantu pengguna:

- mencatat transaksi
- memahami kondisi keuangan
- membuat budget
- memberikan insight
- memberikan rekomendasi pengelolaan keuangan
- menjelaskan laporan keuangan dengan bahasa sederhana

AI tidak boleh memberikan klaim sebagai penasihat keuangan profesional.

---

# 3. PRODUCT GOALS

Tujuan utama aplikasi:

1. Membuat pencatatan keuangan menjadi mudah.
2. Membantu pengguna mengetahui kondisi keuangan secara real-time.
3. Membantu pengguna mengontrol pengeluaran.
4. Membantu pengguna membuat dan menjaga budget.
5. Membantu pengguna memahami pola pengeluaran.
6. Mengurangi pekerjaan manual menggunakan AI.
7. Menyediakan laporan keuangan yang mudah dipahami.
8. Menyediakan sistem yang aman untuk menyimpan data finansial pengguna.

---

# 4. TARGET USER

Target utama:

- pekerja
- mahasiswa
- freelancer
- entrepreneur
- keluarga
- pengguna yang ingin mengatur keuangan pribadi
- pengguna yang memiliki beberapa rekening bank/e-wallet

Persona utama:

### Persona 1 — Young Professional

Memiliki beberapa rekening:

- Blu
- SeaBank
- BRI
- Mandiri
- e-wallet

Membutuhkan:

- tracking saldo
- tracking pengeluaran
- budget
- laporan bulanan

### Persona 2 — Freelancer

Memiliki pemasukan tidak tetap.

Membutuhkan:

- tracking income
- expense tracking
- cash flow
- financial goals

### Persona 3 — Family User

Memiliki pengeluaran rutin:

- listrik
- internet
- cicilan
- kebutuhan rumah
- pendidikan

Membutuhkan:

- budget
- tagihan
- kategori
- laporan

---

# 5. CORE NAVIGATION

Gunakan Bottom Navigation dengan 5 menu:

1. Beranda
2. Pengeluaran
3. Tombol tambah transaksi
4. Budget
5. Transaksi

Tombol tambah transaksi berada di tengah dan dibuat lebih menonjol.

---

# 6. INFORMATION ARCHITECTURE

Struktur aplikasi:

Authentication
│
├── Login
├── Register
├── Forgot Password
└── Onboarding

Main
│
├── Beranda
│
├── Pengeluaran
│
├── Budget
│
├── Transaksi
│
└── Tambah Transaksi

Financial
│
├── Total Keuangan
├── Laporan Keuangan
├── Rekening
├── Kategori
├── Budget
├── Tagihan
└── Target Finansial

AI
│
├── FinGPT
├── AI Transaction Input
└── AI Financial Insight

Documents
│
├── Upload Document
├── Document List
└── Document Detail

Profile
│
├── Profile
├── Security
├── Notification
├── Appearance
└── Logout

---

# 7. AUTHENTICATION

Gunakan Supabase Auth.

Authentication methods:

- Email
- Password
- Google Sign-In jika tersedia dan dikonfigurasi

## Register

Field:

- nama
- email
- password
- konfirmasi password

Validation:

- email valid
- password minimal 8 karakter
- password confirmation harus sama

## Login

Field:

- email
- password

Fitur:

- remember session
- logout
- forgot password

## Security

Jangan pernah menyimpan:

- password
- access token secara plaintext
- secret key Supabase di repository

Gunakan secure storage Android untuk credential/session yang diperlukan.

---

# 8. ONBOARDING

Setelah registrasi pertama kali:

### Step 1

"Selamat datang di MariFin"

### Step 2

"Catat transaksi dengan mudah"

### Step 3

"Pantau pengeluaran dan budget"

### Step 4

"Biarkan FinGPT membantu"

### Step 5

Setup rekening pertama.

User dapat:

- skip
- tambah rekening

---

# 9. HOME / BERANDA

Home adalah halaman utama aplikasi.

## Header

Tampilkan:

"👋 Hai, {nama}"

Profile icon di kanan.

## Date Range

Default:

25 Agu - 24 Sep 2026

Namun tanggal harus dinamis berdasarkan periode berjalan.

User dapat mengubah periode.

Pilihan:

- minggu ini
- bulan ini
- bulan lalu
- custom range

---

# 10. FINANCIAL SUMMARY

Tampilkan dua mode:

### Sisa Keuangan

Menampilkan:

- total pemasukan
- total pengeluaran
- saldo tersisa

### Total Saldo

Menampilkan total saldo dari seluruh rekening.

Contoh:

Rp1.683.296

User dapat tap untuk melihat detail rekening.

---

# 11. ACCOUNT CAROUSEL

Tampilkan rekening dalam horizontal carousel.

Contoh:

- Blu by BCA Digital
- SeaBank
- BRI Mobile
- Mandiri
- m-BCA
- DANA
- Lainnya

Setiap account:

- logo/icon
- nama
- saldo

Contoh:

Blu by BCA Digital
Rp1.551.555

SeaBank
Rp162.241

---

# 12. ACCOUNT MANAGEMENT

User dapat:

- tambah rekening
- edit rekening
- hapus rekening
- archive rekening
- melihat detail rekening

Field:

- nama rekening
- institusi
- tipe rekening
- nomor rekening opsional
- saldo awal
- mata uang
- icon/logo

Account type:

- Bank
- E-Wallet
- Cash
- Credit Card
- Investment
- Other

---

# 13. TRANSACTION SYSTEM

Jenis transaksi:

1. Pengeluaran
2. Pemasukan
3. Transfer
4. Tagihan

---

# 14. EXPENSE TRANSACTION

Field:

- amount
- account
- category
- date
- merchant
- description
- attachment
- note

Contoh:

Pengeluaran

Rp50.000

Blu

Makanan & minuman

Warung Padang

01 Sep 2026

---

# 15. INCOME TRANSACTION

Field:

- amount
- account
- category
- date
- source
- description

Contoh:

Pemasukan

Rp5.000.000

Blu

Gaji

---

# 16. TRANSFER

Transfer tidak boleh dihitung sebagai pemasukan/pengeluaran.

Contoh:

Blu
Rp500.000

→

SeaBank

Sistem harus membuat pasangan transaksi:

OUT:
Blu -500.000

IN:
SeaBank +500.000

Gunakan transaction group/reference ID untuk menghubungkan kedua transaksi.

---

# 17. BILL PAYMENT

Tagihan dapat memiliki:

- nama tagihan
- nominal
- tanggal jatuh tempo
- rekening pembayaran
- kategori
- status
- recurring setting

Status:

- upcoming
- due
- paid
- overdue

---

# 18. CATEGORY SYSTEM

Default categories:

## Kebutuhan Sehari-hari

- Makanan & minuman
- Transportasi

## Kebutuhan Rumah Tangga

- Kebutuhan rumah
- Tagihan & utilitas
- Binatang peliharaan
- Keluarga

## Kebutuhan Pribadi

- Shopping
- Kesehatan & olahraga
- Donasi & hadiah
- Travel
- Hiburan
- Edukasi

## Finansial & Keuangan

- Pinjaman
- Investasi
- Bisnis

User dapat:

- membuat kategori
- edit kategori
- delete category
- favorite category
- membuat subcategory

Kategori memiliki:

- name
- icon
- color
- type
- parent_id
- user_id
- is_default
- is_active

---

# 19. TRANSACTION LIST

Tampilkan transaksi berdasarkan tanggal.

Contoh:

SELASA 04 AGUSTUS 2026

Pemasukan
Blu by BCA Digital
Simpanan

+Rp1.551.555

User dapat:

- search
- filter
- sort
- edit
- delete
- melihat detail

Filter:

- date
- type
- category
- account
- amount range

---

# 20. EXPENSE DASHBOARD

Halaman Pengeluaran.

Tampilkan:

- total pengeluaran
- grafik pengeluaran per bulan
- rata-rata pengeluaran
- kategori terbesar
- merchant terbesar

Contoh:

Total pengeluaran

Rp5.050.000

Grafik:

April
Mei
Juni
Juli
Agustus
September

Gunakan chart yang ringan dan mudah dibaca.

---

# 21. CATEGORY ANALYTICS

Tampilkan donut/pie chart:

Pengeluaran Agustus 2026

Rp5.050.000

Kategori:

Makanan & minuman
30%

Transportasi
15%

Shopping
25%

Tagihan
20%

Lainnya
10%

User dapat tap setiap kategori untuk melihat transaksi.

---

# 22. FINANCIAL REPORT

Halaman:

"Laporan Keuangan"

Mode:

- Bulanan
- Mingguan
- Tahunan
- Custom

Tampilkan:

- total pemasukan
- total pengeluaran
- net cash flow
- saldo
- grafik pemasukan
- grafik pengeluaran
- kategori pengeluaran
- akun
- merchant

Tabs:

1. Transaksi
2. Budget
3. Tagihan
4. Target

---

# 23. TOTAL FINANCIAL PAGE

Halaman:

"Total Keuanganmu"

Tampilkan:

Keuangan hari ini

Rp1.683.296

Tabs:

- Semua
- Saldo
- Investasi
- Hutang

Donut chart:

Alokasi Keuangan

Contoh:

Blu 91%
SeaBank 9%

List:

Blu by BCA Digital
Rp1.551.555
91%

SeaBank
Rp162.241
9%

BRI Mobile
Rp0
0%

---

# 24. BUDGET SYSTEM

User dapat membuat budget.

Budget period:

- weekly
- monthly
- yearly
- custom

Budget dapat dibuat berdasarkan:

- kategori
- akun
- total expense

Contoh:

Budget Makanan

Rp1.500.000

Terpakai:

Rp850.000

Sisa:

Rp650.000

Progress:

56.7%

Status:

- Safe
- Warning
- Over Budget

---

# 25. BUDGET ALERT

Threshold:

50%
75%
90%
100%

Contoh:

"Pengeluaran Makanan sudah mencapai 90% dari budget."

"Budget Transportasi hampir habis."

---

# 26. FINANCIAL GOALS

User dapat membuat target:

Contoh:

"Tabungan Laptop"

Target:

Rp10.000.000

Terkumpul:

Rp3.500.000

Progress:

35%

Field:

- name
- target_amount
- current_amount
- deadline
- account
- description

---

# 27. AI — FinGPT

FinGPT merupakan AI assistant.

Fungsi:

### Transaction Input

User dapat mengetik:

"Beli nasi goreng 25 ribu tadi malam."

AI harus mengubahnya menjadi structured data:

type:
expense

amount:
25000

category:
Makanan & minuman

date:
relative date

merchant:
Nasi goreng

User harus melakukan confirmation sebelum transaksi disimpan.

---

# 28. AI TRANSACTION FLOW

Flow:

User input
↓
AI processing
↓
Structured transaction
↓
Preview
↓
User confirmation
↓
Save transaction

AI TIDAK boleh langsung menyimpan transaksi tanpa confirmation user.

---

# 29. AI RECEIPT/OCR

User dapat upload:

- foto struk
- PDF
- screenshot transaksi
- dokumen

AI/OCR membaca:

- merchant
- amount
- date
- items
- category

Kemudian menampilkan preview.

User dapat mengoreksi hasil.

Baru setelah confirmation:

Save transaction.

---

# 30. AI FINANCIAL INSIGHT

FinGPT dapat memberikan insight:

Contoh:

"Pengeluaran makanan bulan ini naik 20% dibanding bulan sebelumnya."

"Budget hiburan sudah mencapai 82%."

"Saldo cash kamu meningkat dibanding bulan lalu."

AI harus selalu menggunakan data aktual dari database.

Jangan membuat angka fiktif.

---

# 31. AI FINANCIAL CHAT

User dapat bertanya:

"Berapa pengeluaran saya bulan ini?"

"Kategori apa yang paling boros?"

"Apakah budget saya aman?"

"Berapa rata-rata pengeluaran saya?"

"Bagaimana cara menghemat pengeluaran?"

AI harus memberikan jawaban berdasarkan data user jika pertanyaannya berkaitan dengan data finansial.

---

# 32. AI SAFETY

FinGPT bukan penasihat investasi profesional.

Jika user bertanya tentang:

- investasi
- pinjaman
- keputusan finansial berisiko

AI harus memberikan informasi umum dan disclaimer yang sesuai.

AI tidak boleh:

- menjamin keuntungan
- memberikan janji investasi
- menyuruh mengambil hutang tanpa konteks
- membuat keputusan finansial atas nama user

---

# 33. DOCUMENT MANAGEMENT

User dapat upload:

- struk
- invoice
- bukti transfer
- laporan bank
- dokumen finansial

Storage:

Supabase Storage.

Metadata:

- file_name
- file_url/path
- file_type
- size
- transaction_id
- uploaded_at
- user_id

---

# 34. SEARCH

Global transaction search:

Search:

- merchant
- category
- description
- amount
- account

Search harus debounce agar tidak melakukan query berlebihan.

---

# 35. NOTIFICATION

Notification untuk:

- budget hampir habis
- budget over
- tagihan mendekati jatuh tempo
- tagihan terlambat
- target finansial
- financial insight

Gunakan Android notification system.

Jika diperlukan backend scheduled notification, gunakan Supabase Edge Functions atau service backend yang sesuai.

---

# 36. DATABASE ARCHITECTURE

Gunakan PostgreSQL melalui Supabase.

Tables:

profiles

financial_accounts

categories

transactions

transaction_transfers

budgets

budget_categories

bills

financial_goals

goal_contributions

documents

notifications

ai_conversations

ai_messages

ai_transaction_drafts

user_preferences

audit_logs

---

# 37. DATABASE — PROFILES

profiles:

- id UUID PRIMARY KEY
- full_name
- avatar_url
- phone
- currency
- timezone
- onboarding_completed
- created_at
- updated_at

id harus berhubungan dengan auth.users.id.

---

# 38. DATABASE — FINANCIAL ACCOUNTS

financial_accounts:

- id UUID
- user_id UUID
- name
- institution_name
- account_type
- account_number_masked
- currency
- initial_balance
- current_balance
- icon_url
- is_active
- created_at
- updated_at

---

# 39. DATABASE — CATEGORIES

categories:

- id UUID
- user_id UUID nullable
- parent_id UUID nullable
- name
- icon
- color
- type
- is_default
- is_favorite
- is_active
- created_at
- updated_at

Default categories harus dapat digunakan semua user.

Custom category hanya dapat digunakan pemiliknya.

---

# 40. DATABASE — TRANSACTIONS

transactions:

- id UUID
- user_id UUID
- account_id UUID
- category_id UUID nullable
- type
- amount
- currency
- merchant
- description
- transaction_date
- source
- attachment_count
- transfer_group_id nullable
- created_at
- updated_at
- deleted_at nullable

Type:

expense
income
transfer_in
transfer_out
bill

Amount harus menggunakan numeric/decimal, jangan floating point.

---

# 41. DATABASE — BUDGET

budgets:

- id UUID
- user_id UUID
- name
- amount
- period_type
- start_date
- end_date
- alert_threshold
- is_active
- created_at
- updated_at

budget_categories:

- budget_id
- category_id
- allocated_amount

---

# 42. DATABASE — BILLS

bills:

- id UUID
- user_id UUID
- name
- amount
- category_id
- account_id
- due_date
- recurrence
- status
- created_at
- updated_at

---

# 43. DATABASE — FINANCIAL GOALS

financial_goals:

- id UUID
- user_id UUID
- name
- target_amount
- current_amount
- deadline
- account_id
- status
- created_at
- updated_at

goal_contributions:

- id UUID
- goal_id
- amount
- transaction_id
- contribution_date

---

# 44. DATABASE — DOCUMENTS

documents:

- id UUID
- user_id UUID
- transaction_id nullable
- storage_path
- original_name
- mime_type
- file_size
- document_type
- created_at

---

# 45. DATABASE SECURITY

WAJIB menggunakan Supabase Row Level Security.

Setiap user hanya boleh:

SELECT data miliknya.

INSERT data dengan user_id miliknya.

UPDATE data miliknya.

DELETE data miliknya.

Jangan mengandalkan filtering user_id dari Android saja.

Semua security harus enforced di PostgreSQL RLS.

Default categories dapat dibaca semua authenticated users tetapi tidak dapat dimodifikasi user.

Documents hanya dapat diakses oleh pemilik.

Storage bucket juga harus memiliki policy yang sesuai.

---

# 46. ANDROID ARCHITECTURE

Gunakan:

MVVM

Struktur:

presentation
│
├── auth
├── home
├── transaction
├── expense
├── budget
├── report
├── account
├── category
├── bill
├── goal
├── ai
└── profile

data
│
├── remote
│
├── local
├── repository
└── model

domain
│
├── model
└── usecase

core
│
├── utils
├── security
├── network
├── database
└── ui

---

# 47. ROOM

Gunakan Room sebagai local cache.

Entity minimal:

AccountEntity

CategoryEntity

TransactionEntity

BudgetEntity

BillEntity

GoalEntity

DocumentEntity

Gunakan Room untuk:

- caching
- offline viewing
- draft transaction
- optimistic UI jika memungkinkan

---

# 48. SYNC STRATEGY

Supabase:

Source of truth.

Room:

Local cache.

Saat online:

Supabase
↓
Repository
↓
Room
↓
UI

Saat offline:

UI
↓
Room

Untuk transaksi offline:

buat local pending transaction.

Saat online:

sync ke Supabase.

Gunakan WorkManager untuk background synchronization.

---

# 49. NETWORKING

Gunakan networking layer yang clean.

Semua API request harus:

- authenticated
- timeout
- error handling
- retry terbatas
- logging hanya pada debug build

Jangan log:

- password
- token
- financial sensitive data

---

# 50. UI DESIGN SYSTEM

Gunakan desain yang terinspirasi screenshot referensi.

Primary color:

Blue

Gunakan satu primary blue yang konsisten.

UI:

- rounded cards
- clean white background
- blue header
- subtle shadows
- generous spacing
- Material components
- readable typography

Bottom navigation:

Beranda
Pengeluaran
+
Budget
Transaksi

---

# 51. UI PRINCIPLES

Prioritas:

1. Readability
2. Simplicity
3. Consistency
4. Accessibility
5. Performance

Jangan membuat UI terlalu padat.

Gunakan empty state yang informatif.

Gunakan loading skeleton jika diperlukan.

Gunakan shimmer hanya jika memang diperlukan.

---

# 52. EMPTY STATES

Contoh:

Belum ada transaksi.

"Kamu belum punya transaksi."

CTA:

"Catat Transaksi"

Jika belum ada budget:

"Kamu belum punya kategori budget."

CTA:

"Bikin Budget"

Jika belum ada rekening:

"Tambahkan rekening pertamamu."

CTA:

"Tambah Rekening"

---

# 53. ERROR HANDLING

Semua error harus memiliki user-friendly message.

Contoh:

Network error:

"Koneksi internet bermasalah. Coba lagi."

Authentication:

"Email atau password salah."

Database:

"Data belum berhasil disimpan. Coba lagi."

Upload:

"Dokumen gagal diunggah."

AI:

"FinGPT sedang mengalami kendala. Coba beberapa saat lagi."

Jangan menampilkan raw exception kepada user.

---

# 54. PERFORMANCE

Target:

Cold start < 3 detik pada device mid-range.

UI scrolling harus smooth.

Jangan melakukan database query berat di main thread.

Gunakan:

- Coroutines jika menggunakan Kotlin tidak diperbolehkan karena project menggunakan Java, gunakan Executor/LiveData/CompletableFuture sesuai kebutuhan.
- background executor
- pagination
- caching
- lazy loading

Jangan load seluruh transaksi sekaligus.

---

# 55. SECURITY REQUIREMENTS

WAJIB:

- HTTPS
- Supabase Auth
- RLS
- secure token/session storage
- Android Keystore bila diperlukan
- input validation
- output validation
- file type validation
- upload size limit
- database constraints

Jangan hardcode:

- Supabase service_role key
- AI secret key
- private credentials

Service role key tidak boleh berada di aplikasi Android.

---

# 56. AI SECURITY

AI API key tidak boleh disimpan langsung di Android.

Architecture:

Android
↓
Secure backend / Edge Function
↓
AI Provider
↓
Response
↓
Android

Supabase Edge Functions dapat digunakan sebagai proxy AI.

---

# 57. ANALYTICS

Track event secara privacy-conscious.

Contoh event:

app_open

transaction_created

transaction_updated

transaction_deleted

budget_created

budget_exceeded

ai_transaction_started

ai_transaction_confirmed

document_uploaded

goal_created

Jangan mengirim nominal finansial mentah ke third-party analytics kecuali benar-benar diperlukan dan telah mendapat consent.

---

# 58. ACCESSIBILITY

Pastikan:

- text readable
- touch target minimal 48dp
- sufficient contrast
- content description untuk icon
- jangan menggunakan warna saja sebagai indikator
- screen reader friendly

---

# 59. LOCALIZATION

Versi pertama:

Bahasa Indonesia.

Architecture harus memungkinkan English di masa depan.

Currency default:

IDR

Format:

Rp1.683.296

Tanggal mengikuti locale Indonesia.

---

# 60. DATE & TIME

Gunakan timezone user.

Jangan hardcode timezone.

Simpan timestamp dalam format yang aman.

Untuk laporan berdasarkan tanggal:

gunakan timezone user ketika menentukan boundary hari/bulan.

---

# 61. TRANSACTION CALCULATION RULES

Pemasukan:

balance += amount

Pengeluaran:

balance -= amount

Transfer:

source balance -= amount

destination balance += amount

Transfer tidak boleh masuk:

income total

expense total

Net cash flow:

income - expense

---

# 62. FINANCIAL SUMMARY

Untuk periode:

total_income
=
SUM(income)

total_expense
=
SUM(expense)

net_cash_flow
=
total_income - total_expense

Current balance:

SUM(active account balances)

---

# 63. REPORT QUERIES

Gunakan PostgreSQL aggregation.

Contoh kebutuhan:

- monthly income
- monthly expense
- expense by category
- expense by account
- expense by merchant
- average expense
- largest transaction
- monthly comparison
- budget utilization

Jangan mengambil seluruh transaksi ke Android lalu menghitung semuanya di client jika dapat dilakukan secara efisien di PostgreSQL.

---

# 64. DATABASE INDEXING

Index minimal:

transactions(user_id)

transactions(user_id, transaction_date)

transactions(user_id, type)

transactions(user_id, category_id)

transactions(user_id, account_id)

budgets(user_id)

bills(user_id, due_date)

financial_accounts(user_id)

documents(user_id)

Pastikan index tidak berlebihan.

---

# 65. USER FLOW — RECORD TRANSACTION

User membuka tombol +

↓

Pilih:

Pengeluaran / Pemasukan / Transfer / Tagihan

↓

Pilih tanggal

↓

Pilih rekening

↓

Masukkan nominal

↓

Pilih kategori

↓

Merchant/deskripsi

↓

Optional attachment

↓

Review

↓

Simpan

↓

Update local database

↓

Sync Supabase

↓

Update dashboard

---

# 66. USER FLOW — AI TRANSACTION

User tap:

"Catat transaksi pakai AI"

↓

Input:

"Beli kopi 25 ribu di Starbucks"

↓

AI processing

↓

Preview:

Pengeluaran
Rp25.000

Kategori:
Makanan & minuman

Merchant:
Starbucks

Tanggal:
Hari ini

↓

User:

Edit / Confirm

↓

Save

---

# 67. USER FLOW — BUDGET

User membuka Budget

↓

Bikin Budget

↓

Pilih kategori

↓

Masukkan nominal

↓

Pilih periode

↓

Pilih alert

↓

Save

↓

Budget aktif

↓

Transaction masuk

↓

Budget utilization dihitung

↓

Jika threshold tercapai:

Notification

---

# 68. USER FLOW — DOCUMENT

User:

Upload Dokumen

↓

Select file/camera

↓

Validate file

↓

Upload Supabase Storage

↓

Create document metadata

↓

Optional OCR

↓

Extract transaction data

↓

Preview

↓

Confirm

↓

Create transaction

---

# 69. PROFILE

Profile screen:

- avatar
- nama
- email
- currency
- timezone
- notification settings
- security
- appearance
- about
- privacy policy
- terms
- logout

---

# 70. SETTINGS

Settings:

### Appearance

- System
- Light
- Dark

### Currency

Default:

IDR

### Notifications

- Budget
- Bills
- Goals
- Insights

### Security

- biometric lock jika tersedia
- session management

---

# 71. PRODUCTION ENVIRONMENT

Gunakan environment separation:

Development

Staging

Production

Jangan menggunakan database production untuk development.

Configuration harus dipisahkan.

---

# 72. BUILD TYPES

Minimal:

debug

release

Release harus:

- minified jika aman
- signed
- no debug logging
- production Supabase configuration
- production AI endpoint

---

# 73. TESTING

WAJIB melakukan:

## Unit Test

Test:

- transaction calculation
- budget calculation
- financial summary
- transfer calculation
- date range
- category aggregation

## Integration Test

Test:

- login
- register
- transaction creation
- transaction update
- transaction deletion
- budget creation
- account creation
- sync

## UI Test

Test:

- navigation
- login
- add transaction
- budget
- report
- AI confirmation

---

# 74. SECURITY TESTING

Test:

- user A cannot access user B data
- RLS policies
- unauthorized API request
- invalid UUID
- invalid amount
- negative amount
- malformed input
- oversized upload
- unsupported file type

---

# 75. ACCEPTANCE CRITERIA

Aplikasi dianggap MVP selesai apabila:

[ ] User dapat register

[ ] User dapat login

[ ] User dapat logout

[ ] User dapat membuat rekening

[ ] User dapat membuat kategori

[ ] User dapat mencatat pemasukan

[ ] User dapat mencatat pengeluaran

[ ] User dapat mencatat transfer

[ ] User dapat melihat transaksi

[ ] User dapat edit transaksi

[ ] User dapat delete transaksi

[ ] Saldo otomatis terupdate

[ ] User dapat membuat budget

[ ] Budget utilization berjalan

[ ] User dapat melihat laporan

[ ] User dapat melihat total keuangan

[ ] User dapat membuat target finansial

[ ] User dapat membuat tagihan

[ ] User dapat upload dokumen

[ ] AI transaction input berjalan

[ ] AI selalu meminta confirmation

[ ] RLS aktif

[ ] Room cache berjalan

[ ] Offline state ditangani

[ ] Error handling tersedia

[ ] Unit test tersedia

[ ] Integration test tersedia

[ ] Release build berhasil

---

# 76. DEVELOPMENT PHASE

## Phase 1 — Foundation

- Android project
- architecture
- design system
- Supabase
- Auth
- database
- RLS
- Room

## Phase 2 — Core Finance

- accounts
- categories
- transactions
- transfer
- balance calculation

## Phase 3 — Dashboard

- home
- financial summary
- expense dashboard
- reports

## Phase 4 — Budget

- budget
- utilization
- alerts

## Phase 5 — Financial Planning

- bills
- financial goals

## Phase 6 — Documents

- upload
- storage
- OCR preparation

## Phase 7 — AI

- FinGPT
- AI transaction input
- AI insight
- AI receipt extraction

## Phase 8 — Offline & Sync

- Room cache
- pending transaction
- WorkManager
- synchronization

## Phase 9 — Testing

- unit
- integration
- UI
- security
- performance

## Phase 10 — Production Readiness

- release configuration
- security review
- database migration
- logging
- crash monitoring
- final QA

---

# 77. IMPORTANT IMPLEMENTATION RULES

1. Jangan membuat dummy data sebagai data utama.

2. Semua financial data harus berasal dari Supabase/Room.

3. Jangan hardcode saldo.

4. Jangan hardcode laporan.

5. Jangan menghitung saldo secara manual dari UI.

6. Semua financial calculation harus berada di repository/domain layer atau database query yang terkontrol.

7. Gunakan Decimal/Numeric untuk monetary value.

8. Jangan menggunakan floating point untuk uang.

9. Jangan menyimpan secret key di Android.

10. Gunakan RLS.

11. Jangan membuat AI langsung menyimpan transaksi.

12. AI harus menghasilkan draft terlebih dahulu.

13. User harus melakukan confirmation.

14. Semua operasi database harus memiliki error handling.

15. Semua screen harus memiliki loading, success, empty, dan error state.

16. Jangan melakukan network request di main thread.

17. Jangan mengambil seluruh transaksi jika hanya membutuhkan aggregation.

18. Gunakan pagination untuk transaction list.

19. UI harus responsive untuk berbagai ukuran layar Android.

20. Jangan mengubah struktur database tanpa migration yang jelas.

---

# 78. DEFINITION OF DONE

Sebuah fitur dianggap selesai apabila:

- UI selesai
- ViewModel selesai
- Repository selesai
- database integration selesai
- loading state tersedia
- empty state tersedia
- error state tersedia
- validation tersedia
- security diperiksa
- RLS diperiksa jika menyangkut Supabase
- unit test tersedia jika terdapat business logic
- tidak ada hardcoded production credential
- tidak ada crash pada happy path
- tidak ada data leakage antar user
- build berhasil

---

# 79. FINAL PRODUCTION CHECKLIST

## Android

[ ] Debug logging dihapus dari release

[ ] App signing configured

[ ] Proguard/R8 diperiksa

[ ] Network security diperiksa

[ ] Permission diperiksa

[ ] APK/AAB release berhasil

## Supabase

[ ] Production project

[ ] Database migrations

[ ] RLS aktif

[ ] Storage policies

[ ] Auth configuration

[ ] Backup strategy

[ ] Database indexes

## AI

[ ] AI key tidak berada di Android

[ ] AI endpoint secure

[ ] Prompt injection mitigation

[ ] User confirmation

[ ] Rate limiting

[ ] Error fallback

## Security

[ ] Authentication

[ ] Authorization

[ ] RLS

[ ] Input validation

[ ] File validation

[ ] Secure storage

## QA

[ ] Unit test

[ ] Integration test

[ ] UI test

[ ] Offline test

[ ] Network failure test

[ ] Security test

[ ] Performance test

---

# 80. FINAL INSTRUCTION TO AI CODING AGENT

Bangun aplikasi MariFin berdasarkan PRD ini.

Jangan hanya membuat mockup UI.

Implementasikan:

1. Android Java project
2. XML UI
3. MVVM
4. Repository Pattern
5. Room
6. Supabase
7. PostgreSQL
8. Supabase Auth
9. Supabase Storage
10. RLS
11. Transaction system
12. Account system
13. Category system
14. Budget system
15. Bill system
16. Financial goals
17. Reports
18. AI transaction system
19. Document upload
20. Offline cache
21. Synchronization
22. Error handling
23. Validation
24. Unit testing
25. Integration testing
26. UI testing

Gunakan referensi screenshot yang diberikan sebagai inspirasi visual.

Jangan menyalin branding, logo, asset proprietary, atau identitas visual aplikasi lain.

Pertahankan konsep:

- blue financial dashboard
- clean white cards
- rounded UI
- modern financial application
- bottom navigation
- prominent add transaction button
- AI assistant
- financial analytics

Prioritaskan:

Security > Data Integrity > Correctness > Performance > UI polish.

Setelah implementasi selesai, lakukan:

1. Build project.
2. Fix compilation errors.
3. Fix runtime errors.
4. Run tests.
5. Check database schema.
6. Check RLS.
7. Check authentication.
8. Check transaction calculations.
9. Check balance calculations.
10. Check budget calculations.
11. Check offline behavior.
12. Check synchronization.
13. Check AI confirmation flow.
14. Check document upload.
15. Check UI consistency.
16. Perform production-readiness review.

Jangan mengklaim fitur sudah selesai jika belum benar-benar diimplementasikan dan diuji.

Jika ada bagian yang belum dapat diimplementasikan karena membutuhkan credential/API eksternal, buat abstraction/interface yang siap digunakan dan dokumentasikan konfigurasi yang diperlukan tanpa memasukkan secret ke source code.

Target akhir:

Aplikasi Android Java "MariFin" yang production-ready secara teknis, aman, scalable, dan memiliki pengalaman pengguna modern untuk personal finance management.