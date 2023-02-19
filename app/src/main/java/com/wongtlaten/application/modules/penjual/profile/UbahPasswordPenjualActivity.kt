package com.wongtlaten.application.modules.penjual.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wongtlaten.application.R

class UbahPasswordPenjualActivity : AppCompatActivity() {

    private lateinit var etPasswordLama: TextInputEditText
    private lateinit var passwordLamaContainer: TextInputLayout
    private lateinit var etPasswordBaru: TextInputEditText
    private lateinit var passwordBaruContainer: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password_penjual)

        etPasswordLama = findViewById(R.id.etPasswordLama)
        passwordLamaContainer = findViewById(R.id.passwordLamaContainer)
        etPasswordBaru = findViewById(R.id.etPasswordBaru)
        passwordBaruContainer = findViewById(R.id.passwordBaruContainer)

        passwordLamaFocusListener()
        passwordBaruFocusListener()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

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
        // Jika panjang password kurang dari 6 maka akan gagal login
        if(password.length > 16) {
            return "Password Tidak Boleh Lebih Dari 16 Karakter!"
        }
        // Jika panjang password tidak mengandung huruf maka akan gagal login
        if (!password.matches(".*[a-z].*".toRegex())){
            return "Password Harus Mengandung Huruf"
        }
        // Jika panjang password tidak mengandung angka maka akan gagal login
        if (!password.matches(".*[0-9].*".toRegex())){
            return "Password Harus Mengandung Angka"
        }
        // Jika panjang password tidak mengandung karakter maka akan gagal login
        if (!password.matches(".*[?=.*/><,!@#$%^&()_=+].*".toRegex())){
            return "Password Harus Mengandung Karakter"
        }
        return null
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}