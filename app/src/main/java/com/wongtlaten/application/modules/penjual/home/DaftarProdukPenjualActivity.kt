package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity

class DaftarProdukPenjualActivity : AppCompatActivity() {

    private lateinit var fsInactive : TextView
    private lateinit var fsActive : TextView
    private lateinit var popularInactive : TextView
    private lateinit var popularActive : TextView
    private lateinit var newInactive : TextView
    private lateinit var newActive : TextView
    private lateinit var addProduk : AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_produk_penjual)

        fsInactive = findViewById(R.id.fsInactive)
        fsActive = findViewById(R.id.fsActive)
        popularInactive = findViewById(R.id.popularInactive)
        popularActive = findViewById(R.id.popularActive)
        newInactive = findViewById(R.id.newInactive)
        newActive = findViewById(R.id.newActive)
        addProduk = findViewById(R.id.plusButton)

        fsInactive.visibility = View.VISIBLE
        popularInactive.visibility = View.VISIBLE
        newInactive.visibility = View.VISIBLE

        fsInactive.setOnClickListener {
            fsActive.visibility = View.VISIBLE
            fsInactive.visibility = View.INVISIBLE
        }
        fsActive.setOnClickListener {
            fsInactive.visibility = View.VISIBLE
            fsActive.visibility = View.INVISIBLE
        }
        popularInactive.setOnClickListener {
            popularActive.visibility = View.VISIBLE
            popularInactive.visibility = View.INVISIBLE
        }
        popularActive.setOnClickListener {
            popularInactive.visibility = View.VISIBLE
            popularActive.visibility = View.INVISIBLE
        }
        newInactive.setOnClickListener {
            newActive.visibility = View.VISIBLE
            newInactive.visibility = View.INVISIBLE
        }
        newActive.setOnClickListener {
            newInactive.visibility = View.VISIBLE
            newActive.visibility = View.INVISIBLE
        }

        addProduk.setOnClickListener {
            showDialogAdd()
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

    private fun showDialogAdd(){
        var dialog = Dialog(this@DaftarProdukPenjualActivity)
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
        var btnCekSekarang = dialog.findViewById(R.id.btnCekSekarang) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        btnCekSekarang.setOnClickListener {
            Intent(applicationContext, TambahProdukPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        dialog.show()
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}