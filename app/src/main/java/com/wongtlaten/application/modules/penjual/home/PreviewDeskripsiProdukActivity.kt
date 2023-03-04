package com.wongtlaten.application.modules.penjual.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products

class PreviewDeskripsiProdukActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_preview_deskripsi_produk)

        imageProduk = findViewById(R.id.imageProduk)
        namaProduk = findViewById(R.id.nameProduk)
        beratProduk = findViewById(R.id.berat)
        minimalPemesanan = findViewById(R.id.minimalPemesanan)
        kategoriProduk = findViewById(R.id.kategori)
        deskripsiProduk = findViewById(R.id.descriptionProduk)
        stockProduk = findViewById(R.id.stok)
        prevButton = findViewById(R.id.prevButton)

        val idProduct = intent.getStringExtra(EXTRA_ID_PRODUCT)!!
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                namaProduk.text = produk.namaProduct
                beratProduk.text = "${produk.beratProduct} kg"
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

}