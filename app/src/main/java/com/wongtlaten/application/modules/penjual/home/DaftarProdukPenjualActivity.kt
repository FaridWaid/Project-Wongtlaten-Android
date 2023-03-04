package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CategoryProduct
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity

class DaftarProdukPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarProdukList: ArrayList<Products>
    private lateinit var adapter: DaftarProdukPenjualAdapter
    private lateinit var fsInactive : TextView
    private lateinit var fsActive : TextView
    private lateinit var popularInactive : TextView
    private lateinit var popularActive : TextView
    private lateinit var newInactive : TextView
    private lateinit var newActive : TextView
    private lateinit var countProduk : TextView
    private lateinit var addProduk : AppCompatImageView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_produk_penjual)

        reference = FirebaseDatabase.getInstance().getReference("dataProduk")

        fsInactive = findViewById(R.id.fsInactive)
        fsActive = findViewById(R.id.fsActive)
        popularInactive = findViewById(R.id.popularInactive)
        popularActive = findViewById(R.id.popularActive)
        newInactive = findViewById(R.id.newInactive)
        newActive = findViewById(R.id.newActive)
        addProduk = findViewById(R.id.plusButton)
        countProduk = findViewById(R.id.countProduk)

        fsInactive.visibility = View.VISIBLE
        popularInactive.visibility = View.VISIBLE
        newInactive.visibility = View.VISIBLE

        fsInactive.setOnClickListener {
            fsActive.visibility = View.VISIBLE
            fsInactive.visibility = View.INVISIBLE
            popularActive.visibility = View.INVISIBLE
            newActive.visibility = View.INVISIBLE
            popularInactive.visibility = View.VISIBLE
            newInactive.visibility = View.VISIBLE
            filterJenis("flash sale")
        }
        fsActive.setOnClickListener {
            fsInactive.visibility = View.VISIBLE
            fsActive.visibility = View.INVISIBLE
            showListProduct()
        }
        popularInactive.setOnClickListener {
            popularActive.visibility = View.VISIBLE
            popularInactive.visibility = View.INVISIBLE
            fsActive.visibility = View.INVISIBLE
            newActive.visibility = View.INVISIBLE
            fsInactive.visibility = View.VISIBLE
            newInactive.visibility = View.VISIBLE
            filterJenis("popular")
        }
        popularActive.setOnClickListener {
            popularInactive.visibility = View.VISIBLE
            popularActive.visibility = View.INVISIBLE
            showListProduct()
        }
        newInactive.setOnClickListener {
            newActive.visibility = View.VISIBLE
            newInactive.visibility = View.INVISIBLE
            fsActive.visibility = View.INVISIBLE
            popularActive.visibility = View.INVISIBLE
            fsInactive.visibility = View.VISIBLE
            popularInactive.visibility = View.VISIBLE
            filterJenis("new")
        }
        newActive.setOnClickListener {
            newInactive.visibility = View.VISIBLE
            newActive.visibility = View.INVISIBLE
            showListProduct()
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarProdukList = arrayListOf<Products>()
        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

        addProduk.setOnClickListener {
            showDialogAdd()
        }

        // Mendefinisikan variabel "etSearch", ketika memasukkan query ke etSearch maka akan memanggil fungsi filter
        // terdapat closeSearch digunakan untuk menghapus query/inputan
        // overridePendingTransition digunakan untuk animasi dari intent
        etSearch = findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
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
        var btnTambahProduk = dialog.findViewById(R.id.btnTambahProduk) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        btnTambahProduk.setOnClickListener {
            Intent(applicationContext, TambahProdukPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showListProduct() {
        reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProdukList.clear()
                    var count = 0
                    var countFs = 0
                    var countNew = 0
                    var countPopular = 0
                    for (i in snapshot.children){
                        count += 1
                        val products = i.getValue(Products::class.java)
                        if (products != null){
                            daftarProdukList.add(products)
                        }
                        if (products?.jenisProduct == "flash sale"){
                            countFs += 1
                        } else if (products?.jenisProduct == "new"){
                            countNew += 1
                        } else if (products?.jenisProduct == "popular"){
                            countPopular += 1
                        }
                    }
                    countProduk.text = "$count Produk"
                    fsActive.text = "Flash Sale ($countFs)"
                    fsInactive.text = "Flash Sale ($countFs)"
                    newActive.text = "New ($countNew)"
                    newInactive.text = "New ($countNew)"
                    popularActive.text = "Popular ($countPopular)"
                    popularInactive.text = "Popular ($countPopular)"

                    adapter = DaftarProdukPenjualAdapter(daftarProdukList, this@DaftarProdukPenjualActivity)
                    rvDaftarProduk.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<Products>()
        // setiap data yang ada pada daftarProdukList disamakan dengan filteredNames
        daftarProdukList.filterTo(filteredNames) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.namaProduct.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)
    }

    // Membuat fungsi "filterJenis" yang digunakan untuk memfilter recyclerView,
    private fun filterJenis(text: String) {
        // mendefiniskan variabel "filteredJenis" yang berisi arraylist dari data users
        val filteredJenis = ArrayList<Products>()
        // setiap data yang ada pada daftarAnggotaList disamakan dengan filteredJenis
        daftarProdukList.filterTo(filteredJenis) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.jenisProduct.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredJenis)
    }

    // Membuat fungsi "animationToTop" yang berisi animasi ketika pinday activity
    // fungsi ini digunakan pada adapter
    fun animationToTop() {
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}