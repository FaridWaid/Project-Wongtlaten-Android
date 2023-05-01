package com.wongtlaten.application.modules.pembeli.customize

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_JASA_PENGIRIMAN

class UbahJasaPengirimanPembeliActivity : AppCompatActivity() {

    private lateinit var btnTerapkanInactivated : Button
    private lateinit var btnTerapkanActivated : Button
    private lateinit var checkbox1 : CheckBox
    private lateinit var checkbox2 : CheckBox
    private lateinit var textJasaPengiriman: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_jasa_pengiriman_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        btnTerapkanActivated = findViewById(R.id.btnTerapkanActivated)
        btnTerapkanInactivated = findViewById(R.id.btnTerapkanInactivated)
        checkbox1 = findViewById(R.id.checkbox1)
        checkbox2 = findViewById(R.id.checkbox2)

        btnTerapkanInactivated.visibility = View.VISIBLE

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox2.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        checkbox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox1.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        btnTerapkanActivated.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkbox1.isChecked){
                textJasaPengiriman = "JNE"
            } else{
                textJasaPengiriman = "POS"
            }
            var intent = Intent()
            intent.putExtra(EXTRA_JASA_PENGIRIMAN, textJasaPengiriman)
            setResult(RESULT_OK, intent);
            finish()
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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

    // Fungsi ini digunakan untuk menampilkan dialog peringatan tidak tersambung ke internet,
    // jika tetep tidak connect ke internet maka tetap looping dialog tersebut
    private fun showInternetDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle("PERINGATAN!")
            setMessage("Tidak ada koneksi internet, mohon nyalakan mobile data/wifi anda terlebih dahulu")
            setIcon(R.drawable.ic_alert)
            setCancelable(false)
            setPositiveButton(
                "Coba lagi",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (!isConnected(this@UbahJasaPengirimanPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahJasaPengirimanPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}