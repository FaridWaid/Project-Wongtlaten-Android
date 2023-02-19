package com.wongtlaten.application.modules.penjual.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity

class ProfileDataPribadiPenjualActivity : AppCompatActivity() {

    private lateinit var btnUbahDataPribadi : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_data_pribadi_penjual)

        btnUbahDataPribadi = findViewById(R.id.btnUbahDataPribadi)
        btnUbahDataPribadi.setOnClickListener {
            Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
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