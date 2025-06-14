# Cara Import Database `pos_java` di phpMyAdmin

Ikuti langkah berikut untuk membuat database dan mengimpor file SQL menggunakan phpMyAdmin.

---

## 1. Buka phpMyAdmin

- Pastikan XAMPP atau Laragon sudah dijalankan, dan Apache serta MySQL dalam keadaan aktif.
- Buka browser, lalu akses alamat berikut:  
  `http://localhost/phpmyadmin`

---

## 2. Buat Database Baru

- Klik menu **New** di sidebar kiri untuk membuat database baru.
- Masukkan nama database: `pos_java`
- Pilih collation `utf8_general_ci` (bawaan/default).
- Klik tombol **Create** untuk membuat database.

---

## 3. Import File SQL

- Klik database `pos_java` yang sudah dibuat pada sidebar kiri.
- Pilih tab **Import** di bagian atas.
- Klik tombol **Choose File**, lalu cari dan pilih file SQL yang ingin diimpor, misalnya `pos_java.sql`.
- Pastikan format file yang dipilih adalah **SQL**.
- Klik tombol **Go** untuk mulai proses import.

---

## 4. Tambahkan Nama Aplikasi & Logo

- Setelah import berhasil, buka database `pos_java`.
- Klik tabel `settings`.
- Pilih tab **Insert**.
- Isi kolom-kolom sebagai berikut:

  | Field            | Value                                  |
  |------------------|----------------------------------------|
  | name_application | POS Java                               |
  | logo             | logo.png *(atau nama file logo kamu)*  |
  | created_at       | *(boleh dikosongkan jika otomatis)*     |
  | updated_at       | *(boleh dikosongkan jika otomatis)*     |

- Klik **Go** untuk menyimpan data.

---

## 5. Jalankan Aplikasi

- Buka aplikasi POS Java yang sudah kamu punya.
- Saat login, gunakan username dan password berikut:  
  - **Username:** admin  
  - **Password:** admin#123

---

## 6. Selamat!

- Jika berhasil masuk, aplikasi akan berjalan dan menampilkan nama serta logo yang sudah kamu masukkan.
- Aplikasi siap digunakan.

---

## Butuh Bantuan?

Jika ada kendala atau kesulitan, silakan kontak WA developer:  
**0895-3239-5552**
