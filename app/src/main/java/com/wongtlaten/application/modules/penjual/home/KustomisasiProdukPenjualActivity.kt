package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
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

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

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
                        val products = i.getValue(CustomizeProducts::class.java)
                        if (products != null && products.statusProduct != "deleted"){
                            daftarProdukList.add(products)
                            count += 1
                        }
                        if (products?.kategoriProduct == "kecil" && products.statusProduct != "deleted"){
                            countKecil += 1
                        } else if (products?.kategoriProduct == "sedang" && products.statusProduct != "deleted"){
                            countSedang += 1
                        } else if (products?.kategoriProduct == "besar" && products.statusProduct != "deleted"){
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

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<CustomizeProducts>()
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
        val filteredJenis = ArrayList<CustomizeProducts>()
        // setiap data yang ada pada daftarAnggotaList disamakan dengan filteredJenis
        daftarProdukList.filterTo(filteredJenis) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.kategoriProduct.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredJenis)
        adapter.setOnItemClickCallback(object : KustomisasiProdukPenjualAdapter.OnItemClickCallback{
            override fun onItemClicked() {
                showListProduct()
                kecilActive.visibility = View.INVISIBLE
                sedangActive.visibility = View.INVISIBLE
                besarActive.visibility = View.INVISIBLE
                kecilInactive.visibility = View.VISIBLE
                sedangInactive.visibility = View.VISIBLE
                besarInactive.visibility = View.VISIBLE
            }

        })
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
            showListProduct()
            kecilActive.visibility = View.INVISIBLE
            sedangActive.visibility = View.INVISIBLE
            besarActive.visibility = View.INVISIBLE
            kecilInactive.visibility = View.VISIBLE
            sedangInactive.visibility = View.VISIBLE
            besarInactive.visibility = View.VISIBLE
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
                    if (!isConnected(this@KustomisasiProdukPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: KustomisasiProdukPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}