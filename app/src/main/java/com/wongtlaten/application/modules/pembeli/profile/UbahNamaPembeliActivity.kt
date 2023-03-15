package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.LoadingDialog
import kotlin.properties.Delegates

class UbahNamaPembeliActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var btnSimpanPerubahan: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_nama_pembeli)

        etNama = findViewById(R.id.etNama)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)

        val nama = intent.getStringExtra(NAMA)!!
        val kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!
        val alamat = intent.getStringExtra(ALAMAT)!!
        checkClick = true

        etNama.setText(nama)

        btnSimpanPerubahan.setOnClickListener {

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

                // Pindah ke UbahDataPribadiPembeliActivity
                Intent(applicationContext, UbahDataPribadiPembeliActivity::class.java).also {
                    it.putExtra("NAMA", namaInput)
                    it.putExtra("KELAMIN", kelamin)
                    it.putExtra("EMAIL", email)
                    it.putExtra("TELEPON", telepon)
                    it.putExtra("ALAMAT", alamat)
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

}