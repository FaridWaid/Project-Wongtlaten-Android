package com.wongtlaten.application.modules.penjual.profile

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.UbahKelaminPembeliActivity

class UbahKelaminPenjualActivity : AppCompatActivity() {

    private lateinit var manActivated : ImageView
    private lateinit var manInactivated : ImageView
    private lateinit var womanActivated : ImageView
    private lateinit var womanInactivated : ImageView
    private lateinit var btnSimpanPerubahan: Button
    private lateinit var kelamin : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_kelamin_penjual)

        manActivated = findViewById(R.id.icManActived)
        manInactivated = findViewById(R.id.icManInactived)
        womanActivated = findViewById(R.id.icWomanActived)
        womanInactivated = findViewById(R.id.icWomanInactived)
        btnSimpanPerubahan = findViewById(R.id.btnSimpanPerubahan)

        val nama = intent.getStringExtra(NAMA)!!
        kelamin = intent.getStringExtra(KELAMIN)!!
        val email = intent.getStringExtra(EMAIL)!!
        val telepon = intent.getStringExtra(TELEPON)!!

        if (kelamin == "belum diisi"){
            manInactivated.visibility = View.VISIBLE
            womanInactivated.visibility = View.VISIBLE
        } else if (kelamin == "Laki-Laki"){
            manActivated.visibility = View.VISIBLE
            womanInactivated.visibility = View.VISIBLE
        } else if (kelamin == "Perempuan"){
            manInactivated.visibility = View.VISIBLE
            womanActivated.visibility = View.VISIBLE
        }

        manInactivated.setOnClickListener {
            manActivated.visibility = View.VISIBLE
            womanActivated.visibility = View.INVISIBLE
            womanInactivated.visibility = View.VISIBLE
            kelamin = "Laki-Laki"
        }
        womanInactivated.setOnClickListener {
            womanActivated.visibility = View.VISIBLE
            manActivated.visibility = View.INVISIBLE
            manInactivated.visibility = View.VISIBLE
            kelamin = "Perempuan"
        }

        btnSimpanPerubahan.setOnClickListener {

            if (kelamin == ""){
                alertDialog("GAGAL!", "Gagal mengubah data kelamin, silakan pilih jenis kelamin anda terlebih dahulu!", false)
            } else {
                // Pindah ke UbahDataPribadiPenjualActivity
                Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                    it.putExtra("NAMA", nama)
                    it.putExtra("KELAMIN", kelamin)
                    it.putExtra("EMAIL", email)
                    it.putExtra("TELEPON", telepon)
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                    finish()
                }
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

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            window.setBackgroundDrawableResource(android.R.color.background_light)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                })
        }
        alertDialog.show()
    }

    companion object{
        const val NAMA = "NAMA"
        const val KELAMIN = "KELAMIN"
        const val EMAIL = "EMAIL"
        const val TELEPON = "TELEPON"
    }

}