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
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.UbahTeleponPembeliActivity
import kotlin.properties.Delegates

class UbahTeleponPenjualActivity : AppCompatActivity() {

    private lateinit var etTelepon: EditText
    private lateinit var btnSimpanPerubahan: Button
    private lateinit var telepon : String
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_telepon_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        etTelepon = findViewById(R.id.etTelepon)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)
        checkClick = true

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        telepon = intent.getStringExtra(TELEPON)!!

        if (telepon == "belum diisi"){
            telepon = "0"
        }

        etTelepon.setText(telepon)

        btnSimpanPerubahan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                // Membuat variabel baru yang berisi inputan user
                val teleponInput = etTelepon.text.toString().trim()

                // Jika teleponInput kosong maka akan muncul error harus isi terlebih dahulu
                if (teleponInput.isEmpty()){
                    etTelepon.error = "Masukkan nomor telepon terlebih dahulu!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika teleponInput memiliki inputan symbol maka akan muncul error harus isi terlebih dahulu
                if(teleponInput.matches(".*[?=.*/><,!@#$%^&()_=+].*".toRegex())) {
                    etTelepon.error = "Tidak boleh ada simbol pada nomor telepon!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
                if(teleponInput.matches(".*[a-z].*".toRegex())) {
                    etTelepon.error = "Tidak boleh ada huruf pada nomor telepon!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
                if(teleponInput.length < 10) {
                    etTelepon.error = "Masukkan nomor telepon yang valid!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
                if(teleponInput.length > 13) {
                    etTelepon.error = "Masukkan nomor telepon yang valid!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }
                if (teleponInput.substring(0,2) != "08"){
                    etTelepon.error = "Masukkan nomor telepon yang valid!"
                    etTelepon.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }

                // Pindah ke UbahDataPribadiPenjualActivity
                Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                    it.putExtra("NAMA", nama)
                    it.putExtra("KELAMIN", kelamin)
                    it.putExtra("EMAIL", email)
                    it.putExtra("TELEPON", teleponInput)
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
                    if (!isConnected(this@UbahTeleponPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahTeleponPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}