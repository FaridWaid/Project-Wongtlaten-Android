package com.wongtlaten.application.modules.penjual.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class UbahKelaminPenjualActivity : AppCompatActivity() {

    private lateinit var manActivated : ImageView
    private lateinit var manInactivated : ImageView
    private lateinit var womanActivated : ImageView
    private lateinit var womanInactivated : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_kelamin_penjual)

        manActivated = findViewById(R.id.icManActived)
        manInactivated = findViewById(R.id.icManInactived)
        womanActivated = findViewById(R.id.icWomanActived)
        womanInactivated = findViewById(R.id.icWomanInactived)

        manInactivated.visibility = View.VISIBLE
        womanInactivated.visibility = View.VISIBLE

        manInactivated.setOnClickListener {
            manActivated.visibility = View.VISIBLE
            womanActivated.visibility = View.INVISIBLE
        }
        womanInactivated.setOnClickListener {
            womanActivated.visibility = View.VISIBLE
            manActivated.visibility = View.INVISIBLE
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