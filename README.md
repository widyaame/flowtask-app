# TaskFlow

TaskFlow adalah sistem manajemen tugas berbasis desktop yang dibangun menggunakan Java Swing. Aplikasi ini dirancang untuk membantu pengguna mengelola tugas harian mereka dengan efisien melalui antarmuka pengguna yang modern dan intuitif.

## Fitur Utama

*   **Autentikasi Pengguna**: Sistem login dan registrasi yang aman untuk melindungi data pengguna.
*   **Dasbor Interaktif**: Tampilan ringkasan yang memberikan akses cepat ke fungsi utama melalui navigasi sidebar.
*   **Manajemen Tugas**:
    *   Membuat, membaca, memperbarui, dan menghapus tugas (CRUD).
    *   Status tugas (Pending, Progress, Done).
    *   Dukungan untuk judul, deskripsi, penerima tugas, dan tanggal pembuatan.
*   **Penyaringan Data**: Kemampuan untuk menyaring tugas berdasarkan status.
*   **Pengaturan Akun**: Fitur untuk mengubah kata sandi pengguna demi keamanan akun.
*   **Antarmuka Modern**: Desain datar (flat design) dengan komponen UI kustom, input membulat, dan tata letak responsif.

## Teknologi

Aplikasi ini dikembangkan menggunakan teknologi berikut:

*   **Bahasa Pemrograman**: Java (JDK 21)
*   **Antarmuka Pengguna**: Java Swing (AWT/Swing)
*   **Penyimpanan Data**: Format CSV (untuk data pengguna dan tugas)

## Persyaratan Sistem

*   Java Development Kit (JDK) versi 21 atau yang lebih baru.
*   Sistem operasi yang mendukung Java (Windows, macOS, Linux).

## Cara Menjalankan Aplikasi

Ikuti langkah-langkah berikut untuk mengompilasi dan menjalankan aplikasi:

1.  **Kompilasi Kode Program**
    Jalankan skrip berikut untuk mengompilasi kode sumber ke dalam direktori `bin`:
    ```bash
    ./build.sh
    ```

2.  **Menjalankan Aplikasi**
    Setelah kompilasi berhasil, jalankan aplikasi dengan perintah:
    ```bash
    ./run.sh
    ```

## Struktur Proyek

*   `src/app`: Titik masuk aplikasi (`MainApp.java`).
*   `src/model`: Definisi objek data (`User`, `Task`).
*   `src/service`: Logika bisnis dan manajemen data (`AuthService`, `TaskFileManager`).
*   `src/ui`: Komponen antarmuka pengguna (`LoginFrame`, `DashboardFrame`, `TaskListFrame`, `SettingsPanel`).
*   `src/util`: Utilitas dan konstanta UI (`UIConstants`, `RoundedButtonUI`).

---
Hak Cipta 2024 TaskFlow.
