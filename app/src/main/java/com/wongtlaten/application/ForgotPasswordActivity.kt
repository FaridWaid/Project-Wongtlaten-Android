package com.wongtlaten.application

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.wongtlaten.application.core.LoadingDialog
import java.util.regex.Pattern
import kotlin.properties.Delegates

class ForgotPasswordActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var prevButton: AppCompatImageView
    // Mendefinisikan variabel global dari view
    private lateinit var etEmail: TextInputEditText
    private lateinit var emailContainer: TextInputLayout
    private lateinit var btnSubmit: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        checkClick = true

        prevButton = findViewById(R.id.prevButton)
        btnSubmit = findViewById(R.id.btnSubmit)
        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        etEmail = findViewById(R.id.etEmail)
        emailContainer = findViewById(R.id.emailContainer)

        prevButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke LoginActivity
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        btnSubmit.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                // Membuat variabel baru yang berisi inputan user
                val email = etEmail.text.toString().trim()

                // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                emailContainer.helperText = validEmail()

                // Jika sudah benar, maka helper pada edittext diisikan dengan null
                val validEmail = emailContainer.helperText == null

                // Jika semua sudah diisi maka akan melakukan autentikasi email
                if (validEmail){
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
                }else{
                    loadingBar(2000)
                    alertDialog("Gagal Mengirim Email!", "Pastikan anda menginputkan email dengan benar!", false)
                    // Jika gagal membuat akun baru, maka akan memunculkan toast error
                    checkClick = true
                }
            } else{
                return@setOnClickListener
            }

        }

        // Memanggil fungsi "emailFocusListener"
        emailFocusListener()

    }

    // Membuat fungsi "emailFocusListener"
    fun emailFocusListener() {
        // Memastikan apakah etEmail sudah sesuai dengan format pengisian
        etEmail.setOnFocusChangeListener { _, focused ->
            if(!focused){
                emailContainer.helperText = validEmail()
            }
        }
    }

    // Membuat fungsi "validEmail"
     fun validEmail(): String? {

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

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
     fun loadingBar(time: Long) {
        val loading = LoadingDialog(this)
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
    }

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
     fun alertDialog(title: String, message: String, backActivity: Boolean){
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
                    if (!isConnected(this@ForgotPasswordActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: ForgotPasswordActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}