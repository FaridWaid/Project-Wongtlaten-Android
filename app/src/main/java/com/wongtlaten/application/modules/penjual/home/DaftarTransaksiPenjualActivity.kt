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
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.profile.RiwayatTransaksiPembeliAdapter
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.penjual.payment.DetailPesananPenjualActivity

class DaftarTransaksiPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    private lateinit var rvDaftarTransaksi: RecyclerView
    private lateinit var daftarTransaksiList: ArrayList<com.wongtlaten.application.core.Transaction>
    private lateinit var adapter: DaftarTransaksiPenjualAdapter
    private lateinit var kustomisasiInactive : TextView
    private lateinit var kustomisasiActive : TextView
    private lateinit var normalInactive : TextView
    private lateinit var normalActive : TextView
    private lateinit var selesaiInactive : TextView
    private lateinit var selesaiActive : TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_transaksi_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        kustomisasiInactive = findViewById(R.id.kustomisasiInactive)
        kustomisasiActive = findViewById(R.id.kustomisasiActive)
        normalInactive = findViewById(R.id.normalInactive)
        normalActive = findViewById(R.id.normalActive)
        selesaiInactive = findViewById(R.id.selesaiInactive)
        selesaiActive = findViewById(R.id.selesaiActive)

        kustomisasiInactive.visibility = View.VISIBLE
        normalActive.visibility = View.VISIBLE
        selesaiInactive.visibility = View.VISIBLE

        kustomisasiInactive.setOnClickListener {
            kustomisasiActive.visibility = View.VISIBLE
            kustomisasiInactive.visibility = View.INVISIBLE
            normalActive.visibility = View.INVISIBLE
            selesaiActive.visibility = View.INVISIBLE
            normalInactive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.VISIBLE
            filterJenis("custom")
        }
        normalInactive.setOnClickListener {
            normalActive.visibility = View.VISIBLE
            normalInactive.visibility = View.INVISIBLE
            kustomisasiActive.visibility = View.INVISIBLE
            selesaiActive.visibility = View.INVISIBLE
            kustomisasiInactive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.VISIBLE
            filterJenis("normal")
        }
        selesaiInactive.setOnClickListener {
            selesaiActive.visibility = View.VISIBLE
            selesaiInactive.visibility = View.INVISIBLE
            kustomisasiActive.visibility = View.INVISIBLE
            normalActive.visibility = View.INVISIBLE
            kustomisasiInactive.visibility = View.VISIBLE
            normalInactive.visibility = View.VISIBLE
            filterJenis("Selesai")
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarTransaksi = findViewById(R.id.rvDaftarTransaksi)
        rvDaftarTransaksi.setHasFixedSize(true)
        rvDaftarTransaksi.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarTransaksiList = arrayListOf<com.wongtlaten.application.core.Transaction>()

        adapter = DaftarTransaksiPenjualAdapter(daftarTransaksiList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListTransaksi()

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

    private fun showListTransaksi() {
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarTransaksiList.clear()
                    for (i in snapshot.children){
                        val transactions = i.getValue(com.wongtlaten.application.core.Transaction::class.java)!!
                        if (transactions != null){
                            daftarTransaksiList.add(transactions)
                        }
                    }

                    adapter = DaftarTransaksiPenjualAdapter(daftarTransaksiList)
                    adapter.setOnItemClickCallback(object : DaftarTransaksiPenjualAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: ArrayList<Transaction>) {
                            Intent(applicationContext, DetailPesananPenjualActivity::class.java).also {
                                it.putExtra(DetailPesananPembeliActivity.EXTRA_TRANSACTION, data)
                                startActivity(it)
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                finish()
                            }
                        }

                    })
                    rvDaftarTransaksi.adapter = adapter
                    filterJenis("normal")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        var inputText = ""
        if (text.toLowerCase() == "lunas"){
            inputText = "settlement"
        } else if (text.toLowerCase() == "cancel"){
            inputText = "expire"
        } else if (text.toLowerCase() == "belum lunas"){
            inputText = "pending"
        }
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<com.wongtlaten.application.core.Transaction>()
        // setiap data yang ada pada daftarProdukList disamakan dengan filteredNames
        daftarTransaksiList.filterTo(filteredNames) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.statusPembayaran.toLowerCase().contains(inputText.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)
    }

    // Membuat fungsi "filterJenis" yang digunakan untuk memfilter recyclerView,
    private fun filterJenis(text: String) {
        // mendefiniskan variabel "filteredJenis" yang berisi arraylist dari data users
        val filteredJenis = ArrayList<com.wongtlaten.application.core.Transaction>()
        if (text == "Selesai"){
            // setiap data yang ada pada daftarAnggotaList disamakan dengan filteredJenis
            daftarTransaksiList.filterTo(filteredJenis) {
                // jika namaProduk sama dengan text input yang dimasukkan oleh user
                it.statusProduk.toLowerCase().contains(text.toLowerCase())
            }
        } else{
            // setiap data yang ada pada daftarAnggotaList disamakan dengan filteredJenis
            daftarTransaksiList.filterTo(filteredJenis) {
                // jika namaProduk sama dengan text input yang dimasukkan oleh user
                it.jenisTransaksi.toLowerCase().contains(text.toLowerCase()) && it.statusProduk != "Selesai"
            }
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredJenis)
        adapter.setOnItemClickCallback(object : DaftarTransaksiPenjualAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ArrayList<Transaction>) {
                Intent(applicationContext, DetailPesananPenjualActivity::class.java).also {
                    it.putExtra(DetailPesananPembeliActivity.EXTRA_TRANSACTION, data)
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    finish()
                }
            }

        })
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
                    if (!isConnected(this@DaftarTransaksiPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DaftarTransaksiPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}