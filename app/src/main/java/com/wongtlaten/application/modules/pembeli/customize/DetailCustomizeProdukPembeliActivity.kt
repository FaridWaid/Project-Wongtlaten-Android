package com.wongtlaten.application.modules.pembeli.customize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class DetailCustomizeProdukPembeliActivity : AppCompatActivity() {

    private lateinit var btnUbahJasaPengiriman : Button
    private lateinit var btnTambahAlamat : Button
    private lateinit var btnBayarSekarang : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_customize_produk_pembeli)

        btnUbahJasaPengiriman = findViewById(R.id.btnUbahJasaPengiriman)
        btnTambahAlamat = findViewById(R.id.btnTambahAlamat)
        btnBayarSekarang = findViewById(R.id.btnBayarSekarang)

        btnUbahJasaPengiriman.setOnClickListener {
            Intent(applicationContext, UbahJasaPengirimanPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        btnTambahAlamat.setOnClickListener {
            Intent(applicationContext, TambahAlamatPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        btnBayarSekarang.setOnClickListener {
            Intent(applicationContext, PembelianBerhasilActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
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

}