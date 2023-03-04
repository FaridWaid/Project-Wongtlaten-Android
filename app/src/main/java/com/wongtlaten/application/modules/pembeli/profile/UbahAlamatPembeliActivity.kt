package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity

class UbahAlamatPembeliActivity : AppCompatActivity() {

    private lateinit var etAlamat: EditText
    private lateinit var btnSimpanPerubahan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_alamat_pembeli)

        etAlamat = findViewById(R.id.etAlamat)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!
        val alamat = intent.getStringExtra(ALAMAT)!!

        etAlamat.setText(alamat)

        btnSimpanPerubahan.setOnClickListener {

            // Membuat variabel baru yang berisi inputan user
            val alamatInput = etAlamat.text.toString().trim()

            // Jika alamatInput kosong maka akan muncul error harus isi terlebih dahulu
            if (alamatInput.isEmpty()){
                etAlamat.error = "Masukkan alamat terlebih dahulu!"
                etAlamat.requestFocus()
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

}