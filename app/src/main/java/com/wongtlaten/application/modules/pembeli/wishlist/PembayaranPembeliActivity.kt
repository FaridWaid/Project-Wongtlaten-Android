package com.wongtlaten.application.modules.pembeli.wishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R

class PembayaranPembeliActivity : AppCompatActivity() {

    private lateinit var produkNotFound : LinearLayout
    private lateinit var rvDaftarProduk : RecyclerView
    private lateinit var btnPesanSekarang : Button
    private lateinit var textPembayaran : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran_pembeli)

        produkNotFound = findViewById(R.id.produkNotFound)
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        btnPesanSekarang = findViewById(R.id.btnPesanSekarang)
        textPembayaran = findViewById(R.id.textPembayaran)

        produkNotFound.visibility = View.VISIBLE

        btnPesanSekarang.setOnClickListener {
            produkNotFound.visibility = View.INVISIBLE
            rvDaftarProduk.visibility = View.VISIBLE
        }

        textPembayaran.setOnClickListener {
            
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