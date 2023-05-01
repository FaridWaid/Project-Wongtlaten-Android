package com.wongtlaten.application.modules.pembeli.profile

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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.LoadingDialog
import kotlin.properties.Delegates

class UbahAlamatPembeliActivity : AppCompatActivity() {

    private lateinit var etAlamat: EditText
    private lateinit var btnSimpanPerubahan: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_alamat_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        etAlamat = findViewById(R.id.etAlamat)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)
        checkClick = true

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!
        val alamat = intent.getStringExtra(ALAMAT)!!

        etAlamat.setText(alamat)

        btnSimpanPerubahan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                // Membuat variabel baru yang berisi inputan user
                val alamatInput = etAlamat.text.toString().trim()

                // Jika alamatInput kosong maka akan muncul error harus isi terlebih dahulu
                if (alamatInput.isEmpty()){
                    etAlamat.error = "Masukkan alamat terlebih dahulu!"
                    etAlamat.requestFocus()
                    checkClick = true
                    return@setOnClickListener
                }

                // Pindah ke UbahDataPribadiPembeliActivity
                Intent(applicationContext, UbahDataPribadiPembeliActivity::class.java).also {
                    it.putExtra("NAMA", nama)
                    it.putExtra("KELAMIN", kelamin)
                    it.putExtra("EMAIL", email)
                    it.putExtra("TELEPON", telepon)
                    it.putExtra("ALAMAT", alamatInput)
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
        const val ALAMAT = "ALAMAT"
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
                    if (!isConnected(this@UbahAlamatPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahAlamatPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}