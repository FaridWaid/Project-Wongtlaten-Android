package com.wongtlaten.application.modules.pembeli.customize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class PembelianBerhasilActivity : AppCompatActivity() {

    private lateinit var btnLanjutBeli: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembelian_berhasil)

        btnLanjutBeli = findViewById(R.id.btnLanjutBelanja)
        btnLanjutBeli.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
            finish()
        }

    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
    }


}