package com.wongtlaten.application.modules.pembeli.profile

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.wongtlaten.application.R
import com.wongtlaten.application.core.LoadingDialog

class UbahPasswordPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    // Mendefinisikan variabel global dari view
    private lateinit var etPasswordLama: TextInputEditText
    private lateinit var passwordLamaContainer: TextInputLayout
    private lateinit var etPasswordBaru: TextInputEditText
    private lateinit var passwordBaruContainer: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password_pembeli)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!

        etPasswordLama = findViewById(R.id.etPasswordLama)
        passwordLamaContainer = findViewById(R.id.passwordLamaContainer)
        etPasswordBaru = findViewById(R.id.etPasswordBaru)
        passwordBaruContainer = findViewById(R.id.passwordBaruContainer)

        passwordLamaFocusListener()
        passwordBaruFocusListener()

        // Mendefinisikan variabel "buttonSubmit"
        // Ketika "buttonSubmit" di klik maka akan mencoba menjalankan kondisi yang ada di dalam
        val buttonSubmit: Button = findViewById(R.id.btnSimpanPerubahan)
        buttonSubmit.setOnClickListener {

            // Membuat variabel baru yang berisi inputan user
            val oldPassword = etPasswordLama.text.toString()
            val newPassword = etPasswordBaru.text.toString()

            // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
            passwordLamaContainer.helperText = validPassword(oldPassword)
            passwordBaruContainer.helperText = validPassword(newPassword)

            // Jika sudah benar, maka helper pada edittext diisikan dengan null
            val validOldPassword = passwordLamaContainer.helperText == null
            val validNewPassword = passwordBaruContainer.helperText == null

            // Jika semua sudah diisi maka akan masuk ke dalam kondisi untuk autentikasi email dari user,
            // kemudian mengupdate password baru yang telah dibuat oleh user,
            // jika berhasil maka akan mnemapilkan alert dialog berhasil,
            // dan jika gagal maka akan mnemapilkan alert dialog gagal
            if (validOldPassword && validNewPassword) {
                userIdentity.let {
                    val userCredential = EmailAuthProvider.getCredential(it.email!!, oldPassword)
                    it.reauthenticate(userCredential).addOnCompleteListener {
                        if (it.isSuccessful){
                            loadingBar(2000)
                            userIdentity.updatePassword(newPassword).addOnCompleteListener {
                                if (it.isSuccessful){
                                    alertDialog("Konfirmasi!","Password anda berhasil diubah!", true)
                                } else{
                                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else if (it.exception is FirebaseAuthInvalidCredentialsException){
                            alertDialog("Gagal Mengubah Password!","Password lama yang anda masukkan salah!", false)
                        } else{
                            alertDialog("Gagal Mengubah Password!","${it.exception?.message}", false)
                        }
                    }
                }
            }else {
                // Jika gagal maka akan memunculkan toast gagal
                alertDialog("Gagal Mengubah Password!","Pastikan password yang anda inputkan sudah sesuai format!", false)
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

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            window.setBackgroundDrawableResource(android.R.color.background_light)
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

    // Membuat fungsi "passwordFocusListener"
    private fun passwordLamaFocusListener() {
        // Memastikan apakah etPassword sudah sesuai dengan format pengisian
        etPasswordLama.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                val password = etPasswordLama.text.toString()
                passwordLamaContainer.helperText = validPassword(password)
            }
        }
    }

    // Membuat fungsi "passwordFocusListener"
    private fun passwordBaruFocusListener() {
        // Memastikan apakah etPassword sudah sesuai dengan format pengisian
        etPasswordBaru.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                val password = etPasswordBaru.text.toString()
                passwordBaruContainer.helperText = validPassword(password)
            }
        }
    }

    // Membuat fungsi "validPassword"
    private fun validPassword(passwordInput: String): String? {
        val password = passwordInput
        // Jika password kosong maka akan gagal login dan muncul error harus isi terlebih dahulu
        if (password.isEmpty()){
            return "Password Harus Diisi!"
        }
        // Jika panjang password kurang dari 6 maka akan gagal login
        if(password.length < 6) {
            return "Password Harus Lebih Dari 6 Karakter!"
        }
        return null
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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

}