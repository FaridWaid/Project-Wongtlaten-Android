package com.wongtlaten.application.modules.penjual.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.*

class UbahDataPribadiPenjualActivity : AppCompatActivity() {

    private lateinit var textNama : TextView
    private lateinit var textEmail : TextView
    private lateinit var textTelepon : TextView
    private lateinit var textKelamin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_data_pribadi_penjual)

        textNama = findViewById(R.id.Nama)
        textEmail = findViewById(R.id.Email)
        textTelepon = findViewById(R.id.NomorTelepon)
        textKelamin = findViewById(R.id.JenisKelamin)

        textNama.setOnClickListener {
            Intent(applicationContext, UbahNamaPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        textEmail.setOnClickListener {
            Intent(applicationContext, UbahEmailPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        textTelepon.setOnClickListener {
            Intent(applicationContext, UbahTeleponPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        textKelamin.setOnClickListener {
            Intent(applicationContext, UbahKelaminPembeliActivity::class.java).also {
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