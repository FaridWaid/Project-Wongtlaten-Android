package com.wongtlaten.application.modules.penjual.home

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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products

class KustomisasiProdukPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarProdukList: ArrayList<CustomizeProducts>
    private lateinit var adapter: KustomisasiProdukPenjualAdapter
    private lateinit var kecilInactive : TextView
    private lateinit var kecilActive : TextView
    private lateinit var sedangInactive : TextView
    private lateinit var sedangActive : TextView
    private lateinit var besarInactive : TextView
    private lateinit var besarActive : TextView
    private lateinit var addProduk : AppCompatImageView
    private lateinit var countProduk : TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kustomisasi_produk_penjual)

        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")

        kecilInactive = findViewById(R.id.kecilInactive)
        kecilActive = findViewById(R.id.kecilActive)
        sedangInactive = findViewById(R.id.sedangInactive)
        sedangActive = findViewById(R.id.sedangActive)
        besarInactive = findViewById(R.id.besarInactive)
        besarActive = findViewById(R.id.besarActive)
        addProduk = findViewById(R.id.plusButton)
        countProduk = findViewById(R.id.countProduk)

        kecilInactive.visibility = View.VISIBLE
        sedangInactive.visibility = View.VISIBLE
        besarInactive.visibility = View.VISIBLE

        kecilInactive.setOnClickListener {
            kecilActive.visibility = View.VISIBLE
            kecilInactive.visibility = View.INVISIBLE
            sedangActive.visibility = View.INVISIBLE
            besarActive.visibility = View.INVISIBLE
            sedangInactive.visibility = View.VISIBLE
            besarInactive.visibility = View.VISIBLE
            filterJenis("kecil")
        }
        kecilActive.setOnClickListener {
            kecilInactive.visibility = View.VISIBLE
            kecilActive.visibility = View.INVISIBLE
            showListProduct()
        }
        sedangInactive.setOnClickListener {
            sedangActive.visibility = View.VISIBLE
            sedangInactive.visibility = View.INVISIBLE
            kecilActive.visibility = View.INVISIBLE
            besarActive.visibility = View.INVISIBLE
            kecilInactive.visibility = View.VISIBLE
            besarInactive.visibility = View.VISIBLE
            filterJenis("sedang")
        }
        sedangActive.setOnClickListener {
            sedangInactive.visibility = View.VISIBLE
            sedangActive.visibility = View.INVISIBLE
            showListProduct()
        }
        besarInactive.setOnClickListener {
            besarActive.visibility = View.VISIBLE
            besarInactive.visibility = View.INVISIBLE
            kecilActive.visibility = View.INVISIBLE
            sedangActive.visibility = View.INVISIBLE
            kecilInactive.visibility = View.VISIBLE
            sedangInactive.visibility = View.VISIBLE
            filterJenis("besar")
        }
        besarActive.setOnClickListener {
            besarInactive.visibility = View.VISIBLE
            besarActive.visibility = View.INVISIBLE
            showListProduct()
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarProdukList = arrayListOf<CustomizeProducts>()

        adapter = KustomisasiProdukPenjualAdapter(daftarProdukList, this@KustomisasiProdukPenjualActivity)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

        addProduk.setOnClickListener {
            showDialogAdd()
        }

        countProduk.setOnClickListener {
            showDialogDetailProduk()
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

    private fun showListProduct() {
        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProdukList.clear()
                    var count = 0
                    var countKecil = 0
                    var countSedang = 0
                    var countBesar = 0
                    for (i in snapshot.children){
                        count += 1
                        val products = i.getValue(CustomizeProducts::class.java)
                        if (products != null){
                            daftarProdukList.add(products)
                        }
                        if (products?.kategoriProduct == "kecil"){
                            countKecil += 1
                        } else if (products?.kategoriProduct == "sedang"){
                            countSedang += 1
                        } else if (products?.kategoriProduct == "besar"){
                            countBesar += 1
                        }
                    }
                    countProduk.text = "$count Produk"
                    kecilActive.text = "Kecil ($countKecil)"
                    kecilInactive.text = "Kecil ($countKecil)"
                    sedangActive.text = "Sedang ($countSedang)"
                    sedangInactive.text = "Sedang ($countSedang)"
                    besarActive.text = "Besar ($countBesar)"
                    besarInactive.text = "Besar ($countBesar)"

                    adapter = KustomisasiProdukPenjualAdapter(daftarProdukList, this@KustomisasiProdukPenjualActivity)
                    rvDaftarProduk.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filterJenis" yang digunakan untuk memfilter recyclerView,
    private fun filterJenis(text: String) {
        // mendefiniskan variabel "filteredJenis" yang berisi arraylist dari data users
        val filteredJenis = ArrayList<CustomizeProducts>()
        // setiap data yang ada pada daftarAnggotaList disamakan dengan filteredJenis
        daftarProdukList.filterTo(filteredJenis) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.kategoriProduct.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredJenis)
    }

    private fun showDialogAdd(){
        var dialog = Dialog(this@KustomisasiProdukPenjualActivity)
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout_kustomisasi)
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
            Intent(applicationContext, TambahKustomisasiProdukPenjualActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDialogDetailProduk(){
        var dialog = Dialog(this@KustomisasiProdukPenjualActivity)
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