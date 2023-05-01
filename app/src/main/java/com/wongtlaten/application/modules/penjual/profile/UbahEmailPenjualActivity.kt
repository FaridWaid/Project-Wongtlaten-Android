package com.wongtlaten.application.modules.penjual.profile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.UbahEmailPembeliActivity
import java.util.regex.Pattern
import kotlin.properties.Delegates

class UbahEmailPenjualActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var emailContainer: TextInputLayout
    private lateinit var btnSimpanPerubahan: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_email_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        etEmail = findViewById(R.id.etEmail)
        emailContainer = findViewById(R.id.emailContainer)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)
        checkClick = true

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!

        etEmail.setText(email)

        emailFocusListener()

        btnSimpanPerubahan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                val email = etEmail.text.toString().trim()

                // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                emailContainer.helperText = validEmail()

                // Jika sudah benar, maka helper pada edittext diisikan dengan null
                val validEmail = emailContainer.helperText == null

                if (validEmail){
                    // Pindah ke UbahDataPribadiPenjualActivity
                    Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                        it.putExtra("NAMA", nama)
                        it.putExtra("KELAMIN", kelamin)
                        it.putExtra("EMAIL", email)
                        it.putExtra("TELEPON", telepon)
                        startActivity(it)
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                        finish()
                        checkClick = true
                    }
                }
                checkClick = true
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

    // Membuat fungsi "emailFocusListener"
    private fun emailFocusListener() {
        // Memastikan apakah etEmail sudah sesuai dengan format pengisian
        etEmail.setOnFocusChangeListener { _, focused ->
            if(!focused){
                emailContainer.helperText = validEmail()
            }
        }
    }

    // Membuat fungsi "validEmail"
    private fun validEmail(): String? {

        val EMAIL_ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(gmail)+\\.(com)\$")

        val email = etEmail.text.toString()
        // Jika email kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (email.isEmpty()){
            return "Email Harus Diisi!"
        }
        // Jika email tidak sesuai format maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email Tidak Valid. Seharusnya your@gmail.com"
        }
        // Jika email tidak sesuai format maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            return "Email Tidak Valid. Seharusnya your@gmail.com"
        }
        return null
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
                    if (!isConnected(this@UbahEmailPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahEmailPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}