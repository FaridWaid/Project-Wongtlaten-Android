package com.wongtlaten.application.modules.pembeli.home

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.PreviewDeskripsiProdukActivity

class DetailDeskripsiProdukPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    private lateinit var imageProduk : ImageView
    private lateinit var namaProduk : TextView
    private lateinit var beratProduk : TextView
    private lateinit var minimalPemesanan : TextView
    private lateinit var kategoriProduk : TextView
    private lateinit var stockProduk : TextView
    private lateinit var deskripsiProduk : TextView
    private lateinit var prevButton : AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_deskripsi_produk_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        imageProduk = findViewById(R.id.imageProduk)
        namaProduk = findViewById(R.id.nameProduk)
        beratProduk = findViewById(R.id.berat)
        minimalPemesanan = findViewById(R.id.minimalPemesanan)
        kategoriProduk = findViewById(R.id.kategori)
        deskripsiProduk = findViewById(R.id.descriptionProduk)
        stockProduk = findViewById(R.id.stok)
        prevButton = findViewById(R.id.prevButton)

        val idProduct = intent.getStringExtra(PreviewDeskripsiProdukActivity.EXTRA_ID_PRODUCT)!!
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                namaProduk.text = produk.namaProduct
                beratProduk.text = "${produk.beratProduct} gram"
                minimalPemesanan.text = "${produk.minimumPemesananProduct} buah"
                kategoriProduk.text = "${produk.kategoriProduct}"
                stockProduk.text = "${produk.stockProduct} buah"
                deskripsiProduk.text = produk.deskripsiProduct
                Picasso.get().load(produk.photoProduct1).into(imageProduk)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        prevButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }

    }

    companion object{
        const val EXTRA_ID_PRODUCT = "EXTRA_ID_PRODUCT"
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
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
                    if (!isConnected(this@DetailDeskripsiProdukPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DetailDeskripsiProdukPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}