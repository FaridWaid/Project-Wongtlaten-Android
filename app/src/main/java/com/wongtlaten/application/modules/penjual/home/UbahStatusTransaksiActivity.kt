package com.wongtlaten.application.modules.penjual.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R

class UbahStatusTransaksiActivity : AppCompatActivity() {

    private lateinit var checkbox1 : CheckBox
    private lateinit var checkbox2 : CheckBox
    private lateinit var checkbox3 : CheckBox
    private lateinit var btnTerapkanActivated : Button
    private lateinit var btnTerapkanInactivated : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_status_transaksi)

        checkbox1 = findViewById(R.id.checkbox1)
        checkbox2 = findViewById(R.id.checkbox2)
        checkbox3 = findViewById(R.id.checkbox3)
        btnTerapkanActivated = findViewById(R.id.btnTerapkanActivated)
        btnTerapkanInactivated = findViewById(R.id.btnTerapkanInactivated)

        btnTerapkanInactivated.visibility = View.VISIBLE

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox2.isChecked = false
                checkbox3.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
            }
        }

        checkbox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox1.isChecked = false
                checkbox3.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
            }
        }

        checkbox3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox1.isChecked = false
                checkbox2.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
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