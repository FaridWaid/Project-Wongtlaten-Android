package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products

class DaftarReviewProdukPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarProdukList: ArrayList<Products>
    private lateinit var adapter: DaftarReviewProdukPenjualAdapter
    private lateinit var fsInactive : TextView
    private lateinit var fsActive : TextView
    private lateinit var popularInactive : TextView
    private lateinit var popularActive : TextView
    private lateinit var newInactive : TextView
    private lateinit var newActive : TextView
    private lateinit var countProduk : TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_review_produk_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        reference = FirebaseDatabase.getInstance().getReference("dataProduk")

        fsInactive = findViewById(R.id.fsInactive)
        fsActive = findViewById(R.id.fsActive)
        popularInactive = findViewById(R.id.popularInactive)
        popularActive = findViewById(R.id.popularActive)
        newInactive = findViewById(R.id.newInactive)
        newActive = findViewById(R.id.newActive)
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

        adapter = DaftarReviewProdukPenjualAdapter(daftarProdukList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

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
                        val products = i.getValue(Products::class.java)
                        if (products != null && products.statusProduct != "deleted"){
                            daftarProdukList.add(products)
                            count += 1
                        }
                        if (products?.jenisProduct == "flash sale" && products.statusProduct != "deleted"){
                            countFs += 1
                        } else if (products?.jenisProduct == "new" && products.statusProduct != "deleted"){
                            countNew += 1
                        } else if (products?.jenisProduct == "popular" && products.statusProduct != "deleted"){
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

                    adapter = DaftarReviewProdukPenjualAdapter(daftarProdukList)
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
                    if (!isConnected(this@DaftarReviewProdukPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DaftarReviewProdukPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}