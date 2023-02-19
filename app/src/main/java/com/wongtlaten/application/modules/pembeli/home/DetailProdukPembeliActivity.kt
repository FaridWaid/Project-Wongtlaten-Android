package com.wongtlaten.application.modules.pembeli.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity

class DetailProdukPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global dari view
    private lateinit var textBacaSelengkapnya: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk_pembeli)

        textBacaSelengkapnya = findViewById(R.id.textBacaSelengkapnya)
        textBacaSelengkapnya.setOnClickListener {
            Intent(applicationContext, DetailDeskripsiProdukPembeliActivity::class.java).also {
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