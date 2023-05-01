package com.wongtlaten.application

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.wongtlaten.application.core.LoadingDialog
import kotlin.properties.Delegates

class ResetPasswordActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var btnResetPassword: Button
    private lateinit var btnBack: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        checkClick = true

        val email = intent.getStringExtra(EMAIL)!!

        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnResetPassword.setOnClickListener {

            if (checkClick){
                checkClick = false
                // Mengirimkan pesan ke email user untuk membuat password baru, dan jika berhasil..
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        loadingBar(2000)
                        alertDialog("Konfirmasi!", "Cek email anda untuk membuat password baru pada akun anda!", true)
                        checkClick = true
                    } else {
                        loadingBar(2000)
                        alertDialog("Gagal Mengirim Email!", "Email anda belum terdaftar pada aplikasi, silakan melakukan registrasi terlebih dahulu!", false)
                        // Jika gagal membuat akun baru, maka akan memunculkan toast error
                        checkClick = true
                    }
                }
            } else{
                return@setOnClickListener
            }
        }

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            // Jika berhasil maka akan pindah ke LoginActivity
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            alertDialog.setCancelable(false)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                })
        }
        alertDialog.show()
    }

    companion object {
        const val EMAIL = "EMAIL"
        const val USER = "USER"
    }

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
    private fun loadingBar(time: Long) {
        val loading = LoadingDialog(this)
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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
                    if (!isConnected(this@ResetPasswordActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: ResetPasswordActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}