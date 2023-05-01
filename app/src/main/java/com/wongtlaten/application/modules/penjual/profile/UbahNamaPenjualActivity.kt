package com.wongtlaten.application.modules.penjual.profile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import kotlin.properties.Delegates

class UbahNamaPenjualActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var btnSimpanPerubahan: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_nama_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        etNama = findViewById(R.id.etNama)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)
        checkClick = true

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!

        etNama.setText(nama)

        btnSimpanPerubahan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                // Membuat variabel baru yang berisi inputan user
                val namaInput = etNama.text.toString().trim()

                // Jika namaInput kosong maka akan muncul error harus isi terlebih dahulu
                if (namaInput.isEmpty()){
                    etNama.error = "Masukkan nama terlebih dahulu!"
                    etNama.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika namaInput memiliki inputan symbol maka akan muncul error harus isi terlebih dahulu
                if(namaInput.matches(".*[?=.*/><,!@#$%^&()_=+].*".toRegex())) {
                    etNama.error = "Tidak boleh ada simbol pada nama!"
                    etNama.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika namaInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
                if(namaInput.matches(".*[0-9].*".toRegex())) {
                    etNama.error = "Tidak boleh ada angka pada nama!"
                    etNama.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika namaInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
                if(namaInput.length > 16) {
                    etNama.error = "Nama tidak boleh lebih dari 16 karakter!"
                    etNama.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }

                // Pindah ke UbahDataPribadiPenjualActivity
                Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                    it.putExtra("NAMA", namaInput)
                    it.putExtra("KELAMIN", kelamin)
                    it.putExtra("EMAIL", email)
                    it.putExtra("TELEPON", telepon)
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    finish()
                    checkClick = true
                }
            } else{
                return@setOnClickListener
            }
        }

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val NAMA = "NAMA"
        const val KELAMIN = "KELAMIN"
        const val EMAIL = "EMAIL"
        const val TELEPON = "TELEPON"
    }

    // Fungsi ini digunakan untuk menampilkan dialog peringatan tidak tersambung ke internet,
    // jika tetep tidak connect ke internet maka tetap looping dialog tersebut
    private fun showInternetDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle("PERINGATAN!")
            setMessage("Tidak ada koneksi internet, mohon nyalakan mobile data/wifi anda terlebih dahulu")
            setIcon(R.drawable.ic_alert)
            setCancelable(false)
            setPositiveButton(
                "Coba lagi",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (!isConnected(this@UbahNamaPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahNamaPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}