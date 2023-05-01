package com.wongtlaten.application

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.TempCostOngkir
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualAdapter

class CekOngkirActivity : AppCompatActivity() {

    private lateinit var rvDaftarOngkir: RecyclerView
    private lateinit var adapter: PrediksiOngkirAdapter
    private lateinit var daftarOngkir : ArrayList<TempCostOngkir>
    private lateinit var textKotaAsal : TextView
    private lateinit var textKotaTujuan : TextView
    private lateinit var textBerat : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_ongkir)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        daftarOngkir = intent.getSerializableExtra(EXTRA_ONGKIR) as ArrayList<TempCostOngkir>
        textKotaAsal = findViewById(R.id.textKotaAsal)
        textKotaTujuan = findViewById(R.id.textKotaTujuan)
        textBerat = findViewById(R.id.textBerat)

        textKotaAsal.text = daftarOngkir[0].origin
        textKotaTujuan.text = daftarOngkir[0].destination
        textBerat.text = "(${daftarOngkir[0].weight} KG)"

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarOngkir = findViewById(R.id.rvDaftarOngkir)
        rvDaftarOngkir.setHasFixedSize(true)
        rvDaftarOngkir.layoutManager = LinearLayoutManager(this)

        adapter = PrediksiOngkirAdapter(daftarOngkir)
        rvDaftarOngkir.adapter = adapter

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

    companion object{
        const val EXTRA_ONGKIR = "EXTRA_ONGKIR"
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
                    if (!isConnected(this@CekOngkirActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: CekOngkirActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}