package com.wongtlaten.application.modules.pembeli.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualAdapter

class FlashSaleActivity : AppCompatActivity() {

    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarFsList: ArrayList<Products>
    private lateinit var adapterFs: FsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_sale)

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = GridLayoutManager(this, 2)

        // Memasukkan data DaftarProduk ke dalam "daftarFsList" sebagai array list
        daftarFsList = arrayListOf<Products>()

        adapterFs = FsAdapter(daftarFsList, this@FlashSaleActivity)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun showListProduct() {
        val ref = FirebaseDatabase.getInstance().getReference("dataProduk")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarFsList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)
                        if (products?.jenisProduct == "flash sale"){
                            daftarFsList.add(products)
                        }
                    }

                    adapterFs = FsAdapter(daftarFsList, FlashSaleActivity())
                    rvDaftarProduk.adapter = adapterFs
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}