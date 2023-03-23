package com.wongtlaten.application.modules.pembeli.customize

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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.penjual.profile.UbahEmailPenjualActivity

class CustomizeProdukPembeliActivity : AppCompatActivity() {

    private lateinit var ukuranKecil: TextView
    private lateinit var ukuranSedang: TextView
    private lateinit var totalCustomize: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_produk_pembeli)

        ukuranKecil = findViewById(R.id.ukuranKecil)
        ukuranSedang = findViewById(R.id.ukuranSedang)
        totalCustomize = findViewById(R.id.totalCustomize)

        ukuranKecil.setOnClickListener {
            totalCustomize.visibility = View.VISIBLE
        }

        ukuranSedang.setOnClickListener {
            showDialogDetailProduk()
        }

        totalCustomize.setOnClickListener {
            Intent(applicationContext, DetailCustomizeProdukPembeliActivity::class.java).also {
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

    private fun showDialogDetailProduk(){
        var dialog = Dialog(this@CustomizeProdukPembeliActivity)
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_detail_produk_kustomisasi)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

        //back button
        override fun onBackPressed() {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

}