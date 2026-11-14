# 📱 Database Perbenihan — Aplikasi Android (APK)

Aplikasi Android berupa *wrapper* untuk Dashboard Web App **Database Perbenihan**
(Google Apps Script). Semua data, login, dan hak akses tetap berjalan di server
Spreadsheet + Drive seperti biasa — tidak ada yang perlu diubah di Apps Script.

## Fitur
- ✅ Splash screen logo benih + nama aplikasi
- ✅ Login per bagian tetap berfungsi (sesi 24 jam, cookie dikelola otomatis)
- ✅ Tombol **Unggah** di dashboard → memilih file dari penyimpanan HP
- ✅ Tombol **Unduh** → file tersimpan ke folder **Download** HP dengan notifikasi
- ✅ Tombol **kembali** di HP → mundur 1 halaman web
- ✅ Layar **offline/error** dengan tombol "Coba Lagi"
- ✅ Ikon aplikasi adaptif (rumpun benih, warna hijau Kementan)

> Catatan: aplikasi butuh Android 8.0 (API 26) ke atas.

## 🔨 Cara membuat APK (tanpa install apa pun di komputer)

1. **Buat repository GitHub** (mis. `database-perbenihan-apk`), publik atau privat.
2. **Upload seluruh isi folder ini** ke repository tersebut (via web GitHub:
   *Add file → Upload files* — pastikan struktur folder ikut, termasuk
   `.github/workflows/build-apk.yml`).
3. Buka tab **Actions** di repository → workflow **Build APK** akan berjalan
   otomatis pada setiap push (atau klik *Run workflow* untuk manual).
4. Setelah selesai (±3–5 menit, ikon ✓ hijau), klik run tersebut → scroll ke
   bawah ke bagian **Artifacts** → unduh **Database-Perbenihan-APK**.
5. Ekstrak file ZIP → dapatkan `app-debug.apk`.
6. Kirim/instal di HP:
   - Salin `app-debug.apk` ke HP (WhatsApp, email, kabel data, Google Drive).
   - Buka file di HP → jika muncul "instal aplikasi tidak dikenal",
     izinkan **Instal aplikasi dari sumber ini** → **Instal**.
   - Buka aplikasi **Database Perbenihan** → login seperti di web.

## 🔄 Mengubah URL Web App
URL dashboard tertulis di:
`app/src/main/java/id/go/pertanian/perbenihan/MainActivity.java`
(bagian atas, konstanta `URL_APLIKASI`).
Ganti URL-nya, commit/push, dan APK baru otomatis di-build ulang.

## 🚀 Naikkan versi aplikasi
Saat ada perubahan, naikkan di `app/build.gradle`:
```gradle
versionCode 2     // naikkan 1 setiap rilis baru
versionName "1.1" // nomor versi yang terlihat
```

## 📁 Struktur project
```
apk-perbenihan/
├── .github/workflows/build-apk.yml   ← workflow build otomatis di GitHub
├── settings.gradle
├── build.gradle
├── gradle.properties
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/id/go/pertanian/perbenihan/MainActivity.java
        └── res/  (layout, tema, ikon, teks)
```
