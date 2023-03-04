package com.wongtlaten.application.modules.pembeli.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualAdapter

class SearchPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarProdukList: ArrayList<Products>
    private lateinit var adapter: DaftarProdukAdapter
    private lateinit var etSearch: EditText
    private lateinit var itemClose: CardView
    private lateinit var produkNotFound: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pembeli)

        reference = FirebaseDatabase.getInstance().getReference("dataProduk")

        produkNotFound = findViewById(R.id.produkNotFound)
        itemClose = findViewById(R.id.itemFiturClose)
        produkNotFound.visibility = View.INVISIBLE

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProdukh ke dalam "daftarProdukList" sebagai array list
        daftarProdukList = arrayListOf<Products>()
        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

        etSearch = findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                itemClose.visibility = View.INVISIBLE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
                val countChar = etSearch.text.toString()
                if (countChar.isEmpty()){
                    editable?.clear()
                    rvDaftarProduk.visibility = View.INVISIBLE
                    itemClose.visibility = View.INVISIBLE
                } else {
                    itemClose.visibility = View.VISIBLE
                }
                itemClose.setOnClickListener {
                    editable?.clear()
                    rvDaftarProduk.visibility = View.INVISIBLE
                    itemClose.visibility = View.INVISIBLE
                }

            }

        })



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
        reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProdukList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)
                        if (products != null){
                            daftarProdukList.add(products)
                        }
                    }

                    adapter = DaftarProdukAdapter(daftarProdukList, this@SearchPembeliActivity)
                    rvDaftarProduk.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<Products>()
        // setiap data yang ada pada daftarProdukList disamakan dengan filteredNames
        daftarProdukList.filterTo(filteredNames) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.namaProduct.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)

        if (filteredNames.isEmpty()){
            produkNotFound.visibility = View.VISIBLE
            rvDaftarProduk.visibility = View.INVISIBLE
        } else {
            rvDaftarProduk.visibility = View.VISIBLE
            produkNotFound.visibility = View.INVISIBLE
        }
    }

    // Membuat fungsi "animationToLeft" yang berisi animasi ketika pinday activity
    // fungsi ini digunakan pada adapter
    fun animationToLeft() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}