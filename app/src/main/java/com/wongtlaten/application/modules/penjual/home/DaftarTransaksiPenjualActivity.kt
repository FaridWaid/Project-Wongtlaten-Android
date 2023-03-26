package com.wongtlaten.application.modules.penjual.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class DaftarTransaksiPenjualActivity : AppCompatActivity() {

    private lateinit var kustomisasiInactive : TextView
    private lateinit var kustomisasiActive : TextView
    private lateinit var normalInactive : TextView
    private lateinit var normalActive : TextView
    private lateinit var selesaiInactive : TextView
    private lateinit var selesaiActive : TextView
    private lateinit var textDaftarTransaksi : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_transaksi_penjual)

        kustomisasiInactive = findViewById(R.id.kustomisasiInactive)
        kustomisasiActive = findViewById(R.id.kustomisasiActive)
        normalInactive = findViewById(R.id.normalInactive)
        normalActive = findViewById(R.id.normalActive)
        selesaiInactive = findViewById(R.id.selesaiInactive)
        selesaiActive = findViewById(R.id.selesaiActive)
        textDaftarTransaksi = findViewById(R.id.textDaftarTransaksi)

        kustomisasiInactive.visibility = View.VISIBLE
        normalInactive.visibility = View.VISIBLE
        selesaiInactive.visibility = View.VISIBLE

        textDaftarTransaksi.setOnClickListener {
            Intent(applicationContext, DetailTransaksiNormalPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        kustomisasiInactive.setOnClickListener {
            kustomisasiActive.visibility = View.VISIBLE
            kustomisasiInactive.visibility = View.INVISIBLE
            normalActive.visibility = View.INVISIBLE
            selesaiActive.visibility = View.INVISIBLE
            normalInactive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.VISIBLE
//            filterJenis("flash sale")
        }
        kustomisasiActive.setOnClickListener {
            kustomisasiInactive.visibility = View.VISIBLE
            kustomisasiActive.visibility = View.INVISIBLE
//            showListProduct()
        }
        normalInactive.setOnClickListener {
            normalActive.visibility = View.VISIBLE
            normalInactive.visibility = View.INVISIBLE
            kustomisasiActive.visibility = View.INVISIBLE
            selesaiActive.visibility = View.INVISIBLE
            kustomisasiInactive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.VISIBLE
//            filterJenis("popular")
        }
        normalActive.setOnClickListener {
            normalInactive.visibility = View.VISIBLE
            normalActive.visibility = View.INVISIBLE
//            showListProduct()
        }
        selesaiInactive.setOnClickListener {
            selesaiActive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.INVISIBLE
            kustomisasiActive.visibility = View.INVISIBLE
            normalActive.visibility = View.INVISIBLE
            kustomisasiInactive.visibility = View.VISIBLE
            normalInactive.visibility = View.VISIBLE
//            filterJenis("new")
        }
        selesaiActive.setOnClickListener {
            selesaiInactive.visibility = View.VISIBLE
            selesaiActive.visibility = View.INVISIBLE
//            showListProduct()
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