package com.wongtlaten.application.modules.pembeli.wishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class KeranjangPembeliActivity : AppCompatActivity() {

    private lateinit var btnBeliInactivated : Button
    private lateinit var btnBeliActivated : Button
    private lateinit var checkbox1 : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang_pembeli)

        btnBeliInactivated = findViewById(R.id.btnBeliInactivated)
        btnBeliActivated = findViewById(R.id.btnBeliActivated)
        checkbox1 = findViewById(R.id.checkbox1)

        btnBeliInactivated.visibility = View.VISIBLE

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btnBeliActivated.visibility = View.VISIBLE
                btnBeliInactivated.visibility = View.INVISIBLE
            } else {
                btnBeliActivated.visibility = View.INVISIBLE
                btnBeliInactivated.visibility = View.VISIBLE
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