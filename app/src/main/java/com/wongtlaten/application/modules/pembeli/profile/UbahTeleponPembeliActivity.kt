package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class UbahTeleponPembeliActivity : AppCompatActivity() {

    private lateinit var etTelepon: EditText
    private lateinit var btnSimpanPerubahan: Button
    private lateinit var telepon : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_telepon_pembeli)

        etTelepon = findViewById(R.id.etTelepon)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        telepon = intent.getStringExtra(TELEPON)!!
        val alamat = intent.getStringExtra(ALAMAT)!!

        if (telepon == "belum diisi"){
            telepon = "0"
        }

        etTelepon.setText(telepon)

        btnSimpanPerubahan.setOnClickListener {

            // Membuat variabel baru yang berisi inputan user
            val teleponInput = etTelepon.text.toString().trim()

            // Jika teleponInput kosong maka akan muncul error harus isi terlebih dahulu
            if (teleponInput.isEmpty()){
                etTelepon.error = "Masukkan nomor telepon terlebih dahulu!"
                etTelepon.requestFocus()
                return@setOnClickListener
            }
            // Jika teleponInput memiliki inputan symbol maka akan muncul error harus isi terlebih dahulu
            if(teleponInput.matches(".*[?=.*/><,!@#$%^&()_=+].*".toRegex())) {
                etTelepon.error = "Tidak boleh ada simbol pada nomor telepon!"
                etTelepon.requestFocus()
                return@setOnClickListener
            }
            // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
            if(teleponInput.matches(".*[a-z].*".toRegex())) {
                etTelepon.error = "Tidak boleh ada huruf pada nomor telepon!"
                etTelepon.requestFocus()
                return@setOnClickListener
            }
            // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
            if(teleponInput.length < 10) {
                etTelepon.error = "Masukkan nomor telepon yang valid!"
                etTelepon.requestFocus()
                return@setOnClickListener
            }
            // Jika teleponInput memiliki inputan angka maka akan muncul error harus isi terlebih dahulu
            if(teleponInput.length > 13) {
                etTelepon.error = "Masukkan nomor telepon yang valid!"
                etTelepon.requestFocus()
                return@setOnClickListener
            }

            // Pindah ke UbahDataPribadiPembeliActivity
            Intent(applicationContext, UbahDataPribadiPembeliActivity::class.java).also {
                it.putExtra("NAMA", nama)
                it.putExtra("KELAMIN", kelamin)
                it.putExtra("EMAIL", email)
                it.putExtra("ALAMAT", alamat)
                it.putExtra("TELEPON", teleponInput)
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